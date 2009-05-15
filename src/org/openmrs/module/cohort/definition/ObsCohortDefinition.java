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

import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.api.PatientSetService.Modifier;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.parameter.Param;

public class ObsCohortDefinition extends DateRangeCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@Param(required=true)
	private Concept question;
	
	@Param(required=false)
	private Modifier modifier;
	
	@Param(required=false)
	private TimeModifier timeModifier;
	
	@Param(required=false)
	private Object value;

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
			if (value != null) {
				String conceptName = ((Concept) value).getBestName(locale).getName();
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
			if (value != null && modifier != null) {
				ret.append(" " + modifier.getSqlRepresentation() + " ");
				if (value instanceof Concept) {
					ret.append(((Concept) value).getBestName(locale).getName());
				}
				else {
					ret.append(value);
				}
			}
		}
		ret.append(getDateRangeDescription());
		return ret.toString();
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
     * @return the value
     */
    public Object getValue() {
    	return value;
    }
	
    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
    	this.value = value;
    }
}
