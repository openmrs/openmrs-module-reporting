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
	
	@ConfigurationProperty
	private List<Location> locationsToMatch;
	
	@ConfigurationProperty
	private String textToMatch;  // This can contain % as a wild card.  So "ID-%" matches anything that starts with "ID-"

    @ConfigurationProperty
	private String regexToMatch;
	
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
}