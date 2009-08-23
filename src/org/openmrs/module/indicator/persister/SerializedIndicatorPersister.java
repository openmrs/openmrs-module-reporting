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
package org.openmrs.module.indicator.persister;

import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.indicator.Indicator;

/**
 * This class returns Indicators that have been Serialized to the database
 * This class is annotated as a Handler that supports all Indicator classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a Indicator.  To override this behavior, any additional IndicatorPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={Indicator.class})
public class SerializedIndicatorPersister implements IndicatorPersister {

    //****************
    // Properties
    //****************
	
	private SerializedObjectDAO dao = null;

    //****************
    // Instance methods
    //****************
    
	/**
     * @see IndicatorPersister#getIndicator(Integer)
     */
    public Indicator getIndicator(Integer id) {
    	return dao.getObject(Indicator.class, id);
    }
    
	/**
     * @see IndicatorPersister#getIndicatorByUuid(String)
     */
    public Indicator getIndicatorByUuid(String uuid) {
    	return dao.getObjectByUuid(Indicator.class, uuid);
    }

	/**
     * @see IndicatorPersister#getAllIndicators(boolean)
     */
    public List<Indicator> getAllIndicators(boolean includeRetired) {
    	return dao.getAllObjects(Indicator.class, includeRetired);
    }

	/**
     * @see IndicatorPersister#getIndicatorByName(String, boolean)
     */
    public List<Indicator> getIndicators(String name, boolean exactMatchOnly) {
    	return dao.getAllObjectsByName(Indicator.class, name, exactMatchOnly);
    }
    
	/**
     * @see IndicatorPersister#saveIndicator(Indicator)
     */
    public Indicator saveIndicator(Indicator indicator) {
    	return dao.saveObject(indicator);
    }

	/**
     * @see IndicatorPersister#purgeIndicator(Indicator)
     */
    public void purgeIndicator(Indicator indicator) {
    	dao.purgeObject(indicator.getId());
    }

    //****************
    // Property access
    //****************
	
    /**
     * @return the dao
     */
    public SerializedObjectDAO getDao() {
    	return dao;
    }

    /**
     * @param dao the dao to set
     */
    public void setDao(SerializedObjectDAO dao) {
    	this.dao = dao;
    }
}
