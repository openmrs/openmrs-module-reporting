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
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Builds a SqlFileDataSetDefinition from configuration
 */
@Component
public class SqlDataSetFactory implements DataSetFactory {

    public DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, File baseConfigDir) {
        File sqlFile = new File(baseConfigDir, dataSetDescriptor.getConfig());
        if (!sqlFile.exists()) {
            throw new RuntimeException("SQL file " + dataSetDescriptor.getConfig() + " not found");
        }
        SqlFileDataSetDefinition dsd = new SqlFileDataSetDefinition();
        dsd.setName(dataSetDescriptor.getName());
        dsd.setSqlFile(sqlFile.getAbsolutePath());
        return dsd;
    }

}
