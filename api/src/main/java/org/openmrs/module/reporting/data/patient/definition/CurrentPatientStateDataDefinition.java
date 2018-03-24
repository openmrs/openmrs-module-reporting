/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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