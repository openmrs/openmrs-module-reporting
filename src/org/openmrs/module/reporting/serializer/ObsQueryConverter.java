package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Defines how ObsQuerys should be converted
 */
public class ObsQueryConverter extends ReportingShortConverter {
	
	/**
	 * Constructor
	 */
	public ObsQueryConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	/**
	 * @see ReportingShortConverter#getDefinitionType()
	 */
	@Override
	public Class<? extends Definition> getDefinitionType() {
		return ObsQuery.class;
	}

	/**
	 * @see ReportingShortConverter#getDefinitionService()
	 */
	@Override
	public DefinitionService<?> getDefinitionService() {
		return Context.getService(ObsQueryService.class);
	}	
}
