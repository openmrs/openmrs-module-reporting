package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Defines how Indicators should be converted
 */
public class IndicatorConverter extends ReportingShortConverter {
	
	/**
	 * Constructor
	 */
	public IndicatorConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	/**
	 * @see ReportingShortConverter#getDefinitionType()
	 */
	@Override
	public Class<? extends Definition> getDefinitionType() {
		return Indicator.class;
	}

	/**
	 * @see ReportingShortConverter#getDefinitionService()
	 */
	@Override
	public DefinitionService<?> getDefinitionService() {
		return Context.getService(IndicatorService.class);
	}	
}