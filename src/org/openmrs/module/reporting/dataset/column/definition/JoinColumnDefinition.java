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
package org.openmrs.module.reporting.dataset.column.definition;

import java.util.Date;
import java.util.List;

import org.openmrs.Auditable;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.User;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;

/**
 * Join Column Definition
 */
public abstract class JoinColumnDefinition<T extends RowPerObjectColumnDefinition> extends BaseColumnDefinition implements RowPerObjectColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private T columnDefinition;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public JoinColumnDefinition() {
		super();
	}

	/**
	 * Constructor to populate name only
	 */
	public JoinColumnDefinition(T columnDefinition) {
		this.columnDefinition = columnDefinition;
	}

	//***** INSTANCE METHODS ****
	
	/**
	 * Returns the property path for the joined definitions
	 */
	public abstract String getJoinProperty();
	
	/** 
	 * @see BaseColumnDefinition#getRawDataType()
	 */
	@Override
	public Class<?> getRawDataType() {
		return columnDefinition.getRawDataType();
	}

	/** 
	 * @see Parameterizable#addParameter(Parameter)
	 */
	public void addParameter(Parameter parameter) {
		columnDefinition.addParameter(parameter);
	}

	/** 
	 * @see Parameterizable#getParameter(String)
	 */
	public Parameter getParameter(String name) {
		return columnDefinition.getParameter(name);
	}

	/** 
	 * @see Parameterizable#getParameters()
	 */
	public List<Parameter> getParameters() {
		return columnDefinition.getParameters();
	}

	/** 
	 * @see Parameterizable#removeParameter(Parameter)
	 */
	public void removeParameter(Parameter parameter) {
		columnDefinition.removeParameter(parameter);
	}

	/** 
	 * @see Parameterizable#removeParameter(String)
	 */
	public void removeParameter(String parameterName) {
		columnDefinition.removeParameter(parameterName);
	}

	/** 
	 * @see OpenmrsMetadata#getDescription()
	 */
	public String getDescription() {
		return columnDefinition.getDescription();
	}

	/** 
	 * @see OpenmrsMetadata#getName()
	 */
	public String getName() {
		return columnDefinition.getName();
	}

	/** 
	 * @see OpenmrsMetadata#setDescription(String)
	 */
	public void setDescription(String description) {
		columnDefinition.setDescription(description);
	}

	/** 
	 * @see OpenmrsMetadata#setName(String)
	 */
	public void setName(String name) {
		columnDefinition.setName(name);
	}

	/** 
	 * @see Retireable#getDateRetired()
	 */
	public Date getDateRetired() {
		return columnDefinition.getDateRetired();
	}

	/** 
	 * @see Retireable#getRetiredBy()
	 */
	public User getRetiredBy() {
		return columnDefinition.getRetiredBy();
	}

	/** 
	 * @see Retireable#getRetireReason()
	 */
	public String getRetireReason() {
		return columnDefinition.getRetireReason();
	}

	/** 
	 * @see Retireable#isRetired()
	 */
	public Boolean isRetired() {
		return columnDefinition.isRetired();
	}

	/** 
	 * @see Retireable#setDateRetired(Date)
	 */
	public void setDateRetired(Date dateRetired) {
		columnDefinition.setDateRetired(dateRetired);
	}

	/** 
	 * @see Retireable#setRetired(Boolean)
	 */
	public void setRetired(Boolean retired) {
		columnDefinition.setRetired(retired);
	}

	/** 
	 * @see Retireable#setRetiredBy(User)
	 */
	public void setRetiredBy(User retiredBy) {
		columnDefinition.setRetiredBy(retiredBy);
	}

	/** 
	 * @see Retireable#setRetireReason(String)
	 */
	public void setRetireReason(String retireReason) {
		columnDefinition.setRetireReason(retireReason);
	}

	/** 
	 * @see Auditable#getChangedBy()
	 */
	public User getChangedBy() {
		return columnDefinition.getChangedBy();
	}

	/** 
	 * @see Auditable#getCreator()
	 */
	public User getCreator() {
		return columnDefinition.getCreator();
	}

	/** 
	 * @see Auditable#getDateChanged()
	 */
	public Date getDateChanged() {
		return columnDefinition.getDateChanged();
	}

	/** 
	 * @see Auditable#getDateCreated()
	 */
	public Date getDateCreated() {
		return columnDefinition.getDateCreated();
	}

	/** 
	 * @see Auditable#setChangedBy(User)
	 */
	public void setChangedBy(User changedBy) {
		columnDefinition.setChangedBy(changedBy);
	}

	/** 
	 * @see Auditable#setCreator(User)
	 */
	public void setCreator(User creator) {
		columnDefinition.setCreator(creator);
	}

	/** 
	 * @see Auditable#setDateChanged(Date)
	 */
	public void setDateChanged(Date dateChanged) {
		columnDefinition.setDateChanged(dateChanged);
	}

	/** 
	 * @see Auditable#setDateCreated(Date)
	 */
	public void setDateCreated(Date dateCreated) {
		columnDefinition.setDateCreated(dateCreated);
	}

	/** 
	 * @see OpenmrsObject#getId()
	 */
	public Integer getId() {
		return columnDefinition.getId();
	}

	/** 
	 * @see OpenmrsObject#getUuid()
	 */
	public String getUuid() {
		return columnDefinition.getUuid();
	}

	/** 
	 * @see OpenmrsObject#setId(Integer)
	 */
	public void setId(Integer id) {
		columnDefinition.setId(id);
	}

	/** 
	 * @see OpenmrsObject#setUuid(String)
	 */
	public void setUuid(String uuid) {
		columnDefinition.setUuid(uuid);
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the columnDefinitionDefinition
	 */
	public T getColumnDefinition() {
		return columnDefinition;
	}

	/**
	 * @param columnDefinition the columnDefinition to set
	 */
	public void setColumnDefinition(T columnDefinition) {
		this.columnDefinition = columnDefinition;
	}
}