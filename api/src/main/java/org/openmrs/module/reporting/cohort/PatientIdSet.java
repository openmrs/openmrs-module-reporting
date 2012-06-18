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
package org.openmrs.module.reporting.cohort;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.reporting.query.IdSet;

/**
 * A Set of Patient Ids
 */
public class PatientIdSet extends Cohort implements IdSet<Patient> {
	
	public static final long serialVersionUID = 1L;
	
	//***** CONSTRUCTORS *****
	
    public PatientIdSet() {
    	super();
    }
    
    public PatientIdSet(Set<Integer> memberIds) {
    	setMemberIds(memberIds);
    }
    
    public PatientIdSet(Integer... memberIds) {
    	for (Integer memberId : memberIds) {
    		addMember(memberId);
    	}
    }

    //***** INSTANCE METHODS *****
    
	/**
	 * @see Object#clone()
	 */
	@Override
	public PatientIdSet clone() {
		PatientIdSet ret = new PatientIdSet();
		ret.setMemberIds(new HashSet<Integer>(getMemberIds()));
		return ret;
	}
}
