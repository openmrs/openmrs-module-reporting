/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.definition;

import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Birth Date Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.BirthdateDataDefinition")
public class BirthdateDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public BirthdateDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public BirthdateDataDefinition(String name) {
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return Birthdate.class;
	}
}