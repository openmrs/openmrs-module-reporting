/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.serializer;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationRegistration;
import org.openmrs.calculation.api.CalculationRegistrationService;
import org.openmrs.module.reporting.data.patient.definition.PatientCalculationDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.serialization.xstream.converter.BaseShortConverter;

/**
 * Converter for {@link org.openmrs.calculation.Calculation} objects
 */
public class CalculationRegistrationShortConverter extends BaseShortConverter {

	/**
	 * Constructor
	 */
	public CalculationRegistrationShortConverter(Mapper mapper, ConverterLookup converterLookup) {
		super(mapper, converterLookup);
	}

	/**
	 * populates a CalculationRegistration from its uuid
	 *
	 * @see BaseShortConverter#getByUUID(String)
	 */
	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(CalculationRegistrationService.class).getCalculationRegistrationByUuid(uuid);
	}

	/**
	 * @see BaseShortConverter#canConvert(Class)
	 */
	@Override
	public boolean canConvert(Class c) {
		return CalculationRegistration.class.isAssignableFrom(c);
	}
}
