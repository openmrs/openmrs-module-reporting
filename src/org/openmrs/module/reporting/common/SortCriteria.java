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
	
	public class SortElement {
		
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
