/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.definition;

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient Identifier Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.PatientIdentifierDataDefinition")
public class PatientIdentifierDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private List<PatientIdentifierType> types;

    @ConfigurationProperty
    private Boolean includeFirstNonNullOnly = Boolean.FALSE;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public PatientIdentifierDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name and type only
	 */
	public PatientIdentifierDataDefinition(String name, PatientIdentifierType... types) {
		super(name);
		if (types != null) {
			for (PatientIdentifierType type : types) {
				addType(type);
			}
		}
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
        if (getIncludeFirstNonNullOnly() == Boolean.TRUE) {
            return PatientIdentifier.class;
        }
		return List.class;
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the types
	 */
	public List<PatientIdentifierType> getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(List<PatientIdentifierType> types) {
		this.types = types;
	}
	
	/**
	 * @param type the type to add
	 */
	public void addType(PatientIdentifierType type) {
		if (types == null) {
			types = new ArrayList<PatientIdentifierType>();
		}
		types.add(type);
	}

    /**
     * @return whether or not to include only the first non-null value for each patient
     */
    public Boolean getIncludeFirstNonNullOnly() {
        return includeFirstNonNullOnly;
    }

    /**
     * @param includeFirstNonNullOnly whether or not to include only the first non-null value for each patient
     */
    public void setIncludeFirstNonNullOnly(Boolean includeFirstNonNullOnly) {
        this.includeFirstNonNullOnly = includeFirstNonNullOnly;
    }
}