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

import org.openmrs.OrderType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Order for visit data definition that returns the orders of a visit based on provided parameters
 */
@OpenmrsProfile(openmrsPlatformVersion = "1.10.2 - 2.*")
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class OrderForVisitDataDefinition extends BaseDataDefinition implements VisitDataDefinition {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty
	private List<OrderType> types;

	/**
	 * Default Constructor
	 */
	public OrderForVisitDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name only
	 */
	public OrderForVisitDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return List.class;
	}
	
	public List<OrderType> getTypes() {
		return types;
	}

	public void setTypes(List<OrderType> types) {
		this.types = types;
	}

}