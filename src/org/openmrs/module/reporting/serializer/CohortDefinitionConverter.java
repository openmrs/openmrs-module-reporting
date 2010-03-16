package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class CohortDefinitionConverter extends ReportingShortConverter implements Converter {
	
	public CohortDefinitionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(CohortDefinitionService.class).getDefinitionByUuid(uuid);
	}
	
	public boolean canConvert(Class c) {
		return CohortDefinition.class.isAssignableFrom(c);
	}
	
}
