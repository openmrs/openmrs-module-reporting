/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.dimension.persister;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.definition.persister.SerializedDefinitionPersister;
import org.openmrs.module.reporting.indicator.dimension.Dimension;

/**
 * This class returns Dimensions that have been Serialized to the database
 * This class is annotated as a Handler that supports all Dimension classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a Dimension.  To override this behavior, any additional DimensionPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={Dimension.class}, order=100)
public class SerializedDimensionPersister extends SerializedDefinitionPersister<Dimension> implements DimensionPersister {

	private SerializedDimensionPersister() { }

	/**
	 * @see SerializedDefinitionPersister#getBaseClass()
	 */
	@Override
	public Class<Dimension> getBaseClass() {
		return Dimension.class;
	}
}
