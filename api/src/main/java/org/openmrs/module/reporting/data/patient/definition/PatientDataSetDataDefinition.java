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
package org.openmrs.module.reporting.data.patient.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataSetDataDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Patient DataSet Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.PatientDataSetDataDefinition")
public class PatientDataSetDataDefinition extends DataSetDataDefinition implements PatientDataDefinition {
	
	/**
	 * Default Constructor
	 */
	public PatientDataSetDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate definition only
	 */
	public PatientDataSetDataDefinition(RowPerObjectDataSetDefinition definition) {
		super(definition);
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public PatientDataSetDataDefinition(RowPerObjectDataSetDefinition definition, TimeQualifier whichValues, Integer numberOfValues) {
		super(definition, whichValues, numberOfValues);
	}
}