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
import java.lang.reflect.Field;
import java.util.Collection;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.web.widget.WidgetTag;
import org.openmrs.module.reporting.web.widget.html.RepeatingWidget;
import org.openmrs.module.util.ReflectionUtil;

/**
 * FieldGenHandler for String Types
 */
@Handler(supports={Collection.class}, order=50)
public class CollectionHandler extends WidgetHandler {	
	
	/** 
	 * @see WidgetHandler#handle(WidgetTag)
	 */
	@Override
	public void handle(WidgetTag tag) throws IOException {
		if (tag.getObject() == null || tag.getProperty() == null) {
			throw new IllegalArgumentException("Collection Handling requires object/property.");
		}
		RepeatingWidget widget = WidgetHandler.getWidgetInstance(tag, RepeatingWidget.class);
		Field f = ReflectionUtil.getField(tag.getObject().getClass(), tag.getProperty());
		widget.setRepeatingType(ReflectionUtil.getGenericTypeOfCollection(f));
		widget.render(tag.getPageContext());
	}
}
