/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.obs.definition;

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

public class PersonToObsDataDefinition  extends JoinDataDefinition<PersonDataDefinition> implements ObsDataDefinition {

    /**
     * Default Constructor
     */
    public PersonToObsDataDefinition() {
        super();
    }

    /**
     * Default Constructor
     */
    public PersonToObsDataDefinition(PersonDataDefinition joinedDataDefinition) {
        super(joinedDataDefinition);
    }

    /**
     * Constructor to populate name
     */
    public PersonToObsDataDefinition(String name, PersonDataDefinition joinedDataDefinition) {
        super(name, joinedDataDefinition);
    }

    /**
     * @see JoinDataDefinition#getJoinedDefinitionType()
     */
    @Override
    public Class<PersonDataDefinition> getJoinedDefinitionType() {
        return PersonDataDefinition.class;
    }



}
