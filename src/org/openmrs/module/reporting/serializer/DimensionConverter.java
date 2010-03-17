package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class DimensionConverter extends ReportingShortConverter implements Converter {
	
	public DimensionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(DimensionService.class).getDefinitionByUuid(uuid);
	}
	
	public boolean canConvert(Class c) {
		return Dimension.class.isAssignableFrom(c);
	}
	
}
