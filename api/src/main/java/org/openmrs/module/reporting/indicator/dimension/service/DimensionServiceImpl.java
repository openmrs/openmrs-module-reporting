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
package org.openmrs.module.reporting.indicator.dimension.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Implementation of DimensionService
 */
public class DimensionServiceImpl extends BaseDefinitionService<Dimension> implements DimensionService {

	protected static Log log = LogFactory.getLog(DimensionServiceImpl.class);
	
	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	@Transactional(readOnly = true)
	public Class<Dimension> getDefinitionType() {
		return Dimension.class;
	}
}
