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

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * Represents all patients not in a wrapped Cohort Definition
 */
@Localized("reporting.InverseCohortDefinition")
public class InverseCohortDefinition extends BaseCohortDefinition {
	
    public static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
    private CohortDefinition baseDefinition;

	//***** STATIC METHODS *****

	/**
     * Takes a Mapped<CohortDefinition> and returns <Mapped<NOT CohortDefinition>>
     * The inverted cohort definition will have the same parameters as the original cohort definition.
     */
	public static Mapped<InverseCohortDefinition> invert(Mapped<? extends CohortDefinition> original) {
	    InverseCohortDefinition inv = new InverseCohortDefinition(original.getParameterizable());
	    Mapped<InverseCohortDefinition> ret = new Mapped<InverseCohortDefinition>(inv, original.getParameterMappings());
	    return ret;
    }
	
	//***** CONSTRUCTORS *****
	
    /**
     * Default constructor
     */
	public InverseCohortDefinition() { }
	
    /**
     * Default constructor
     */
	public InverseCohortDefinition(CohortDefinition baseDefinition) {
		this.baseDefinition = baseDefinition;
	}
	
	//***** INSTANCE METHODS *****
	
	   /**
	 * @see BaseDefinition#addParameter(Parameter)
	 */
	@Override
	public void addParameter(Parameter parameter) {
		if (baseDefinition == null) {
			throw new RuntimeException("You cannot add a parameter to an InverseCohortDefinition until you set the baseDefinition");
		}
		getBaseDefinition().addParameter(parameter);
	}

	/**
	 * @see BaseDefinition#addParameters(List)
	 */
	@Override
	public void addParameters(List<Parameter> parameters) {
		if (baseDefinition == null) {
			throw new RuntimeException("You cannot add a parameter to an InverseCohortDefinition until you set the baseDefinition");
		}
		getBaseDefinition().getParameters().addAll(parameters);
	}

	/**
	 * @see BaseDefinition#getParameter(String)
	 */
	@Override
	public Parameter getParameter(String name) {
		if (baseDefinition == null) {
			return null;
		}
		return getBaseDefinition().getParameter(name);
	}

	/**
	 * @see BaseDefinition#getParameters()
	 */
	@Override
	public List<Parameter> getParameters() {
		if (baseDefinition == null) {
			return new ArrayList<Parameter>();
		}
		return getBaseDefinition().getParameters();
	}

	/**
	 * @see BaseDefinition#removeParameter(Parameter)
	 */
	@Override
	public void removeParameter(Parameter parameter) {
		if (baseDefinition != null) {
			getBaseDefinition().removeParameter(parameter);
		}
	}

	/**
	 * @see BaseDefinition#removeParameter(String)
	 */
	@Override
	public void removeParameter(String parameterName) {
		if (baseDefinition != null) {
			getBaseDefinition().removeParameter(parameterName);
		}
	}

	/**
	 * @see BaseDefinition#setParameters(List)
	 */
	@Override
	public void setParameters(List<Parameter> parameters) {
		if (baseDefinition != null) {
			getBaseDefinition().getParameters().clear();
		}
		addParameters(parameters);
	}
	

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		CohortDefinition filter = getBaseDefinition();
		return "NOT " + (filter == null ? "?" : filter.toString());
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
