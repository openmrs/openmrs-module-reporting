/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
