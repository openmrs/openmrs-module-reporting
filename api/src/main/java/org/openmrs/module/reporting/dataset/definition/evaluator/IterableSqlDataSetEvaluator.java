/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SqlRunner;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.IterableSqlDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.IterableSqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.common.SqlIterator;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.Properties;

/**
 * The logic that evaluates a {@link IterableSqlDataSetDefinition} and produces an {@link DataSet}
 *
 * @see IterableSqlDataSetDefinition
 */
@Handler(supports = {IterableSqlDataSetDefinition.class})
public class IterableSqlDataSetEvaluator implements DataSetEvaluator {

    private static final Logger log = LoggerFactory.getLogger(IterableSqlDataSetEvaluator.class);

    /**
     * Public constructor
     */
    public IterableSqlDataSetEvaluator() {
    }

    /**
     * @should evaluate a IterableSqlDataSetDefinition
     * @should evaluate a IterableSqlDataSetDefinition with parameters
     * @should evaluate a IterableSqlDataSetDefinition with in statement
     * @should protect SQL Query Against database modifications
     * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
     */
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

        context = ObjectUtil.nvl(context, new EvaluationContext());

        IterableSqlDataSetDefinition sqlDsd = (IterableSqlDataSetDefinition) dataSetDefinition;

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();

        String sqlQuery = sqlDsd.getSql();

        queryBuilder.append(sqlQuery);

        queryBuilder.setParameters(context.getParameterValues());

        IterableSqlDataSetDefinition defenition = (IterableSqlDataSetDefinition) dataSetDefinition;
        Properties connectionProperties = getConnectionProperties(defenition.getConnectionPropertyFile());
        Iterator iterator = null;
        Connection connection = null;
        try {
            connection = createConnection(connectionProperties);
            SqlRunner runner = new SqlRunner(connection);

            if (StringUtils.isNotBlank(defenition.getSql())) {
                iterator = runner.executeSqlToIterator(defenition.getSql());

            }

        } catch (EvaluationException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EvaluationException("An error occurred while evaluating a SqlFileDataSetDefinition", e);
        }

        return new IterableSqlDataSet(context, sqlDsd, (SqlIterator) iterator);
    }

    /**
     * @return a new connection given a set of connection properties
     */
    protected Connection createConnection(Properties connectionProperties) throws EvaluationException {
        try {
            String driver = connectionProperties.getProperty("connection.driver_class", "com.mysql.jdbc.Driver");
            String url = connectionProperties.getProperty("connection.url");
            url += "&useCursorFetch=true";
            String user = connectionProperties.getProperty("connection.username");
            String password = connectionProperties.getProperty("connection.password");
            Context.loadClass(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new EvaluationException("Unable to create a new connection to the database", e);
        }
    }

    /**
     * @return the connection properties to use
     */
    protected Properties getConnectionProperties(String connectionPropertyFile) throws EvaluationException {
        Properties properties = Context.getRuntimeProperties();
        if (StringUtils.isNotBlank(connectionPropertyFile)) {
            properties = new Properties();
            InputStream is = null;
            try {
                File file = new File(OpenmrsUtil.getApplicationDataDirectory(), connectionPropertyFile);
                is = new FileInputStream(file);
                properties.load(is);
            } catch (Exception e) {
                throw new EvaluationException("Unable to load connection properties from file <" + connectionPropertyFile + ">", e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return properties;
    }

}