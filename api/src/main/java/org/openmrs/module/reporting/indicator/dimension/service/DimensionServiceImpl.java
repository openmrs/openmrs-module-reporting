/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
