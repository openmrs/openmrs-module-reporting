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
package org.openmrs.module.reporting.query;

import org.openmrs.OpenmrsObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates a set of OpenmrsObject ids
 */
public abstract class BaseIdSet<T extends OpenmrsObject> implements IdSet<T> {
	
	//***** PROPERTIES *****

    private Set<Integer> memberIds;
    
    //***** CONSTRUCTORS *****
    
    public BaseIdSet() {
    	super();
    }
    
    public BaseIdSet(Set<Integer> memberIds) {
		this();
    	setMemberIds(memberIds);
    }
    
    public BaseIdSet(Integer... memberIds) {
		this();
    	add(memberIds);
    }
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the memberIds
	 */
	public Set<Integer> getMemberIds() {
		if (memberIds == null) {
			memberIds = new HashSet<Integer>();
		}
		return memberIds;
	}

	/**
	 * @param memberIds the memberIds to set
	 */
	public void setMemberIds(Set<Integer> memberIds) {
		this.memberIds = memberIds;
	}
	
	/**
	 * @param memberIds to add to the Query
	 */
	public void add(Integer... memberIds) {
		for (Integer memberId : memberIds) {
			getMemberIds().add(memberId);
		}
	}
	
	/**
	 * @see IdSet#contains(Integer)
	 */
	public boolean contains(Integer memberId) {
		return getMemberIds().contains(memberId);
	}

	/**
	 * @see IdSet#getSize() ()
	 */
	public int getSize() {
		return getMemberIds().size();
	}
	
	/**
	 * @see IdSet#isEmpty()
	 */
	public boolean isEmpty() {
		return getMemberIds().isEmpty();
	}

	/**
	 * @see Object#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public BaseIdSet<T> clone() {
		try {
			BaseIdSet<T> ret = this.getClass().newInstance();
			ret.setMemberIds(new HashSet<Integer>(getMemberIds()));
			return ret;
		}
		catch (Exception e) {
			throw new RuntimeException("Error while cloning an IdSet", e);
		}
	}
}
