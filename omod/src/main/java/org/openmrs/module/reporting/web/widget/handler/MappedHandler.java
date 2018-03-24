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
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * FieldGenHandler for Mapped Types
 */
@Handler(supports={Mapped.class}, order=50)
public class MappedHandler extends WidgetHandler {
	
	/** 
	 * @see WidgetHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config, Writer w) throws IOException {
		w.write("This will be a widget for a Mapped property");
	}
	
	/** 
	 * @see WidgetHandler#parse(String)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		throw new IllegalArgumentException("Not supported");
	}
}