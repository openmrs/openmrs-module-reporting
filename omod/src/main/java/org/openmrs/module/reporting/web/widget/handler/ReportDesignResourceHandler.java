/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.widget.handler;

import java.io.IOException;
import java.io.Writer;

import org.openmrs.annotation.Handler;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.WidgetHandler;
import org.openmrs.module.htmlwidgets.web.html.FileUploadWidget;
import org.openmrs.module.htmlwidgets.web.html.Widget;
import org.openmrs.module.htmlwidgets.web.html.WidgetFactory;
import org.openmrs.module.reporting.report.ReportDesignResource;

/**
 * FieldGenHandler for Properties Types
 */
@Handler(supports={ReportDesignResource.class}, order=50)
public class ReportDesignResourceHandler extends WidgetHandler {
	
	/** 
	 * @see WidgetHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config, Writer w) throws IOException {
		Widget widget = WidgetFactory.getInstance(FileUploadWidget.class, config);
		ReportDesignResource r = (ReportDesignResource)config.getDefaultValue();
		if (r != null) {
			config.setConfiguredAttribute("linkName", r.getName());
			String url = "/module/reporting/reports/viewResource.form?uuid="+r.getUuid();
			config.setConfiguredAttribute("linkUrl", url);
		}
		
		widget.render(config, w);
	}
	
	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return null; // TODO;
	}
}
