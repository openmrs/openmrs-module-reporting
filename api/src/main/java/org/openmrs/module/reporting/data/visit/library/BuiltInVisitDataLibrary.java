/*
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

package org.openmrs.module.reporting.data.visit.library;

import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitIdDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.stereotype.Component;

/**
 * Basic set of visit data columns
 */
@Component
public class BuiltInVisitDataLibrary extends BaseDefinitionLibrary<VisitDataDefinition> {

    public static final String PREFIX = "reporting.library.visitDataDefinition.builtIn.";

    @Override
    public Class<? super VisitDataDefinition> getDefinitionType() {
        return VisitDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition("visitId")
    public VisitDataDefinition getVisitId() {
        return new VisitIdDataDefinition();
    }
}
