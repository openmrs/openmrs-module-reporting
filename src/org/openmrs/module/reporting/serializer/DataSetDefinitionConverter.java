package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class DataSetDefinitionConverter extends ReportingShortConverter implements Converter {
	
	public DataSetDefinitionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(uuid);
	}
	
	public boolean canConvert(Class c) {
		return DataSetDefinition.class.isAssignableFrom(c);
	}
	
}
