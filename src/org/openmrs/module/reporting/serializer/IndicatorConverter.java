package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class IndicatorConverter extends ReportingShortConverter implements Converter {
	
	public IndicatorConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
	}
	
	public boolean canConvert(Class c) {
		return Indicator.class.isAssignableFrom(c);
	}
	
}
