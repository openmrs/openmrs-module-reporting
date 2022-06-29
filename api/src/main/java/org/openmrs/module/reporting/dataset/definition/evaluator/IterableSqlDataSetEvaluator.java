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
import org.openmrs.module.reporting.common.ResultSetIterator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * The logic that evaluates a {@link IterableSqlDataSetDefinition} and produces an {@link DataSet}
 *
 * @see IterableSqlDataSetDefinition
 */
@Handler(supports = {IterableSqlDataSetDefinition.class}, order = 50)
public class IterableSqlDataSetEvaluator extends SqlFileDataSetEvaluator {

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
    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

        context = ObjectUtil.nvl(context, new EvaluationContext());
        IterableSqlDataSetDefinition sqlDsd = (IterableSqlDataSetDefinition) dataSetDefinition;
        String sqlQuery = sqlDsd.getSql();

        IterableSqlDataSetDefinition definition = (IterableSqlDataSetDefinition) dataSetDefinition;
        Properties connectionProperties = getConnectionProperties(definition.getConnectionPropertyFile());
        Iterator iterator = null;
        Connection connection = null;

        try {
            createConnection(connectionProperties);
            SqlRunner runner = new SqlRunner(this);
            Map<String, Object> parameterValues = constructParameterValues(definition, context);

            if (StringUtils.isNotBlank(definition.getSqlFile())) {
                File sqlFile = new File(definition.getSqlFile());
                if (!sqlFile.exists()) {
                    throw new EvaluationException("Unable to find Sql File to execute: " + definition.getSqlFile());
                }
                log.info("Executing SQL File at " + sqlFile + " with parameters " + parameterValues);
                iterator = runner.executeSqlFileToIterator(sqlFile, parameterValues);
            } else if (StringUtils.isNotBlank(definition.getSqlResource())) {
                log.info("Executing SQL Resource at " + definition.getSqlResource() + " with parameters " + parameterValues);
                iterator = runner.executeSqlResourceToIterator(definition.getSqlResource(), parameterValues);
            } else if (StringUtils.isNotBlank(definition.getSql())) {
                log.info("Executing SQL with parameters " + parameterValues);
                iterator = runner.executeSqlToIterator(definition.getSql(), parameterValues);
            } else {
                throw new EvaluationException("A SqlFileDataSetDefinition must define either a SQL File or SQL Resource");
            }

        } catch (EvaluationException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EvaluationException("An error occurred while evaluating a SqlFileDataSetDefinition", e);
        }

        return new IterableSqlDataSet(context, sqlDsd, (ResultSetIterator) iterator);
    }

    /**
     * @return a new connection given a set of connection properties
     */
    @Override
    protected void createConnection(Properties connectionProperties) throws EvaluationException {
        try {
            String driver = connectionProperties.getProperty("connection.driver_class", "com.mysql.jdbc.Driver");
            String url = connectionProperties.getProperty("connection.url");
            url += "&useCursorFetch=true";
            String user = connectionProperties.getProperty("connection.username");
            String password = connectionProperties.getProperty("connection.password");
            Context.loadClass(driver);
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new EvaluationException("Unable to create a new connection to the database", e);
        }
    }

}