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
package org.openmrs.module.reporting.query.person;

import java.util.Set;

import org.openmrs.Person;
import org.openmrs.module.reporting.query.BaseIdSet;

/**
 * A Set of Person Ids
 */
public class PersonIdSet extends BaseIdSet<Person> {
    
    public PersonIdSet() {
    	super();
    }
    
    public PersonIdSet(Set<Integer> memberIds) {
    	setMemberIds(memberIds);
    }
    
    public PersonIdSet(Integer... memberIds) {
    	add(memberIds);
    }
}