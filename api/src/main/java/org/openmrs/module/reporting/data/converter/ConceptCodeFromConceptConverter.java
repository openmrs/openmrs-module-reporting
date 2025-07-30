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
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;

public class ConceptCodeFromConceptConverter implements DataConverter {

    private String conceptSourceName;

    public ConceptCodeFromConceptConverter() {};

    public ConceptCodeFromConceptConverter(String conceptSourceName) {
        this.conceptSourceName = conceptSourceName;
    }

    public ConceptCodeFromConceptConverter(ConceptSource conceptSource) {
        this.conceptSourceName = conceptSource.getName();
    }

    @Override
    public Object convert(Object original) {

        Concept concept = (Concept) original;

        if (concept == null) {
            return null;
        }

        for (ConceptMap map : concept.getConceptMappings()) {
            // TODO right now assumes only one reference term per source, just returns first term found
            if (map.getConceptReferenceTerm().getConceptSource().getName().equalsIgnoreCase(conceptSourceName)) {
                return map.getConceptReferenceTerm().getCode();
            }
        }
        return null;
    }

    @Override
    public Class<?> getInputDataType() {
        return Concept.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }
}
