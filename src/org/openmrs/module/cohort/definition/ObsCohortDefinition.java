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

import java.util.Date;
import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.PatientSetService.Modifier;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.configuration.ConfigurationProperty;

public class ObsCohortDefinition extends DateRangeCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private Concept question;
	
	@ConfigurationProperty(required=false)
	private Modifier modifier;
	
	@ConfigurationProperty(required=false)
	private TimeModifier timeModifier;
	
	@ConfigurationProperty(required=false)
	private Concept valueCoded;
	
	@ConfigurationProperty(required=false)
	private Drug valueDrug;
	
	@ConfigurationProperty(required=false)
	private Date valueDatetime;
	
	@ConfigurationProperty(required=false)
	private Double valueNumeric;
	
	@ConfigurationProperty(required=false)
	private Boolean valueBoolean;
	
	@ConfigurationProperty(required=false)
	private String valueText;

	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public ObsCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		Locale locale = Context.getLocale();
		
		StringBuffer ret = new StringBuffer();
		if (question == null) {
			if (getValue() != null) {
				String conceptName = ((Concept) getValue()).getBestName(locale).getName();
				ret.append("Patients with " + timeModifier + " obs with value " + conceptName);
			}
			else {
				ret.append("question and value are both null");
			}
		} 
		else {
			ret.append("Patients with ");
			ret.append(timeModifier + " ");
			ret.append((question == null ? "CONCEPT" : question.getBestName(locale).getName()));
			if (getValue() != null && modifier != null) {
				ret.append(" " + modifier.getSqlRepresentation() + " ");
				if (getValue() instanceof Concept) {
					ret.append(((Concept) getValue()).getBestName(locale).getName());
				}
				else {
					ret.append(getValue());
				}
			}
		}
		ret.append(getDateRangeDescription());
		return ret.toString();
	}
	
	/**
	 * Return the specific value field that is non-null
	 * @return the value field that is non-null, or null otherwise
	 */
	public Object getValue() {
		if (valueCoded != null) {
			return valueCoded;
		}
		else if (valueDrug != null) {
			return valueDrug;
		}
		else if (valueDatetime != null) {
			return valueDatetime;
		}
		else if (valueNumeric != null) {
			return valueNumeric;			
		}
		else if (valueBoolean != null) { 
			return valueBoolean;
		}
		return valueText;
	}
	
	//***** PROPERTY ACCESS *****
	
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
     * @return the modifier
     */
    public Modifier getModifier() {
    	return modifier;
    }
	
    /**
     * @param modifier the modifier to set
     */
    public void setModifier(Modifier modifier) {
    	this.modifier = modifier;
    }
	
    /**
     * @return the timeModifier
     */
    public TimeModifier getTimeModifier() {
    	return timeModifier;
    }

    /**
     * @param timeModifier the timeModifier to set
     */
    public void setTimeModifier(TimeModifier timeModifier) {
    	this.timeModifier = timeModifier;
    }

	/**
	 * @return the valueCoded
	 */
	public Concept getValueCoded() {
		return valueCoded;
	}

	/**
	 * @param valueCoded the valueCoded to set
	 */
	public void setValueCoded(Concept valueCoded) {
		this.valueCoded = valueCoded;
	}

	/**
	 * @return the valueDrug
	 */
	public Drug getValueDrug() {
		return valueDrug;
	}

	/**
	 * @param valueDrug the valueDrug to set
	 */
	public void setValueDrug(Drug valueDrug) {
		this.valueDrug = valueDrug;
	}

	/**
	 * @return the valueDatetime
	 */
	public Date getValueDatetime() {
		return valueDatetime;
	}

	/**
	 * @param valueDatetime the valueDatetime to set
	 */
	public void setValueDatetime(Date valueDatetime) {
		this.valueDatetime = valueDatetime;
	}

	/**
	 * @return the valueNumeric
	 */
	public Double getValueNumeric() {
		return valueNumeric;
	}

	/**
	 * @param valueNumeric the valueNumeric to set
	 */
	public void setValueNumeric(Double valueNumeric) {
		this.valueNumeric = valueNumeric;
	}

	/**
	 * @return the valueBoolean
	 */
	public Boolean getValueBoolean() {
		return valueBoolean;
	}

	/**
	 * @param valueNumeric the valueNumeric to set
	 */
	public void setValueBoolean(Boolean valueBoolean) {
		this.valueBoolean = valueBoolean;
	}	
	
	/**
	 * @return the valueText
	 */
	public String getValueText() {
		return valueText;
	}

	/**
	 * @param valueText the valueText to set
	 */
	public void setValueText(String valueText) {
		this.valueText = valueText;
	}
}
