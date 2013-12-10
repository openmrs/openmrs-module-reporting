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
package org.openmrs.module.reporting.query.encounter;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.query.BaseIdSet;

import java.util.List;
import java.util.Set;

/**
 * A Set of Encounter Ids
 */
public class EncounterIdSet extends BaseIdSet<Encounter> {
    
    public EncounterIdSet() {
    	super();
    }
    
    public EncounterIdSet(Set<Integer> memberIds) {
    	setMemberIds(memberIds);
    }

    public EncounterIdSet(List<Integer> memberIds) {
        add(memberIds.toArray(new Integer[0]));
    }

    public EncounterIdSet(Integer... memberIds) {
    	add(memberIds);
    }
}