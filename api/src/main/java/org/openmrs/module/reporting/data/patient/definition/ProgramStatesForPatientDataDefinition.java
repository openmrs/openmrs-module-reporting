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
import java.util.List;

import org.openmrs.Location;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Program States For Patient Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ProgramStatesForPatientDataDefinition")
public class ProgramStatesForPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private TimeQualifier which;

	@ConfigurationProperty
	private ProgramWorkflow workflow;
	
	@ConfigurationProperty
	private ProgramWorkflowState state;

	@ConfigurationProperty
	private Location location;
	
	@ConfigurationProperty
	private Date activeOnDate;
	
	@ConfigurationProperty
	private Date startedOnOrBefore;
	
	@ConfigurationProperty
	private Date startedOnOrAfter;
	
	@ConfigurationProperty
	private Date endedOnOrBefore;
	
	@ConfigurationProperty
	private Date endedOnOrAfter;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public ProgramStatesForPatientDataDefinition() {
		super();
	}
	
	/**
	 * Name only Constructor
	 */
	public ProgramStatesForPatientDataDefinition(String name) {
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		if (state != null || workflow != null) {
			if (which == TimeQualifier.LAST || which == TimeQualifier.FIRST || activeOnDate != null) {
				return PatientState.class;
			}
		}
		return List.class;
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the which
	 */
	public TimeQualifier getWhich() {
		return which;
	}

	/**
	 * @param which the which to set
	 */
	public void setWhich(TimeQualifier which) {
		this.which = which;
	}

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
	 * @return the state
	 */
	public ProgramWorkflowState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(ProgramWorkflowState state) {
		this.state = state;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the activeOnDate
	 */
	public Date getActiveOnDate() {
		return activeOnDate;
	}

	/**
	 * @param activeOnDate the activeOnDate to set
	 */
	public void setActiveOnDate(Date activeOnDate) {
		this.activeOnDate = activeOnDate;
	}

	/**
	 * @return the startedOnOrBefore
	 */
	public Date getStartedOnOrBefore() {
		return startedOnOrBefore;
	}

	/**
	 * @param startedOnOrBefore the startedOnOrBefore to set
	 */
	public void setStartedOnOrBefore(Date startedOnOrBefore) {
		this.startedOnOrBefore = startedOnOrBefore;
	}

	/**
	 * @return the startedOnOrAfter
	 */
	public Date getStartedOnOrAfter() {
		return startedOnOrAfter;
	}

	/**
	 * @param startedOnOrAfter the startedOnOrAfter to set
	 */
	public void setStartedOnOrAfter(Date startedOnOrAfter) {
		this.startedOnOrAfter = startedOnOrAfter;
	}

	/**
	 * @return the endedOnOrBefore
	 */
	public Date getEndedOnOrBefore() {
		return endedOnOrBefore;
	}

	/**
	 * @param endedOnOrBefore the endedOnOrBefore to set
	 */
	public void setEndedOnOrBefore(Date endedOnOrBefore) {
		this.endedOnOrBefore = endedOnOrBefore;
	}

	/**
	 * @return the endedOnOrAfter
	 */
	public Date getEndedOnOrAfter() {
		return endedOnOrAfter;
	}

	/**
	 * @param endedOnOrAfter the endedOnOrAfter to set
	 */
	public void setEndedOnOrAfter(Date endedOnOrAfter) {
		this.endedOnOrAfter = endedOnOrAfter;
	}
}