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

import java.util.Collection;
import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * {@link CalculationProvider} used to expose saved Patient and Person data definitions as calculations
 */
@Handler
public class PatientDataCalculationProvider implements CalculationProvider {
	
	/**
	 * Creates a calculation instances for the matching data definitions by loading the data
	 * definition, if none is found, a saved instance is looked up
	 * 
	 * @see CalculationProvider#getCalculation(String, String)
	 */
	@SuppressWarnings("unchecked")
	public PatientDataCalculation getCalculation(String calculationName, String configuration) throws InvalidCalculationException {
		DataDefinition d = null;
		d = Context.getService(PatientDataService.class).getDefinitionByUuid(calculationName);
		if (d == null) {
			d = Context.getService(PersonDataService.class).getDefinitionByUuid(calculationName);
		}
		if (d == null) {
			throw new InvalidCalculationException("Unable to load Data Definition by uuid from calculationName: " + calculationName);
		}
		return new PatientDataCalculation(d);
	}
}
