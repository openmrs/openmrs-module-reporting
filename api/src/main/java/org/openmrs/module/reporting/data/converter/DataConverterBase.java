/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.data.converter;

/**
 * Base converter, to provide a simple means of formatting Data values
 */
public abstract class DataConverterBase implements DataConverter {
	
	/**
	 * Converter the passed object from one datatype to another
	 */
	protected abstract Object convertObject(Object original);

    @Override
    public final Object convert(Object original) {
        try {
            return convertObject(original);
        }
        catch (Exception e) {
            throw new ConversionException(original, this.getClass(), e);
        }
    }

}