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
package org.openmrs.module.cohort.definition;

import java.util.Iterator;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class EncounterCohortDefinition extends DateRangeCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=false)
	private List<EncounterType> encounterTypeList;
	
	@ConfigurationProperty(required=false)
	private Form form;
	
	@ConfigurationProperty(required=false)
	private Integer atLeastCount;
	
	@ConfigurationProperty(required=false)
	private Integer atMostCount;
	
	@ConfigurationProperty(required=false)
	private Location location;

	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public EncounterCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		StringBuffer ret = new StringBuffer();
		ret.append("Patients with ");
		if (atLeastCount != null || atMostCount != null) {
			if (atLeastCount != null) {
				ret.append("at least " + atLeastCount + " ");
			}
			if (atMostCount != null) {
				ret.append("at most " + atMostCount + " ");
			}
		}
		else {
			ret.append("any ");
		}
		if (encounterTypeList != null) {
			ret.append("[");
			for (Iterator<EncounterType> i = encounterTypeList.iterator(); i.hasNext();) {
				ret.append(" " + i.next().getName() + (i.hasNext() ? " , " : ""));
			}
			ret.append(" ] ");
		}
		ret.append("encounters ");
		if (form != null) {
			ret.append("from form " + form.getName() + " ");
		}
		if (location != null) {
			ret.append("at " + location.getName() + " ");
		}
		ret.append(getDateRangeDescription());
		return ret.toString();
	}

	
    /**
     * @return the encounterTypeList
     */
    public List<EncounterType> getEncounterTypeList() {
    	return encounterTypeList;
    }
	
    /**
     * @param encounterTypeList the encounterTypeList to set
     */
    public void setEncounterTypeList(List<EncounterType> encounterTypeList) {
    	this.encounterTypeList = encounterTypeList;
    }

    /**
     * @return the form
     */
    public Form getForm() {
    	return form;
    }

    /**
     * @param form the form to set
     */
    public void setForm(Form form) {
    	this.form = form;
    }
	
    /**
     * @return the atLeastCount
     */
    public Integer getAtLeastCount() {
    	return atLeastCount;
    }
	
    /**
     * @param atLeastCount the atLeastCount to set
     */
    public void setAtLeastCount(Integer atLeastCount) {
    	this.atLeastCount = atLeastCount;
    }

    /**
     * @return the atMostCount
     */
    public Integer getAtMostCount() {
    	return atMostCount;
    }

    /**
     * @param atMostCount the atMostCount to set
     */
    public void setAtMostCount(Integer atMostCount) {
    	this.atMostCount = atMostCount;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
    	return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
    	this.location = location;
    }
}
