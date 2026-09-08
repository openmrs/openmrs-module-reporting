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

/**
 * A dimension category is a categorical breakdown of a dimension.  For instance, within the
 * GENDER dimension, there are three dimension categories (male, female, unknown) that cover
 * the entire spectrum of that dimension.  The dimension categories for a dimension
 * MUST cover the values within a group in a non-overlapping manner.  In other words, a
 * data point cannot exist in multiple dimension categories.  For instance, a person, cannot
 * be both an adult and a child as the category must be defined in such a way that all data
 * points fall in one category.
 */
public interface DimensionCategory {
	
}
