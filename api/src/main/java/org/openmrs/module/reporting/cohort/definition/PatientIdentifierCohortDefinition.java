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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.util.OpenmrsUtil;

/**
 * A {@link CohortDefinition} which queries for patients based on matching on Patient Identifiers
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.PatientIdentifierCohortDefinition")
public class PatientIdentifierCohortDefinition extends BaseCohortDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private List<PatientIdentifierType> typesToMatch;
	
	@ConfigurationProperty(group = "where")
	private List<Location> locationsToMatch;
	
	@ConfigurationProperty
	private String textToMatch;  // This can contain % as a wild card.  So "ID-%" matches anything that starts with "ID-"

    @ConfigurationProperty
	private String regexToMatch;

    @ConfigurationProperty(group = "where")
    private boolean includeChildLocations = false;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public PatientIdentifierCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("Patients with identifiers");
		if (typesToMatch != null) {
			sb.append(" of type " + OpenmrsUtil.join(typesToMatch, " or "));
		}
		if (locationsToMatch != null) {
			sb.append(" at location " + OpenmrsUtil.join(locationsToMatch, " or "));
		}
		if (ObjectUtil.notNull(textToMatch)) {
			sb.append(" matching text " + textToMatch);
		} 
		if (ObjectUtil.notNull(regexToMatch)) {
			sb.append(" matching regular expression " + regexToMatch);
		} 
		return sb.toString();
	}
	
	//***** PROPERTY ACCESS ******

	/**
	 * @return the typesToMatch
	 */
	public List<PatientIdentifierType> getTypesToMatch() {
		return typesToMatch;
	}

	/**
	 * @param typesToMatch the typesToMatch to set
	 */
	public void setTypesToMatch(List<PatientIdentifierType> typesToMatch) {
		this.typesToMatch = typesToMatch;
	}
	
	/**
	 * @param type the {@link PatientIdentifierType} to add
	 */
	public void addTypeToMatch(PatientIdentifierType type) {
		if (typesToMatch == null) {
			typesToMatch = new ArrayList<PatientIdentifierType>();
		}
		typesToMatch.add(type);
	}

	/**
	 * @return the locationsToMatch
	 */
	public List<Location> getLocationsToMatch() {
		return locationsToMatch;
	}

	/**
	 * @param locationsToMatch the locationsToMatch to set
	 */
	public void setLocationsToMatch(List<Location> locationsToMatch) {
		this.locationsToMatch = locationsToMatch;
	}
	
	/**
	 * @param type the {@link Location} to add
	 */
	public void addLocationToMatch(Location location) {
		if (locationsToMatch == null) {
			locationsToMatch = new ArrayList<Location>();
		}
		locationsToMatch.add(location);
	}

	/**
	 * @return the textToMatch
	 */
	public String getTextToMatch() {
		return textToMatch;
	}

	/**
	 * @param textToMatch the textToMatch to set
	 */
	public void setTextToMatch(String textToMatch) {
		this.textToMatch = textToMatch;
	}

	/**
	 * @return the regexToMatch
	 */
	public String getRegexToMatch() {
		return regexToMatch;
	}

	/**
	 * @param regexToMatch the regexToMatch to set
	 */
	public void setRegexToMatch(String regexToMatch) {
		this.regexToMatch = regexToMatch;
	}

	public boolean isIncludeChildLocations() {
		return includeChildLocations;
	}

	public boolean getIncludeChildLocations() {
		return isIncludeChildLocations();
	}

	public void setIncludeChildLocations(boolean includeChildLocations) {
		this.includeChildLocations = includeChildLocations;
	}
}