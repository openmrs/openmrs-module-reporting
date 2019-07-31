/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import org.openmrs.Concept;
import org.openmrs.Obs;

public class ObsFromObsGroupConverter implements DataConverter {

    private Concept concept;

    public ObsFromObsGroupConverter() {};

    public ObsFromObsGroupConverter(Concept concept) {
        this.concept = concept;
    }

    @Override
    public Object convert(Object original) {
        Obs o = (Obs) original;
        if (o == null) {
            return null;
        }
        // just returns the first match if more than one group member with that concept
        for (Obs groupMembers : o.getGroupMembers()) {
            if (groupMembers.getConcept().equals(concept)) {
                return groupMembers;
            }
        }
        return null;
    }

    @Override
    public Class<?> getInputDataType() {
        return Obs.class;
    }

    @Override
    public Class<?> getDataType() {
        return Obs.class;
    }
}
