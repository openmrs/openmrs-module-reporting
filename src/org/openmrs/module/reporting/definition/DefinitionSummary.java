package org.openmrs.module.reporting.definition;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.db.SerializedObject;

/**
 * A lightweight summary of a Definition
 */
public class DefinitionSummary {

	private String uuid;
	private String name;
	private String description;
	private String type;
	
	public DefinitionSummary() {
	}

    public DefinitionSummary(SerializedObject so) {
    	this.uuid = so.getUuid();
    	this.name = so.getName();
    	this.description = so.getDescription();
    	this.type = so.getSubtype();
    }
	
    public DefinitionSummary(OpenmrsMetadata metadata) {
	    this.uuid = metadata.getUuid();
	    this.name = metadata.getName();
	    this.description = metadata.getDescription();
	    this.type = metadata.getClass().getName();
    }

	/**
     * @return the uuid
     */
    public String getUuid() {
    	return uuid;
    }

	
    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
    	this.uuid = uuid;
    }

	
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
     * @return the description
     */
    public String getDescription() {
    	return description;
    }

	
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
    	this.description = description;
    }

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
