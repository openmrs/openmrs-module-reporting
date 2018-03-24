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

import java.util.Collection;

/**
 * Returns the count of elements in a collection
 */
public class CountConverter implements DataConverter {

    private boolean returnNullInsteadOfZero = false;

    public CountConverter() {
    }

    public CountConverter(boolean returnNullInsteadOfZero) {
        this.returnNullInsteadOfZero = returnNullInsteadOfZero;
    }

    @Override
    public Object convert(Object original) {
        Collection c = (Collection) original;
        int size = c == null ? 0 : c.size();
        return (returnNullInsteadOfZero && size == 0) ? null : size;
    }

    @Override
    public Class<?> getInputDataType() {
        return Collection.class;
    }

    @Override
    public Class<?> getDataType() {
        return Integer.class;
    }
}
