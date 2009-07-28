package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.handler.WidgetHandler;
import org.openmrs.util.HandlerUtil;

public class RepeatingWidget implements Widget {
	
	/** 
	 * @see Widget#render(WidgetConfig)
	 */
	public void render(WidgetConfig config) throws IOException {
		
		HtmlUtil.renderResource(config.getPageContext(), "/scripts/jquery/jquery-1.2.6.min.js");
		HtmlUtil.renderResource(config.getPageContext(), "/moduleResources/reporting/scripts/reporting.js");
		Writer w = config.getPageContext().getOut();
		String id = config.getId();
		
		Class<?> type = null;
		if (config.getGenericTypes() != null && config.getGenericTypes().length == 1) {
			try {
				type = (Class<?>) config.getGenericTypes()[0];
			}
			catch (Exception e) {
				// Do Nothing 
			}
		}
		if (type == null) {
			throw new IllegalArgumentException("Invalid CollectionHandler configuration: " + config);
		}
		
		// Ensure that we have an appropriate Handler
		WidgetHandler handler = HandlerUtil.getPreferredHandler(WidgetHandler.class, type);
		if (handler == null) {
			throw new RuntimeException("No Preferred Handler found for: " + type);
		}

		HtmlUtil.renderOpenTag(w, "div", "id="+id+"MultiFieldDiv");
		if (config.getDefaultValue() != null) {
			if (config.getDefaultValue() instanceof Collection) {
				List<?> valuesToRender = new ArrayList<Object>((Collection<?>)config.getDefaultValue());
				for (int i=0; i<=valuesToRender.size(); i++) {
					Object o = (i == valuesToRender.size() ? null : valuesToRender.get(i));
					WidgetConfig c = config.clone();
					if (config.getId() != null) {
						c.setId(config.getId() + "_" + i);
					}
					c.setGenericTypes(null);
					c.setDefaultValue(o);
					
					Set<Attribute> atts = new HashSet<Attribute>();
					atts.add(new Attribute("class", "multiFieldInput", null, null));
					if (o == null) {
						atts.add(new Attribute("id", config.getId()+"Template", null, null));
						atts.add(new Attribute("style", "display:none;", null, null));
					}
					
					HtmlUtil.renderOpenTag(w, "span", atts);
					handler.handle(c);
					HtmlUtil.renderSimpleTag(w, "input", "type=button|value=X|size=1|onclick=removeParentWithClass(this,'multiFieldInput');");
					HtmlUtil.renderSimpleTag(w, "br", "");
					HtmlUtil.renderCloseTag(w, "span");
				}
					
				HtmlUtil.renderSimpleTag(w, "input", "type=button|value=+|size=1|onclick=cloneAndInsertBefore('"+id+"Template', this);");
				HtmlUtil.renderCloseTag(w, "div");
			}
		}
	}
}