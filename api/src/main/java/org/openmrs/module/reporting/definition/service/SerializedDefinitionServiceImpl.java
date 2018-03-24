/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.ExceptionUtil;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.serialization.OpenmrsSerializer;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *  Base Implementation of the ReportingService API
 */
@Transactional
public class SerializedDefinitionServiceImpl extends BaseOpenmrsService implements SerializedDefinitionService {
	
	protected static Log log = LogFactory.getLog(SerializedDefinitionServiceImpl.class);
	
    //****************
    // Constructor
    //****************
	private SerializedDefinitionServiceImpl() { }
	
    //****************
    // Properties
    //****************
	
	private SerializedObjectDAO dao = null;
	private OpenmrsSerializer serializer = null;
	
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
    
    /**
     * @return the serializer
     */
    public OpenmrsSerializer getSerializer() {
    	return serializer;
    }
	
    /**
     * @param serializer the serializer to set
     */
    public void setSerializer(OpenmrsSerializer serializer) {
    	this.serializer = serializer;
    }
	
	
    //****************
    // Instance methods
    //****************
    
	/** 
	 * @see SerializedDefinitionService#getSupportedDefinitionTypes()
	 */
    @SuppressWarnings("unchecked")
    @Authorized
	public List<Class<Definition>> getSupportedDefinitionTypes() {
		List<Class<Definition>> d = new ArrayList<Class<Definition>>();
		for (Class<? extends OpenmrsObject> c : dao.getSupportedTypes()) {
			if (Definition.class.isAssignableFrom(c)) {
				d.add((Class<Definition>)c);
			}
		}
		return d;
	}

	/**
     * @see SerializedDefinitionService#getDefinition(Class, Integer)
     */
    @Authorized
    public <T extends Definition> T getDefinition(Class<T> definitionType, Integer id) {
    	SerializedObject so = dao.getSerializedObject(id);
    	try {
    		return dao.convertSerializedObject(definitionType, so);
    	}
    	catch (Exception e) {
    		ExceptionUtil.rethrowAuthenticationException(e);
    		log.warn("Unable to deserialize Definition: " + so, e);
    		return null;
    	}
    }

	/**
     * @see SerializedDefinitionService#getDefinitionByUuid(Class, String)
     */
    @Authorized
    public <T extends Definition> T getDefinitionByUuid(Class<T> definitionType, String uuid) {
    	SerializedObject so = dao.getSerializedObjectByUuid(uuid);
    	try {
    		return dao.convertSerializedObject(definitionType, so);
    	}
    	catch (Exception e) {
    		ExceptionUtil.rethrowAuthenticationException(e);
    		log.warn("Unable to deserialize Definition : " + so, e);
    		return null;
    	}
    }

    /**
     * @see SerializedDefinitionService#getAllDefinitions(Class, boolean)
     */
    @Authorized
    public <T extends Definition> List<T> getAllDefinitions(Class<T> definitionType, boolean includeRetired) {
    	List<T> ret = new ArrayList<T>();
    	for (SerializedObject so : dao.getAllSerializedObjects(definitionType, includeRetired)) {
        	try {
        		ret.add(dao.convertSerializedObject(definitionType, so));
        	}
        	catch (Exception e) {
        		ExceptionUtil.rethrowAuthenticationException(e);
        		log.warn("Unable to deserialize Definition: " + so, e);
        	}
    	}
    	return ret;
    }
    
