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
package org.openmrs.module.reporting.encounter.query.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.encounter.query.db.EncounterQueryDAO;

/**
 * Default implementation of the {@link EncounterQueryService}
 * <p>
 * This class should not be instantiated alone, get a service class from the Context:
 * Context.getService(EncounterQueryService.class)
 * 
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.module.reporting.encounter.query.service.EncounterQueryService
 */
public class EncounterQueryServiceImpl  extends BaseOpenmrsService implements EncounterQueryService {

	protected final Log log = LogFactory.getLog(getClass());
	
	protected EncounterQueryDAO dao;
	    
	/**
	 * @see org.openmrs.module.reporting.encounter.query.service.EncounterQueryService#setEncounterDAO(org.openmrs.module.reporting.encounter.query.db.EncounterQueryDAO)
	 */
	public void setEncounterQueryDAO(EncounterQueryDAO dao) {
	    this.dao = dao;
	}
	
	
	/*
	 * @see org.openmrs.module.reporting.encounter.query.service.EncounterQueryService#getEncounters(org.openmrs.Cohort, java.util.List, java.util.List, java.util.Date, java.util.Date, org.openmrs.module.reporting.common.TimeQualifier, java.lang.Integer)
	 */
	public List<Encounter> getEncounters(Cohort cohort, List<EncounterType> encounterTypes, List<Form> forms, Date encounterDatetimeOnOrAfter, Date encounterDatetimeOnOrBefore, TimeQualifier whichEncounterQualifier, Integer quantity) {
		
		List<Encounter> encounters = dao.getEncounters(cohort, encounterTypes, forms, encounterDatetimeOnOrAfter, encounterDatetimeOnOrBefore, whichEncounterQualifier);
		
		if (quantity != null){
	           Map<Patient, List<Encounter>> map = new LinkedHashMap<Patient, List<Encounter>>(); 
	           for (Encounter enc:encounters){
	               if (!map.containsKey(enc.getPatient())){
	                   List<Encounter> eList = new ArrayList<Encounter>();
	                   eList.add(enc);
	                   map.put(enc.getPatient(), eList);
	               } else {
	                   List<Encounter> eList = map.get(enc.getPatient());
	                   if (eList.size() < quantity){
	                       eList.add(enc);
	                       map.put(enc.getPatient(), eList);
	                   }
	               }
	           }
	           List<Encounter> ret = new ArrayList<Encounter>();
	           for (Map.Entry<Patient, List<Encounter>> e : map.entrySet()){
	               for (Encounter enc : e.getValue()){
	                   ret.add(enc);
	               }
	           }
	           encounters = ret;
	        }
		
		return encounters;
	}
}
