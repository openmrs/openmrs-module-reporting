package org.openmrs.module.reporting.serializer;

import org.openmrs.module.indicator.dimension.Dimension;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class DimensionConverter extends ReportingShortConverter implements Converter {
	
	public DimensionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	@Override
	public Object getByUUID(String uuid) {
		throw new RuntimeException("Not Yet Implemented");
	}
	
	public boolean canConvert(Class c) {
		return Dimension.class.isAssignableFrom(c);
	}
	
}
