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

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.indicator.Indicator;

/**
 * This interface exposes the functionality required to access the Data Access
 * functionality for a particular set of Indicator implementations
 */
public interface IndicatorPersister {
	
	/**
	 * Gets the {@link Indicator} that matches the given id
	 * 
	 * @param id the id to match
	 * @return the {@link Indicator} with the given id among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return Indicator when exists
	 */
	public Indicator getIndicator(Integer id);
	
	/**
	 * Gets the {@link Indicator} that matches the given uuid
	 * 
	 * @param uuid	the uuid to match
	 * @return the {@link Indicator} with the given uuid among those managed by this persister
	 * 
	 * @should return null when does not exist
	 * @should return {@link Indicator} when exists
	 */
	public Indicator getIndicatorByUuid(String uuid);
	
	/**
	 * @param includeRetired - if true, include retired {@link Indicator} in the returned List
	 * @return All {@link Indicator} whose persistence is managed by this persister
	 * 
	 * @should get all {@link Indicator} including retired
	 * @should get all {@link Indicator} not including retired
	 */
	public List<Indicator> getAllIndicators(boolean includeRetired);
	
	/**
	 * @param includeRetired indicates whether to also include retired Indicators in the count
	 * @return the number of saved Indicators
	 */
	public int getNumberOfIndicators(boolean includeRetired);
	
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
	public List<Indicator> getIndicators(String name, boolean exactMatchOnly);
	
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
	public Indicator saveIndicator(Indicator indicator);
	
	/**
	 * Deletes a {@link Indicator} from the system.
	 * 
	 * @param datasetDefinition	the {@link Indicator} to purge
	 * 
	 * @should remove the Indicator
	 */
	public void purgeIndicator(Indicator indicator);
}
