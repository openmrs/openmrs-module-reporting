/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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
