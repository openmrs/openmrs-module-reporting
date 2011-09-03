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
package org.openmrs.module.reporting.idset;

import org.openmrs.Cohort;

/**
 * Patient Id Set IdSet
 */
public class PatientIdSet extends Cohort implements IdSet {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public PatientIdSet() { }
	
	/**
	 * Constructor that takes a Cohort
	 */
	public PatientIdSet(Cohort c) {
		super(c.getMemberIds());
	}

	/**
	 * @see IdSet#add(Integer[])
	 */
	public void add(Integer... memberIds) {
		for (Integer memberId : memberIds) {
			addMember(memberId);
		}
	}
    
}