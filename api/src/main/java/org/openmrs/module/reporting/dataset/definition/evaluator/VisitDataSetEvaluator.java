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

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.VisitDataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.QueryUtil;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.module.reporting.query.visit.definition.AllVisitQuery;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.openmrs.module.reporting.query.visit.service.VisitQueryService;

/**
 * The logic that evaluates a {@link org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition} and produces an {@link org.openmrs.module.reporting.dataset.DataSet}
 */
@Handler(supports=VisitDataSetDefinition.class)
public class VisitDataSetEvaluator implements DataSetEvaluator {

    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * Public constructor
     */
    public VisitDataSetEvaluator() { }

    /**
     * @see DataSetEvaluator#evaluate(org.openmrs.module.reporting.dataset.definition.DataSetDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     */
    @SuppressWarnings("unchecked")
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

        VisitDataSetDefinition dsd = (VisitDataSetDefinition) dataSetDefinition;
        context = ObjectUtil.nvl(context, new EvaluationContext());

        SimpleDataSet dataSet = new SimpleDataSet(dsd, context);
        dataSet.setSortCriteria(dsd.getSortCriteria());

        // Construct a VisitEvaluationContext based on the visit filter
        VisitIdSet r = null;
        if (dsd.getRowFilters() != null) {
            for (Mapped<? extends VisitQuery> q : dsd.getRowFilters()) {
                VisitIdSet s = Context.getService(VisitQueryService.class).evaluate(q, context);
                r = QueryUtil.intersectNonNull(r, s);
            }
        }
        if (r == null) {
            r = Context.getService(VisitQueryService.class).evaluate(new AllVisitQuery(), context);
        }
        VisitEvaluationContext vec = new VisitEvaluationContext(context, r);
        vec.setBaseCohort(null); // We can do this because the visitIdSet is already limited by these

        // Evaluate each specified ColumnDefinition for all of the included rows and add these to the dataset
        for (RowPerObjectColumnDefinition cd : dsd.getColumnDefinitions()) {

            if (log.isDebugEnabled()) {
                log.debug("Evaluating column: " + cd.getName());
                log.debug("With Data Definition: " + DefinitionUtil.format(cd.getDataDefinition().getParameterizable()));
                log.debug("With Mappings: " + cd.getDataDefinition().getParameterMappings());
                log.debug("With Parameters: " + vec.getParameterValues());
            }

            StopWatch sw = new StopWatch();
            sw.start();

            MappedData<? extends VisitDataDefinition> dataDef = (MappedData<? extends VisitDataDefinition>) cd.getDataDefinition();
            EvaluatedVisitData data = Context.getService(VisitDataService.class).evaluate(dataDef, vec);

            DataSetColumn column = new DataSetColumn(cd.getName(), cd.getName(), dataDef.getParameterizable().getDataType()); // TODO: Support One-Many column definition to column

            for (Integer id : r.getMemberIds()) {
                Object val = data.getData().get(id);
                val = DataUtil.convertData(val, dataDef.getConverters());
                dataSet.addColumnValue(id, column, val);
            }

            sw.stop();
            if (log.isDebugEnabled()) {
                log.debug("Added encounter column: " + sw.toString());
            }

        }

        return dataSet;
    }
}