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
