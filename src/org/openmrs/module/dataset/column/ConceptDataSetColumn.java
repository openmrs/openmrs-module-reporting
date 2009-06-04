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
package org.openmrs.module.dataset.column;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

/**
 * Simple Implementation of a DataSetColumn
 */
public class ConceptDataSetColumn implements DataSetColumn {
	
	private Concept concept;
	
	
	/**
	 * Default Constructor
	 */
	public ConceptDataSetColumn(Concept concept) { 
		this.concept = concept;
	} 
	
	
    /**
     * @return the key
	 * TODO Test when short name does not exist for a given concept
	 * TODO Figure out the best way to get the short name for a concept
     */
    public String getKey() {
    	return concept.getName() != null ? 
    			concept.getName().getName() : 
    				"" + concept.getConceptId();    
    }
	
	/**
	 * Gets the preferred name in the current locale (or 
	 * @return the columnName
	 * TODO Test when short name does not exist for a given concept
	 * TODO Figure out the best way to get the short name for a concept
	 */
	public String getColumnName() {	    
    	return concept.getName() != null ? 
    			concept.getName().getName() : 
    				"" + concept.getConceptId();
		/*
		return concept.getName() != null ?
    			concept.getName().getShortestName() :
    				"" + concept.getConceptId();
    	*/
	}

	/**
     * @return the description
     */
    public String getDescription() {
    	return concept.getDescription() != null ?
    			concept.getDescription().getDescription() :
    				"" + concept.getConceptId();
    }
	
    /**
     * TODO Need to convert concept data type to Class
     * @return the dataType
     */
    public Class<?> getDataType() {
    	return String.class;
    }

    /**
     * @return the concept 
     */
    public Concept getConcept() { 
    	return this.concept;
    }
    
    
	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return concept.toString();
    }

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
   		if (obj instanceof DataSetColumn) {
   			DataSetColumn col = (DataSetColumn) obj;
			if (StringUtils.equals(getKey(), col.getKey())) {
				return true;
			}
		}
		return false;
    }

	/**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (getKey() == null ? 0 : getKey().hashCode());
		return hash;
    }

    /**
     * Compares columns by their name.  
     */
	public int compareTo(DataSetColumn other) {		
		return this.getColumnName().compareTo(other.getColumnName());		
	}
}