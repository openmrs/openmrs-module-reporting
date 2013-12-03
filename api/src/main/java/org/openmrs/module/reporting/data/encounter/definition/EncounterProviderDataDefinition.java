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
package org.openmrs.module.reporting.data.encounter.definition;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.List;

/**
 * Encounter Provider Column (for 1.9.x and above)
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.EncounterProviderDataDefinition")
public class EncounterProviderDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
	
	public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private OpenmrsMetadata encounterRole;

    @ConfigurationProperty
    private boolean singleProvider = true;

	public EncounterProviderDataDefinition() {
		super();
	}

	public EncounterProviderDataDefinition(String name) {
		super(name);
	}

    /**
     * @return encounterRole will be of type EncounterRole.class
     */
    public OpenmrsMetadata getEncounterRole() {
        return encounterRole;
    }

    /**
     * @param encounterRole must be of type EncounterRole.class
     */
    public void setEncounterRole(OpenmrsMetadata encounterRole) {
        this.encounterRole = encounterRole;
    }

    public boolean isSingleProvider() {
        return singleProvider;
    }

    public void setSingleProvider(boolean singleProvider) {
        this.singleProvider = singleProvider;
    }

    @Override
    public Class<?> getDataType() {
        return singleProvider ? OpenmrsMetadata.class : List.class;
    }
}