/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.propertyeditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

public class ReportDefinitionEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public ReportDefinitionEditor() { }
	
	public void setAsText(String text) throws IllegalArgumentException {
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		if (StringUtils.hasText(text)) {
			try {
				setValue(rs.getDefinitionByUuid(text));
			}
			catch (Exception ex) {
				throw new IllegalArgumentException("Unable to load report definition: " + text, ex);
			}
		} else {
			setValue(null);
		}
	}
	
	public String getAsText() {
		ReportDefinition rd = (ReportDefinition) getValue();
		if (rd == null) {
			return "";
		} else {
			return rd.getUuid().toString();
		}
	}
	
}
