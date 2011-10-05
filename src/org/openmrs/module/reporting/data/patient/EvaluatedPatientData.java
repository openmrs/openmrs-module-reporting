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
package org.openmrs.module.reporting.data.patient;

import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluated Patient Data Implementation
 */
public class EvaluatedPatientData extends PatientData implements Evaluated<PatientDataDefinition> {

	//***** PROPERTIES *****
	
    private PatientDataDefinition definition;
    private EvaluationContext context;
    
    //***** CONSTRUCTORS *****
    
	/**
	 * Default Constructor
	 */
    public EvaluatedPatientData() {
    	super();
    }
    
	/**
	 * Full Constructor
	 */
    public EvaluatedPatientData(PatientDataDefinition definition, EvaluationContext context) {
    	this.definition = definition;
    	this.context = context;
    } 

    //***** PROPERTY ACCESS *****

	/**
	 * @return the definition
	 */
	public PatientDataDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(PatientDataDefinition definition) {
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
}