/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.definition;

import org.openmrs.module.reporting.data.BaseDefinitionLibraryDataDefinition;

/**
 * Lets you evaluate a {@link PatientDataDefinition} that is looked up in
 * {@link org.openmrs.module.reporting.definition.library.AllDefinitionLibraries} at evaluation time
 *
 * We intentionally do not define a CachingStrategy since we are just delegating to another definition.
 */
public class DefinitionLibraryPatientDataDefinition extends BaseDefinitionLibraryDataDefinition implements PatientDataDefinition {

    public DefinitionLibraryPatientDataDefinition() {
    }

    public DefinitionLibraryPatientDataDefinition(String definitionKey) {
        setDefinitionKey(definitionKey);
    }

}
