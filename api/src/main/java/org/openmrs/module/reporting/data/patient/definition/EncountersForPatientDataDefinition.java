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
package org.openmrs.module.reporting.data.patient.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Encounters For Patient Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.EncountersForPatientDataDefinition")
public class EncountersForPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private TimeQualifier which;
	
	@ConfigurationProperty(required=true)
	private List<EncounterType> types;
	
	@ConfigurationProperty
	private Date onOrAfter;
	
	@ConfigurationProperty
	private Date onOrBefore;

    @ConfigurationProperty
    private boolean onlyInActiveVisit;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public EncountersForPatientDataDefinition() {
		super();
	}
	
	/**
	 * Name only Constructor
	 */
	public EncountersForPatientDataDefinition(String name) {
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		if (which == TimeQualifier.LAST || which == TimeQualifier.FIRST) {
			return Encounter.class;
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

    public boolean getOnlyInActiveVisit() {
        return onlyInActiveVisit;
    }

    public void setOnlyInActiveVisit(boolean activeVisit) {
        this.onlyInActiveVisit = activeVisit;
    }

    /**
	 * @return the types
	 */
	public List<EncounterType> getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(List<EncounterType> types) {
		this.types = types;
	}
	
	/**
	 * @param type the type to add
	 */
	public void addType(EncounterType type) {
		if (types == null) {
			types = new ArrayList<EncounterType>();
		}
		types.add(type);
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
}