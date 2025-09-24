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

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Defines how PersonDataDefinitions should be converted
 */
public class PersonDataDefinitionConverter extends ReportingShortConverter {
	
	/**
	 * Constructor
	 */
	public PersonDataDefinitionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }
	
	/**
	 * @see ReportingShortConverter#getDefinitionType()
	 */
	@Override
	public Class<? extends Definition> getDefinitionType() {
		return PersonDataDefinition.class;
	}

	/**
	 * @see ReportingShortConverter#getDefinitionService()
	 */
	@Override
	public DefinitionService<?> getDefinitionService() {
		return Context.getService(PersonDataService.class);
	}	
}