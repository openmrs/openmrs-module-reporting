/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.persister;

import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.reporting.definition.persister.SerializedDefinitionPersister;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.indicator.Indicator;

/**
 * This class returns Indicators that have been Serialized to the database
 * This class is annotated as a Handler that supports all Indicator classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a Indicator.  To override this behavior, any additional IndicatorPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={Indicator.class})
public class SerializedIndicatorPersister extends SerializedDefinitionPersister<Indicator> implements IndicatorPersister {
	
	private SerializedIndicatorPersister() { }

	/**
	 * @see SerializedDefinitionPersister#getBaseClass()
	 */
	@Override
	public Class<Indicator> getBaseClass() {
		return Indicator.class;
	}
}
