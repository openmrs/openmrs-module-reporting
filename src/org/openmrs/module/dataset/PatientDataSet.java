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
package org.openmrs.module.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;

/**
 * A dataset with one-row-per-encounter.
 * 
 * @see EncounterDataSetDefinition
 */
public class PatientDataSet implements DataSet<Object> {
	
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private PatientDataSetDefinition definition;	
	private EvaluationContext evaluationContext;	
	private List<Patient> patients;
	
	
	public PatientDataSet(PatientDataSetDefinition definition, EvaluationContext context, List<Patient> patients) { 
		this.definition = definition;
		this.evaluationContext = context;
		this.patients = patients;
	}
	
	/**
	 * This is wrapped around (List<Obs>).iterator() This implementation is NOT thread-safe, so do
	 * not access the wrapped iterator.
	 */
	class HelperIterator implements Iterator<Map<DataSetColumn, Object>> {
		
		private Iterator<Patient> iter;
		
		public HelperIterator(Iterator<Patient> iter) {
			this.iter = iter;
		}
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		/**
		 * @see java.util.Iterator#next()
		 */
		public Map<DataSetColumn, Object> next() {
			Map<DataSetColumn, Object> vals = new HashMap<DataSetColumn, Object>();
			//Locale locale = Context.getLocale();
			
			// Add default values for the encounter dataset
			// TODO These need to be added as columns to the dataset definition
			// TODO We need a way to sync these up
			Patient patient = iter.next();
			
			// Build the dataset row
			// TODO I'm not in love with the this approach, but we'll refactor later if we need to
			vals.put(new SimpleDataSetColumn(PatientDataSetDefinition.PATIENT_ID), patient.getPatientId());
			vals.put(new SimpleDataSetColumn(PatientDataSetDefinition.NAME), patient.getPersonName());
			vals.put(new SimpleDataSetColumn(PatientDataSetDefinition.GENDER), patient.getGender());	
			vals.put(new SimpleDataSetColumn(PatientDataSetDefinition.AGE), patient.getAge());			
			vals.put(new SimpleDataSetColumn(PatientDataSetDefinition.HEALTH_CENTER), getCurrentHealthCenter(patient));			
			vals.put(new SimpleDataSetColumn(PatientDataSetDefinition.TREATMENT_GROUP), getCurrentTreatmentGroup(patient));			
			
			return vals;
		}
		
		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			iter.remove();
		}
		
	}
	
	/**
	 * @see org.openmrs.module.dataset.api.DataSet#iterator()
	 */
	public Iterator<Map<DataSetColumn, Object>> iterator() {
		return new HelperIterator(patients.iterator());
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<DataSetColumn> getColumns() { 
		return definition.getColumns();
	}
	
	
	/**
	 * @return the data
	 */
	public List<Patient> getPatients() {
		return patients;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}
	
	/**
	 * @return the definition
	 */
	public DataSetDefinition getDataSetDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDataSetDefinition(PatientDataSetDefinition definition) {
		this.definition = definition;
	}
	
	/**
	 * @see org.openmrs.module.dataset.DataSet#getEvaluationContext()
	 */
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
	
	/**
	 * @param evaluationContext the evaluationContext to set
	 */
	public void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}
	
	
	
	/**
	 * TODO Refactor this -- we don't want logic like this in generic datasets.
	 */
	public String getCurrentTreatmentGroup(Patient patient) { 
		try { 
			
			Program program = 
				Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
			
			List<PatientProgram> patientPrograms = 
				Context.getProgramWorkflowService().getPatientPrograms(
					patient, program, null, null, null, null, false);

			// 9 = TREATMENT GROUP
			ProgramWorkflow workflow = 
				Context.getProgramWorkflowService().getWorkflow(9);
			
			if (!patientPrograms.isEmpty()) {
				PatientState currentState = patientPrograms.get(0).getCurrentState(workflow);
				return currentState.getState().getName();
			}
			else { 
				return "None";
			}
			
		} 
		catch (Exception e) { 
			log.info("Unable to retrieve patient's current treatment group: " + e.getMessage());
		}
		
		return "Unknown";
		
	}
	
	/**
	 * TODO Refactor this -- we don't want logic like this in generic datasets.
	 * 
	 */
	public String getCurrentHealthCenter(Patient patient) { 
		try { 
			
			// Health Center
			PersonAttributeType attributeType = 
				Context.getPersonService().getPersonAttributeType(7);
			
			Integer locationId = 
				Integer.parseInt(patient.getAttribute(attributeType).getValue());
			
			return Context.getLocationService().getLocation(locationId).getName();
			
		} 
		catch (Exception e) { 
			log.info("Unable to retrieve patient's current health center: "  + e.getMessage());
		}
		
		return "Unknown";
		
	}	
	
}
