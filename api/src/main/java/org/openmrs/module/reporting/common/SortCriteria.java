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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This construct is meant to represent one or more sort criteria for sorting elements of a complex object
 */
public class SortCriteria {

	//***** PROPERTIES ******
	
	private List<SortElement> sortElements;
	
	//***** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public SortCriteria() {	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Add a new Sort Element with the given name and direction
	 */
	public void addSortElement(String elementName, SortDirection direction) {
		getSortElements().add(new SortElement(elementName, direction));
	}
	
	/**
	 * Get the sort element with the given name;
	 */
	public SortElement getSortElement(String elementName) {
		for (SortElement e : getSortElements()) {
			if (ObjectUtil.areEqual(e.getElementName(), elementName)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Removes a new Sort Element with the given name
	 */
	public void removeSortElement(String elementName) {
		for (Iterator<SortElement> i = getSortElements().iterator(); i.hasNext();) {
			SortElement e = i.next();
			if (ObjectUtil.areEqual(e.getElementName(), elementName)) {
				i.remove();
			}
		}
	}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the sortElements
	 */
	public List<SortElement> getSortElements() {
		if (sortElements == null) {
			sortElements = new ArrayList<SortElement>();
		}
		return sortElements;
	}
	
	/**
	 * @param sortElements the sortElements to set
	 */
	public void setSortElements(List<SortElement> sortElements) {
		this.sortElements = sortElements;
	}
	
	//***** ENUMS *****

	/**
	 * This enum is meant to represent the possible sort directions of ascending and descending
	 */
	public enum SortDirection {
		ASC, DESC
	}
	
	//***** INNER CLASSES *****
	
	public class SortElement implements Serializable {

		public static final long serialVersionUID = 1L;

		//***** PROPERTIES *****

		private String elementName;
		private SortDirection direction;
		
		//***** CONSTRUCTORS *****
		
		/**
		 * Default Constructor
		 */
		public SortElement() {}
		
		/**
		 * Full Constructor
		 */
		public SortElement(String elementName, SortDirection direction) {
			this.elementName = elementName;
			this.direction = direction;
		}
		
		//***** PROPERTY ACCESS *****

		/**
		 * @return the elementName
		 */
		public String getElementName() {
			return elementName;
		}

		/**
		 * @param elementName the elementName to set
		 */
		public void setElementName(String elementName) {
			this.elementName = elementName;
		}

		/**
		 * @return the direction
		 */
		public SortDirection getDirection() {
			return direction;
		}

		/**
		 * @param direction the direction to set
		 */
		public void setDirection(SortDirection direction) {
			this.direction = direction;
		}
	}
}
