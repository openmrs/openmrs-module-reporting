package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
	@SuppressWarnings("unchecked")
	public void render(WidgetConfig config) throws IOException {
		
		HtmlUtil.renderResource(config.getPageContext(), "/moduleResources/reporting/scripts/jquery/jquery-1.3.2.min.js");
		HtmlUtil.renderResource(config.getPageContext(), "/moduleResources/reporting/scripts/reporting.js");
		Writer w = config.getPageContext().getOut();
		String id = config.getId();
		
		Class<?> type = null;
		Type[] genericTypes = null;
		if (config.getGenericTypes() != null && config.getGenericTypes().length == 1) {
			try {
				Type firstGenericType = config.getGenericTypes()[0];
				if (firstGenericType instanceof Class) {
					type = (Class<?>) firstGenericType;
				}
				else if (firstGenericType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) firstGenericType;
					Type rawType = pt.getRawType();
					type = (Class<?>) rawType;
					genericTypes = pt.getActualTypeArguments();
				}
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
		
		List<?> valuesToRender = new ArrayList<Object>();
		if (config.getDefaultValue() != null) {
			if (config.getDefaultValue() instanceof Collection) {
				valuesToRender.addAll((Collection) config.getDefaultValue());
			}
			else if (config.getDefaultValue() instanceof Object[]) {
				valuesToRender = Arrays.asList((Object[]) config.getDefaultValue());
			}
			else {
				throw new RuntimeException("Default value for a repeating widget must be an Object[], Collection, or null");
			}
		}
		
		w.write("<script type=\"text/javascript\" charset=\"utf-8\">");
		w.write("	$(document).ready(function() {");
		w.write("		$(\"#"+id+"AddButton\").click(function(event){");
		w.write("			var count = parseInt($('#"+id+"Count').html()) + 1;");
		w.write("			$('#"+id+"Count').html(count);");
		w.write("			var $newRow = cloneAndInsertBefore('"+id+"Template', this);");
		w.write("			$newRow.attr('id', '"+id+"' + count);");
		w.write("			var newRowChildren = $newRow.children();");
		w.write("			for (var i=0; i<newRowChildren.length; i++) {");
		w.write("				newRowChildren[i].id = newRowChildren[i].id + count;");
		w.write("			}");
		w.write(		"});");
		w.write("	});");
		w.write("</script>");
		
		HtmlUtil.renderOpenTag(w, "div", "id="+id+"MultiFieldDiv");

		for (int i=0; i<=valuesToRender.size(); i++) {
			Object o = (i == valuesToRender.size() ? null : valuesToRender.get(i));
			WidgetConfig c = config.clone();
			if (config.getId() != null) {
				c.setId(config.getId() + "_" + i);
			}
			c.setType(type);
			c.setGenericTypes(genericTypes);
			c.setDefaultValue(o);
			
			Set<Attribute> atts = new HashSet<Attribute>();
			atts.add(new Attribute("class", "multiFieldInput", null, null));
			if (o == null) {
				atts.add(new Attribute("id", id+"Template", null, null));
				atts.add(new Attribute("style", "display:none;", null, null));
			}
			
			HtmlUtil.renderOpenTag(w, "span", atts);
			handler.render(c);
			HtmlUtil.renderSimpleTag(w, "input", "type=button|value=X|size=1|onclick=removeParentWithClass(this,'multiFieldInput');");
			HtmlUtil.renderSimpleTag(w, "br", "");
			HtmlUtil.renderCloseTag(w, "span");
		}
			
		HtmlUtil.renderSimpleTag(w, "input", "id="+id+"AddButton|type=button|value=+|size=1");
		HtmlUtil.renderOpenTag(w, "span", "id="+id+"Count|style=display:none;");
		w.write(valuesToRender.size() + 1);
		HtmlUtil.renderCloseTag(w, "span");
		HtmlUtil.renderCloseTag(w, "div");
	}
}