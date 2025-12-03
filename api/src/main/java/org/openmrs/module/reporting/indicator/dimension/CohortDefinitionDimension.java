/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;

/**
 * Represents a Dimension of an Indicator based on CohortDefinitions
 */
@Localized("reporting.CohortDefinitionDimension")
public class CohortDefinitionDimension extends BaseDefinition implements CohortDimension {

	public static final long serialVersionUID = 192837748L;
	
	//***** PROPERTIES *****
	
	private Integer id;
	
	@ConfigurationProperty
	private Map<String, Mapped<CohortDefinition>> cohortDefinitions;
    
    /**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		if (getName() != null & getOptionKeys() != null) {
			return getName() + " " + getOptionKeys();
		}
		return super.toString();
	}

	/**
     * @see Dimension#getOptionKeys()
     */
    public List<String> getOptionKeys() {
    	return new ArrayList<String>(getCohortDefinitions().keySet());
    }
    
    /**
     * Returns the CohortDefinition for the given Option key
     * @param key the option key to lookup
     * @return the associated CohortDefinition
     */
    public Mapped<CohortDefinition> getCohortDefinition(String key) {
    	return getCohortDefinitions().get(key);
    }
    
	/**
     * @param definition - The CohortDefinition to add
     */
    public void addCohortDefinition(String key, CohortDefinition definition, Map<String, Object> mappings) {
    	if (IndicatorUtil.containsIgnoreCase(getCohortDefinitions().keySet(), key)) {
    		throw new APIException("Dimension already contains an element named <" + key + ">");
    	}
    	getCohortDefinitions().put(key, new Mapped<CohortDefinition>(definition, mappings));
    }

	/**
     * @param definition - The CohortDefinition to add
     */
    public void addCohortDefinition(String key, Mapped<CohortDefinition> definition) {
    	if (IndicatorUtil.containsIgnoreCase(getCohortDefinitions().keySet(), key)) {
    		throw new APIException("Dimension already contains an element named <" + key + ">");
    	}
    	getCohortDefinitions().put(key, definition);
    }

    //****** PROPERTY ACCESS *******

    /**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
    
    /**
     * @return the cohortDefinitions
     */
    public Map<String, Mapped<CohortDefinition>> getCohortDefinitions() {
    	if (cohortDefinitions == null) {
    		cohortDefinitions = new TreeMap<String, Mapped<CohortDefinition>>();
    	}
    	return cohortDefinitions;
    }

	/**
     * @param cohortDefinitions the cohortDefinitions to set
     */
    public void setCohortDefinitions(Map<String, Mapped<CohortDefinition>> cohortDefinitions) {
    	this.cohortDefinitions = cohortDefinitions;
    }
}
