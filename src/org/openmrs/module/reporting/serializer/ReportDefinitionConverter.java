package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Defines how ReportDefinitions should be converted
 */
public class ReportDefinitionConverter extends ReportingShortConverter {
	
	/**
	 * Constructor
	 */
	public ReportDefinitionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	/**
	 * @see ReportingShortConverter#getDefinitionType()
	 */
	@Override
	public Class<? extends Definition> getDefinitionType() {
		return ReportDefinition.class;
	}

	/**
	 * @see ReportingShortConverter#getDefinitionService()
	 */
	@Override
	public DefinitionService<?> getDefinitionService() {
		return Context.getService(ReportDefinitionService.class);
	}	
}