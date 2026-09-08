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

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.api.APIException;

/**
 * Contains methods pertaining to creating/updating/deleting/retiring/registering/evaluating Dimensions
 */
public interface DimensionService extends DefinitionService<Dimension> {
	
	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Authorized({ ReportingConstants.PRIV_MANAGE_DIMENSION_DEFINITIONS })
	public <D extends Dimension> D saveDefinition(D definition) throws APIException;
	
}
