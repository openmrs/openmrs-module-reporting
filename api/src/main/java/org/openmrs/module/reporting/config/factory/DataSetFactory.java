/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.config.factory;

import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;

import java.io.File;

/**
 * Interface for providers that are capable of constructing and returning new DataSetDefinition instances from config
 */
public interface DataSetFactory {

    DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, File baseConfigDir);

}
