package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.WebConstants;
import org.openmrs.web.taglib.HtmlIncludeTag;

/**
 * This represents utility methods for writing tags
 */
public class HtmlUtil  {
	
	public static final List<String> STANDARD_ATTRIBUTES = Arrays.asList(
		"id","class","style","dir","lang","accesskey","tabindex","title","xml:lang"
	);
	
	public static final List<String> INPUT_ATTRIBUTES = Arrays.asList(
		"accept","alt","checked","disabled","maxlength","name","readonly","size","src","type","value"
	);
	
	public static final List<String> OPTION_ATTRIBUTES = Arrays.asList(
		"disabled","label","value","selected"
	);
	
	public static final List<String> OPTGROUP_ATTRIBUTES = Arrays.asList(
		"disabled","label"
	);

	public static final List<String> SELECT_ATTRIBUTES = Arrays.asList(
		"disabled","multiple","name","size"
	);
	
	public static final List<String> TEXTAREA_ATTRIBUTES = Arrays.asList(
		"cols","rows","disabled","name","readonly"
	);
	
	public static final List<String> STANDARD_EVENTS = Arrays.asList(
		"onblur","onchange","onclick","ondblclick","onfocus","onmousedown","onmousemove",
		"onmouseout","onmouseover","onmouseup","onkeydown","onkeypress","onkeyup","onselect"
	);
	
	public static final List<String> JS_EXTENSIONS = Arrays.asList("js","javascript","jscript");
	
	private static final List<String> CSS_EXTENSIONS = Arrays.asList("css","style","stylesheet");

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
		if ("optgroup".equals(tagName)) { return OPTGROUP_ATTRIBUTES.contains(att); }
		if ("select".equals(tagName)) { return SELECT_ATTRIBUTES.contains(att); }
		if ("textarea".equals(tagName)) { return TEXTAREA_ATTRIBUTES.contains(att); }
		return false;
	}
	
	/**
	 * Render the specified resource in the page
	 * @param pageContext
	 * @param resources
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void renderResource(Writer w, HttpServletRequest request, String resource) throws IOException {
		
		String ext = resource.substring(resource.lastIndexOf(".")+1).toLowerCase();
		boolean isJs = JS_EXTENSIONS.contains(ext);
		boolean isCss = CSS_EXTENSIONS.contains(ext);
		HttpSession session = request.getSession();
		
		if (isJs || isCss) {
			String initialRequestId = (String) request.getAttribute(WebConstants.INIT_REQ_UNIQUE_ID);
			String lastRequestId = (String) session.getAttribute(HtmlIncludeTag.OPENMRS_HTML_INCLUDE_REQUEST_ID_KEY);
			Map<String, String> m = (HashMap<String, String>) session.getAttribute(HtmlIncludeTag.OPENMRS_HTML_INCLUDE_MAP_KEY);
			if (m == null || !initialRequestId.equals(lastRequestId)) {
				m = new HashMap<String, String>();
			}
			String otherResource = "/" + WebConstants.WEBAPP_NAME + resource;
			if (!m.containsKey(resource) && !m.containsKey(otherResource)) {
				m.put(resource, "true");
				session.setAttribute(HtmlIncludeTag.OPENMRS_HTML_INCLUDE_MAP_KEY, m);
				session.setAttribute(HtmlIncludeTag.OPENMRS_HTML_INCLUDE_REQUEST_ID_KEY, initialRequestId);
				
				String prefix = request.getContextPath();
				if (!resource.startsWith(prefix + "/")) {
					resource = prefix + resource;
				}
				resource += "?v=" + OpenmrsConstants.OPENMRS_VERSION_SHORT;
				

				if (isJs) {
					w.write("<script src=\"" + resource + "\" type=\"text/javascript\" ></script>");
				}
				else if (isCss) {
					w.write("<link href=\"" + resource + "\" type=\"text/css\" rel=\"stylesheet\" />");
				}
			}
		}
	}
	
	/**
	 * Render each specified resource as an HtmlInclude in the page
	 * @param pageContext
	 * @param resources
	 * @throws IOException
	 */
	public static void renderResourceFiles(Writer w, HttpServletRequest request, List<String> resources) throws IOException {
		if (resources != null) {
			for (String s : resources) {
				renderResource(w, request, s);
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
	 * Render a simple tag that has no body
	 * @param context
	 * @param attributes
	 * @throws IOException
	 */
	public static void renderSimpleTag(Writer w, String tagName, String attributeString) throws IOException {
		w.write("<"+tagName);
		renderTagAttributes(w, tagName, attributeString);
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
	 * Render an opening tag
	 * @param context
	 * @param attributes
	 * @throws IOException
	 */
	public static void renderOpenTag(Writer w, String tagName, String attributeString) throws IOException {
		w.write("<"+tagName);
		renderTagAttributes(w, tagName, attributeString);
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
	
	/**
	 * Render the attribute map as it should be output in a tag
	 * @param context
	 * @param attributes
	 * @throws IOException
	 */
	public static void renderTagAttributes(Writer w, String tagName, String attributeString) throws IOException {
		if (StringUtils.isNotEmpty(attributeString)) {
			for (String attribute : attributeString.split("\\|")) {
				String[] nameVal = attribute.split("=");
				if (nameVal.length != 2) {
					throw new IllegalArgumentException("Misformed argument in attributeString: <" + attributeString + ">");
				}
				if (isValidTagAttribute(tagName, nameVal[0])) {
					if (StringUtils.isNotEmpty(nameVal[1])) {
						w.write(" " + nameVal[0] + "=\"" + nameVal[1] + "\"");
					}
				}
			}
		}
	}
}