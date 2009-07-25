package org.openmrs.module.reporting.web.widget.html;

/**
 * Represents an attribute in an HTML Tag
 */
public class Attribute {
	
	//****** Properties ******
	private String name;
	private String value;
	private boolean configurable = true;

	/**
	 * Default Constructor
	 */
    public Attribute() { }
    
    /**
     * Name and value constructor to initialize a configurable attribute
     * @param name
     * @param value
     */
    public Attribute(String name, String value) {
    	this(name, value, true);
    }   
    
    /**
     * Full constructor
     * @param name
     * @param value
     * @param configurable
     */
    public Attribute(String name, String value, boolean configurable) {
    	this.name = name;
    	this.value = value;
    	this.configurable = configurable;
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
		return " " + name + "=\"" + (value == null ? "" : value) + "\"";
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * @return the configurable
	 */
	public boolean isConfigurable() {
		return configurable;
	}
	
	/**
	 * @param configurable the configurable to set
	 */
	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
	}
}
