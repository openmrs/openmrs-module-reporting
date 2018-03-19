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

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.reporting.query.IdSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A Set of Patient Ids
 */
public class PatientIdSet extends Cohort implements IdSet<Patient> {
	
	public static final long serialVersionUID = 1L;

    private Set<Integer> memberIds;

	//***** CONSTRUCTORS *****
	
    public PatientIdSet() {
    	memberIds = new HashSet<Integer>();
    }

    public PatientIdSet(Collection<Integer> memberIds) {
       this.memberIds = new HashSet<Integer>(memberIds);
    }
    
    public PatientIdSet(Integer... memberIds) {
        this.memberIds = new HashSet<Integer>();
    	for (Integer memberId : memberIds) {
    		this.memberIds.add(memberId);
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

    @Override
    public boolean contains(Integer memberId) {
        return memberIds.contains(memberId);
    }

    @Override
    public int getSize() {
        return memberIds.size();
    }

    @Override
    public boolean isEmpty() {
        return memberIds == null || memberIds.size() == 0;
    }

    @Override
    public Set<Integer> getMemberIds() {
        return memberIds;
    }

    @Override
    public void setMemberIds(Set<Integer> memberIds) {
        this.memberIds = memberIds;
    }

    public void retainAll(IdSet<Patient> set) {
	    Set<Integer> s = getMemberIds();
        s.retainAll(set.getMemberIds());
        setMemberIds(s);
    }

    public void removeAll(IdSet<Patient> set) {
        Set<Integer> s = getMemberIds();
        s.removeAll(set.getMemberIds());
        setMemberIds(s);
    }

    public void addAll(IdSet<Patient> set) {
        Set<Integer> s = getMemberIds();
        s.addAll(set.getMemberIds());
        setMemberIds(s);
    }

}
