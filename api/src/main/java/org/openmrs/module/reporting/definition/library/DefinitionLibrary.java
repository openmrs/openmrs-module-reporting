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

package org.openmrs.module.reporting.definition.library;

import org.openmrs.module.reporting.evaluation.Definition;

import java.util.List;

/**
 * Implementations of this class can conveniently implement on-the-fly-created reporting definitions, with inline
 * documentation on how they will behave.
 */
public interface DefinitionLibrary<T extends Definition> {

    Class<? super T> getDefinitionType();

    String getKeyPrefix();

    T getDefinition(String uuid);

    List<LibraryDefinitionSummary> getDefinitionSummaries();

}
