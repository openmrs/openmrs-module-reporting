/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
