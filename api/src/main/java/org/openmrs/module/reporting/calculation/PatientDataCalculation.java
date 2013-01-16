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
package org.openmrs.module.reporting.calculation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.ConfigurableCalculation;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.calculation.parameter.ParameterDefinitionSet;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;

/**
 * Adapter class which exposes a Patient or Person Data Definition as a Patient Calculation
 */
public class PatientDataCalculation implements PatientCalculation, ConfigurableCalculation {
	
	private DataDefinition dataDefinition;
	private List<DataConverter> converters;
	
	/**
	 * Default Constructor
	 */
	public PatientDataCalculation() { }

	/**
	 * @see ConfigurableCalculation#setConfiguration(String)
	 */
	public void setConfiguration(String configuration) throws InvalidCalculationException {
		if (StringUtils.isNotBlank(configuration)) {
			// TODO: Allow full configuration of properties and converters here.
			// For now, as a poc, I'll allow formatting via the ObjectFormatter
			// You can specify a formatString here
			addConverter(new ObjectFormatter(configuration));
		}
	}

	/**
	 * @see Calculation#getParameterDefinitionSet()
	 */
	public ParameterDefinitionSet getParameterDefinitionSet() {
		return ReportingCalculationUtil.getParameterDefinitionSet(dataDefinition);
	}

	/**
	 * @return the dataDefinition
	 */
	public DataDefinition getDataDefinition() {
		return dataDefinition;
	}

	/**
	 * @param dataDefinition the dataDefinition to set
	 */
	public void setDataDefinition(DataDefinition dataDefinition) {
		this.dataDefinition = dataDefinition;
	}

	/**
	 * @return the converters
	 */
	public List<DataConverter> getConverters() {
		if (converters == null) {
			converters = new ArrayList<DataConverter>();
		}
		return converters;
	}

	/**
	 * @param converters the converters to set
	 */
	public void setConverters(List<DataConverter> converters) {
		this.converters = converters;
	}
	
	/**
	 * @param converter adds a Converter to the list of Converters
	 */
	public void addConverter(DataConverter converter) {
		getConverters().add(converter);
	}
}