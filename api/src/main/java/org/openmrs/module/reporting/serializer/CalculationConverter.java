package org.openmrs.module.reporting.serializer;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.definition.PatientCalculationDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Converter for {@link org.openmrs.calculation.Calculation} objects
 */
public class CalculationConverter extends ReportingShortConverter {

	/**
	 * Constructor
	 */
	public CalculationConverter(Mapper mapper, ConverterLookup converterLookup) {
		super(mapper, converterLookup);
	}

	/**
	 * @see ReportingShortConverter#getDefinitionType()
	 */
	@Override
	public Class<? extends Definition> getDefinitionType() {
		return PatientCalculationDataDefinition.class;
	}

	/**
	 * @see ReportingShortConverter#getDefinitionService()
	 */
	@Override
	public DefinitionService<?> getDefinitionService() {
		return Context.getService(PatientDataService.class);
	}
}
