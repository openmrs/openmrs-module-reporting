/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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