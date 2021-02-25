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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.IterableSqlDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.IterableSqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.ResultSetIterator;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The logic that evaluates a {@link IterableSqlDataSetDefinition} and produces an {@link DataSet}
 * @see IterableSqlDataSetDefinition
 */
@Handler(supports = {IterableSqlDataSetDefinition.class})
public class IterableSqlDataSetEvaluator implements IterableDataSetEvaluator {

    protected Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private EvaluationService evaluationService;

    /**
     * Public constructor
     */
    public IterableSqlDataSetEvaluator() {
    }

    /**
     * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
     * @should evaluate a IterableSqlDataSetDefinition
     * @should evaluate a IterableSqlDataSetDefinition with parameters
     * @should evaluate a IterableSqlDataSetDefinition with in statement
     * @should protect SQL Query Against database modifications
     */
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

        context = ObjectUtil.nvl(context, new EvaluationContext());

        IterableSqlDataSetDefinition sqlDsd = (IterableSqlDataSetDefinition) dataSetDefinition;

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

        Iterator iterator = evaluationService.evaluateToIterator(queryBuilder, context);

        // Validate that all defined columns have unique names
        List<DataSetColumn> columns = ((ResultSetIterator) iterator).getColumns();
        Set<String> foundNames = new HashSet<>();
        for (DataSetColumn column : columns) {
            String name = column.getName().toUpperCase();
            if (foundNames.contains(name)) {
                throw new EvaluationException("Invalid query specified.  There are two columns named '" + name + "'");
            }
            foundNames.add(name);
        }

        return new IterableSqlDataSet(context, sqlDsd, (ResultSetIterator) iterator);
    }
}