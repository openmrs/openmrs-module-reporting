/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The logic that evaluates a {@link SqlDataSetDefinition} and produces an {@link DataSet}
 * @see SqlDataSetDefinition
 */
@Handler(supports = { SqlDataSetDefinition.class })
public class SqlDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	private EvaluationService evaluationService;

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
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

		context = ObjectUtil.nvl(context, new EvaluationContext());

		SqlDataSetDefinition sqlDsd = (SqlDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

		SqlQueryBuilder queryBuilder = new SqlQueryBuilder();

		String sqlQuery = sqlDsd.getSqlQuery();

		// We don't need the final semi-colon if it exists
		if (sqlQuery.endsWith(";")) {
			sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 1);
		}

		// Add a limit clause if necessary
		if (context.getLimit() != null && !sqlQuery.contains(" limit ")) {
			sqlQuery += " limit " + context.getLimit();
		}

		queryBuilder.append(sqlQuery);
		queryBuilder.setParameters(context.getParameterValues());

		List<DataSetColumn> columns = evaluationService.getColumns(queryBuilder);

		// Validate that all defined columns have unique names
		Set<String> foundNames = new HashSet<String>();
		for (DataSetColumn column : columns) {
			String name = column.getName().toUpperCase();
			if (foundNames.contains(name)) {
				throw new EvaluationException("Invalid query specified.  There are two columns named '" + name + "'");
			}
			foundNames.add(name);
		}

		List<Object[]> results = evaluationService.evaluateToList(queryBuilder, context);

		if (context.getBaseCohort() != null && !context.getBaseCohort().isEmpty()) {
			int patientIdColumn = -1;
			for (int i=0; i<columns.size(); i++) {
				if ("patient_id".equalsIgnoreCase(columns.get(i).getName())) {
					patientIdColumn = i;
				}
			}
			if (patientIdColumn >= 0) {
				for (Object[] row : results) {
					if (context.getBaseCohort().contains((Integer)row[patientIdColumn])) {
						addRow(dataSet, row, columns);
					}
				}
			}
			else {
				log.warn("Unable to restrict result by base cohort since no patient_id column in result set. Returning empty set.");
			}
		}
		else {
			for (Object[] row : results) {
				addRow(dataSet, row, columns);
			}
		}

		return dataSet;
	}

	public void addRow(SimpleDataSet dataSet, Object[] row, List<DataSetColumn> columns) {
		DataSetRow dataSetRow = new DataSetRow();
		for (int i=0; i<columns.size(); i++) {
			dataSetRow.addColumnValue(columns.get(i), row[i]);
		}
		dataSet.addRow(dataSetRow);
	}
}