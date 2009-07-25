package org.openmrs.module.reporting.web.widget.html;

import org.openmrs.api.context.Context;

/**
 * Represents a Coded Option
 */
public class Option {
	
	//****** Properties ******
	private String code;
	private String labelText;
	private String labelCode;
	private Object value;

	/**
	 * Default Constructor
	 */
    public Option() { }
    
    /**
     * Full Constructor
     */
    public Option(String code, String labelText, String labelCode, Object value) {
    	this.code = code;
    	this.labelText = labelText;
    	this.labelCode = labelCode;
    	this.value = value;
    }   

    //***** Instance Methods *****
    
	/**
	 * Return the display label, based on the configuration of labelCode and labelText
	 * @return the display label
	 */
	public String getLabel() {
		if (getLabelCode() != null) {
			return Context.getMessageSourceService().getMessage(getLabelCode());
		}
		return (getLabelText() == null ? "" : getLabelText());
	}
	
	//***** Property Access *****
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the labelText
	 */
	public String getLabelText() {
		return labelText;
	}

	/**
	 * @param labelText the labelText to set
	 */
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	/**
	 * @return the labelCode
	 */
	public String getLabelCode() {
		return labelCode;
	}

	/**
	 * @param labelCode the labelCode to set
	 */
	public void setLabelCode(String labelCode) {
		this.labelCode = labelCode;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}