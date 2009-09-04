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
package org.openmrs.module.dataset.definition.evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.SimpleDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.util.CohortUtil;

/**
 * The logic that evaluates a {@link PatientDataSetDefinition} and produces an {@link DataSet}
 * @see PatientDataSetDefinition
 */
@Handler(supports={PatientDataSetDefinition.class})
public class PatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public PatientDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		PatientDataSetDefinition definition = (PatientDataSetDefinition) dataSetDefinition;
		
		Cohort cohort = context.getBaseCohort();

		// By default, get all patients
		if (cohort == null)
			throw new APIException("Cohort cannot be empty");
					
		if (context.getLimit() != null)
			CohortUtil.limitCohort(cohort, context.getLimit());

		// Get a list of patients based on the cohort members
		List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());
		
		// Pre-calculate the program states
		Map<ProgramWorkflow, Map<Integer, PatientState>> states = new HashMap<ProgramWorkflow, Map<Integer, PatientState>>();
		for (ProgramWorkflow wf : definition.getProgramWorkflows()) {
			states.put(wf, Context.getPatientSetService().getCurrentStates(cohort, wf));
		}
		
		for (Patient p : patients) {			
			DataSetRow<Object> row = new DataSetRow<Object>();
			row.addColumnValue(PatientDataSetDefinition.PATIENT_ID, p.getPatientId());			
			row.addColumnValue(PatientDataSetDefinition.GIVEN_NAME, p.getGivenName());
			row.addColumnValue(PatientDataSetDefinition.FAMILY_NAME, p.getFamilyName());
			row.addColumnValue(PatientDataSetDefinition.GENDER, p.getGender());	
			row.addColumnValue(PatientDataSetDefinition.AGE, p.getAge());
			
			for (PatientIdentifierType t : definition.getIdentifierTypes()) {
				DataSetColumn c = definition.getColumn(t.getName());
				PatientIdentifier id = p.getPatientIdentifier(t);
				row.addColumnValue(c, id == null ? null : id.getIdentifier());
			}
			
			for (PersonAttributeType t : definition.getPersonAttributeTypes()) {
				DataSetColumn c = definition.getColumn(t.getName());
				PersonAttribute att = p.getAttribute(t);
				row.addColumnValue(c, att == null ? null : att.getHydratedObject());
			}
			
			for (ProgramWorkflow t : definition.getProgramWorkflows()) {
				DataSetColumn c = definition.getColumn(t.getName());
				PatientState ps = states.get(t).get(p.getPatientId());
				row.addColumnValue(c, (ps == null || !ps.getActive()) ? null : ps.getState().getName());
			}
			
			dataSet.addRow(row);
		}
		return dataSet;
	}
}
