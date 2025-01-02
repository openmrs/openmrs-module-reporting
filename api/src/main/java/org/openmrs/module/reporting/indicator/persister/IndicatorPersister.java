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

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.definition.persister.DefinitionPersister;
import org.openmrs.module.reporting.indicator.Indicator;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of Indicator implementations
 */
public interface IndicatorPersister extends DefinitionPersister<Indicator> {
	
	/**
	 * Gets the {@link Indicator} that matches the given id
	 * 
	 * @param id the id to match
	 * @return the {@link Indicator} with the given id among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return Indicator when exists
	 */
	public Indicator getDefinition(Integer id);
	
	/**
	 * Gets the {@link Indicator} that matches the given uuid
	 * 
	 * @param uuid	the uuid to match
	 * @return the {@link Indicator} with the given uuid among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return {@link Indicator} when exists
	 */
	public Indicator getDefinitionByUuid(String uuid);
	
	/**
	 * @param includeRetired - if true, include retired {@link Indicator} in the returned List
	 * @return All {@link Indicator} whose persistence is managed by this persister
	 * 
	 * @should get all {@link Indicator} including retired
	 * @should get all {@link Indicator} not including retired
	 */
	public List<Indicator> getAllDefinitions(boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired Indicators in the count
	 * @return the number of saved Indicators
	 */
	public int getNumberOfDefinitions(boolean includeRetired);
	
	/**
	 * Returns a List of {@link Indicator} whose name contains the passed name.
	 * An empty list will be returned if there are none found. Search is case insensitive.
	 * 
	 * @param name The search string
	 * @param exactMatchOnly if true will only return exact matches
	 * 
	 * @throws APIException
	 * @return a List<Indicator> objects whose name contains the passed name
	 */
	public List<Indicator> getDefinitions(String name, boolean exactMatchOnly);
	
	/**
	 * Saves the given {@link Indicator} to the system.
	 * 
	 * @param datasetDefinition	the {@link Indicator} to save
	 * @return the {@link Indicator} that was 
	 * 
	 * @should create new {@link Indicator}
	 * @should update existing {@link Indicator}
	 * @should set identifier after save
	 */
	public Indicator saveDefinition(Indicator indicator);
	
	/**
	 * Deletes a {@link Indicator} from the system.
	 * 
	 * @param datasetDefinition	the {@link Indicator} to purge
	 * 
	 * @should remove the Indicator
	 */
	public void purgeDefinition(Indicator indicator);
}
