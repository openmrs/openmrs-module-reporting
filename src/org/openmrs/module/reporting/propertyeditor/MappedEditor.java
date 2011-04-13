package org.openmrs.module.reporting.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.serialization.SerializationException;

/**
 * Converts between a {@link Mapped} and a String representation
 */
public class MappedEditor extends PropertyEditorSupport {
	
	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
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
	
	/**
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
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
