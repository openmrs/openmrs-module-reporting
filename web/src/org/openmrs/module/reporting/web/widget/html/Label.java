package org.openmrs.module.reporting.web.widget.html;

import org.apache.commons.lang.ObjectUtils;
import org.openmrs.api.context.Context;

/**
 * Represents text to display
 */
public class Label {
	
	//****** Properties ******
	private String labelText;
	private String labelCode;

	/**
	 * Default Constructor
	 */
    public Label() { }
    
	/**
	 * Full Constructor
	 */
    public Label(String labelText, String labelCode) {
    	this.labelText = labelText;
    	this.labelCode = labelCode;
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
	
	/** 
	 * @see Object#equals(bject)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Label) {
			Label l = (Label) obj;
			return ObjectUtils.equals(this.getLabel(), l.getLabel());
		}
		return this == obj;
	}

	/** 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 31 * 7 + getLabel().hashCode();
	}

	/** 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getLabel();
	}
	
	//***** Property Access *****

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
}