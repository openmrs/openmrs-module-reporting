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
package org.openmrs.module.dataset.column;

import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;

/**
 * Simple Implementation of a DataSetColumn
 */
public class IndicatorDataSetColumn extends SimpleDataSetColumn {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The indicator backing this column
	 */
	private Mapped<CohortIndicator> indicator;
	

	/**
	 * Public Constructor
	 */
	public IndicatorDataSetColumn(String columnKey, String displayName, String description, Class<?> dataType, Mapped<CohortIndicator> indicator) {
		super(columnKey, displayName, description, dataType);
		this.indicator = indicator;
	} 
	
	/**
	 * Public Constructor
	 */
	public IndicatorDataSetColumn(String columnKey, String displayName, Class<?> dataType, Mapped<CohortIndicator> indicator) {
		this(columnKey, displayName, null, dataType, indicator);
	} 
	
    /**
     * @return the concept 
     */
    public Mapped<CohortIndicator> getIndicator() { 
    	return this.indicator;
    }
    
}