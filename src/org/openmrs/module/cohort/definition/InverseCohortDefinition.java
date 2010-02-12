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
package org.openmrs.module.cohort.definition;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.evaluation.parameter.Mapped;

public class InverseCohortDefinition extends BaseCohortDefinition {
	
    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
    private CohortDefinition baseDefinition;

	//***** STATIC METHODS *****

	/**
     * Takes a Mapped<CohortDefinition> and returns <Mapped<NOT CohortDefinition>>
     * The inverted cohort definition will have the same parameters as the original cohort definition.
     * 
     * @param original
     * @return
     */
	public static Mapped<InverseCohortDefinition> invert(Mapped<? extends CohortDefinition> original) {
	    InverseCohortDefinition inv = new InverseCohortDefinition(original.getParameterizable());
	    inv.setParameters(original.getParameterizable().getParameters());
	    Mapped<InverseCohortDefinition> ret = new Mapped<InverseCohortDefinition>(inv, original.getParameterMappings());
	    return ret;
    }
	
	//***** CONSTRUCTORS *****
	
    /**
     * Default constructor
     */
	public InverseCohortDefinition() {
		super();
	}
	
    /**
     * Full constructor
     */
	public InverseCohortDefinition(CohortDefinition baseDefinition) {
		this();
		this.baseDefinition = baseDefinition;
	}
	
	//***** INSTANCE METHODS *****

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		CohortDefinition filter = getBaseDefinition();
		return "NOT " + (filter == null ? "?" : filter.getDescription());
	}

	//***** PROPERTY ACCESS *****
	
    /**
     * @return the baseDefinition
     */
    public CohortDefinition getBaseDefinition() {
    	return baseDefinition;
    }
	
    /**
     * @param baseDefinition the baseDefinition to set
     */
    public void setBaseDefinition(CohortDefinition baseDefinition) {
    	this.baseDefinition = baseDefinition;
    }

}
