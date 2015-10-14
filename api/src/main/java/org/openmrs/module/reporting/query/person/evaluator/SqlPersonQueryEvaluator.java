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
package org.openmrs.module.reporting.query.person.evaluator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.reporting.IllegalDatabaseAccessException;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.openmrs.module.reporting.query.person.definition.SqlPersonQuery;
import org.openmrs.module.reporting.report.util.SqlUtils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The logic that evaluates a {@link SqlPersonQuery} and produces an {@link Query}
 */
@Handler(supports = SqlPersonQuery.class)
public class SqlPersonQueryEvaluator implements PersonQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	/**
	 * Public constructor
	 */
	public SqlPersonQueryEvaluator() {
	}
	
	/**
	 * @see PersonQueryEvaluator#evaluate(PersonQuery, EvaluationContext)
	 * @should evaluate a SQL query into an PersonQuery
	 * @should filter results given a base Person Query Result in an EvaluationContext
	 * @should filter results given a base cohort in an EvaluationContext
	 */
	public PersonQueryResult evaluate(PersonQuery definition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		SqlPersonQuery queryDefinition = (SqlPersonQuery) definition;
		PersonQueryResult queryResult = new PersonQueryResult(queryDefinition, context);
		
		// TODO: Probably need to fix this
		Set<Integer> personIds = null;
		if (context instanceof PersonEvaluationContext) {
			PersonEvaluationContext pec = (PersonEvaluationContext) context;
			if (pec.getBasePersons() != null) {
				personIds = pec.getBasePersons().getMemberIds();
			}
		}
		if (personIds == null) {
			if (context.getBaseCohort() != null) {
				personIds = context.getBaseCohort().getMemberIds();
			}
		}
		
		StringBuilder sqlQuery = new StringBuilder(queryDefinition.getQuery());
		boolean whereFound = sqlQuery.indexOf("where") != -1;
		if (personIds != null) {
			whereFound = true;
			sqlQuery.append(whereFound ? " and " : " where ");
			sqlQuery.append("person_id in (" + OpenmrsUtil.join(personIds, ",") + ")");
		}
		
		if (context.getLimit() != null) {
			sqlQuery.append(" limit " + context.getLimit());
		}
		
		// TODO: Consolidate this, the cohort, and the dataset implementations and improve them
		Connection connection = null;
		try {
			connection = sessionFactory.getCurrentSession().connection();
			ResultSet resultSet = null;
			
			PreparedStatement statement = SqlUtils.prepareStatement(connection, sqlQuery.toString(),
			    context.getParameterValues());
			boolean result = statement.execute();
			
			if (!result) {
				throw new EvaluationException("Unable to evaluate sql query");
			}
			resultSet = statement.getResultSet();
			while (resultSet.next()) {
				queryResult.add(resultSet.getInt(1));
			}
		}
		catch (IllegalDatabaseAccessException ie) {
			throw ie;
		}
		catch (Exception e) {
			throw new EvaluationException("Unable to evaluate sql query", e);
		}
		return queryResult;
	}
}
