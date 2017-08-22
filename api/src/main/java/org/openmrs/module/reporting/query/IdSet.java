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

import java.util.Set;

/**
 * IdSet Interface
 */
public interface IdSet<T extends OpenmrsObject> extends Cloneable {

	/**
	 * @return all memberIds in the set
	 */
	Set<Integer> getMemberIds();

    /**
     * @param memberIds the memberIds to set
     */
	void setMemberIds(Set<Integer> memberIds);

    /**
     * Retains only those members that exist in the passed set
     */
	void retainAll(IdSet<T> set);

    /**
     * Removes those members that exist in the passed set
     */
	void removeAll(IdSet<T> set);

    /**
     * Adds all members from the passed set
     */
	void addAll(IdSet<T> set);

	/**
	 * @return true of the passed memberId exists in the set
	 */
	boolean contains(Integer memberId);

	/**
	 * @return the number of members within the set
	 */
	int getSize();
	
	/**
	 * @return true if there are no members within the set
	 */
	boolean isEmpty();
	
	/**
	 * @return a cloned copy of the current IdSet
	 */
	IdSet<T> clone();
}