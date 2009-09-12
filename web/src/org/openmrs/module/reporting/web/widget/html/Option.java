package org.openmrs.module.reporting.web.widget.html;

/**
 * Represents a Coded Option
 */
public class Option extends Label implements Comparable<Option> {
	
	//****** Properties ******
	private String code;
	private Object value;
	private OptionGroup group;

	/**
	 * Default Constructor
	 */
    public Option() { 
    	super();
    }
    
    /**
     * Full Constructor with null group
     */
    public Option(String code, String labelText, String labelCode, Object value) {
    	this(code, labelText, labelCode, value, null);
    }   
    
    /**
     * Full Constructor
     */
    public Option(String code, String labelText, String labelCode, Object value, OptionGroup group) {
    	super(labelText, labelCode);
    	this.code = code;
    	this.value = value;
    	this.group = group;
    }
	
	/** 
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Option o) {
		return this.getLabel().compareTo(o.getLabel());
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

	/**
	 * @return the group
	 */
	public OptionGroup getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(OptionGroup group) {
		this.group = group;
	}
}