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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.PatientSetService.GroupMethod;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.configuration.ConfigurationProperty;
import org.openmrs.module.evaluation.EvaluationContext;

public class DrugOrderCohortDefinition extends DateRangeCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	protected final Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=false)
	private List<Drug> drugList;
	
	@ConfigurationProperty(required=false)
	private List<Concept> drugSets;
	
	@ConfigurationProperty(required=false)
	private GroupMethod anyOrAll;
	
	//***** CONSTRUCTORS *****
	
    /**
     * Default Constructor
     */
	public DrugOrderCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Convenience method to return a List of all Drugs to filter against
	 * @return List<Drug> of drugs to filter against, or empty list if none
	 */
	public List<Drug> getDrugListToUse(EvaluationContext context) {
		List<Drug> ret = new ArrayList<Drug>();
		if (drugList != null) {
			ret.addAll(drugList);
		}
		if (drugSets != null) {
			Set<Concept> generics = new HashSet<Concept>();
			for (Concept drugSet : drugSets) {
				List<Concept> list = Context.getConceptService().getConceptsByConceptSet(drugSet);
				generics.addAll(list);
			}
			for (Concept generic : generics) {
				ret.addAll(Context.getConceptService().getDrugsByConcept(generic));
			}
		}
		return ret;
	}
	
	@Override
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		boolean currentlyCase = isCurrentCase(null);
		List<Drug> drugListToUse = getDrugListToUse(null);
		
		StringBuffer ret = new StringBuffer();
		ret.append("Patients " + (currentlyCase ? "currently " : ""));
		
		if (drugListToUse.isEmpty()) {
			if (anyOrAll == GroupMethod.NONE) {
				ret.append(currentlyCase ? "taking no drugs" : "who never took any drugs");
			}
			else {
				ret.append(currentlyCase ? "taking any drugs" : "ever taking any drugs");
			}
		} 
		else {
			if (drugListToUse.size() == 1) {
				ret.append((anyOrAll == GroupMethod.NONE ? "not " : "") + "taking ");
				ret.append(drugListToUse.get(0).getName());
			} 
			else {
				ret.append("taking " + anyOrAll + " of [");
				for (Iterator<Drug> i = drugListToUse.iterator(); i.hasNext();) {
					ret.append(i.next().getName() + (i.hasNext() ? " , " : ""));
				}
				ret.append("]");
			}
		}
		ret.append(" " + getDateRangeDescription());
		return ret.toString();
	}

	//***** PROPERTY ACCESS *****
	
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
     * @param drugSet the drugSet to add
     */
    public void addDrug(Drug drug) {
    	if (this.drugList == null) {
    		this.drugList = new ArrayList<Drug>();
    	}
    	this.drugList.add(drug);
    }
	
    /**
     * @return the drugSets
     */
    public List<Concept> getDrugSets() {
    	return drugSets;
    }
	
    /**
     * @param drugSets the drugSets to set
     */
    public void setDrugSets(List<Concept> drugSets) {
    	this.drugSets = drugSets;
    }
    
    /**
     * @param drugSet the drugSet to add
     */
    public void addDrugSet(Concept drugSet) {
    	if (this.drugSets == null) {
    		this.drugSets = new ArrayList<Concept>();
    	}
    	this.drugSets.add(drugSet);
    }

    /**
     * @return the anyOrAll
     */
    public GroupMethod getAnyOrAll() {
    	return anyOrAll;
    }

    /**
     * @param anyOrAll the anyOrAll to set
     */
    public void setAnyOrAll(GroupMethod anyOrAll) {
    	this.anyOrAll = anyOrAll;
    }
}
