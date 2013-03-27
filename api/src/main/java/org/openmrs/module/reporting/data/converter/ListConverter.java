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
package org.openmrs.module.reporting.data.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;

/**
 * List data converter
 */
public class ListConverter implements DataConverter {

	// ***** PROPERTIES *****

	private TimeQualifier whichItems;
	private Integer maxNumberOfItems;
	private Class<?> typeOfItem;

	// ***** CONSTRUCTORS *****

	public ListConverter() {
	}

	/**
	 * Full Constructor
	 */
	public ListConverter(TimeQualifier whichItems, Integer maxNumberOfItems,
			Class<?> typeOfItem) {
		this.whichItems = whichItems;
		this.maxNumberOfItems = maxNumberOfItems;
		this.typeOfItem = typeOfItem;
	}

	// ***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#converter(Object)
	 * @should convert a Date into a String with the passed format
	 */
	@SuppressWarnings("rawtypes")
	public Object convert(Object original) {
		List l = (List) original;
		try {
			if (l != null) {
				TimeQualifier which = ObjectUtil.nvl(whichItems,
						TimeQualifier.ANY);
				int max = (maxNumberOfItems == null
						|| maxNumberOfItems > l.size() ? l.size()
						: maxNumberOfItems);
				if (which != TimeQualifier.FIRST) {
					Collections.reverse(l);
				}
				if (max == 1) {
					return l.get(0);
				} else {
					List<Object> ret = new ArrayList<Object>();
					for (int i = 0; i <= max; i++) {
						ret.add(l.get(i));
					}
					return ret;
				}
			}
		} catch (Exception e) {
			throw new ConversionException("Unable to convert Date" + original
					+ "into a String with the passed format, due to: " + e, e);
		}
		return null;
	}

	/**
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		if (maxNumberOfItems == null || maxNumberOfItems > 1) {
			return List.class;
		}
		return typeOfItem;
	}

	/**
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return List.class;
	}

	// ***** PROPERTY ACCESS *****

	/**
	 * @return the whichItems
	 */
	public TimeQualifier getWhichItems() {
		return whichItems;
	}

	/**
	 * @param whichItems
	 *            the whichItems to set
	 */
	public void setWhichItems(TimeQualifier whichItems) {
		this.whichItems = whichItems;
	}

	/**
	 * @return the maxNumberOfItems
	 */
	public Integer getMaxNumberOfItems() {
		return maxNumberOfItems;
	}

	/**
	 * @param maxNumberOfItems
	 *            the maxNumberOfItems to set
	 */
	public void setMaxNumberOfItems(Integer maxNumberOfItems) {
		this.maxNumberOfItems = maxNumberOfItems;
	}

	/**
	 * @return the typeOfItem
	 */
	public Class<?> getTypeOfItem() {
		return typeOfItem;
	}

	/**
	 * @param typeOfItem
	 *            the typeOfItem to set
	 */
	public void setTypeOfItem(Class<?> typeOfItem) {
		this.typeOfItem = typeOfItem;
	}
}