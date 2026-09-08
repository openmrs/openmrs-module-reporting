/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.definition;

import org.openmrs.module.reporting.common.AuditInfo;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Gets details of who created and updated encounters, and when.
 * Note that the framework prevents getting voided encounters in reports, so the voided fields will always be false/null
 * for any results that are returned.
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class AuditInfoEncounterDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    @Override
    public Class<?> getDataType() {
        return AuditInfo.class;
    }

}
