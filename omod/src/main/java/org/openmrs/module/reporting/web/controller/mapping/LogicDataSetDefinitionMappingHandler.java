/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
 package org.openmrs.module.reporting.web.controller.mapping;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Handler that determines what pages are redirected for creating and editing DataSetDefinitions
 */
@Handler(supports=LogicDataSetDefinition.class, order=50)
public class LogicDataSetDefinitionMappingHandler extends DefinitionMappingHandler {
	
	/**
	 * @see DefinitionMappingHandler#getCreateUrl(Class)
	 */
	@Override
	public String getCreateUrl(Class<? extends Definition> type) {
		return "/module/reporting/datasets/logicDataSetEditor.form";
	}
	
	@Override
	public String getEditUrl(Definition definition) {
		return getCreateUrl(definition.getClass()) + "?uuid=" + definition.getUuid();
	}
	
}
