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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.common.LogicUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.DataSetRowList;
import org.openmrs.module.reporting.dataset.LazyPageableDataSet;
import org.openmrs.module.reporting.dataset.PageableDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition.Column;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition.ColumnFormatter;
import org.openmrs.module.reporting.dataset.definition.PageableDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Prepares a {@link LazyPageableDataSet} that will lazily evaluate LogicDataSetDefinitions
 */
@Deprecated
@Handler(supports={LogicDataSetDefinition.class})
public class LogicDataSetEvaluator implements LazyPageableDataSetEvaluator {

	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public PageableDataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws ReportingException {
		LogicDataSetDefinition def = (LogicDataSetDefinition) dataSetDefinition;

		// test the logic expressions in the definition, so if any of them are broken we fail here
		// rather than returning a LazyPageableDataSet that will fail on every partial evaluation
		for (Column col : def.getColumns()) {
			try {
				LogicUtil.parse(col.getLogic());
			} catch (LogicException ex) {
				throw new ReportingException("Error parsing logic: " + col.getLogic(), ex);
			}
		}
		
		LazyPageableDataSet ret = new LazyPageableDataSet(this, evalContext, def);
		//TODO profile with different values for this: ret.setMaximumPatientsToEvaluate(250);
		return ret;
	}

	/**
	 * @see LazyPageableDataSetEvaluator#evaluatePartial(PageableDataSetDefinition, EvaluationContext, List)
	 */
	public Iterator<DataSetRow> evaluatePartial(PageableDataSetDefinition definition, EvaluationContext context,
	                                        List<Integer> patientIds) {
		LogicService logicService = Context.getLogicService();
		LogicDataSetDefinition def = (LogicDataSetDefinition) definition;
	    Cohort cohort = new Cohort(patientIds);
	    
	    List<ColumnFormatter> columnFormatters = new ArrayList<ColumnFormatter>();
	    for (Column col : def.getColumns()) {
	    	columnFormatters.add(col.getFormatter());
	    }
	    
	    // Note that we cannot import or reference LogicCriteria in this class because it was
	    // changed from a Class to an Interface in OpenMRS 1.6, so building the module while
	    // referencing the 1.5.x branch, and trying to run in the 1.6.x branch gives an
	    // IncompatibleClassChangeException. (We don't want to branch this module into
	    // 1.5.x-compatible versus 1.6.x-and-later versions if we can help it.) So we will
	    // run the rules one-by-one rather than creating a List<LogicCriteria>.
	    
	    // (Map from patientId to one result per column)
		Map<Integer, List<Result>> results = new LinkedHashMap<Integer, List<Result>>();
		for (Column col : def.getColumns()) {
			try {
				// implicitly parse the String to a LogicCriteria in this line
				Map<Integer, Result> temp = logicService.eval(cohort, LogicUtil.parse(col.getLogic()));
				for (Map.Entry<Integer, Result> e : temp.entrySet()) {
					List<Result> forPatient = results.get(e.getKey());
					if (forPatient == null) {
						forPatient = new ArrayList<Result>();
						results.put(e.getKey(), forPatient);
					}
					forPatient.add(e.getValue());
				}
			} catch (LogicException ex) {
				throw new ReportingException("Error evaluating column -> " + col.getLogic(), ex);
			}
		}

		boolean anyColumns = def.getColumns().size() > 0;
		DataSetRowList ret = new DataSetRowList();
		for (Integer ptId : cohort.getMemberIds()) {
			List<Result> forPatient = results.get(ptId);
			if (forPatient == null && anyColumns)
				throw new ReportingException("Logic Module did not return results for all patients in cohort");
			DataSetRow row = new DataSetRow();
			for (int i = 0; i < def.getColumns().size(); ++i) {
				Column col = def.getColumns().get(i);
				Result result = forPatient.get(i);
				row.addColumnValue(col, columnFormatters.get(i).format(result));
			}
			ret.add(row);
		}
		
		return ret.iterator();
	}
	
}
