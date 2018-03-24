/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.reporting.query.IdSet;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Set of Patient Ids
 */
public class PatientIdSet extends Cohort implements IdSet<Patient> {
	
	public static final long serialVersionUID = 1L;
	
	//***** CONSTRUCTORS *****
	
    public PatientIdSet() {
    	super();
    }

    public PatientIdSet(List<Integer> memberIds) {
        setMemberIds(new HashSet<Integer>(memberIds));
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

    @Override
    public void setMemberIds(Set<Integer> memberIds) {
	    try {
            Method m = getClass().getMethod("setMemberships", Collection.class);
            if (m != null) {
                m.invoke(this, new HashSet<Integer>());
            }
        }
        catch (NoSuchMethodException nsme) {
	        // Do nothing
        }
        catch (Exception e) {
	        throw new IllegalStateException("Unable to invoke setMemberships", e);
        }
        super.setMemberIds(memberIds);
    }
}
