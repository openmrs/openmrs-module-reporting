package org.openmrs.module.reporting.serializer;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Defines how PatientDataDefinitions should be converted
 */
public class PatientDataDefinitionConverter extends ReportingShortConverter {
	
	/**
	 * Constructor
	 */
	public PatientDataDefinitionConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }

	/**
	 * @see ReportingShortConverter#getDefinitionType()
	 */
	@Override
	public Class<? extends Definition> getDefinitionType() {
		return PatientDataDefinition.class;
	}

	/**
	 * @see ReportingShortConverter#getDefinitionService()
	 */
	@Override
	public DefinitionService<?> getDefinitionService() {
		return Context.getService(PatientDataService.class);
	}	
}