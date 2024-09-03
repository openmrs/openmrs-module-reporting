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
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.definition.service.DefinitionService;
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
        DefinitionService svc = DefinitionContext.getDefinitionService(type);
        Class<? extends Definition> parentType = svc.getDefinitionType();
		return "/module/reporting/definition/editAnnotatedDefinition.form?type="+type.getName() + "&parentType=" + parentType.getName();
	}
}

