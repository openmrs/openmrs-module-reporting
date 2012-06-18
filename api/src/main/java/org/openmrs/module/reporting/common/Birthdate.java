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
package org.openmrs.module.reporting.common;

import java.util.Date;

/**
 * Represents a birthdate which can be either exact or estimated
 */
public class Birthdate {
	
	//***********************
	// PROPERTIES
	//***********************
	
	private Date birthdate;
	private boolean estimated = false;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Full Constructor
	 */
	public Birthdate(Date birthdate) {
		this(birthdate, false);
	}
	
	/**
	 * Full Constructor
	 */
	public Birthdate(Date birthdate, boolean estimated) {
		this.birthdate = birthdate;
		this.estimated = estimated;
	}
	
	//***********************
	// INSTANCE METHOS
	//***********************
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return (isEstimated() ? "~" : "") + DateUtil.formatDate(birthdate, "dd/MMM/yyyy", "");
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the birthdate
	 */
	public Date getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return the estimated
	 */
	public boolean isEstimated() {
		return estimated;
	}

	/**
	 * @param estimated the estimated to set
	 */
	public void setEstimated(boolean estimated) {
		this.estimated = estimated;
	}
}
