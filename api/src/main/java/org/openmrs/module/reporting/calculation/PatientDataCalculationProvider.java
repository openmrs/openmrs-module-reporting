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
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.module.htmlwidgets.util.ReflectionUtil;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * Evaluates a {@link PatientDataCalculation} to produce a Result
 */
@Handler(supports=PatientDataCalculation.class, order=50)
public class PatientDataCalculationProvider implements CalculationProvider {

	/**
	 * @see CalculationProvider#getCalculation(String, String)
	 */
	@SuppressWarnings("unchecked")
	public Calculation getCalculation(String calculationName, String configuration) throws InvalidCalculationException {
		PatientDataCalculation c = new PatientDataCalculation();
		try {
			Class<? extends DataDefinition> clazz = (Class<? extends DataDefinition>)Context.loadClass(calculationName);
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
			c.setDataDefinition(dd);
		}
		catch (Exception e) {
			// If we are unable to instantiate a new class, try loading a saved instance
			List<? extends DataDefinition> l = Context.getService(PatientDataService.class).getDefinitions(calculationName, true);
			if (l.isEmpty()) {
				l = Context.getService(PersonDataService.class).getDefinitions(calculationName, true);
			}
			if (l.size() == 1) {
				c.setDataDefinition(l.get(0));
			}
			else {
				throw new InvalidCalculationException("Unable to load Data Definition from calculationName: " + calculationName);
			}
		}
		c.setConfiguration(configuration);
		return c;
	}
}
