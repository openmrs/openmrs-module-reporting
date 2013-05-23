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
package org.openmrs.module.reporting.data.patient.definition;

import java.util.Date;

import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Patient State Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.CurrentPatientStateDataDefinition")
public class CurrentPatientStateDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private ProgramWorkflow workflow;
	
	@ConfigurationProperty
	private Date effectiveDate;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public CurrentPatientStateDataDefinition() {
		super();
	}
	
	/**
	 * Name only constructor
	 */
	public CurrentPatientStateDataDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate name and workflow only
	 */
	public CurrentPatientStateDataDefinition(String name, ProgramWorkflow workflow) {
		this(name);
		this.workflow = workflow;
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return PatientState.class;
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the workflow
	 */
	public ProgramWorkflow getWorkflow() {
		return workflow;
	}

	/**
	 * @param workflow the workflow to set
	 */
	public void setWorkflow(ProgramWorkflow workflow) {
		this.workflow = workflow;
	}

	/**
	 * @return the effectiveDate
	 */
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	/**
	 * @param effectiveDate the effectiveDate to set
	 */
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}