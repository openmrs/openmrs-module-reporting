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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Handler that determines what pages are redirected for creating and editing Data Definitions by default
 */
@Handler(supports={Definition.class}, order=200)
public class AnnotatedDefinitionMappingHandler extends DefinitionMappingHandler {
	
	/**
	 * @see DefinitionMappingHandler#getCreateUrl(Class)
	 */
	public String getCreateUrl(Class<? extends Definition> type) {
		return "/module/reporting/definition/editAnnotatedDefinition.form?type="+type.getName() + "&parentType=" + type.getName();
	}
}

