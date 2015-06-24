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
package org.openmrs.module.reporting.data.person.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Obs Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ObsForPersonDataDefinition")
public class ObsForPersonDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private TimeQualifier which;
	
	@ConfigurationProperty(required=true)
	private Concept question;
	
	@ConfigurationProperty(group="whichEncounter")
	private List<EncounterType> encounterTypeList;

	@ConfigurationProperty(group="whichEncounter")
	private List<Location> locationList;
	
	@ConfigurationProperty(group="whichEncounter")
	private List<Form> formList;
	
	@ConfigurationProperty
	private Date onOrAfter;
	
	@ConfigurationProperty
	private Date onOrBefore;

    @ConfigurationProperty
    private Date valueDatetimeOrAfter;

    @ConfigurationProperty
    private Date valueDatetimeOnOrBefore;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public ObsForPersonDataDefinition() {
		super();
	}
	
	/**
	 * Name only Constructor
	 */
	public ObsForPersonDataDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate all properties only
	 */
	public ObsForPersonDataDefinition(String name, TimeQualifier which, Concept question, Date onOrBefore, Date onOrAfter) {
		this(name);
		this.which = which;
		this.question = question;
		this.onOrBefore = onOrBefore;
		this.onOrAfter = onOrAfter;
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		if (which == TimeQualifier.LAST || which == TimeQualifier.FIRST) {
			return Obs.class;
		}
		return List.class;
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the which
	 */
	public TimeQualifier getWhich() {
		return which;
	}

	/**
	 * @param which the which to set
	 */
	public void setWhich(TimeQualifier which) {
		this.which = which;
	}
	
	/**
	 * @return the question
	 */
	public Concept getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(Concept question) {
		this.question = question;
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
     * @param encounterType the encounter type to add to the list
     */
    public void addEncounterType(EncounterType encounterType) {
    	if (encounterTypeList == null) {
    		encounterTypeList = new ArrayList<EncounterType>();
    	}
    	encounterTypeList.add(encounterType);
    }

	/**
	 * @return the locationList
	 */
	public List<Location> getLocationList() {
		return locationList;
	}

	/**
	 * @param locationList the locationList to set
	 */
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	/**
	 * @param location the location to add to the list
	 */
	public void addLocation(Location location) {
		if (locationList == null) {
			locationList = new ArrayList<Location>();
		}
		locationList.add(location);
	}
    
	/**
     * @return the formList
     */
    public List<Form> getFormList() {
    	return formList;
    }
	
    /**
     * @param formList the formList to set
     */
    public void setFormList(List<Form> formList) {
    	this.formList = formList;
    }
    
    /**
     * @param Form the form to add to the list
     */
    public void addForm(Form Form) {
    	if (formList == null) {
    		formList = new ArrayList<Form>();
    	}
    	formList.add(Form);
    }

	/**
	 * @return the onOrAfter
	 */
	public Date getOnOrAfter() {
		return onOrAfter;
	}

	/**
	 * @param onOrAfter the onOrAfter to set
	 */
	public void setOnOrAfter(Date onOrAfter) {
		this.onOrAfter = onOrAfter;
	}

	/**
	 * @return the onOrBefore
	 */
	public Date getOnOrBefore() {
		return onOrBefore;
	}

	/**
	 * @param onOrBefore the onOrBefore to set
	 */
	public void setOnOrBefore(Date onOrBefore) {
		this.onOrBefore = onOrBefore;
	}

    public Date getValueDatetimeOrAfter() {
        return valueDatetimeOrAfter;
    }

    public void setValueDatetimeOrAfter(Date valueDatetimeOrAfter) {
        this.valueDatetimeOrAfter = valueDatetimeOrAfter;
    }

    public Date getValueDatetimeOnOrBefore() {
        return valueDatetimeOnOrBefore;
    }

    public void setValueDatetimeOnOrBefore(Date valueDatetimeOnOrBefore) {
        this.valueDatetimeOnOrBefore = valueDatetimeOnOrBefore;
    }
}