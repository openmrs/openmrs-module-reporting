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
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.Date;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.DateOfPatientDataCohortDefinition")
public class DateOfPatientDataCohortDefinition extends BaseCohortDefinition {

	//***** CONFIGURATION PROPERTIES *****

	@ConfigurationProperty
	private Mapped<? extends PatientDataDefinition> patientDataDefinition;

	@ConfigurationProperty
	private DataConverter dataConverter;

	@ConfigurationProperty
	private Integer minTimeInPast;

	@ConfigurationProperty
	private DurationUnit minTimeInPastUnits = DurationUnit.DAYS;

	@ConfigurationProperty
	private Integer maxTimeInPast;

	@ConfigurationProperty
	private DurationUnit maxTimeInPastUnits = DurationUnit.DAYS;

	@ConfigurationProperty
	private Date effectiveDate;

	//***** PROPERTY ACCESS *****

	public Mapped<? extends PatientDataDefinition> getPatientDataDefinition() {
		return patientDataDefinition;
	}

	public void setPatientDataDefinition(Mapped<? extends PatientDataDefinition> patientDataDefinition) {
		this.patientDataDefinition = patientDataDefinition;
	}

	public DataConverter getDataConverter() {
		return dataConverter;
	}

	public void setDataConverter(DataConverter dataConverter) {
		this.dataConverter = dataConverter;
	}

	public Integer getMinTimeInPast() {
		return minTimeInPast;
	}

	public void setMinTimeInPast(Integer minTimeInPast) {
		this.minTimeInPast = minTimeInPast;
	}

	public DurationUnit getMinTimeInPastUnits() {
		return minTimeInPastUnits;
	}

	public void setMinTimeInPastUnits(DurationUnit minTimeInPastUnits) {
		this.minTimeInPastUnits = minTimeInPastUnits;
	}

	public Integer getMaxTimeInPast() {
		return maxTimeInPast;
	}

	public void setMaxTimeInPast(Integer maxTimeInPast) {
		this.maxTimeInPast = maxTimeInPast;
	}

	public DurationUnit getMaxTimeInPastUnits() {
		return maxTimeInPastUnits;
	}

	public void setMaxTimeInPastUnits(DurationUnit maxTimeInPastUnits) {
		this.maxTimeInPastUnits = maxTimeInPastUnits;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
