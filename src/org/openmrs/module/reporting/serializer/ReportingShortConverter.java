package org.openmrs.module.reporting.serializer;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.serialization.xstream.converter.BaseShortConverter;
import org.openmrs.module.serialization.xstream.converter.CustomCGLIBEnhancedConverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


public abstract class ReportingShortConverter extends BaseShortConverter {
	
	public ReportingShortConverter(Mapper mapper, ConverterLookup converterLookup) {
	    super(mapper, converterLookup);
    }
	
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
		boolean hasUuid = false;
		try {
			hasUuid = ((OpenmrsObject) obj).getUuid() != null;
		} catch (Exception ex) { }
		if (needsFullSeralization(context) || !hasUuid) {
			if (isCGLibProxy(obj.getClass())) {
				CustomCGLIBEnhancedConverter converter = new CustomCGLIBEnhancedConverter(getMapper(), getConverterLookup());
				converter.marshal(obj, writer, context);
			} else {
				Converter defaultConverter = getConverterLookup().lookupConverterForType(Object.class);
				defaultConverter.marshal(obj, writer, context);
			}
		} else {
			/*
			 * Here cast "obj" to "BaseOpenmrsObject", so that any short converter extending form BaseShortConverter
			 * can directly use this marshal method to serialize "obj".	
			 */
			writer.addAttribute("uuid", ((BaseOpenmrsObject) obj).getUuid());
		}
	}
	
	@Override
	protected boolean needsFullDeserialization(HierarchicalStreamReader reader) {
		String uuid = reader.getAttribute("uuid");
		if (uuid == null)
			return true;
		else
			return super.needsFullDeserialization(reader);
	}
}
