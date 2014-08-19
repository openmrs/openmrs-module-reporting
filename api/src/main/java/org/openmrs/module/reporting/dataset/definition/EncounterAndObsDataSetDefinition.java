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
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Definition of an EncounterAndObs DataSet
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.EncounterAndObsDataSetDefinition")
public class EncounterAndObsDataSetDefinition extends EncounterDataSetDefinition {
	
	public EncounterAndObsDataSetDefinition() {}
}
