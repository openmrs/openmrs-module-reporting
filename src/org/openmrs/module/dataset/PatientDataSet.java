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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * A dataset with one-row-per-encounter.
 * 
 * @see EncounterDataSetDefinition
 */
public class PatientDataSet implements DataSet<Object> {
	
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private PatientDataSetDefinition definition;	
	private EvaluationContext context;	
	private List<Patient> patients;


	// Constants
	final private static String PATIENT_IDENTIFIER = "IMB ID";
	final private static String HIV_PROGRAM = "HIV PROGRAM";
	final private static String HIV_TREATMENT_GROUP = "TREATMENT GROUP"; // ANTIRETROVIRAL TREATMENT GROUP
	final private static String HIV_TREATMENT_STATUS = "TREATMENT STATUS"; // ANTIRETROVIRAL TREATMENT STATUS	
	final private static String HEALTH_CENTER = "Health Center";
	final private static String WORDS_TO_REMOVE = "FOLLOWING,GROUP";
	
	// Static data
	private PatientIdentifierType patientIdentifierType = null;
	private Program hivProgram = null;
	private Map<Patient, List<PatientProgram>> patientProgramMap = null;
	private ProgramWorkflow hivTreatmentGroup = null;
	private PersonAttributeType healthCenterAttributeType = null; 
	private List<Location> locations = null;
	
	
	/**
	 * Constructor 
	 * @param definition
	 * @param context
	 * @param patients
	 */
	public PatientDataSet(PatientDataSetDefinition definition, EvaluationContext context, List<Patient> patients) { 
		this.definition = definition;
		this.context = context;
		this.patients = patients;
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
	public PatientDataSetDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(PatientDataSetDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @return the context
	 */
	public EvaluationContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(EvaluationContext context) {
		this.context = context;
	}

	/**
	 * Gets the current treatment group for the given patient.
	 * 
	 * TODO Refactor this -- we don't want logic like this in generic datasets.
	 */
	public String getCurrentTreatmentGroup(Patient patient) { 

		String treatmentGroup = "NONE";
		
		try { 			
			
			Program program = getHivProgram();
			
			List<PatientProgram> patientPrograms = getPatientPrograms(program, patient);
			
			ProgramWorkflow workflow = getHivTreatmentGroup();
			
			if (patientPrograms!=null && !patientPrograms.isEmpty()) {
				
				PatientState currentState = 
					patientPrograms.get(0).getCurrentState(workflow);
				
				// Assumes that a concept and name are associated with the state
				if (currentState != null && currentState.getActive()) {
					treatmentGroup = ""; // active
					treatmentGroup = currentState.getState().getConcept().getName().getName();

					// Remove unwanted words
					for (String word : WORDS_TO_REMOVE.split(",")) { 
						treatmentGroup = treatmentGroup.replace(word, "");
					}					
					treatmentGroup = treatmentGroup.trim();
					
					
				} else {
					treatmentGroup = ""; // inactive
				}
				
			} else { 
				treatmentGroup = ""; // not enrolled
			}
		} 
		catch (Exception e) { 
			log.error("Unable to retrieve current treatment group for patient " + patient.getPatientId(), e);
		}
		
		return treatmentGroup;
		
	}
	
	/**
	 * Gets the current health center for the given patient.
	 * 
	 * TODO Refactor this -- we don't want logic like this in generic datasets.
	 * 
	 */
	public String getCurrentHealthCenter(Patient patient) { 
		try { 
			
			// Health Center
			PersonAttributeType attributeType = getHealthCenterAttributeType(); 
			
			log.info("Person attribute type: " + attributeType);
			PersonAttribute personAttribute = patient.getAttribute(attributeType);
			log.info("Person attribute: " + personAttribute);
			
			if (personAttribute != null) {
				Integer locationId = 
					Integer.parseInt(personAttribute.getValue());
				Location location = getLocation(locationId);
				if (location != null) 
					return location.getName();
			}			
		} 
		catch (Exception e) { 
			log.error("Unable to retrieve current health center for patient " + patient.getPatientId(), e);
		}
		
		return "No Health Center";
		
	}		

	
	/**
	 * 
	 * @param patient
	 * @return
	 */
	public List<PatientProgram> getPatientPrograms(Program program, Patient patient) { 
		// Initialize the list of patient programs 
		if (patientProgramMap == null || patientProgramMap.isEmpty()) { 

			patientProgramMap = new HashMap<Patient, List<PatientProgram>>();

			
			log.info("hiv program: " + program);
			List<PatientProgram> patientPrograms = 
				Context.getProgramWorkflowService().getPatientPrograms(
					null, program, null, null, null, null, false);				
			
			log.info("Patient programs: " + patientPrograms.size());
			
			for (PatientProgram patientProgram : patientPrograms) { 
				List<PatientProgram> patientProgramList = 
					patientProgramMap.get(patientProgram.getPatient());
				
				if (patientProgramList == null) 
					patientProgramList = new Vector<PatientProgram>();
				
				patientProgramList.add(patientProgram);
				patientProgramMap.put(patientProgram.getPatient(), patientProgramList);				
			}		
		}
		
		return patientProgramMap.get(patient);
	
	}	
	
	/**
	 * Gets a patient identifier for the given patient and indentifierType.
	 * 
	 * @param patient
	 * 		the given patient
	 * @param identifierType
	 * 		the desired identifier type 
	 * @return
	 * 		a patient identifier for the given patient and indentifierType.
	 */
	public String getPatientIdentifier(Patient patient, String identifierType) { 
		PatientIdentifierType patientIdentifierType = getPatientIdentifierType(identifierType);
		
		if (patientIdentifierType == null) { 
			return "No identifier type with name " + identifierType;
		}
		
		PatientIdentifier identifier = 
			patient.getPatientIdentifier(patientIdentifierType);
		
		if (identifier == null) 
			return "No " + identifierType + " identifier";
		else 
			return identifier.getIdentifier();	
	}
	
	
	/**
	 * 
	 * @param patient
	 * @param format
	 * @return
	 */
	public String getPatientName(Patient patient) { 
		
		PersonName personName = patient.getPersonName();
		if (personName == null) { 
			return "No name";
		}
		return patient.getPersonName().getFamilyName() + " " + patient.getPersonName().getGivenName();		
		
		
	}
	
	
	private PatientIdentifierType getPatientIdentifierType(String identifierType) { 

		if (patientIdentifierType == null) {
			patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByName(identifierType);		
		}
		return patientIdentifierType;
	}
	
	
	/**
	 * Get HIV Program
	 * @return	
	 * 		a program
	 */
	private Program getHivProgram() { 
		if (hivProgram == null) {  
			// TODO Needs to be pulled out into global property
			hivProgram =
				Context.getProgramWorkflowService().getProgramByName(HIV_PROGRAM);
		}
		log.info("Returning " + hivProgram);
		return hivProgram;
	}	
	
	/**
	 * Get HIV Treatment Group
	 * @return
	 * 		a program workflow
	 */
	private ProgramWorkflow getHivTreatmentGroup() { 
		// TODO Needs to be pulled out into global property
		if (hivTreatmentGroup == null) { 
			hivTreatmentGroup = getHivProgram().getWorkflowByName(HIV_TREATMENT_GROUP);
		}
		log.info("Returning " + hivTreatmentGroup);
		return hivTreatmentGroup;
	}
	
	
	/**
	 * Get health center attribute type
	 * @return
	 * 		a person attribute type
	 */
	private PersonAttributeType getHealthCenterAttributeType() { 
		// Health Center
		if (healthCenterAttributeType == null) { 
			healthCenterAttributeType = Context.getPersonService().getPersonAttributeTypeByName(HEALTH_CENTER);
		}				
		log.info("Returning " + healthCenterAttributeType + " " + healthCenterAttributeType.getPersonAttributeTypeId());
		return healthCenterAttributeType;
		
	}
	
	
	/**
	 * Get a location by location id.
	 * 
	 * @param locationId
	 * 		the primary key of the location
	 * @return
	 * 		the location that matches the given location id
	 */
	public Location getLocation(Integer locationId) { 
		if (locations == null) { 
			locations = Context.getLocationService().getAllLocations();		
		}
		
		for (Location location : locations) { 
			if (location.getLocationId().equals(locationId))
				return location;
		}
		return new Location();
		
	}
	
	
	
	// ==============================================================================================
	
	
	
	/**
	 * @see org.openmrs.module.dataset.api.DataSet#iterator()
	 */
	public Iterator<Map<DataSetColumn, Object>> iterator() {
		return new HelperIterator(patients.iterator());
	}
	
	/**
	 * Convenience method for JSTL method.  
	 * TODO This will be removed once we get a decent solution for the dataset iterator solution.  
	 */
	public Iterator<Map<DataSetColumn, Object>> getIterator() {
		return iterator();
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
			Map<DataSetColumn, Object> row = new HashMap<DataSetColumn, Object>();
			//Locale locale = Context.getLocale();
			long starttime = System.currentTimeMillis();
			// Add default values for the encounter dataset
			// TODO These need to be added as columns to the dataset definition
			// TODO We need a way to sync these up
			Patient patient = iter.next();
			
			if (patient == null)
				throw new DataSetException("Data set column is invalid");
			
			log.info("Patient: " + patient.getPatientId());
			
			// Build a row in the dataset
			// TODO I'm not in love with the this approach, but we'll refactor later if we need to
			row.put(new SimpleDataSetColumn(PatientDataSetDefinition.PATIENT_ID), 
					patient.getPatientId());
			row.put(new SimpleDataSetColumn(PatientDataSetDefinition.PATIENT_IDENTIFIER), 
					getPatientIdentifier(patient, PATIENT_IDENTIFIER));			
			row.put(new SimpleDataSetColumn(PatientDataSetDefinition.GIVEN_NAME), 
					patient.getGivenName());
			row.put(new SimpleDataSetColumn(PatientDataSetDefinition.FAMILY_NAME), 
					patient.getFamilyName());
			row.put(new SimpleDataSetColumn(PatientDataSetDefinition.GENDER),	
					patient.getGender());	
			row.put(new SimpleDataSetColumn(PatientDataSetDefinition.AGE), 
					patient.getAge());			
			row.put(new SimpleDataSetColumn(PatientDataSetDefinition.HEALTH_CENTER), 
					getCurrentHealthCenter(patient));			
			row.put(new SimpleDataSetColumn(PatientDataSetDefinition.TREATMENT_GROUP),	
					getCurrentTreatmentGroup(patient));						
			return row;
		}
		
		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			iter.remove();
		}
		
	}
	

	
}
