/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.converter;

import java.util.List;

import org.openmrs.api.db.SerializedObject;

/**
 * Interface for all classes which can convert definitions from one type to another
 */
public interface DefinitionConverter {
	
	/**
	 * @return all SerializedObjects which need conversion
	 */
	public List<SerializedObject> getInvalidDefinitions();

	/**
	 * Does the actual work of converting a Definition from one type to another
	 * @return true if conversion was successful, false otherwise
	 */
	public boolean convertDefinition(SerializedObject so);
	
}
