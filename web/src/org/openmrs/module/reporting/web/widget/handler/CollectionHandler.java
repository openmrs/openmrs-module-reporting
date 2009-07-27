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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.web.widget.WidgetTag;
import org.openmrs.module.reporting.web.widget.html.HtmlUtil;
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
		List<String> js = Arrays.asList("/scripts/jquery/jquery-1.2.6.min.js","/moduleResources/reporting/scripts/reporting.js");
		HtmlUtil.renderResourceFiles(tag.getPageContext(), js);
		Writer w = tag.getPageContext().getOut();
		if (tag.getObject() == null || tag.getProperty() == null) {
			throw new IllegalArgumentException("Collection Handling requires object/property.");
		}
		Field f = ReflectionUtil.getField(tag.getObject().getClass(), tag.getProperty());
		Class<?> genericType = ReflectionUtil.getGenericTypeOfCollection(f);
		
		HtmlUtil.renderOpenTag(w, "div", "id="+tag.getId()+"MultiFieldDiv");
		if (tag.getDefaultValue() != null) {
			if (tag.getDefaultValue() instanceof Collection) {
				for (Object o : (Collection<?>)tag.getDefaultValue()) {
					renderWidget(tag, genericType, o, false);
				}
			}
		}
		renderWidget(tag, genericType, null, true);
		HtmlUtil.renderSimpleTag(w, "input", "type=button|value=+|size=1|onclick=cloneAndInsertBefore('"+tag.getId()+"Template', this);");
		HtmlUtil.renderCloseTag(w, "div");
	}

	/**
	 * Utility method which renders a new instance of the passed tag with the given parameters
	 * @param parent
	 * @param clazz
	 * @param defaultValue
	 */
	protected void renderWidget(WidgetTag parent, Class<?> clazz, Object defaultValue, boolean isTemplate) throws IOException {
		Writer w = parent.getPageContext().getOut();
		
		String addAtts = (isTemplate ? "|id="+parent.getId()+"Template|style=display:none;" : "");
		HtmlUtil.renderOpenTag(w, "span", "class=multiFieldInput" + addAtts);

		try {
			WidgetTag t = (WidgetTag)parent.clone();
			t.setObject(null);
			t.setProperty(null);
			t.setClazz(clazz);
			t.setDefaultValue(defaultValue);
			t.doStartTag();
			t.doAfterBody();
			t.doEndTag();
			t.release();
		}
		catch (JspException e) {
			throw new RuntimeException("Error rendering tag", e);
		}

		HtmlUtil.renderSimpleTag(w, "input", "type=button|value=X|size=1|onclick=removeParentWithClass(this,'multiFieldInput');");
		HtmlUtil.renderSimpleTag(w, "br", "");
		HtmlUtil.renderCloseTag(w, "span");
	}
}
