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
package org.openmrs.module.reporting.db;

import java.util.List;

import org.openmrs.module.reporting.definition.DefinitionTag;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Contains DB CRUD operations for {@link Definition}s
 */
public interface DefinitionDAO {
	
	/**
	 * Gets all {@link DefinitionTag}s with the specified tag and definitionUuid. If the tag is
	 * null, empty or a white space character, all definition tags are returned
	 * 
	 * @param tag the tag to match against
	 * @param definitionUuid the definitionUuid to match against
	 * @return a list of matching definition tags
	 */
	public List<DefinitionTag> getDefinitionTags(String tag, String definitionUuid);
	
	/**
	 * Saves a {@link DefinitionTag} to the database
	 * 
	 * @param definitionTag the definition tag to save
	 * @return the saved definition tag
	 */
	public DefinitionTag saveDefinitionTag(DefinitionTag definitionTag);
	
	/**
	 * Deletes the Definition tag with a matching uuid and tag
	 */
	public void deleteDefinitionTag(String uuid, String tag);
	
	/**
	 * Checks if the DefinitionTag exists with the specified definition uuid and tag
	 * 
	 * @see DefinitionService#hasTag(String, String)
	 * @see DefinitionService#hasTag(Definition, String)
	 */
	public boolean checkIfTagExists(String definitionUuid, String tag);
}
