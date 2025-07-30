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

import java.util.Date;

import org.openmrs.Concept;

/**
 * Encapsulates the data of whether death is true or false, and if true, what the date and reason is
 */
public class VitalStatus {
	
	//***********************
	// PROPERTIES
	//***********************
	
	private Boolean dead;
	private Date deathDate;
	private Concept causeOfDeath;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default Constructor
	 */
	public VitalStatus() { }
	
	/**
	 * Full Constructor
	 */
	public VitalStatus(Boolean dead, Date deathDate, Concept causeOfDeath) {
		this.dead = dead;
		this.deathDate = deathDate;
		this.causeOfDeath = causeOfDeath;
	}
	
	//***********************
	// INSTANCE METHOS
	//***********************
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		String ret = "alive";
		if (dead == Boolean.TRUE) {
			ret = "died";
			ret += (deathDate == null ? "" : " on " + DateUtil.formatDate(deathDate, "dd/MMM/yyyy", ""));
			ret += (causeOfDeath == null ? "" : " due to " + causeOfDeath.getDisplayString());
		}
		return ret;
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the dead
	 */
	public Boolean getDead() {
		return dead;
	}

	/**
	 * @param dead the dead to set
	 */
	public void setDead(Boolean dead) {
		this.dead = dead;
	}

	/**
	 * @return the deathDate
	 */
	public Date getDeathDate() {
		return deathDate;
	}

	/**
	 * @param deathDate the deathDate to set
	 */
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	/**
	 * @return the causeOfDeath
	 */
	public Concept getCauseOfDeath() {
		return causeOfDeath;
	}

	/**
	 * @param causeOfDeath the causeOfDeath to set
	 */
	public void setCauseOfDeath(Concept causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}
}
