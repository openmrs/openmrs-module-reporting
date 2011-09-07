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
package org.openmrs.module.reporting.dataset.column.definition;

import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Base Column Interface
 */
public interface ColumnDefinition extends Definition {
    
    /**
     * @return the converter to apply to this Column
     */
    public ColumnConverter getConverter();
    
    /**
     * @return the data type for this Column, prior to Conversion
     */
    public Class<?> getRawDataType();
    
    /**
     * @return the data type for this Column after any Conversion
     */
    public Class<?> getDataType();
}