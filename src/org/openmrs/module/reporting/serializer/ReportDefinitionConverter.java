package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReportDefinitionConverter extends ReportingShortConverter implements Converter {
	
	public ReportDefinitionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);
	}
	
	public boolean canConvert(Class c) {
		return ReportDefinition.class.isAssignableFrom(c);
	}
	
}
