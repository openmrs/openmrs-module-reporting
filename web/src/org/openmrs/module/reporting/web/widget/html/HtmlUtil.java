package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.openmrs.web.taglib.HtmlIncludeTag;

/**
 * This represents utility methods for writing tags
 */
public class HtmlUtil  {
	
	public static List<String> STANDARD_ATTRIBUTES = Arrays.asList(
		"id","class","style","dir","lang","accesskey","tabindex","title","xml:lang"
	);
	
	public static List<String> INPUT_ATTRIBUTES = Arrays.asList(
		"accept","alt","checked","disabled","maxlength","name","readonly","size","src","type","value"
	);
	
	public static List<String> OPTION_ATTRIBUTES = Arrays.asList(
		"disabled","label","value","selected"
	);

	public static List<String> SELECT_ATTRIBUTES = Arrays.asList(
		"disabled","multiple","name","size"
	);
	
	public static List<String> TEXTAREA_ATTRIBUTES = Arrays.asList(
		"cols","rows","disabled","name","readonly"
	);
	
	public static List<String> STANDARD_EVENTS = Arrays.asList(
		"onblur","onchange","onclick","ondblclick","onfocus","onmousedown","onmousemove",
		"onmouseout","onmouseover","onmouseup","onkeydown","onkeypress","onkeyup","onselect"
	);

	/**
	 * Returns true if the passed attribute is valid for the passed tagName
	 * @param tagName the tagName to check
	 * @param attribute the attribute to check
	 * @return true if the passed attribute is valid for the passed tagName
	 */
	public static boolean isValidTagAttribute(String tagName, String attribute) {
		String att = attribute.toLowerCase().trim();
		if (STANDARD_ATTRIBUTES.contains(att) || STANDARD_EVENTS.contains(att)) {
			return true;
		}
		if ("input".equals(tagName)) { return INPUT_ATTRIBUTES.contains(att); }
		if ("option".equals(tagName)) { return OPTION_ATTRIBUTES.contains(att); }
		if ("select".equals(tagName)) { return SELECT_ATTRIBUTES.contains(att); }
		if ("textarea".equals(tagName)) { return TEXTAREA_ATTRIBUTES.contains(att); }
		return false;
	}
	
	/**
	 * Render each specified resource as an HtmlInclude in the page
	 * @param pageContext
	 * @param resources
	 * @throws IOException
	 */
	public static void renderResourceFiles(PageContext pageContext, List<String> resources) throws IOException {
		if (resources != null) {
			for (String s : resources) {
				HtmlIncludeTag hit = new HtmlIncludeTag();
				hit.setPageContext(pageContext);
				hit.setFile(s);
				try {
					hit.doStartTag();
				}
				catch (Exception e) {
					throw new IllegalArgumentException("Unable to include resource: " + s, e);
				}
			}
		}
	}
	
	/**
	 * Render a simple tag that has no body
	 * @param context
	 * @param attributes
	 * @throws IOException
	 */
	public static void renderSimpleTag(Writer w, String tagName, Collection<Attribute> attributes) throws IOException {
		w.write("<"+tagName);
		renderTagAttributes(w, tagName, attributes);
		w.write("/>");
	}

	/**
	 * Render an opening tag
	 * @param context
	 * @param attributes
	 * @throws IOException
	 */
	public static void renderOpenTag(Writer w, String tagName, Collection<Attribute> attributes) throws IOException {
		w.write("<"+tagName);
		renderTagAttributes(w, tagName, attributes);
		w.write(">");
	}
	
	/**
	 * Render a closing tag
	 * @param context
	 * @param attributes
	 * @throws IOException
	 */
	public static void renderCloseTag(Writer w, String tagName) throws IOException {
		w.write("</"+tagName+">");
	}
	
	/**
	 * Render the attribute map as it should be output in a tag
	 * @param context
	 * @param attributes
	 * @throws IOException
	 */
	public static void renderTagAttributes(Writer w, String tagName, Collection<Attribute> attributes) throws IOException {
		if (attributes != null) {
			for (Attribute a : attributes) {
				if (isValidTagAttribute(tagName, a.getName())) {
					if (StringUtils.isNotEmpty(a.getValue())) {
						w.write(" " + a.getName() + "=\"" + a.getValue() + "\"");
					}
				}
			}
		}
	}
}