    /**
     * @see SerializedDefinitionService#getAllDefinitionSummaries(Class, boolean)
     */
    @Authorized
    public <T extends Definition> List<DefinitionSummary> getAllDefinitionSummaries(Class<T> definitionType,
                                                                                    boolean includeRetired) {
    	List<DefinitionSummary> ret = new ArrayList<DefinitionSummary>();
    	for (SerializedObject so : dao.getAllSerializedObjects(definitionType, includeRetired)) {
    		ret.add(new DefinitionSummary(so));
    	}
    	Collections.sort(ret, new Comparator<DefinitionSummary>() {
			public int compare(DefinitionSummary o1, DefinitionSummary o2) {
				return o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase());
			}
    	});
    	return ret;
    }
    
    /**
     * @see SerializedDefinitionService#getInvalidDefinitions(boolean)
     */
    @Authorized
    public List<SerializedObject> getInvalidDefinitions(boolean includeRetired) {
    	List<SerializedObject> ret = new ArrayList<SerializedObject>();
    	for (Class<Definition> clazz : getSupportedDefinitionTypes()) {
    		ret.addAll(getInvalidDefinitions(clazz, includeRetired));
    	}
    	return ret;
    }
    
    /**
     * @see SerializedDefinitionService#getInvalidDefinitions(Class, boolean)
     */
    @Authorized
    public <T extends Definition> List<SerializedObject> getInvalidDefinitions(Class<T> definitionType, boolean includeRetired) {
    	List<SerializedObject> ret = new ArrayList<SerializedObject>();
    	for (SerializedObject so : dao.getAllSerializedObjects(definitionType, includeRetired)) {
        	try {
        		dao.convertSerializedObject(definitionType, so);
        	}
        	catch (Exception e) {
        		ExceptionUtil.rethrowAuthenticationException(e);
        		ret.add(so);
        	}
    	}
    	return ret;
    }
    
    /**
     * @see SerializedDefinitionService#getNumberOfDefinitions(Class, boolean)
     */
    @Authorized
	public <T extends Definition> int getNumberOfDefinitions(Class<T> definitionType, boolean includeRetired) {
		return dao.getAllSerializedObjects(definitionType, includeRetired).size();
	}

	/**
     * @see SerializedDefinitionService#getDefinitions(Class, String, boolean)
     */
    @Authorized
	public <T extends Definition> List<T> getDefinitions(Class<T> definitionType, String name, boolean exactMatchOnly) {
    	List<T> ret = new ArrayList<T>();
    	for (SerializedObject so : dao.getAllSerializedObjectsByName(definitionType, name, exactMatchOnly)) {
        	try {
        		ret.add(dao.convertSerializedObject(definitionType, so));
        	}
        	catch (Exception e) {
        		ExceptionUtil.rethrowAuthenticationException(e);
        		log.warn("Unable to deserialize Definition: " + so, e);
        	}
    	}
    	return ret;
    }

	/**
	 * @see SerializedDefinitionService#saveDefinition(Definition)
	 */
    @Authorized
    public <T extends Definition> T saveDefinition(T definition) {
    	//TODO This setting of audit fields can be safely removed after TRUNK-3876 is fixed.
		//check if existing definition
		if (definition.getId() != null) {
			definition.setChangedBy(Context.getAuthenticatedUser());
			definition.setDateChanged(new Date());
    	}
		else {
			//new definition
			if (definition.getCreator() == null) {
				definition.setCreator(Context.getAuthenticatedUser());
			}
			if (definition.getDateCreated() == null) {
				definition.setDateCreated(new Date());
			}
		}
     	
    	return dao.saveObject(definition, serializer);
    }

    /**
     * @see SerializedDefinitionService#purgeDefinition(Definition)
     */
    @Authorized
    public <T extends Definition> void purgeDefinition(T definition) {
    	dao.purgeObject(definition.getId());
    }
    
	/** 
	 * @see SerializedDefinitionService#purgeDefinition(String)
	 */
    @Authorized
	public void purgeDefinition(String uuid) {
		SerializedObject obj = dao.getSerializedObjectByUuid(uuid);
		dao.purgeObject(obj.getId());
	}

	/** 
	 * @see SerializedDefinitionService#getSerializedDefinitionByUuid(String)
	 */
    @Authorized
	public SerializedObject getSerializedDefinitionByUuid(String uuid) {
		return dao.getSerializedObjectByUuid(uuid);
	}

	/** 
	 * @see SerializedDefinitionService#saveSerializedDefinition(SerializedObject)
	 */
    @Authorized
	public void saveSerializedDefinition(SerializedObject serializedDefinition) {
		Definition d = dao.convertSerializedObject(Definition.class, serializedDefinition);
		dao.saveObject(d);
	}
}
