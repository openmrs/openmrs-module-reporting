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
package org.openmrs.module.reporting.query.obs.definition.evaluator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.IllegalDatabaseAccessException;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.QueryResult;
import org.openmrs.module.reporting.query.obs.EvaluatedObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.openmrs.module.reporting.query.obs.definition.SqlObsQuery;
import org.openmrs.module.reporting.report.util.SqlUtils;
import org.openmrs.util.DatabaseUpdater;
import org.openmrs.util.OpenmrsUtil;

/**
 * The logic that evaluates a {@link SqlObsQuery} and produces an {@link Query}
 */
@Handler(supports=SqlObsQuery.class)
public class SqlObsQueryEvaluator implements ObsQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public SqlObsQueryEvaluator() { }
	
	/**
	 * @see ObsQueryEvaluator#evaluate(ObsQuery, EvaluationContext)
	 * @should evaluate a SQL query into an ObsQuery
	 * @should filter results given a base Obs Query Result in an EvaluationContext
	 * @should filter results given a base Encounter Query Result in an EvaluationContext
	 * @should filter results given a base cohort in an EvaluationContext
	 */
	public EvaluatedObsQuery evaluate(ObsQuery definition, EvaluationContext context) {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		SqlObsQuery sqlDef = (SqlObsQuery) definition;
		EvaluatedObsQuery queryResult = new EvaluatedObsQuery();
		
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
			QueryResult baseObsQuery = context.getQueryResult(Obs.class);
			QueryResult baseEncounterQuery = context.getQueryResult(Encounter.class);
			Cohort basePatientQuery = context.getBaseCohort();
			
			if (baseEncounterQuery != null) {
				String query = "select obs_id from obs where encounter_id in (" + OpenmrsUtil.join(baseEncounterQuery.getMemberIds(), ",") + ")";
				List<List<Object>> ret = Context.getAdministrationService().executeSQL(query, true);
				EvaluatedObsQuery encounterObsQuery = new EvaluatedObsQuery();
				for (List<Object> l : ret) {
					encounterObsQuery.add((Integer)l.get(0));
				}
				if (baseObsQuery == null) {
					baseObsQuery = encounterObsQuery;
				}
				else {
					baseObsQuery.getMemberIds().retainAll(encounterObsQuery.getMemberIds());
				}
			}
			
			if (basePatientQuery != null) {
				String query = "select obs_id from obs where person_id in (" + basePatientQuery.getCommaSeparatedPatientIds() + ")";
				List<List<Object>> ret = Context.getAdministrationService().executeSQL(query, true);
				EvaluatedObsQuery patientObsQuery = new EvaluatedObsQuery();
				for (List<Object> l : ret) {
					patientObsQuery.add((Integer)l.get(0));
				}
				if (baseObsQuery == null) {
					baseObsQuery = patientObsQuery;
				}
				else {
					baseObsQuery.getMemberIds().retainAll(patientObsQuery.getMemberIds());
				}
			}
			
			while (resultSet.next()) {
				Integer id = resultSet.getInt(1);
				if (baseObsQuery == null || baseObsQuery.contains(id)) { // TODO: Figure out a way to do this in the query
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
