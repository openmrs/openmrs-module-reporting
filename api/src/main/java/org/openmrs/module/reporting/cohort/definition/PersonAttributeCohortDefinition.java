/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Cohort query that returns patients with certain person attribute types and values.
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.PersonAttributeCohortDefinition")
public class PersonAttributeCohortDefinition extends BaseCohortDefinition {
	
    public static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
    /**
     * Configuration group that allows you to match 
     * on the person attribute type.
     */
    @ConfigurationProperty(group="attributeTypeGroup")
	private PersonAttributeType attributeType;

    /**
     * Configuration group that allows you to match 
     * on the person attribute value.
     */    
    @ConfigurationProperty(group="attributeValuesGroup")
	private List<String> values;
    
    /**
     * Configuration group that allows you to match 
     * on the person attribute value.
     */    
    @ConfigurationProperty(group="attributeValuesGroup")
	private List<Concept> valueConcepts;
    
    /**
     * Configuration group that allows you to match 
     * on the person attribute value.
     */    
    @ConfigurationProperty(group="attributeValuesGroup")
	private List<Location> valueLocations;
    
	//***** CONSTRUCTORS *****
	
	/**
	 * This currently only returns patients, although it's named for persons.
	 */
	public PersonAttributeCohortDefinition() {
		super();
	}

	//***** INSTANCE METHODS *****
	
	/**
	 * @param value
	 */
	public void addValue(Object value) {
		if (value != null) {
			if (value instanceof Concept) {
                addConceptValue((Concept) value);
            }
			else if (value instanceof Location) {
                addLocationValue((Location) value);
            }
			else if (value instanceof String) {
                addStringValue((String) value);
            }
			else {
				throw new IllegalArgumentException("You cannot add value " + value + " that is not a Location, Concept, or String");
			}
		}
	}

    private void addStringValue(String value) {
        if (values==null){
            values = new ArrayList<String>();
        }
        values.add(value);
    }

    private void addLocationValue(Location value) {
        if (valueLocations==null){
            valueLocations = new ArrayList<Location>();
        }
        valueLocations.add(value);
    }

    private void addConceptValue(Concept value) {
        if (valueConcepts== null){
            valueConcepts = new ArrayList<Concept>();
        }
        valueConcepts.add(value);
    }

    /**
     * @see java.lang.Object#toString()
     */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Patients with ");
		sb.append(attributeType != null ? attributeType.getName() : " any attribute");
		if (values != null) {
			sb.append(" in the set of (");
			sb.append(values);
			sb.append(")");
		}
		if (valueConcepts != null) {
			sb.append(" in the set of (");
			sb.append(valueConcepts);
			sb.append(")");
		}
		if (valueLocations != null) {
			sb.append(" in the set of (");
			sb.append(valueLocations);
			sb.append(")");
		}
		return sb.toString();
	}

	/**
	 * @return the attributeType
	 */
	public PersonAttributeType getAttributeType() {
		return attributeType;
	}

	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(PersonAttributeType attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}

	/**
	 * @return the valueConcepts
	 */
	public List<Concept> getValueConcepts() {
		return valueConcepts;
	}

	/**
	 * @param valueConcepts the valueConcepts to set
	 */
	public void setValueConcepts(List<Concept> valueConcepts) {
		this.valueConcepts = valueConcepts;
	}

	/**
	 * @return the valueLocations
	 */
	public List<Location> getValueLocations() {
		return valueLocations;
	}

	/**
	 * @param valueLocations the valueLocations to set
	 */
	public void setValueLocations(List<Location> valueLocations) {
		this.valueLocations = valueLocations;
	}
}
