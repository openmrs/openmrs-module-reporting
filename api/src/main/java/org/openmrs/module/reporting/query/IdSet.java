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