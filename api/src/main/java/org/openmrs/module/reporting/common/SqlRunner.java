/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.report.util.ReportUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Executes a SQL script
 */
public class SqlRunner {

	private static Log log = LogFactory.getLog(SqlRunner.class);

	// Regular expression to identify a change in the delimiter.  This ignores spaces, allows delimiter in comment, allows an equals-sign
    private static final Pattern DELIMITER_PATTERN = Pattern.compile("^\\s*(--)?\\s*delimiter\\s*=?\\s*([^\\s]+)+\\s*.*$", Pattern.CASE_INSENSITIVE);

    //*********** INSTANCE PROPERTIES ******************

    private Connection connection;
    private String delimiter = ";";

    //*********** CONSTRUCTORS ******************

    public SqlRunner(Connection connection) {
        this.connection = connection;
    }

    public SqlRunner(Connection connection, String delimiter) {
        this(connection);
        this.delimiter = delimiter;
    }

    /**
     * Executes a Sql Script located under resources
     */
    public SqlResult executeSqlResource(String resourceName, Map<String, Object> parameterValues) {
        String sql = ReportUtil.readStringFromResource(resourceName);
        return executeSql(sql, parameterValues);
    }

    /**
     * Executes a Sql Script located as a file
     */
    public SqlResult executeSqlFile(File sqlFile, Map<String, Object> parameterValues) {
        try {
            String sql = FileUtils.readFileToString(sqlFile, "UTF-8");
            return executeSql(sql, parameterValues);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load file: " + sqlFile, e);
        }
    }

	/**
     * Executes a Sql Script
	 */
	public SqlResult executeSql(String sql, Map<String, Object> parameterValues) {

	    SqlResult result = new SqlResult();
        log.info("Executing SQL...");

        List<String> sqlStatements = new ArrayList<String>();
        sqlStatements.addAll(parseParametersIntoStatements(parameterValues));
        sqlStatements.addAll(parseSqlIntoStatements(sql));

        Boolean originalAutoCommit = null;

        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            for (String sqlStatement : sqlStatements) {
                Statement statement = null;
                try {
                    statement = connection.createStatement();
                    log.debug("Executing: " + sqlStatement);
                    statement.execute(sqlStatement);
                    ResultSet resultSet = statement.getResultSet();

                    if (resultSet != null) {
                        ResultSetMetaData rsmd = resultSet.getMetaData();
                        int numCols = rsmd.getColumnCount();

                        for (int i = 1; i <= numCols; i++) {
                            result.addColumn(rsmd.getColumnLabel(i));
                        }

                        while (resultSet.next()) {
                            Map<String, Object> row = new HashMap<String, Object>();
                            for (int i = 1; i <= numCols; i++) {
                                String columnName = result.getColumns().get(i - 1);
                                Object value = resultSet.getObject(i);
                                row.put(columnName, value);
                            }
                            result.addData(row);
                        }
                    }
                }
                catch (Exception e) {
                    String message = "Error executing statement:  " + e.getMessage();
                    log.error(message);
                    result.addError(message);
                    throw e;
                }
                finally {
                    closeStatement(statement);
                }
            }
            rollback(); // Always rollback, as this is only intended to support querying
        }
        catch (Exception e) {
            rollback();
        }
        finally {
            resetAutocommit(originalAutoCommit);
        }

