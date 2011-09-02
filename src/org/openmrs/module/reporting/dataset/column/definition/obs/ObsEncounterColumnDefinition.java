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
package org.openmrs.module.reporting.dataset.column.definition.obs;

import org.openmrs.Obs;
import org.openmrs.module.reporting.dataset.column.definition.JoinColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.encounter.EncounterColumnDefinition;

/**
 * Obs-Encounter Join Column Definition
 */
public class ObsEncounterColumnDefinition extends JoinColumnDefinition<EncounterColumnDefinition> implements ObsColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public ObsEncounterColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public ObsEncounterColumnDefinition(EncounterColumnDefinition definition) {
		super(definition);
	}

	/** 
	 * @see RowPerObjectColumnDefinition#getBaseType()
	 */
	public Class<Obs> getBaseType() {
		return Obs.class;
	}
	
	/** 
	 * @see RowPerObjectColumnDefinition#getIdProperty()
	 */
	public String getIdProperty() {
		return "obsId";
	}

	/** 
	 * @see JoinColumnDefinition#getJoinProperty()
	 */
	@Override
	public String getJoinProperty() {
		return "encounter";
	}
}