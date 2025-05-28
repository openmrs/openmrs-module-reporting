/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.library.implementerconfigured;

import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;

public abstract class BaseImplementerConfiguredLibraryTest extends BaseModuleContextSensitiveTest {

	protected File baseDir;

	protected void copyResource(String type, String name) throws Exception {
		String sqlQuery = getContents(type, name);
		ReportUtil.writeStringToFile(new File(getConfigDir(type), name), sqlQuery);
	}

	protected String getContents(String type, String name) {
		return ReportUtil.readStringFromResource("implementerconfigured/" + type + "/" + name).trim();
	}

	protected File getConfigDir(String type) {
		if (baseDir == null) {
			baseDir = new File(OpenmrsUtil.getApplicationDataDirectory(), BaseImplementerConfiguredDefinitionLibrary.BASE_DIR);
		}
		File configDir = new File(baseDir, type);
		if (!configDir.exists()) {
			configDir.mkdirs();
		}
		return configDir;
	}
}
