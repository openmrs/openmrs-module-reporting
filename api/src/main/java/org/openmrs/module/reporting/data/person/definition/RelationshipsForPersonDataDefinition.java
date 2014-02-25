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

import org.openmrs.RelationshipType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.List;

/**
 * Relationship Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.RelationshipsForPersonDataDefinition")
public class RelationshipsForPersonDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	//***** PROPERTIES *****

	@ConfigurationProperty
	private List<RelationshipType> relationshipTypes;

	@ConfigurationProperty
	private Boolean valuesArePersonA = Boolean.TRUE;

	@ConfigurationProperty
	private Boolean valuesArePersonB = Boolean.TRUE;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public RelationshipsForPersonDataDefinition() {
		super();
	}

	/**
	 * Name only Constructor
	 */
	public RelationshipsForPersonDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return List.class;
	}
	
	//****** PROPERTY ACCESS ******

	public List<RelationshipType> getRelationshipTypes() {
		return relationshipTypes;
	}

	public void setRelationshipTypes(List<RelationshipType> relationshipTypes) {
		this.relationshipTypes = relationshipTypes;
	}

	public void addRelationshipType(RelationshipType relationshipType) {
		if (relationshipTypes == null) {
			relationshipTypes = new ArrayList<RelationshipType>();
		}
		relationshipTypes.add(relationshipType);
	}

	public Boolean getValuesArePersonA() {
		return valuesArePersonA;
	}

	public void setValuesArePersonA(Boolean valuesArePersonA) {
		this.valuesArePersonA = valuesArePersonA;
	}

	public Boolean getValuesArePersonB() {
		return valuesArePersonB;
	}

	public void setValuesArePersonB(Boolean valuesArePersonB) {
		this.valuesArePersonB = valuesArePersonB;
	}
}