
/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ConditionCohortDefinition")
public class ConditionCohortDefinition extends BaseCohortDefinition {
	
	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(value = "conditionCoded")
	private Concept conditionCoded;
	
	@ConfigurationProperty(value = "conditionNonCoded")
	private String conditionNonCoded;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date onsetDateOnOrBefore;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date onsetDateOnOrAfter;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date endDateOnOrBefore;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date endDateOnOrAfter;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date createdOnOrBefore;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date createdOnOrAfter;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date activeOnDate;
	
	public Concept getConditionCoded() {
		return conditionCoded;
	}
	
	public void setConditionCoded(Concept conditionCoded) {
		this.conditionCoded = conditionCoded;
	}
	
	public String getConditionNonCoded() {
		return conditionNonCoded;
	}
	
	public void setConditionNonCoded(String conditionNonCoded) {
		this.conditionNonCoded = conditionNonCoded;
	}

	
	public Date getOnsetDateOnOrBefore() {
		return onsetDateOnOrBefore;
	}

	
	public void setOnsetDateOnOrBefore(Date onsetDateOnOrBefore) {
		this.onsetDateOnOrBefore = onsetDateOnOrBefore;
	}

	
	public Date getOnsetDateOnOrAfter() {
		return onsetDateOnOrAfter;
	}

	
	public void setOnsetDateOnOrAfter(Date onsetDateOnOrAfter) {
		this.onsetDateOnOrAfter = onsetDateOnOrAfter;
	}

	
	public Date getEndDateOnOrBefore() {
		return endDateOnOrBefore;
	}

	
	public void setEndDateOnOrBefore(Date endDateOnOrBefore) {
		this.endDateOnOrBefore = endDateOnOrBefore;
	}

	
	public Date getEndDateOnOrAfter() {
		return endDateOnOrAfter;
	}

	
	public void setEndDateOnOrAfter(Date endDateOnOrAfter) {
		this.endDateOnOrAfter = endDateOnOrAfter;
	}

	
	public Date getCreatedOnOrBefore() {
		return createdOnOrBefore;
	}

	
	public void setCreatedOnOrBefore(Date createdOnOrBefore) {
		this.createdOnOrBefore = createdOnOrBefore;
	}

	
	public Date getCreatedOnOrAfter() {
		return createdOnOrAfter;
	}

	
	public void setCreatedOnOrAfter(Date createdOnOrAfter) {
		this.createdOnOrAfter = createdOnOrAfter;
	}

	
	public Date getActiveOnDate() {
		return activeOnDate;
	}

	
	public void setActiveOnDate(Date activeOnDate) {
		this.activeOnDate = activeOnDate;
	}
	
	
}
