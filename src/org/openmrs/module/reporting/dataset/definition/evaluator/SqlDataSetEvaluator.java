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
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.DataSetColumn;
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
	
	
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) {
		if (dataSetDefinition instanceof SqlDataSetDefinition) { 
			return evaluate((SqlDataSetDefinition)dataSetDefinition, evalContext);			
		} 
		throw new APIException("JdbcDataSetEvaluator cannot evaluate dataset definition of type" + dataSetDefinition.getClass().getName());
	}
	
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(SqlDataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		
		// By default, get all patients
		Cohort cohort = context.getBaseCohort();
		if (cohort == null)
			cohort = Context.getPatientSetService().getAllPatients();
					
		if (context.getLimit() != null)
			CohortUtil.limitCohort(cohort, context.getLimit());

		// Get a list of patients based on the cohort members
		List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());

		List<DataSetColumn> columns = dataSetDefinition.getColumns();
		Connection connection = null;
		try { 		
			connection = DatabaseUpdater.getConnection();
			ResultSet resultSet = null;
			Statement statement = connection.createStatement();
			boolean result = statement.execute(dataSetDefinition.getSqlQuery());
			if (result) { 
				resultSet = statement.getResultSet();
			}
			while(resultSet.next()) { 

				DataSetRow dataSetRow = new DataSetRow();
				for (DataSetColumn column : columns) { 
					String value = resultSet.getString(column.getColumnKey());
					dataSetRow.addColumnValue(column, value);
				}					
				dataSet.addRow(dataSetRow);
			}			
		} catch (Exception e) { 
			log.error("Error while getting connection ", e);			
		} finally { 
			try { 
				if (connection != null) 
					connection.close();
			} catch (Exception e) { log.error("Error while closing connection", e); } 			
		}
		return dataSet;

	}
	
	
	class LogicResultHolder { 
	
		private String token;
		private Boolean evaluated;
		private LogicCriteria criteria;
		private Map<Integer, Result> results;
		
		public LogicResultHolder(String token, LogicCriteria criteria, Map<Integer,Result> results, Boolean evaluated) { 
			this.token = token;
			this.criteria = criteria;
			this.results = results;
			this.evaluated = evaluated;			
		}
		
		
		public String getToken() { 
			return this.token;
		}
		
		public void setToken(String token) { 
			this.token = token;
		}
		
		public Boolean getEvaluated() {
			return evaluated;
		}
		public void setEvaluated(Boolean evaluated) {
			this.evaluated = evaluated;
		}
		public LogicCriteria getCriteria() {
			return criteria;
		}
		public void setCriteria(LogicCriteria criteria) {
			this.criteria = criteria;
		}
		public Map<Integer, Result> getResults() {
			return results;
		}
		public void setResults(Map<Integer, Result> results) {
			this.results = results;
		}
	
	}


	
}
