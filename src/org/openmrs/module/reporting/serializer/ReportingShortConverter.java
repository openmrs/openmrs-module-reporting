package org.openmrs.module.reporting.serializer;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.serialization.xstream.converter.BaseShortConverter;
import org.openmrs.module.serialization.xstream.converter.CustomCGLIBEnhancedConverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
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
	
	/**
	 * @see BaseShortConverter#marshal(Object, HierarchicalStreamWriter, MarshallingContext)
	 */
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
		
		boolean hasUuid = false;
		String uuid = null;
		try {
			uuid = ((OpenmrsObject) obj).getUuid();	
			hasUuid = (uuid != null);
		} 
		catch (Exception ex) {}
		
		if (hasUuid && obj instanceof OpenmrsObject){
			hasUuid = getByUUID(uuid) == null ? false : true;
			if (hasUuid == false && !needsFullSeralization(context)){  //needsFullSerialization returns true of object is the parent.
				((OpenmrsObject) obj).setUuid(null);
			}		
		}

		if (needsFullSeralization(context) || !hasUuid) {
			if (isCGLibProxy(obj.getClass())) {
				CustomCGLIBEnhancedConverter converter = new CustomCGLIBEnhancedConverter(getMapper(), getConverterLookup());
				converter.marshal(obj, writer, context);
			} 
			else {
				Converter defaultConverter = getConverterLookup().lookupConverterForType(Object.class);
				defaultConverter.marshal(obj, writer, context);
			}
		} 
		else {
			/*
			 * Here cast "obj" to "BaseOpenmrsObject", so that any short converter extending form BaseShortConverter
			 * can directly use this marshal method to serialize "obj".	
			 */
			writer.addAttribute("uuid", ((BaseOpenmrsObject) obj).getUuid());
		}
	}
}
