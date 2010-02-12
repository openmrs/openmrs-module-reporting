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

import java.util.List;

import org.openmrs.Form;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class FormCohortDefinition extends DateRangeCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=false)
	private List<Form> forms;

	public FormCohortDefinition() {
		super();
	}
	
    /**
     * @return the form
     */
    public List<Form> getForms() {
    	return forms;
    }

    /**
     * @param form the form to set
     */
    public void setForms(List<Form> forms) {
    	this.forms = forms;
    }
	
}
