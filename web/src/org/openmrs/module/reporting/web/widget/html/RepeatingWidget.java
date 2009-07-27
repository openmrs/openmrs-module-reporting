package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.openmrs.module.reporting.web.widget.WidgetTag;

public class RepeatingWidget extends BaseWidget {
	
	//***** PROPERTIES *****
	
	private Class<?> repeatingType;
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see BaseWidget#configure()
	 */
	@Override
	public void configure() {
		addResource("/scripts/jquery/jquery-1.2.6.min.js");
		addResource("/moduleResources/reporting/scripts/reporting.js");
	}
	
	/** 
	 * @see BaseWidget#render(Writer)
	 */
	@Override
	public void render(Writer w) throws IOException { }
	
	/** 
	 * @see Widget#render(PageContext)
	 */
	public void render(PageContext pageContext) throws IOException {
		Writer w = pageContext.getOut();
		
		HtmlUtil.renderResourceFiles(pageContext, getResources());
		HtmlUtil.renderOpenTag(w, "div", "id="+getId()+"MultiFieldDiv");
		if (getDefaultValue() != null) {
			if (getDefaultValue() instanceof Collection) {
				for (Object o : (Collection<?>)getDefaultValue()) {
					renderWidget(pageContext, getRepeatingType(), o, false);
				}
			}
		}
		renderWidget(pageContext, getRepeatingType(), null, true);
		HtmlUtil.renderSimpleTag(w, "input", "type=button|value=+|size=1|onclick=cloneAndInsertBefore('"+getId()+"Template', this);");
		HtmlUtil.renderCloseTag(w, "div");
	}

	/**
	 * Utility method which renders a new repeating section
	 * @param parent
	 * @param clazz
	 * @param defaultValue
	 */
	protected void renderWidget(PageContext context, Class<?> clazz, Object defaultValue, boolean isTemplate) throws IOException {
		Writer w = context.getOut();
		
		String addAtts = (isTemplate ? "|id="+getId()+"Template|style=display:none;" : "");
		HtmlUtil.renderOpenTag(w, "span", "class=multiFieldInput" + addAtts);

		WidgetTag t = new WidgetTag();
		t.setPageContext(context);
		t.setId(getId());
		t.setName(getName());
		t.setClazz(clazz);
		t.setDefaultValue(defaultValue);
		StringBuilder attributeString = new StringBuilder();
		for (Iterator<Attribute> i = getAttributes().iterator(); i.hasNext();) {
			Attribute a = i.next();
			attributeString.append(a.getName()+"="+a.getValue()+(i.hasNext() ? "|" : ""));
		}
		t.setAttributes(attributeString.toString());
		try {
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
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the repeatingType
	 */
	public Class<?> getRepeatingType() {
		return repeatingType;
	}

	/**
	 * @param repeatingType the repeatingType to set
	 */
	public void setRepeatingType(Class<?> repeatingType) {
		this.repeatingType = repeatingType;
	}
}