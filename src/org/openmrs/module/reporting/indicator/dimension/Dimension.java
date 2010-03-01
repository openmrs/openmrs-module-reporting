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
package org.openmrs.module.reporting.indicator.dimension;

import java.util.Arrays;
import java.util.List;

import org.openmrs.module.reporting.evaluation.Definition;

/**
 * A Dimension represents the ability to produce classifications that cover an entire spectrum of values
 */
public interface Dimension extends Definition {
	
	/**
	 * Reserved Dimension Option Key that represents all elements
	 */
	public final static String ALL = "*";
	
	/**
	 * Reserved Dimension Option Key that represents unclassified elements
	 */
	public final static String UNCLASSIFIED = "?";
	
	/**
	 * When evaluating an indicator for a given set of Dimensions,
	 * this is the separator character to use to separate Dimensions
	 */
	public final static String DIMENSION_SEPARATOR = ",";
	
	/**
	 * When evaluating an indicator for a given set of Dimensions,
	 * this is the separator character to use to separate the name and value of a Dimension
	 */
	public final static String OPTION_SEPARATOR = "=";
	
	/**
	 * List of all <em>Reserved</em> Dimension Options Keys
	 */
	public final static List<String> RESERVED_WORDS = 
		Arrays.asList(ALL, UNCLASSIFIED, DIMENSION_SEPARATOR, OPTION_SEPARATOR);
	
	/**
	 * @return a List of all Option keys available for this Dimension.
	 */
	public List<String> getOptionKeys();
}
