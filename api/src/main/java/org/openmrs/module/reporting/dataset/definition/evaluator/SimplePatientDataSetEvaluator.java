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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.ProgramWorkflow;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The logic that evaluates a {@link SimplePatientDataSetDefinition} and produces an {@link DataSet}
 * @see SimplePatientDataSetDefinition
 */
@Handler(supports={SimplePatientDataSetDefinition.class})
public class SimplePatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public SimplePatientDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 * @should evaluate a SimplePatientDataSetDefinition
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		SimplePatientDataSetDefinition definition = (SimplePatientDataSetDefinition) dataSetDefinition;
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();

		// By default, get all patients
		if (cohort == null) {
			cohort = Cohorts.allPatients(context);
		}
					
		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		// Get a list of patients based on the cohort members
		List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());
		
		// Pre-calculate the program states
		Map<ProgramWorkflow, Map<Integer, PatientState>> states = new HashMap<ProgramWorkflow, Map<Integer, PatientState>>();
		for (ProgramWorkflow wf : definition.getProgramWorkflows()) {
			states.put(wf, Context.getPatientSetService().getCurrentStates(cohort, wf));
		}
		
		for (Patient p : patients) {			
			DataSetRow row = new DataSetRow();
			for (PatientIdentifierType t : definition.getIdentifierTypes()) {
				DataSetColumn c = new DataSetColumn(t.getName(), t.getName(), String.class);
				PatientIdentifier id = p.getPatientIdentifier(t);
				row.addColumnValue(c, id == null ? null : id.getIdentifier());
			}
			for (String s : definition.getPatientProperties()) {
				try {
					Method m = Patient.class.getMethod("get" + StringUtils.capitalize(s), new Class[] {});
					DataSetColumn c = new DataSetColumn(s, s, m.getReturnType());
					Object o = m.invoke(p, new Object[] {});
					row.addColumnValue(c, o);
				}
				catch (Exception e) {
					log.error("Unable to get property " + s + " on patient for dataset", e);
				}
			}
			for (PersonAttributeType t : definition.getPersonAttributeTypes()) {
				DataSetColumn c = new DataSetColumn(t.getName(), t.getName(), String.class);
				PersonAttribute att = p.getAttribute(t);
				row.addColumnValue(c, att == null ? null : att.getHydratedObject());
			}
			
			for (ProgramWorkflow t : definition.getProgramWorkflows()) {
				String name = ObjectUtil.format(t.getProgram()) + " - " + ObjectUtil.format(t);
				DataSetColumn c = new DataSetColumn(name, name, String.class);
				PatientState ps = states.get(t).get(p.getPatientId());
				
				row.addColumnValue(c, (ps == null || !ps.getActive()) ? null : ps.getState().getConcept() == null ? ps.getState().toString() : ps.getState().getConcept().getDisplayString());
			}
			dataSet.addRow(row);
		}
		return dataSet;
	}
}
