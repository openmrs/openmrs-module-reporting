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
package org.openmrs.module.reporting.web.controller.mapping;

import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Handler that determines what pages are redirected for creating and editing definitions
 */
public abstract class DefinitionMappingHandler {
	
	/**
	 * @return The URL for viewing an existing Definition
	 */
	public String getViewUrl(Definition definition) {
		return getEditUrl(definition);
	}
	
	/**
	 * @return The URL for editing an existing Definition
	 */
	public String getEditUrl(Definition definition) {
		String baseUrl = getCreateUrl(definition.getClass());
		return baseUrl + (baseUrl.indexOf("?") != -1 ? "&" : "?") + "uuid=" + definition.getUuid();
	}
	
	/**
	 * @return The URL for creating a new Definition
	 */
	public abstract String getCreateUrl(Class<? extends Definition> type);
	
}

