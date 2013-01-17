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

import java.util.Collection;
import java.util.List;

/**
 * {@link CalculationProvider} used to expose saved Patient and Person data definitions as calculations
 */
@Handler
public class PatientDataClasspathCalculationProvider implements CalculationProvider {

	/**
	 * Creates a calculation instance by instantiating a new DataDefinition by classname
	 * @see CalculationProvider#getCalculation(String, String)
	 */
	@SuppressWarnings("unchecked")
	public PatientDataCalculation getCalculation(String calculationName, String configuration) throws InvalidCalculationException {
		try {
			Class<? extends DataDefinition> clazz = (Class<? extends DataDefinition>) Context.loadClass(calculationName);
			DataDefinition dd = clazz.newInstance();
			for (Property p : DefinitionUtil.getConfigurationProperties(dd)) {
				Class<? extends Collection<?>> collectionType = null;
				Class<?> fieldType = p.getField().getType();
				if (ReflectionUtil.isCollection(p.getField())) {
					collectionType = (Class<? extends Collection<?>>) p.getField().getType();
					fieldType = (Class<?>) ReflectionUtil.getGenericTypes(p.getField())[0];
				}
				dd.addParameter(new Parameter(p.getField().getName(), p.getDisplayName(), fieldType, collectionType, p.getValue()));
			}
			return new PatientDataCalculation(dd);
		}
		catch (Exception e) {
			throw new InvalidCalculationException("Unable to construct calculation from Data Definition Class name: " + calculationName);
		}
	}
}
