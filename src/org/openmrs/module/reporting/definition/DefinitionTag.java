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

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * DefinitionTag is a piece of text that can be applied to a {@link Definition}, it provides a means
 * to categorize {@link Definition}s
 */
public class DefinitionTag extends BaseOpenmrsObject implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String tag;
	
	private String definitionUuid;
	
	private String definitionType;
	
	/**
	 * Default constructor
	 */
	public DefinitionTag() {
	}
	
	/**
	 * Convenience constructor that takes in a tag name and the {@link Definition} to tag
	 * 
	 * @param tag
	 * @param definition
	 */
	public DefinitionTag(String tag, Definition definition) {
		this.tag = tag;
		setDefinitionUuid(definition.getUuid());
		setDefinitionType(definition.getClass().getName());
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
	
	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	/**
	 * @return the definitionUuid
	 */
	public String getDefinitionUuid() {
		return definitionUuid;
	}
	
	/**
	 * @param definitionUuid the definitionUuid to set
	 */
	public void setDefinitionUuid(String definitionUuid) {
		this.definitionUuid = definitionUuid;
	}
	
	/**
	 * @return the definitionType
	 */
	public String getDefinitionType() {
		return definitionType;
	}
	
	/**
	 * @param definitionType the definitionType to set
	 */
	public void setDefinitionType(String definitionType) {
		this.definitionType = definitionType;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BaseOpenmrsObject))
			return false;
		BaseOpenmrsObject other = (BaseOpenmrsObject) obj;
		if (getUuid() == null)
			return false;
		return getUuid().equals(other.getUuid());
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (getUuid() == null)
			return super.hashCode();
		return getUuid().hashCode();
	}
}
