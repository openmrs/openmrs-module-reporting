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
package org.openmrs.module.reporting.cohort.definition;

import java.util.List;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.report.util.DynamicEnumUtil;

/**
 * A CohortDefinition where a user can define in any JSR-223 (javax.script) supported script
 * (Groovy, Javascript, etc)
 */
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
	
	//empty because it is populated at runtime in the static block below
	public enum ScriptingLanguage {

	}
	
	static {
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = scriptEngineManager.getEngineFactories();
		for (ScriptEngineFactory factory : factories) {
			DynamicEnumUtil.addEnum(ScriptingLanguage.class, factory.getLanguageName());
		}
	}
}
