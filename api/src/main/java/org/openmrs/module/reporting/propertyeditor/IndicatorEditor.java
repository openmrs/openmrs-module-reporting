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

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.springframework.util.StringUtils;

public class IndicatorEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public IndicatorEditor() {
	}
	
	public void setAsText(String text) throws IllegalArgumentException {
		IndicatorService cds = Context.getService(IndicatorService.class);
		if (StringUtils.hasText(text)) {
			try {
				setValue(cds.getDefinitionByUuid(text));
			}
			catch (Exception ex) {
				log.error("Error setting text" + text, ex);
				throw new IllegalArgumentException("Indicator not found: " + ex.getMessage());
			}
		} else {
			setValue(null);
		}
	}
	
	public String getAsText() {
		Indicator i = (Indicator) getValue();
		if (i == null) {
			return "";
		} else {
			return i.getUuid().toString();
		}
	}
	
}
