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

import org.openmrs.Person;
import org.openmrs.module.reporting.dataset.column.definition.person.PersonColumnDefinition;
import org.openmrs.module.reporting.dataset.filter.Filter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Person
 * @see DataSetDefinition
 */
public class RowPerPersonDataSetDefinition extends RowPerObjectDataSetDefinition<PersonColumnDefinition> {
	
	//***** PROPERTIES *****
	
    @ConfigurationProperty
	private Mapped<? extends Filter<Person>> personFilter;

    //***** PROPERTY ACCESS *****
    
	/**
	 * @return the personFilter
	 */
	public Mapped<? extends Filter<Person>> getPersonFilter() {
		return personFilter;
	}

	/**
	 * @param personFilter the personFilter to set
	 */
	public void setPersonFilter(Mapped<? extends Filter<Person>> personFilter) {
		this.personFilter = personFilter;
	}
}