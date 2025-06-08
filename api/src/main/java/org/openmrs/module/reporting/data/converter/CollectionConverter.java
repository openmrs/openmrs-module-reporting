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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Collection converter
 */
public class CollectionConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private DataConverter itemConverter;
	private Boolean removeDuplicates;
	private Comparator<Object> orderComparator;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public CollectionConverter() { }
	
	/**
	 * Full Constructor
	 */
	public CollectionConverter(DataConverter itemConverter, Boolean removeDuplicates, Comparator<Object> orderComparator) {
		this.itemConverter = itemConverter;
		this.removeDuplicates = removeDuplicates;
		this.orderComparator = orderComparator;
	}
	
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#converter(Object)
	 * @should convert a Date into a String with the passed format
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object original) {
		if (original != null) {
			Collection c = (removeDuplicates ? new HashSet() : new ArrayList());
			for (Object o : ((Collection)original)) {
				c.add(itemConverter == null ? o : itemConverter.convert(o));
			}
			if (orderComparator != null) {
				List<Object> l = new ArrayList<Object>(c);
				Comparator<Object> comparator = (Comparator<Object>)orderComparator;
				Collections.sort(l, comparator);
				c = l;
			}
			return c;
		}
		return null;
	}

	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return Collection.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Collection.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the itemConverter
	 */
	public DataConverter getItemConverter() {
		return itemConverter;
	}

	/**
	 * @param itemConverter the itemConverter to set
	 */
	public void setItemConverter(DataConverter itemConverter) {
		this.itemConverter = itemConverter;
	}

	/**
	 * @return the removeDuplicates
	 */
	public Boolean getRemoveDuplicates() {
		return removeDuplicates;
	}

	/**
	 * @param removeDuplicates the removeDuplicates to set
	 */
	public void setRemoveDuplicates(Boolean removeDuplicates) {
		this.removeDuplicates = removeDuplicates;
	}

	/**
	 * @return the orderComparator
	 */
	public Comparator<Object> getOrderComparator() {
		return orderComparator;
	}

	/**
	 * @param orderComparator the orderComparator to set
	 */
	public void setOrderComparator(Comparator<Object> orderComparator) {
		this.orderComparator = orderComparator;
	}
}