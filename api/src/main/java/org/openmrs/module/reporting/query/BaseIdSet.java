/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query;

import org.openmrs.OpenmrsObject;

import java.util.Collection;
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

    public void retainAll(IdSet<T> set) {
        getMemberIds().retainAll(set.getMemberIds());
    }

    public void removeAll(IdSet<T> set) {
        getMemberIds().removeAll(set.getMemberIds());
    }

    public void addAll(IdSet<T> set) {
        getMemberIds().addAll(set.getMemberIds());
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
	 * @param memberIds to add to the Query
	 */
	public void addAll(Collection<Integer> memberIds) {
		getMemberIds().addAll(memberIds);
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

	@Override
	public String toString() {
		return getMemberIds().toString();
	}
}
