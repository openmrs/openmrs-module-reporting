package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReportDefinitionConverter extends ReportingShortConverter implements Converter {
	
	public ReportDefinitionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
	}
	
	public boolean canConvert(Class c) {
		return ReportDefinition.class.isAssignableFrom(c);
	}
	
}
