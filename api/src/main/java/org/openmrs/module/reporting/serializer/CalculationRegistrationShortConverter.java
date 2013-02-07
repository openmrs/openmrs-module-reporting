package org.openmrs.module.reporting.serializer;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationRegistration;
import org.openmrs.calculation.api.CalculationRegistrationService;
import org.openmrs.module.reporting.data.patient.definition.PatientCalculationDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.serialization.xstream.converter.BaseShortConverter;

/**
 * Converter for {@link org.openmrs.calculation.Calculation} objects
 */
public class CalculationRegistrationShortConverter extends BaseShortConverter {

	/**
	 * Constructor
	 */
	public CalculationRegistrationShortConverter(Mapper mapper, ConverterLookup converterLookup) {
		super(mapper, converterLookup);
	}

	/**
	 * hydrates a CalculationRegistration from its uuid
	 *
	 * @see BaseShortConverter#getByUUID(String)
	 */
	@Override
	public Object getByUUID(String uuid) {
		return Context.getService(CalculationRegistrationService.class).getCalculationRegistrationByUuid(uuid);
	}

	/**
	 * @see BaseShortConverter#canConvert(Class)
	 */
	@Override
	public boolean canConvert(Class c) {
		return CalculationRegistration.class.isAssignableFrom(c);
	}
}
