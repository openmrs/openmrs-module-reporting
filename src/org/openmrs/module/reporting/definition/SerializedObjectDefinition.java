/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.definition;

import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.evaluation.BaseDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.serialization.OpenmrsSerializer;

public abstract class SerializedObjectDefinition extends BaseDefinition {

    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
    
    private Integer id;
	
	@ConfigurationProperty(required=true)
	private String serializedData;
	
	@ConfigurationProperty(required=true)
	private String type;
	
	@ConfigurationProperty(required=true)
	private String subtype;
	
	@ConfigurationProperty(required=true)
	private Class<? extends OpenmrsSerializer> serializationClass;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public SerializedObjectDefinition() {
		super();
	}
	
	/**
	 * Default Constructor
	 */
	public SerializedObjectDefinition(SerializedObject serializedObject) {
		super();
		this.serializedData = serializedObject.getSerializedData();
		this.type = serializedObject.getType();
		this.subtype = serializedObject.getSubtype();
		this.serializationClass = serializedObject.getSerializationClass();
		setId(serializedObject.getId());
		setUuid(serializedObject.getUuid());
		setName(serializedObject.getName());
		setDescription(serializedObject.getDescription());
		setCreator(serializedObject.getCreator());
		setDateCreated(serializedObject.getDateCreated());
		setChangedBy(serializedObject.getChangedBy());
		setDateChanged(serializedObject.getDateChanged());
		setRetired(serializedObject.getRetired());
		setRetiredBy(serializedObject.getRetiredBy());
		setDateRetired(serializedObject.getDateRetired());
		setRetireReason(serializedObject.getRetireReason());
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Converts this to a SerializedObject
	 */
	public SerializedObject toSerializedObject() {
		SerializedObject so = new SerializedObject();
		so.setId(getId());
		so.setUuid(getUuid());
		so.setName(getName());
		so.setDescription(getDescription());
		so.setSerializedData(this.serializedData);
		so.setType(this.type);
		so.setSubtype(this.subtype);
		so.setSerializationClass(this.serializationClass);
		so.setDateCreated(getDateCreated());
		so.setChangedBy(getChangedBy());
		so.setDateChanged(getDateChanged());
		so.setRetired(getRetired());
		so.setRetiredBy(getRetiredBy());
		so.setDateRetired(getDateRetired());
		so.setRetireReason(getRetireReason());
		return so;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}


	//***** PROPERTY ACCESS *****

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the serializedData
	 */
	public String getSerializedData() {
		return serializedData;
	}

	/**
	 * @param serializedData the serializedData to set
	 */
	public void setSerializedData(String serializedData) {
		this.serializedData = serializedData;
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

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @param subtype the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	/**
	 * @return the serializationClass
	 */
	public Class<? extends OpenmrsSerializer> getSerializationClass() {
		return serializationClass;
	}

	/**
	 * @param serializationClass the serializationClass to set
	 */
	public void setSerializationClass(Class<? extends OpenmrsSerializer> serializationClass) {
		this.serializationClass = serializationClass;
	}
}
