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

import java.util.Set;

/**
 * Base IdSet Interface
 */
public interface IdSet {
    
    /**
     * @return the member ids within this IdSet
     */
    public Set<Integer> getMemberIds();
    
	/**
	 * @param memberIds to add to the IdSet
	 */
	public void add(Integer... memberIds);
	
	/**
	 * @param memberId to check within the idSet
	 */
	public boolean contains(Integer memberId);
	
	/**
	 * @return the number of ids in the set
	 */
	public int size();
}