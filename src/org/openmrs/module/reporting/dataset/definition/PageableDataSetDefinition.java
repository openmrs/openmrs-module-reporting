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
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.dataset.DataSetMetaData;
import org.openmrs.module.reporting.dataset.PageableDataSet;

/**
 * <p>Extends {@link DataSetDefinition} to further specify that if you break a cohort into
 * complete non-overlapping subcohorts, then the union of the DataSetRows you get from
 * evaluating each subcohort is equal to the DataSet you would get by evaluating the
 * original cohort.</p>
 * 
 * <p>Evaluating a PageableDataSetDefinition gets you a {@link PageableDataSet}.
 * Usually this evaluation will be fast, because work is deferred until you request rows
 * from the resulting dataset, and usually requesting a subset of rows from that data set
 * will be faster than requesting all its rows. But implementations of
 * PageableDataSetDefinition are not required to behave that way.</p> 
 * 
 * <p>A definition that produces one row per patient <em>should</em> implement this
 * interface, signaling that it can be evaluated on a page of patients at a time.</p>
 * 
 * <p>A definition that counts how many patients in the input cohort meet some criteria
 * should <em>not</em> implement this interface, because the count returned for a single
 * 'page' would be different from the count for the whole input cohort.</p>  
 */
public interface PageableDataSetDefinition extends DataSetDefinition {

	/**
	 * @return metadata describing the columns you'll get when you evaluate this data set definition
	 */
	DataSetMetaData getDataSetMetadata();
		
}
