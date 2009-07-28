package org.openmrs.module.reporting.web.widget.html;

/**
 * Represents an attribute in an HTML Tag
 */
public class Attribute {
	
	//****** Properties ******
	private String name;
	private String fixedValue;
	private String configuredValue;
	private String defaultValue;
	
	/**
	 * Default Constructor
	 */
    public Attribute() { }
    
    /**
     * Full constructor
     * @param name
     * @param fixedValue
     * @param configuredValue
     * @param defaultValue
     */
    public Attribute(String name, String fixedValue, String configuredValue, String defaultValue) {
    	this.name = name;
    	this.fixedValue = fixedValue;
    	this.configuredValue = configuredValue;
    	this.defaultValue = defaultValue;
    }

    //***** Instance Methods *****
    
	/** 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Attribute) {
			Attribute a = (Attribute) obj;
			if (this.getName() != null) {
				return (this.getName().equalsIgnoreCase(a.getName()));
			}
		}
		return this == obj;
	}

	/** 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (getName() == null ? 0 : getName().toLowerCase().hashCode());
		return hash;
	}

	/** 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return " " + name + "=\"" + getValue() == null ? "" : getValue() + "\"";
	}
	
	/**
	 * @return first not-null of fixedValue, configuredValue, defaultValue
	 */
	public String getValue() {
		if (fixedValue != null) { return fixedValue; }
		if (configuredValue != null) { return configuredValue; }
		return defaultValue;
	}
	
	//***** Property Access *****

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the fixedValue
	 */
	public String getFixedValue() {
		return fixedValue;
	}

	/**
	 * @param fixedValue the fixedValue to set
	 */
	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}

	/**
	 * @return the configuredValue
	 */
	public String getConfiguredValue() {
		return configuredValue;
	}

	/**
	 * @param configuredValue the configuredValue to set
	 */
	public void setConfiguredValue(String configuredValue) {
		this.configuredValue = configuredValue;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
