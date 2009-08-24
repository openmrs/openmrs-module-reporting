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
package org.openmrs.module.indicator.dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.APIException;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.BaseDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.util.IndicatorUtil;

/**
 * Represents a Dimension of an Indicator based on CohortDefinitions
 */
public class CohortDefinitionDimension extends BaseDefinition implements CohortDimension {

	private static final long serialVersionUID = 192837748L;
	
	//***** PROPERTIES *****
	
	private Integer id;
	private Map<String, Mapped<CohortDefinition>> cohortDefinitions;
    
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
     * @param cohortDefinition - The CohortDefinition to add
     */
    public void addCohortDefinition(String key, CohortDefinition definition, String mappings) {
    	if (IndicatorUtil.containsIgnoreCase(getCohortDefinitions().keySet(), key)) {
    		throw new APIException("Dimension already contains an element named <" + key + ">");
    	}
    	getCohortDefinitions().put(key, new Mapped<CohortDefinition>(definition, mappings));
    }

	/**
     * @param cohortDefinition - The CohortDefinition to add
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
    		cohortDefinitions = new HashMap<String, Mapped<CohortDefinition>>();
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
