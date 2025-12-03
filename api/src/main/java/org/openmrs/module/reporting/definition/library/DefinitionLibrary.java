/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
