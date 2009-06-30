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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.caching.AnnotatedParameterCachingStrategy;
import org.openmrs.module.evaluation.caching.Caching;
import org.openmrs.module.evaluation.parameter.BaseParameterizable;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.ParameterException;
import org.openmrs.module.evaluation.parameter.ParameterUtil;
import org.openmrs.module.evaluation.parameter.Parameterizable;

/**
 * Base Implementation of CohortDefinition which provides core method
 * implementations for handling Parameters and common Property values
 */
@Caching(strategy=AnnotatedParameterCachingStrategy.class)
public abstract class BaseCohortDefinition extends BaseParameterizable implements CohortDefinition {
	
	private static Log log = LogFactory.getLog(BaseCohortDefinition.class);
    private static final long serialVersionUID = 1920394873L;
    
    //***** PROPERTIES *****

    private Integer id;
	private List<Parameter> boundParameters = new ArrayList<Parameter>(); 
	//LazyList.decorate(new ArrayList(), FactoryUtils.instantiateFactory(Parameter.class));
	
    
    
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public BaseCohortDefinition() {
    	super();
    }
    
    //***** INSTANCE METHODS *****
    
    
    /**
     * @see CohortDefinition#setParameters()
     */
    public void setParameters(List<Parameter> parameters) { 
    	log.info("Setting available parameters: " + parameters);
    	super.setParameters(parameters);
    }
    
    /**
	 * @see CohortDefinition#getParameters()
	 */
	public List<Parameter> getParameters() {
    	log.info("Getting available parameters: " + super.getParameters());
		return ParameterUtil.getAnnotatedParameters(this);
	}
	
	/**
     * @see CohortDefinition#setParameters()
     */
    public void setBoundParameters(List<Parameter> boundParameters) { 
    	log.info("Setting bound parameters: ");
    	this.boundParameters = boundParameters;
    }
    
    /**
	 * @see CohortDefinition#getParameters()
	 */
	public List<Parameter> getBoundParameters() {
		log.info("Getting bound parameters: ");
		if (boundParameters.isEmpty()) { 
			setBoundParameters(ParameterUtil.getAnnotatedParameters(this));
		}
		return boundParameters;
	}
	
	/**
	 * @see CohortDefinition#enableParameter(String, Object, boolean)
	 */
	public void enableParameter(String name, Object defaultValue, boolean required) {
		for (Parameter p : getParameters()) {
			if (StringUtils.equals(name, p.getName())) {
				p.setDefaultValue(defaultValue);
				p.setRequired(required);
				super.addParameter(p);
				return;
			}
		}
		throw new ParameterException("Unable to find available parameter <" + name + ">");
	}

	/**
	 * @see Parameterizable#addParameter(Parameter)
	 */
	@Override
	public void addParameter(Parameter p) throws ParameterException {
		if (!getParameters().contains(p)) {
			throw new ParameterException("Unable to find available parameter <" + p.getName() + ">");
		}
		super.addParameter(p);
	}
	
    //***** Property Access *****
	
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
    
}