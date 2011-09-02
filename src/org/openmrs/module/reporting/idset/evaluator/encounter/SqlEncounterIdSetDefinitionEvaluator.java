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
package org.openmrs.module.reporting.idset.evaluator.encounter;

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
import org.openmrs.module.reporting.idset.EncounterIdSet;
import org.openmrs.module.reporting.idset.EvaluatedIdSet;
import org.openmrs.module.reporting.idset.IdSet;
import org.openmrs.module.reporting.idset.definition.IdSetDefinition;
import org.openmrs.module.reporting.idset.definition.encounter.SqlEncounterIdSetDefinition;
import org.openmrs.module.reporting.idset.evaluator.IdSetDefinitionEvaluator;
import org.openmrs.module.reporting.report.util.SqlUtils;
import org.openmrs.util.DatabaseUpdater;

/**
 * The logic that evaluates a {@link SqlEncounterIdSetDefinition} and produces an {@link IdSet}
 */
@Handler(supports=SqlEncounterIdSetDefinition.class)
public class SqlEncounterIdSetDefinitionEvaluator implements IdSetDefinitionEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public SqlEncounterIdSetDefinitionEvaluator() { }
	
	/**
	 * @see IdSetDefinitionEvaluator#evaluate(IdSetDefinition, EvaluationContext)
	 * @should evaluate a SQL query into an EncounterIdSet
	 * @should filter results given a base Encounter Id Set in an EvaluationContext
	 * @should filter results given a base cohort in an EvaluationContext
	 */
	public EvaluatedIdSet evaluate(IdSetDefinition definition, EvaluationContext context) {
		
		EncounterIdSet idSet = new EncounterIdSet();
		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		SqlEncounterIdSetDefinition sqlDef = (SqlEncounterIdSetDefinition) definition;
		
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
			IdSet baseEncounterIdSet = context.getIdSet(Encounter.class);
			Cohort basePatientIdSet = context.getBaseCohort();
			if (basePatientIdSet != null) {
				String query = "select encounter_id from encounter where patient_id in (" + basePatientIdSet.getCommaSeparatedPatientIds() + ")";
				List<List<Object>> ret = Context.getAdministrationService().executeSQL(query, true);
				EncounterIdSet patientEncounterIdSet = new EncounterIdSet();
				for (List<Object> l : ret) {
					patientEncounterIdSet.add((Integer)l.get(0));
				}
				if (baseEncounterIdSet == null) {
					baseEncounterIdSet = patientEncounterIdSet;
				}
				else {
					baseEncounterIdSet.getMemberIds().retainAll(patientEncounterIdSet.getMemberIds());
				}
			}

			while (resultSet.next()) {
				Integer id = resultSet.getInt(1);
				if (baseEncounterIdSet == null || baseEncounterIdSet.contains(id)) { // TODO: Figure out a way to do this in the query
					idSet.add(id);
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
		return new EvaluatedIdSet(sqlDef, context, idSet);
	}
}
