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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.reporting.IllegalDatabaseAccessException;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.util.SqlScriptParser;
import org.openmrs.module.reporting.report.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The logic that evaluates a {@link SqlDataSetDefinition} and produces an {@link DataSet}
 * @see SqlDataSetDefinition
 */
@Handler(supports = { SqlDataSetDefinition.class })
public class SqlDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	/**
	 * Public constructor
	 */
	public SqlDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 * @should evaluate a SQLDataSetDefinition
	 * @should evaluate a SQLDataSetDefinition with parameters
	 * @should evaluate a SQLDataSetDefinition with in statement
	 * @should protect SQL Query Against database modifications
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		SqlDataSetDefinition sqlDsd = (SqlDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		
		// By default, get all patients
		Cohort cohort = context.getBaseCohort();
				
		Connection connection = null;
		try {
			connection = sessionFactory.getCurrentSession().connection();
			ResultSet resultSet = null;

			String sqlQuery = sqlDsd.getSqlQuery();
			sqlQuery = SqlScriptParser.parse(new StringReader(sqlQuery))[0];
			
			// if the user asked for only a subset, append a "limit" clause to the query so that 
			// the query runs faster in the database
			if (context.getLimit() != null && !sqlQuery.contains(" limit ")) {
				if (sqlQuery.endsWith(";"))
					sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 1);
				// this is safe to simply append because limit is always the last clause in queries
				sqlQuery += " limit " + context.getLimit();
			}
			
			PreparedStatement statement = SqlUtils.prepareStatement(connection, sqlQuery, context.getParameterValues());
			boolean result = statement.execute();
			if (result) {
				resultSet = statement.getResultSet();
			}
			
			int patientIdColumnIndex = -1;
			ResultSetMetaData rsmd = resultSet.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				DataSetColumn column = new DataSetColumn();
				column.setName(rsmd.getColumnLabel(i));
				column.setDataType(Context.loadClass(rsmd.getColumnClassName(i)));
				column.setLabel(rsmd.getColumnLabel(i));
				dataSet.getMetaData().addColumn(column);
				if ("patient_id".equals(rsmd.getColumnName(i))) {
					patientIdColumnIndex = i;
				}
			}
			
			while (resultSet.next()) {
				// Limit the DataSet to only patient in the base cohort, if there exists a column named "patientId"
				if (patientIdColumnIndex > 0) {
					Integer patientId = resultSet.getInt(patientIdColumnIndex);
					if (cohort != null && !cohort.contains(patientId)) {
						continue;
					}
				}
				DataSetRow dataSetRow = new DataSetRow();
				for (DataSetColumn column : dataSet.getMetaData().getColumns()) {
					dataSetRow.addColumnValue(column, resultSet.getObject(column.getName()));
				}
				dataSet.addRow(dataSetRow);
			}
		}
		catch (IllegalDatabaseAccessException ie) {
			throw ie;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dataSet;
	}
}
