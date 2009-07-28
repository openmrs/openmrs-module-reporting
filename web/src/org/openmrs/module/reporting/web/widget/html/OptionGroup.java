package org.openmrs.module.reporting.web.widget.html;

/**
 * Represents a grouping of Options
 */
public class OptionGroup extends Label {
	
	/**
	 * Default Constructor
	 */
    public OptionGroup() {
    	super();
    }
    
	/**
	 * Full Constructor
	 */
    public OptionGroup(String labelText, String labelCode) {
    	super(labelText, labelCode);
    }
}