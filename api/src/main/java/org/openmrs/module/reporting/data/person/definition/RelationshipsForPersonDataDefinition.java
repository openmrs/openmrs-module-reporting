/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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