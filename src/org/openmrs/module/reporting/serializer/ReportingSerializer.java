package org.openmrs.module.reporting.serializer;

import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.serialization.SerializationException;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReportingSerializer extends XStreamShortSerializer {

	/**
	 * @throws SerializationException
	 * @should serialize a cohort definition
	 * @should serialize an indicator that contains a persisted cohort definition
	 * @should serialize an indicator that contains an unsaved cohort definition
	 */
	public ReportingSerializer() throws SerializationException {
	    super();

	    Mapper mapper = xstream.getMapper();
	    ConverterLookup converterLookup = xstream.getConverterLookup();

	    xstream.registerConverter(new CohortDefinitionConverter(mapper, converterLookup));
	    xstream.registerConverter(new DataSetDefinitionConverter(mapper, converterLookup));
	    xstream.registerConverter(new ReportDefinitionConverter(mapper, converterLookup));
	    xstream.registerConverter(new IndicatorConverter(mapper, converterLookup));
	    xstream.registerConverter(new DimensionConverter(mapper, converterLookup));
	}

}
