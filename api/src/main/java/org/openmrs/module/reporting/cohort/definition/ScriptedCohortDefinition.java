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

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ScriptingLanguage;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * A CohortDefinition where a user can define in any JSR-223 (javax.script) supported script
 * (Groovy, Javascript, etc)
 */
@Caching(strategy=ConfigurationPropertyAndParameterCachingStrategy.class)
@Localized("reporting.ScriptedCohortDefinition")
public class ScriptedCohortDefinition extends BaseCohortDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//****************
	// Properties
	//****************
	
	@ConfigurationProperty(required = true)
	private ScriptingLanguage scriptType;
	
	@ConfigurationProperty(required = true, displayFormat = "textarea", displayAttributes = "rows=20|cols=100")
	private String scriptCode;
	
	//****************
	// Constructors
	//****************
	
	/**
	 * Default Constructor
	 */
	public ScriptedCohortDefinition() {
		super();
	}
	
	/**
	 * Full constructor
	 * 
	 * @param scriptType
	 * @param scriptCode
	 */
	public ScriptedCohortDefinition(ScriptingLanguage scriptType, String scriptCode) {
		this();
		this.scriptType = scriptType;
		this.scriptCode = scriptCode;
	}
	
	//****************
	// Property access
	//****************
	
	public ScriptingLanguage getScriptType() {
		return scriptType;
	}
	
	public void setScriptType(ScriptingLanguage scriptType) {
		this.scriptType = scriptType;
	}
	
	public String getScriptCode() {
		return scriptCode;
	}
	
	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}
}
