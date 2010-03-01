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
package org.openmrs.module.reporting.dataset.transform;

import org.openmrs.module.reporting.dataset.DataSet;

/**
 * Defines an object that is capable of performing a transformation 
 * over a dataset, returning a transformed dataset.  
 * 
 * @see org.openmrs.module.reporting.dataset.DataSet
 */
public interface DataSetTransform {
	
	/**
	 * Returns a transformed dataset.
	 * 
	 * @param dataSet	the dataset to be transformed
	 * @return	a transformed dataset.
	 */
	@SuppressWarnings("unchecked")
	public DataSet transform(DataSet dataSet);
	
}
