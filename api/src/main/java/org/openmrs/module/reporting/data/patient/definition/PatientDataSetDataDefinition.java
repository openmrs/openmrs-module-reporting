/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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