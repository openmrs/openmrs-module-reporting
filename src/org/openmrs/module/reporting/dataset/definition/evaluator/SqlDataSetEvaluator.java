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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.util.DatabaseUpdater;

/**
 * The logic that evaluates a {@link SqlDataSetDefinition} and produces an {@link DataSet}
 * @see SqlDataSetDefinition
 */
@Handler(supports={SqlDataSetDefinition.class})
public class SqlDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public SqlDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		SqlDataSetDefinition sqlDsd = (SqlDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		
		// By default, get all patients
		Cohort cohort = context.getBaseCohort();
		if (cohort == null) {
			cohort = Context.getPatientSetService().getAllPatients();
		}
					
		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		Connection connection = null;
		try { 		
			connection = DatabaseUpdater.getConnection();
			ResultSet resultSet = null;
			Statement statement = connection.createStatement();
			boolean result = statement.execute(sqlDsd.getSqlQuery());
			if (result) { 
				resultSet = statement.getResultSet();
			}
			
			int patientIdColumnIndex = -1;
			ResultSetMetaData rsmd = resultSet.getMetaData();
			for (int i=1; i<=rsmd.getColumnCount();i++) {
				DataSetColumn column = new DataSetColumn();
				column.setName(rsmd.getColumnName(i));
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
					if (!cohort.contains(patientId)) {
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
		catch (Exception e) { 
			throw new RuntimeException(e);			
		} 
		finally { 
			try { 
				if (connection != null) {
					connection.close();
				}
			} 
			catch (Exception e) { 
				log.error("Error while closing connection", e); 
			} 			
		}
		return dataSet;
	}
}
