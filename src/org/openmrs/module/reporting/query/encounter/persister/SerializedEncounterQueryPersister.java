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
package org.openmrs.module.reporting.query.encounter.persister;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.definition.persister.SerializedDefinitionPersister;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

/**
 * This class returns EncounterQuerys that have been Serialized to the database
 * This class is annotated as a Handler that supports all EncounterQuery classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * an EncounterQuery.  To override this behavior, any additional EncounterQueryPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={EncounterQuery.class})
public class SerializedEncounterQueryPersister extends SerializedDefinitionPersister<EncounterQuery> {

	protected SerializedEncounterQueryPersister() { }

	/**
	 * @see SerializedDefinitionPersister#getBaseClass()
	 */
	@Override
	public Class<EncounterQuery> getBaseClass() {
		return EncounterQuery.class;
	}
}
