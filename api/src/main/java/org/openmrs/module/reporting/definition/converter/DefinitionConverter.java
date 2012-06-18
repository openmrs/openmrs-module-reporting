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
