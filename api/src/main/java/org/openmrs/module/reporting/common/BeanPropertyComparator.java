/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import java.util.Comparator;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * A comparator that can compare 2 objects based on a particular nested property value
 */
@SuppressWarnings("rawtypes")
public class BeanPropertyComparator implements Comparator {
	
	private String sortSpecification;
	
	/**
	 * Constructor
	 */
	public BeanPropertyComparator(String sortSpecification) {
		this.sortSpecification = sortSpecification;
	}

	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public int compare(Object left, Object right) {
		try {	
			for (String token : sortSpecification.split(",")) {	
				String[] values = token.trim().split(" ");
				String property = values[0];
				boolean sortAsc = (values.length == 1 || !values[1].equalsIgnoreCase("desc"));

				Object valueLeft = PropertyUtils.getNestedProperty(left, property);
				Object valueRight = PropertyUtils.getNestedProperty(right, property);
				
				//We put NULLs at the bottom.
				if (valueLeft == null)
					return 1;
				
				if (valueRight == null)
					return -1;
				
				if (!valueLeft.equals(valueRight)) {
					int ret = ((Comparable)valueLeft).compareTo(valueRight);
					if (!sortAsc) {
						ret = (ret == 1 ? -1 : 1);
					}
					return ret;
				}
				//else values are equal. Try next sort property
			}
		}
		catch (Exception ex) {
			throw new IllegalArgumentException("Unable to compare " + left + " and " + right + " using sort specification: " + sortSpecification);
		}
		
		return 0;
	}
}
