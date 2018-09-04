/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SqlResult;
import org.openmrs.module.reporting.common.SqlRunner;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSetMetaData;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition.MetadataParameterConversion;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;

/**
 * Evaluates a SqlFileDataSetDefinition and produces results
 */
@Handler(supports={SqlFileDataSetDefinition.class})
public class SqlFileDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
        SimpleDataSet data = new SimpleDataSet(dataSetDefinition, context);

        SqlFileDataSetDefinition dsd = (SqlFileDataSetDefinition) dataSetDefinition;

        Properties connectionProperties = getConnectionProperties(dsd.getConnectionPropertyFile());
        Connection connection = null;
        try {
            connection = createConnection(connectionProperties);

            SqlRunner runner = new SqlRunner(connection);
            SqlResult resultData = null;
            Map<String, Object> parameterValues = constructParameterValues(dsd, context);

            if (StringUtils.isNotBlank(dsd.getSqlFile())) {
                File sqlFile = new File(dsd.getSqlFile());
                if (!sqlFile.exists()) {
                    throw new EvaluationException("Unable to find Sql File to execute: " + dsd.getSqlFile());
                }
                log.info("Executing SQL File at " + sqlFile + " with parameters " + parameterValues);
                resultData = runner.executeSqlFile(sqlFile, parameterValues);
            }
            else if (StringUtils.isNotBlank(dsd.getSqlResource())) {
                log.info("Executing SQL Resource at " + dsd.getSqlResource() + " with parameters " + parameterValues);
                resultData = runner.executeSqlResource(dsd.getSqlResource(), parameterValues);
            }
            else if (StringUtils.isNotBlank(dsd.getSql())) {
                log.info("Executing SQL with parameters " + parameterValues);
                resultData = runner.executeSql(dsd.getSql(), parameterValues);
            }
            else {
                throw new EvaluationException("A SqlFileDataSetDefinition must define either a SQL File or SQL Resource");
            }

            if (!resultData.getErrors().isEmpty()) {
                throw new EvaluationException("Errors occurred during mysql execution: " + OpenmrsUtil.join(resultData.getErrors(), "; "));
            }

            SimpleDataSetMetaData metaData = new SimpleDataSetMetaData();
            for (String column : resultData.getColumns()) {
                metaData.addColumn(new DataSetColumn(column, column, Object.class));
            }
            data.setMetaData(metaData);

            for (Map<String, Object> rowData : resultData.getData()) {
                DataSetRow row = new DataSetRow();
                for (DataSetColumn column : metaData.getColumns()) {
                    row.addColumnValue(column, rowData.get(column.getName()));
                }
                data.addRow(row);
            }
        }
        catch (EvaluationException ee) {
            throw ee;
        }
        catch (Exception e) {
            throw new EvaluationException("An error occurred while evaluating a SqlFileDataSetDefinition", e);
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (Exception e) {
                log.warn("Error closing the database connection for SqlFileDataSetEvaluator", e);
            }
        }

		return data;
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
            }
            catch (Exception e) {
                throw new EvaluationException("Unable to load connection properties from file <" + connectionPropertyFile + ">", e);
            }
            finally {
                IOUtils.closeQuietly(is);
            }
        }
        return properties;
    }

    /**
     * @return a new connection given a set of connection properties
     */
    protected Connection createConnection(Properties connectionProperties) throws EvaluationException {
	    try {
            String driver = connectionProperties.getProperty("connection.driver_class", "com.mysql.jdbc.Driver");
            String url = connectionProperties.getProperty("connection.url");
            String user = connectionProperties.getProperty("connection.username");
            String password = connectionProperties.getProperty("connection.password");
            Context.loadClass(driver);
            return DriverManager.getConnection(url, user, password);
        }
        catch (Exception e) {
	        throw new EvaluationException("Unable to create a new connection to the database", e);
        }
    }

    /**
     * @return parameter values to use within SQL statement, converting object references to metadata to scalar properties, defaulting to keys
     */
    protected Map<String, Object> constructParameterValues(SqlFileDataSetDefinition dsd, EvaluationContext context) {
        Map<String, Object> ret = context.getParameterValues();
        for (String key : ret.keySet()) {
            Object o = ret.get(key);
            if (o instanceof OpenmrsMetadata) {
                if (dsd.getMetadataParameterConversion() == MetadataParameterConversion.ID) {
                    ret.put(key, ((OpenmrsMetadata)o).getId());
                }
                else if (dsd.getMetadataParameterConversion() == MetadataParameterConversion.UUID) {
                    ret.put(key, ((OpenmrsMetadata)o).getUuid());
                }
                else if (dsd.getMetadataParameterConversion() == MetadataParameterConversion.NAME) {
                    ret.put(key, ((OpenmrsMetadata)o).getName());
                }
            }
        }
        return ret;
    }
}
