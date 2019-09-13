
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
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ConditionCohortDefinition")
public class ConditionCohortDefinition extends BaseCohortDefinition {
	
	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(value = "concept")
	private Concept concept;
	
	@ConfigurationProperty(value = "conditionNonCoded")
	private String conditionNonCoded;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date onOrAfter;
	
	@ConfigurationProperty(group = "obsDatetimeGroup")
	private Date onOrBefore;
	
	public Concept getConcept() {
		return concept;
	}
	
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	public String getConditionNonCoded() {
		return conditionNonCoded;
	}
	
	public void setConditionNonCoded(String conditionNonCoded) {
		this.conditionNonCoded = conditionNonCoded;
	}
	
	public Date getOnOrAfter() {
		return onOrAfter;
	}
	
	public void setOnOrAfter(Date onOrAfter) {
		this.onOrAfter = onOrAfter;
	}
	
	public Date getOnOrBefore() {
		return onOrBefore;
	}
	
	public void setOnOrBefore(Date onOrBefore) {
		this.onOrBefore = onOrBefore;
	}
}
