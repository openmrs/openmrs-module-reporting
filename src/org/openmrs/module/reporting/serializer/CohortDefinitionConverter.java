package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.serialization.xstream.converter.BaseShortConverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class CohortDefinitionConverter extends ReportingShortConverter implements Converter {
	
	public CohortDefinitionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(CohortDefinitionService.class).getCohortDefinitionByUuid(uuid);
	}
	
	public boolean canConvert(Class c) {
		return CohortDefinition.class.isAssignableFrom(c);
	}
	
}
