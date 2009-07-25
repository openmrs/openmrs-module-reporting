package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * This represents a single widget on a form.
 */
public abstract class CodedWidget extends BaseWidget {
	
	//******* PROPERTIES *************
	private List<Option> options;
	private String separator = "&nbsp;";
	
	//****** PROPERTY ACCESS *********
	
	/**
	 * @return the options
	 */
	public List<Option> getOptions() {
		if (options == null) {
			options = new ArrayList<Option>();
		}
		return options;
	}
	
	/**
	 * @param options the options to set
	 */
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	/**
	 * Add an option to the list
	 */
	public void addOption(Option option) {
		String prefix = StringUtils.isEmpty(option.getCode()) ? "empty" : option.getCode();
		String labelCode = getAttribute(prefix + "Code");
		String labelText = getAttribute(prefix + "Label");
		if (labelCode != null || labelText != null) {
			option.setLabelCode(labelCode);
			option.setLabelText(labelText);
		}
		getOptions().add(option);
	}
	
	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}
	
	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	/** 
	 * @see Widget#render(PageContext)
	 */
	public void render(PageContext pageContext) throws IOException {
		int num = getOptions().size();
		for (int i=0; i<num; i++) {
			Option option = getOptions().get(i);
			List<Attribute> atts = cloneAttributes();
			if (num > 1) {
				for (Attribute att : atts) {
					if (att.getName().equalsIgnoreCase("id") && att.getValue() != null) {
						att.setValue(att.getValue() + "_" + i);
					}
				}
			}
			atts.add(new Attribute("value", option.getCode()));
			if (isSelected(option)) {
				atts.add(new Attribute("checked", "true"));
			}
			HtmlUtil.renderSimpleTag(pageContext, "input", atts);
			pageContext.getOut().write("&nbsp;"+option.getLabel());
			if ((i+1) < num) {
				pageContext.getOut().write(getSeparator() == null ? "&nbsp;" : getSeparator());
			}
		}
	}
	
	/**
	 * Returns true if the given option is selected
	 * @param option the Option to check
	 * @return true if the given option is selected
	 */
	public boolean isSelected(Option option) {
		if (option.getValue() == null) {
			return getDefaultValue() == null;
		}
		return option.getValue().equals(getDefaultValue());
	}
}