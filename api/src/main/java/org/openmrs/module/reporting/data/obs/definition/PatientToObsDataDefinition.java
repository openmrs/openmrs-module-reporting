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
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;

public class PatientToObsDataDefinition extends JoinDataDefinition<PatientDataDefinition> implements ObsDataDefinition {


    /**
     * Default Constructor
     */
    public PatientToObsDataDefinition() {
        super();
    }

    /**
     * Default Constructor
     */
    public PatientToObsDataDefinition(PatientDataDefinition joinedDataDefinition) {
        super(joinedDataDefinition);
    }

    /**
     * Constructor to populate name
     */
    public PatientToObsDataDefinition(String name, PatientDataDefinition joinedDataDefinition) {
        super(name, joinedDataDefinition);
    }

    /**
     * @see JoinDataDefinition#getJoinedDefinitionType()
     */
    @Override
    public Class<PatientDataDefinition> getJoinedDefinitionType() {
        return PatientDataDefinition.class;
    }

}

