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
package org.openmrs.module.reporting.query.encounter.evaluator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.IllegalDatabaseAccessException;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.QueryResult;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.report.util.SqlUtils;
import org.openmrs.util.DatabaseUpdater;

/**
 * The logic that evaluates a {@link SqlEncounterQuery} and produces an {@link Query}
 */
@Handler(supports=SqlEncounterQuery.class)
public class SqlEncounterQueryEvaluator implements EncounterQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public SqlEncounterQueryEvaluator() { }
	
	/**
	 * @see EncounterQueryEvaluator#evaluate(EncounterQuery, EvaluationContext)
	 * @should evaluate a SQL query into an EncounterQuery
	 * @should filter results given a base Encounter Query Result in an EvaluationContext
	 * @should filter results given a base cohort in an EvaluationContext
	 */
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		SqlEncounterQuery sqlDef = (SqlEncounterQuery) definition;
		EncounterQueryResult queryResult = new EncounterQueryResult(sqlDef, context);
		
		// TODO: Consolidate this, the cohort, and the dataset implementations and improve them
		Connection connection = null;
		try {
			connection = DatabaseUpdater.getConnection();
			ResultSet resultSet = null;

			String sqlQuery = sqlDef.getQuery();

			// Limit if indicated in the EvaluationContext
			if (context.getLimit() != null && !sqlQuery.contains(" limit ")) {
				if (sqlQuery.endsWith(";")) {
					sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 1);
				}
				sqlQuery += " limit " + context.getLimit();
			}
			
			PreparedStatement statement = SqlUtils.prepareStatement(connection, sqlQuery, context.getParameterValues());
			boolean result = statement.execute();

			if (result) {
				resultSet = statement.getResultSet();
			}
			else {
				throw new EvaluationException("Unable to evaluate sql query");
			}

			// TODO: This will need replacing
			QueryResult baseEncounterQuery = context.getQueryResult(Encounter.class);
			Cohort basePatientQuery = context.getBaseCohort();
			if (basePatientQuery != null) {
				String query = "select encounter_id from encounter where patient_id in (" + basePatientQuery.getCommaSeparatedPatientIds() + ")";
				List<List<Object>> ret = Context.getAdministrationService().executeSQL(query, true);
				EncounterQueryResult patientEncounterQuery = new EncounterQueryResult();
				for (List<Object> l : ret) {
					patientEncounterQuery.add((Integer)l.get(0));
				}
				if (baseEncounterQuery == null) {
					baseEncounterQuery = patientEncounterQuery;
				}
				else {
					baseEncounterQuery.getMemberIds().retainAll(patientEncounterQuery.getMemberIds());
				}
			}

			while (resultSet.next()) {
				Integer id = resultSet.getInt(1);
				if (baseEncounterQuery == null || baseEncounterQuery.contains(id)) { // TODO: Figure out a way to do this in the query
					queryResult.add(id);
				}
			}
		}
		catch (IllegalDatabaseAccessException ie) {
			throw ie;
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
		return queryResult;
	}
}
