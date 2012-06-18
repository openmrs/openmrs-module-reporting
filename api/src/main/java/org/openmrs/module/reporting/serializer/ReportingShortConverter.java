package org.openmrs.module.reporting.serializer;

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.serialization.xstream.converter.BaseShortConverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Abstract class that defines how reporting objects should be serialized
 */
public abstract class ReportingShortConverter extends BaseShortConverter implements Converter {
	
	/**
	 * Constructor
	 */
	public ReportingShortConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }
	
	/**
	 * @return the Definition type that this converter needs to use
	 */
	public abstract Class<? extends Definition> getDefinitionType();
	
	/**
	 * @return the DefinitionService that this converter needs to use
	 */
	public abstract DefinitionService<?> getDefinitionService();
	
	/**
	 * @see BaseShortConverter#getByUUID(java.lang.String)
	 */
	public Object getByUUID(String uuid) {
		return getDefinitionService().getDefinitionByUuid(uuid);
	}
	
	/**
	 * @see ConverterMatcher#canConvert(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class c) {
		return getDefinitionType().isAssignableFrom(c);
	}
}