        return result;
	}

    protected void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        }
        catch (Exception e) {
            log.warn("An error occurred while trying to close a statement", e);
        }
    }

    protected void commit() {
        try {
            connection.commit();
        }
        catch (Exception e) {
            log.warn("An error occurred while trying to commit", e);
        }
    }

	protected void rollback() {
	    try {
	        connection.rollback();
        }
        catch (Exception e) {
	        log.warn("An error occurred while trying to rollback a connection", e);
        }
    }

    protected void resetAutocommit(Boolean autocommit) {
        try {
            if (autocommit != null) {
                connection.setAutoCommit(autocommit);
            }
        }
        catch (Exception e) {
            log.warn("An error occurred while trying to reset autocommit to " + autocommit, e);
        }
    }

    /**
     * @return a series of statements to set SQL variables based on the passed parameter values
     */
    public List<String> parseParametersIntoStatements(Map<String, Object> parameterValues) {
        List<String> statements = new ArrayList<String>();
        if (parameterValues != null) {
            for (String paramName : parameterValues.keySet()) {
                Object paramValue = parameterValues.get(paramName);
                String sqlVal = "null";
                if (paramValue != null) {
                    if (paramValue instanceof Date) {
                        sqlVal = "'" + DateUtil.formatDate((Date)paramValue, "yyyy-MM-dd") + "'";
                    }
                    else if (paramValue instanceof Number || paramValue instanceof Boolean) {
                        sqlVal = paramValue.toString();
                    }
                    else if (paramValue instanceof OpenmrsObject) {
                        sqlVal = ((OpenmrsObject)paramValue).getId().toString();
                    }
                    else {
                        sqlVal = "'" + paramValue.toString() + "'";
                    }
                }
                statements.add("set @" + paramName + "=" + sqlVal);
            }
        }
        return statements;
    }


    /**
     * @return a List of statements that are parsed out of the passed sql, ignoring comments, and respecting delimiter assignment
     */
    public List<String> parseSqlIntoStatements(String sql) {
	    List<String> statements = new ArrayList<String>();
	    StringBuilder currentStatement = new StringBuilder();

	    String currentDelimiter = getDelimiter();
	    boolean inMultiLineComment = false;

	    for (String line : sql.split("\\r?\\n")) {

	        // First, trim the line and remove any trailing comments in the form of "statement;  -- Comments here"
	        int delimiterIndex = line.indexOf(currentDelimiter);
	        int dashCommentIndex = line.indexOf("--");
	        if (delimiterIndex > 0 && delimiterIndex < dashCommentIndex) {
                line = line.substring(0, dashCommentIndex);
            }
            line = line.trim();

	        // Check to see if this line is within a multi-line comment, or if it ends a multi-line comment
	        if (inMultiLineComment) {
	            if (isEndOfMultiLineComment(line)) {
	                inMultiLineComment = false;
                }
            }
            // If we are not within a multi-line comment, then process the line, if it is not a single line comment or empty space
            else {
                if (!isEmptyLine(line) && !isSingleLineComment(line)) {

                    // If this line starts a multi-line comment, then ignore it and mark for next iteration
                    if (isStartOfMultiLineComment(line)) {
                        inMultiLineComment = true;
                    }
                    else {
                        // If the line is serving to set a new delimiter, set it and continue
                        String newDelimiter = getNewDelimiter(line);
                        if (newDelimiter != null) {
                            currentDelimiter = newDelimiter;
                        }
                        else {
                            // If we are here, that means that this line is part of an actual sql statement
                            if (line.endsWith(currentDelimiter)) {
                                line = line.substring(0, line.lastIndexOf(currentDelimiter));
                                currentStatement.append(line);
                                statements.add(currentStatement.toString());
                                currentStatement = new StringBuilder();
                            }
                            else {
                                currentStatement.append(line).append("\n");
                            }
                        }
                    }
                }
            }
        }
        if (currentStatement.length() > 0) {
            statements.add(currentStatement.toString());
        }
        return statements;
    }

    //********** CONVENIENCE METHODS **************

    protected boolean isEmptyLine(String line) {
        return line == null || StringUtils.isBlank(line);
    }

    protected boolean isSingleLineComment(String line) {
        return line.startsWith("--") || line.startsWith("//") || (isStartOfMultiLineComment(line) && isEndOfMultiLineComment(line));
    }

    protected boolean isStartOfMultiLineComment(String line) {
        return line.startsWith("/*");
    }

    protected boolean isEndOfMultiLineComment(String line) {
        return line.endsWith("*/");
    }

    protected String getNewDelimiter(String line) {
        Matcher matcher = DELIMITER_PATTERN.matcher(line);
        if (matcher.matches()) {
            return matcher.group(2);
        }
        return null;
    }

    //************* PROPERTY ACCESS *******************

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
