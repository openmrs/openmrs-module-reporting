/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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
