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
package org.openmrs.module.reporting.dataset.idset;

import java.util.Set;

import org.openmrs.OpenmrsObject;

/**
 * Base IdSet Interface
 */
public abstract class BaseIdSet<T extends OpenmrsObject> {
	
	private Set<Integer> memberIds;

	/**
	 * @return the memberIds
	 */
	public Set<Integer> getMemberIds() {
		return memberIds;
	}

	/**
	 * @param memberIds the memberIds to set
	 */
	public void setMemberIds(Set<Integer> memberIds) {
		this.memberIds = memberIds;
	}
}