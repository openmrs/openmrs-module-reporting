/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.web.widget.handler;

import java.io.IOException;

import org.openmrs.annotation.Handler;
import org.openmrs.module.report.ReportDesignResource;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.FileUploadWidget;
import org.openmrs.module.reporting.web.widget.html.Widget;
import org.openmrs.module.reporting.web.widget.html.WidgetFactory;

/**
 * FieldGenHandler for Properties Types
 */
@Handler(supports={ReportDesignResource.class}, order=50)
public class ReportDesignResourceHandler extends WidgetHandler {
	
	/** 
	 * @see WidgetHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config) throws IOException {
		Widget w = WidgetFactory.getInstance(FileUploadWidget.class, config);
		ReportDesignResource r = (ReportDesignResource)config.getDefaultValue();
		if (r != null) {
			config.setConfiguredAttribute("linkName", r.getName());
			String url = "/module/reporting/reports/viewResource.form?uuid="+r.getUuid();
			config.setConfiguredAttribute("linkUrl", url);
		}
		
		w.render(config);
	}
	
	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return null; // TODO;
	}
}
