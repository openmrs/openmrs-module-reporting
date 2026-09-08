/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.manager;

import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of ReportManager that provides some common method implementations
 */
public abstract class BaseReportManager implements ReportManager {

	/**
	 * @return by default, no parameters are required
	 */
	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

    /**
     * @return by default, no requests are automatically scheduled
     */
    @Override
    public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
        return new ArrayList<ReportRequest>();
    }
}
