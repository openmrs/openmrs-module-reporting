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
