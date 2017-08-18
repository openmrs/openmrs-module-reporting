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