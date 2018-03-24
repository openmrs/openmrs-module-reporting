/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
	
	//***** PROPERTIES *****
	
	private TimeQualifier whichItems;
	private Integer maxNumberOfItems;
	private Integer specificItemIndex; // 0-based index of specific item in List to return
	private Class<?> typeOfItem;
	
	//***** CONSTRUCTORS *****
	
	public ListConverter() { }
	
	/**
	 * Full Constructor
	 */
	public ListConverter(TimeQualifier whichItems, Integer maxNumberOfItems, Class<?> typeOfItem) {
		this.whichItems = whichItems;
		this.maxNumberOfItems = maxNumberOfItems;
		this.typeOfItem = typeOfItem;
	}

	/**
	 * Full Constructor
	 */
	public ListConverter(Integer specificItemIndex, Class<?> typeOfItem) {
		this.specificItemIndex = specificItemIndex;
		this.typeOfItem = typeOfItem;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 * @should convert a Date into a String with the passed format
	 */
	@SuppressWarnings("rawtypes")
	public Object convert(Object original) {
		List l = (List) original;
		if (l != null) {
			l = new ArrayList(l);
			// First handle the case where a specific item from the list is requested by index
			if (specificItemIndex != null) {
				return (l.size() > specificItemIndex ? l.get(specificItemIndex) : null);
			}

			// Next, handle the case where a certain number of items from the beginning or end of the list is requested
			TimeQualifier which = ObjectUtil.nvl(whichItems, TimeQualifier.ANY);
			int max = (maxNumberOfItems == null || maxNumberOfItems > l.size() ? l.size() : maxNumberOfItems);
			if (which != TimeQualifier.FIRST) {
				Collections.reverse(l);
			}
			if (max == 1) {
				return l.get(0);
			}
			else {
				List<Object> ret = new ArrayList<Object>();
				for (int i=0; i<max; i++) {
					ret.add(l.get(i));
				}
				return ret;				
			}
		}
		return null;
	}

	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		if (specificItemIndex == null) {
			if (maxNumberOfItems == null || maxNumberOfItems > 1) {
				return List.class;
			}
		}
		return typeOfItem;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return List.class;
	}
	
	//***** PROPERTY ACCESS *****

	public TimeQualifier getWhichItems() {
		return whichItems;
	}

	public void setWhichItems(TimeQualifier whichItems) {
		this.whichItems = whichItems;
	}

	public Integer getMaxNumberOfItems() {
		return maxNumberOfItems;
	}

	public void setMaxNumberOfItems(Integer maxNumberOfItems) {
		this.maxNumberOfItems = maxNumberOfItems;
	}

	public Integer getSpecificItemIndex() {
		return specificItemIndex;
	}

	public void setSpecificItemIndex(Integer specificItemIndex) {
		this.specificItemIndex = specificItemIndex;
	}

	public Class<?> getTypeOfItem() {
		return typeOfItem;
	}

	public void setTypeOfItem(Class<?> typeOfItem) {
		this.typeOfItem = typeOfItem;
	}
}