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
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;
import java.util.List;

/**
 * Definition of an EncounterAndObs DataSet
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.EncounterAndObsDataSetDefinition")
public class EncounterAndObsDataSetDefinition extends BaseDataSetDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(group="type")
	public List<EncounterType> encounterTypes;
	
	@ConfigurationProperty(group="form")
	public List<Form> forms;
	
	@ConfigurationProperty(group="when")
	public Date encounterDatetimeOnOrAfter;
	
	@ConfigurationProperty(group="when")
	public Date encounterDatetimeOnOrBefore;
	
	@ConfigurationProperty(group="which")
	public TimeQualifier whichEncounterQualifier;
	
	@ConfigurationProperty(group="which")
	public Integer quantity;
	
	@ConfigurationProperty(group="column")
	public List<PatientIdentifierType> patientIdentifierTypes;
	
	@ConfigurationProperty(group="column")
	public List<ObsOptionalColumn> optionalColumns;
	
	@ConfigurationProperty(group="column")
	public List<ColumnDisplayFormat> columnDisplayFormat;
	
	@ConfigurationProperty(group="column")
	public Integer maxColumnHeaderWidth;
	
	//***** CONSTRUCTORS *****

	public EncounterAndObsDataSetDefinition() {
		super();
	}
	
	/**
	 * Public constructor with name and description
	 */
	public EncounterAndObsDataSetDefinition(String name, String description) {
		super(name, description);
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the patientProperties
	 */
	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}

	/**
	 * @param encounterTypes the encounterTypes to set
	 */
	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}

	/**
	 * @return the form
	 */
	public List<Form> getForms() {
		return forms;
	}

	/**
	 * @param forms the forms to set
	 */
	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	/**
	 * @return the encounterDatetimeOnOrAfter
	 */
	public Date getEncounterDatetimeOnOrAfter() {
		return encounterDatetimeOnOrAfter;
	}

	/**
	 * @param encounterDatetimeOnOrAfter the encounterDatetimeOnOrAfter to set
	 */
	public void setEncounterDatetimeOnOrAfter(Date encounterDatetimeOnOrAfter) {
		this.encounterDatetimeOnOrAfter = encounterDatetimeOnOrAfter;
	}

	/**
	 * @return the encounterDatetimeOnOrBefore
	 */
	public Date getEncounterDatetimeOnOrBefore() {
		return encounterDatetimeOnOrBefore;
	}

	/**
	 * @param encounterDatetimeOnOrBefore the encounterDatetimeOnOrBefore to set
	 */
	public void setEncounterDatetimeOnOrBefore(Date encounterDatetimeOnOrBefore) {
		this.encounterDatetimeOnOrBefore = encounterDatetimeOnOrBefore;
	}

	/**
	 * @return the whichEncounterQualifier
	 */
	public TimeQualifier getWhichEncounterQualifier() {
		return whichEncounterQualifier;
	}

	/**
	 * @param whichEncounterQualifier the whichEncounterQualifier to set
	 */
	public void setWhichEncounterQualifier(TimeQualifier whichEncounterQualifier) {
		this.whichEncounterQualifier = whichEncounterQualifier;
	}

	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the patientIdentifierTypes
	 */
	public List<PatientIdentifierType> getPatientIdentifierTypes() {
		return patientIdentifierTypes;
	}

	/**
	 * @param patientIdentifierTypes the patientIdentifierTypes to set
	 */
	public void setPatientIdentifierTypes(
			List<PatientIdentifierType> patientIdentifierTypes) {
		this.patientIdentifierTypes = patientIdentifierTypes;
	}

	/**
	 * @return the optionalColumns
	 */
	public List<ObsOptionalColumn> getOptionalColumns() {
		return optionalColumns;
	}

	/**
	 * @param optionalColumns the optionalColumns to set
	 */
	public void setOptionalColumns(List<ObsOptionalColumn> optionalColumns) {
		this.optionalColumns = optionalColumns;
	}

	/**
	 * @return the columnDisplayFormat
	 */
	public List<ColumnDisplayFormat> getColumnDisplayFormat() {
		return columnDisplayFormat;
	}

	/**
	 * @param columnDisplayFormat the columnDisplayFormat to set
	 */
	public void setColumnDisplayFormat(List<ColumnDisplayFormat> columnDisplayFormat) {
		this.columnDisplayFormat = columnDisplayFormat;
	}

	/**
	 * @return the maxColumnHeaderWidth
	 */
	public Integer getMaxColumnHeaderWidth() {
		return maxColumnHeaderWidth;
	}

	/**
	 * @param maxColumnHeaderWidth the maxColumnHeaderWidth to set
	 */
	public void setMaxColumnHeaderWidth(Integer maxColumnHeaderWidth) {
		this.maxColumnHeaderWidth = maxColumnHeaderWidth;
	}
	
	/**
	 * This enum represents column formats
	 */
	public enum ColumnDisplayFormat {
		ID, BEST_SHORT_NAME
	}
	
	/**
	 * This enum is meant to represent optional columns in an
	 * EncounterAndObs Data Set
	 */
	public enum ObsOptionalColumn {
		VALUE_MODIFIER, ACCESSION_NUMBER, COMMENT
	}
}
