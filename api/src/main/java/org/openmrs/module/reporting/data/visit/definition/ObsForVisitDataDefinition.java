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
package org.openmrs.module.reporting.data.visit.definition;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Obs for visit data definition that returns the obses of a visit based on a provided concept
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ObsForVisitDataDefinition extends BaseDataDefinition implements VisitDataDefinition {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty
	private TimeQualifier which;
	
	@ConfigurationProperty(required=true)
	private Concept question;

	/**
	 * Default Constructor
	 */
	public ObsForVisitDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name only
	 */
	public ObsForVisitDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return List.class;
	}

	public TimeQualifier getWhich() {
		return which;
	}

	public void setWhich(TimeQualifier which) {
		this.which = which;
	}
	
	public Concept getQuestion() {
		return question;
	}

	public void setQuestion(Concept question) {
		this.question = question;
	}

}
