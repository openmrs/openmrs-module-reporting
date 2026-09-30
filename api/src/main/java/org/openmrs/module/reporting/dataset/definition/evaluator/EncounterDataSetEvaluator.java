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
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.QueryUtil;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.definition.AllEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;

/**
 * The logic that evaluates a {@link EncounterDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports=EncounterDataSetDefinition.class)
public class EncounterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public EncounterDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		EncounterDataSetDefinition dsd = (EncounterDataSetDefinition) dataSetDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());

		SimpleDataSet dataSet = new SimpleDataSet(dsd, context);
		dataSet.setSortCriteria(dsd.getSortCriteria());
		
		// Construct an EncounterEvaluationContext based on the encounter filter
		EncounterIdSet r = null;
		if (dsd.getRowFilters() != null) {
			for (Mapped<? extends EncounterQuery> q : dsd.getRowFilters()) {
				EncounterIdSet s = Context.getService(EncounterQueryService.class).evaluate(q, context);
				r = QueryUtil.intersectNonNull(r, s);
			}
		}
		if (r == null) {
			r = Context.getService(EncounterQueryService.class).evaluate(new AllEncounterQuery(), context);
		}
		EncounterEvaluationContext eec = new EncounterEvaluationContext(context, r);
		eec.setBaseCohort(null); // We can do this because the encounterIdSet is already limited by these

		// Evaluate each specified ColumnDefinition for all of the included rows and add these to the dataset
		for (RowPerObjectColumnDefinition cd : dsd.getColumnDefinitions()) {

			if (log.isDebugEnabled()) {
				log.debug("Evaluating column: " + cd.getName());
				log.debug("With Data Definition: " + DefinitionUtil.format(cd.getDataDefinition().getParameterizable()));
				log.debug("With Mappings: " + cd.getDataDefinition().getParameterMappings());
				log.debug("With Parameters: " + eec.getParameterValues());
			}

			StopWatch sw = new StopWatch();
			sw.start();

			MappedData<? extends EncounterDataDefinition> dataDef = (MappedData<? extends EncounterDataDefinition>) cd.getDataDefinition();
			EvaluatedEncounterData data = Context.getService(EncounterDataService.class).evaluate(dataDef, eec);

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
