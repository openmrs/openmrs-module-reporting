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
