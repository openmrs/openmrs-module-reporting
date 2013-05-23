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

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * ProgramEnrollments For Patient Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ProgramEnrollmentsForPatientDataDefinition")
public class ProgramEnrollmentsForPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private TimeQualifier whichEnrollment;
	
	@ConfigurationProperty(required=true)
	private Program program;
	
	@ConfigurationProperty
	private Date activeOnDate;
	
	@ConfigurationProperty
	private Date enrolledOnOrBefore;
	
	@ConfigurationProperty
	private Date enrolledOnOrAfter;
	
	@ConfigurationProperty
	private Date completedOnOrBefore;
	
	@ConfigurationProperty
	private Date completedOnOrAfter;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public ProgramEnrollmentsForPatientDataDefinition() {
		super();
	}
	
	/**
	 * Name only Constructor
	 */
	public ProgramEnrollmentsForPatientDataDefinition(String name) {
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		if (whichEnrollment == TimeQualifier.LAST || whichEnrollment == TimeQualifier.FIRST || activeOnDate != null) {
			return PatientProgram.class;
		}
		return List.class;
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the whichEnrollment
	 */
	public TimeQualifier getWhichEnrollment() {
		return whichEnrollment;
	}

	/**
	 * @param whichEnrollment the whichEnrollment to set
	 */
	public void setWhichEnrollment(TimeQualifier whichEnrollment) {
		this.whichEnrollment = whichEnrollment;
	}

	/**
	 * @return the program
	 */
	public Program getProgram() {
		return program;
	}

	/**
	 * @param program the program to set
	 */
	public void setProgram(Program program) {
		this.program = program;
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
	 * @return the enrolledOnOrBefore
	 */
	public Date getEnrolledOnOrBefore() {
		return enrolledOnOrBefore;
	}

	/**
	 * @param enrolledOnOrBefore the enrolledOnOrBefore to set
	 */
	public void setEnrolledOnOrBefore(Date enrolledOnOrBefore) {
		this.enrolledOnOrBefore = enrolledOnOrBefore;
	}

	/**
	 * @return the enrolledOnOrAfter
	 */
	public Date getEnrolledOnOrAfter() {
		return enrolledOnOrAfter;
	}

	/**
	 * @param enrolledOnOrAfter the enrolledOnOrAfter to set
	 */
	public void setEnrolledOnOrAfter(Date enrolledOnOrAfter) {
		this.enrolledOnOrAfter = enrolledOnOrAfter;
	}

	/**
	 * @return the completedOnOrBefore
	 */
	public Date getCompletedOnOrBefore() {
		return completedOnOrBefore;
	}

	/**
	 * @param completedOnOrBefore the completedOnOrBefore to set
	 */
	public void setCompletedOnOrBefore(Date completedOnOrBefore) {
		this.completedOnOrBefore = completedOnOrBefore;
	}

	/**
	 * @return the completedOnOrAfter
	 */
	public Date getCompletedOnOrAfter() {
		return completedOnOrAfter;
	}

	/**
	 * @param completedOnOrAfter the completedOnOrAfter to set
	 */
	public void setCompletedOnOrAfter(Date completedOnOrAfter) {
		this.completedOnOrAfter = completedOnOrAfter;
	}
}