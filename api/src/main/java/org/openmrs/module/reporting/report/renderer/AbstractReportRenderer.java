/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.renderer;

import java.util.Collection;

import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Base Abstract implementation of a ReportRenderer.
 */
public abstract class AbstractReportRenderer implements ReportRenderer  {
	
	/**
	 * @see ReportRenderer#canRender(ReportDefinition)
	 */
	public boolean canRender(ReportDefinition reportDefinition) { 		
		Collection<RenderingMode> modes = getRenderingModes(reportDefinition);
		return (modes != null && !modes.isEmpty());
	}
	
	/**
	 * Convenience method to return the display label for the Renderer
	 * @return a String display label
	 */
	public String getLabel() {
		return MessageUtil.getDisplayLabel(this.getClass());
	}
}
