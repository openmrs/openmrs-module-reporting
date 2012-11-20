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
