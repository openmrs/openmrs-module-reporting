/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
 * {@link CalculationProvider} used to expose Patient and Person data definitions as calculations
 */
@Handler(supports = PatientDataCalculation.class, order = 50)
public class PatientDataCalculationProvider implements CalculationProvider {
	
	/**
	 * Creates a calculation instances for the matching data definitions by loading the data
	 * definition, if none is found, a saved instance is looked up
	 * 
	 * @see CalculationProvider#getCalculation(String, String)
	 */
	@SuppressWarnings("unchecked")
	public PatientDataCalculation getCalculation(String calculationName, String configuration)
	    throws InvalidCalculationException {
		PatientDataCalculation c = new PatientDataCalculation();
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
				
				dd.addParameter(new Parameter(p.getField().getName(), p.getDisplayName(), fieldType, collectionType, p
				        .getValue()));
			}
			
			c.setDataDefinition(dd);
		}
		catch (Exception e) {
			// If we are unable to instantiate a new class, try loading a saved instance
			List<? extends DataDefinition> l = Context.getService(PatientDataService.class).getDefinitions(calculationName,
			    true);
			if (l.isEmpty()) {
				l = Context.getService(PersonDataService.class).getDefinitions(calculationName, true);
			}
			if (l.size() == 1) {
				c.setDataDefinition(l.get(0));
			} else {
				throw new InvalidCalculationException("Unable to load Data Definition from calculationName: "
				        + calculationName);
			}
		}
		
		return c;
	}
}
