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
package org.openmrs.module.reporting.cohort.definition.toreview;

import java.util.Iterator;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Filter based on Stopped Drug Orders
 */
public class DrugOrderStopCohortDefinition extends DateRangeCohortDefinition {

    private static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty(required=false)
    private List<Drug> drugList;
    
    @ConfigurationProperty(required=false)
    private List<Concept> genericDrugList;
    
    @ConfigurationProperty(required=false)
    private Boolean discontinued;
    
    @ConfigurationProperty(required=false)
    private List<Concept> discontinuedReasonList;
    
    //***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public DrugOrderStopCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
    	
    	int numDrugs = (drugList == null || drugList.isEmpty()) ? 0 : drugList.size();
    	int numDrugSets = (genericDrugList == null || genericDrugList.isEmpty()) ? 0 : genericDrugList.size();
    	int numReasons = (discontinuedReasonList == null || discontinuedReasonList.isEmpty()) ? 0 : discontinuedReasonList.size();
    	
		StringBuilder sb = new StringBuilder();
		sb.append("Patients who " + (discontinued ? "discontinued " : "stopped as planned "));
		if (numDrugs > 0 || numDrugSets > 0) {
			if (numDrugs > 0) {
				if (numDrugs == 1) {
					sb.append(drugList.get(0).getName());
				}
				else {
					sb.append("any of [");
					for (Iterator<Drug> i = drugList.iterator(); i.hasNext();) {
						sb.append(" " + i.next().getName() + " ");
						if (i.hasNext())
							sb.append(",");
					}
					sb.append("]");
				}
			}
			if (numDrugSets > 0) {
				if (numDrugSets == 1) {
					sb.append("any form of " + genericDrugList.get(0).getPreferredName(Context.getLocale()).getName());
				}
				else {
					sb.append("any form of [");
					for (Iterator<Concept> i = genericDrugList.iterator(); i.hasNext();) {
						sb.append(" " + i.next().getPreferredName(Context.getLocale()).getName() + " ");
						if (i.hasNext()) {
							sb.append(",");
						}
					}
					sb.append(" ]");
				}
			}
		} else {
			sb.append("any drug");
		}
		
		if (numReasons > 0) {
			if (numReasons == 1) {
				String reason = "[name not defined]";
				ConceptName cn = discontinuedReasonList.get(0).getBestName(Context.getLocale());
				if (cn != null) {
					reason = cn.getName();
				}
				sb.append(" because of " + reason);
			} 
			else {
				sb.append(" because of any of [");
				for (Iterator<Concept> i = discontinuedReasonList.iterator(); i.hasNext();) {
					sb.append(" " + i.next().getBestName(Context.getLocale()).getName() + " ");
					if (i.hasNext()) {
						sb.append(",");
					}
				}
				sb.append("] ");
			}
		}
		sb.append(getDateRangeDescription());
		return sb.toString();
	}

    /**
     * @return the drugList
     */
    public List<Drug> getDrugList() {
    	return drugList;
    }

    /**
     * @param drugList the drugList to set
     */
    public void setDrugList(List<Drug> drugList) {
    	this.drugList = drugList;
    }
	
    /**
     * @return the genericDrugList
     */
    public List<Concept> getGenericDrugList() {
    	return genericDrugList;
    }

    /**
     * @param genericDrugList the genericDrugList to set
     */
    public void setGenericDrugList(List<Concept> genericDrugList) {
    	this.genericDrugList = genericDrugList;
    }
	
    /**
     * @return the discontinued
     */
    public Boolean getDiscontinued() {
    	return discontinued;
    }
	
    /**
     * @param discontinued the discontinued to set
     */
    public void setDiscontinued(Boolean discontinued) {
    	this.discontinued = discontinued;
    }

    /**
     * @return the discontinuedReasonList
     */
    public List<Concept> getDiscontinuedReasonList() {
    	return discontinuedReasonList;
    }

    /**
     * @param discontinuedReasonList the discontinuedReasonList to set
     */
    public void setDiscontinuedReasonList(List<Concept> discontinuedReasonList) {
    	this.discontinuedReasonList = discontinuedReasonList;
    }
}
