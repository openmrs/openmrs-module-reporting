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
package org.openmrs.module.reporting.data.encounter.definition;

import org.openmrs.module.reporting.data.DataSetDataDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Encounter DataSet Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class EncounterDataSetDataDefinition extends DataSetDataDefinition implements EncounterDataDefinition {
	
	/**
	 * Default Constructor
	 */
	public EncounterDataSetDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate definition only
	 */
	public EncounterDataSetDataDefinition(RowPerObjectDataSetDefinition definition) {
		super(definition);
	}
}