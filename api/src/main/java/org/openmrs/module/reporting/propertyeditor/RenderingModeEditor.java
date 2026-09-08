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

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * Converts between a {@link RenderingMode} and a String representation.
 */
public class RenderingModeEditor extends PropertyEditorSupport {
	
	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		String renderClass = text;
		String renderArg = null;
		if (renderClass.indexOf("!") > 0) {
			int ind = renderClass.indexOf("!");
			renderArg = renderClass.substring(ind + 1);
			renderClass = renderClass.substring(0, ind);
		}
		ReportRenderer renderer = Context.getService(ReportService.class).getReportRenderer(renderClass);
		if (renderer == null) {
			setValue(null);
		} else {
			RenderingMode ret = new RenderingMode();
			ret.setRenderer(renderer);
			ret.setArgument(renderArg);
			setValue(ret);
		}
	}
	
	/**
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	@Override
	public String getAsText() {
	    RenderingMode ret = (RenderingMode) getValue();
	    return ret == null ? null : ret.getDescriptor();
	}

}
