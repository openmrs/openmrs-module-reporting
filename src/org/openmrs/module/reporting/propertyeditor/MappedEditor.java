package org.openmrs.module.reporting.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.serialization.SerializationException;


public class MappedEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isNotBlank(text)) {
			try {
				setValue(Context.getSerializationService().deserialize(text, Mapped.class, ReportingSerializer.class));
			} catch (SerializationException ex) {
				throw new IllegalArgumentException(ex);
			}
		}
	}
	
	@Override
	public String getAsText() {
	    Mapped<?> mapped = (Mapped<?>) getValue();
	    if (mapped != null) {
	    	try {
	            return Context.getSerializationService().serialize(mapped, ReportingSerializer.class);
            } catch (SerializationException ex) {
	            throw new RuntimeException(ex);
            }
	    } else {
	    	return "";
	    }
	}
	
}
