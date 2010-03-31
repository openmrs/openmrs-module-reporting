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
import java.io.Writer;

import org.openmrs.annotation.Handler;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.WidgetHandler;
import org.openmrs.module.htmlwidgets.web.html.HtmlUtil;
import org.openmrs.module.htmlwidgets.web.html.TextAreaWidget;
import org.openmrs.module.htmlwidgets.web.html.Widget;
import org.openmrs.module.reporting.query.definition.QueryDefinition;
import org.openmrs.module.reporting.query.definition.SqlQueryDefinition;

/**
 * FieldGenHandler for Enumerated Types
 */
@Handler(supports={QueryDefinition.class}, order=40)
public class QueryDefinitionHandler extends WidgetHandler {

	/**
	 * @see org.openmrs.module.htmlwidgets.web.handler.WidgetHandler#parse(java.lang.String, java.lang.Class)
	 */
	@Override
	public Object parse(String text, Class<?> type) {
		// TODO We should actually use the "type" to figure out what type of object to create
		return new SqlQueryDefinition(text);
	}

	/**
	 * @see org.openmrs.module.htmlwidgets.web.handler.WidgetHandler#render(org.openmrs.module.htmlwidgets.web.WidgetConfig, java.io.Writer)
	 */
	@Override
	public void render(WidgetConfig config, Writer writer) throws IOException {		
		config.setDefaultValue("SELECT patient_id FROM patient WHERE patient.voided = false AND patient.patient_id = :patientId");
		config.setDefaultAttribute("cols", "60");
		config.setDefaultAttribute("rows", "8");
		HtmlUtil.renderOpenTag(writer, "textarea", config.getAttributes());
		writer.write(config.getDefaultValue() == null ? "" : config.getDefaultValue().toString());
		HtmlUtil.renderCloseTag(writer, "textarea");		
	}	
}
