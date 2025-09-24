/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.Concept;
import org.openmrs.Obs;

/**
 * Represents, for a given person, the data that determines whether or not that person is on an Obs-based Active List
 */
public class ObsActiveList {
	
	//***** PROPERTIES *****
	
	private Integer personId;
	private Map<Concept, Map<Date, Obs>> startingObs = new HashMap<Concept, Map<Date, Obs>>();
	private Map<Concept, Map<Date, Obs>> endingObs = new HashMap<Concept, Map<Date, Obs>>();
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Full Constructor
	 */
	public ObsActiveList(Integer personId) {
		this.personId = personId;
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	/**
	 * Adds an Active List Item given the passed Obs
	 */
	public void addStartingObs(Obs o) {
		Map<Date, Obs> startDateToObs = startingObs.get(o.getValueCoded());
		if (startDateToObs == null) {
			startDateToObs = new TreeMap<Date, Obs>();
			startingObs.put(o.getValueCoded(), startDateToObs);
		}
		startDateToObs.put(o.getObsDatetime(), o);
	}
	
	/**
	 * Removes an Active List Item given the passed Obs
	 */
	public void addEndingObs(Obs o) {
		Map<Date, Obs> endDateToObs = endingObs.get(o.getValueCoded());
		if (endDateToObs == null) {
			endDateToObs = new TreeMap<Date, Obs>();
			endingObs.put(o.getValueCoded(), endDateToObs);
		}
		endDateToObs.put(o.getObsDatetime(), o);
	}
	
	/**
	 * @return the most recent Obs that started a Concept on the list
	 */
	public Obs getMostRecentStartObs(Concept c) {
		Obs ret = null;
		Map<Date, Obs> m = startingObs.get(c);
		if (m != null) {
			for (Date d : m.keySet()) {
				ret = m.get(d);
			}
		}
		return ret;
	}
	
	/**
	 * @return the most recent Obs that ended a Concept on the list
	 */
	public Obs getMostRecentEndObs(Concept c) {
		Obs ret = null;
		Map<Date, Obs> m = endingObs.get(c);
		if (m != null) {
			for (Date d : m.keySet()) {
				ret = m.get(d);
			}
		}
		return ret;
	}
	
	/**
	 * @return the first Obs after the most recent end Obs
	 */
	public Obs getActiveItem(Concept c) {
		Obs mostRecentEndObs = getMostRecentEndObs(c);
		Map<Date, Obs> m = startingObs.get(c);
		if (m != null) {
			for (Date d : m.keySet()) {
				if (mostRecentEndObs == null || d.after(mostRecentEndObs.getObsDatetime())) {
					return m.get(d);
				}
			}
		}
		return null;
	}
	
	/**
	 * @return a List of Active List Items
	 */
	@SuppressWarnings("unchecked")
	public List<Obs> getActiveItems() {
		List<Obs> ret = new ArrayList<Obs>();
		for (Concept c : startingObs.keySet()) {
			Obs o = getActiveItem(c);
			if (o != null) {
				ret.add(o);
			}
		}
		Collections.sort(ret, new BeanPropertyComparator("obsDatetime"));
		return ret;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the personId
	 */
	public Integer getPersonId() {
		return personId;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	/**
	 * @return the startingObs
	 */
	public Map<Concept, Map<Date, Obs>> getStartingObs() {
		return startingObs;
	}

	/**
	 * @param startingObs the startingObs to set
	 */
	public void setStartingObs(Map<Concept, Map<Date, Obs>> startingObs) {
		this.startingObs = startingObs;
	}

	/**
	 * @return the endingObs
	 */
	public Map<Concept, Map<Date, Obs>> getEndingObs() {
		return endingObs;
	}

	/**
	 * @param endingObs the endingObs to set
	 */
	public void setEndingObs(Map<Concept, Map<Date, Obs>> endingObs) {
		this.endingObs = endingObs;
	}
}
