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

import org.openmrs.api.context.Context;

/**
 * Used to require the currently-authenticated user to have a certain privilege in order to see data.
 *
 * This converter will do a privilege check, and depending on the result it will either pass through the original input,
 * or return a replacement string.
 *
 * This class may return the input datatype or String, so you typically want it to be at the end of the converter chain,
 * i.e. _after_ any PropertyConverters. (It would still be common to put ObjectFormatter after this.)
 */
public class PrivilegedDataConverter implements DataConverter {

    private String requiredPrivilege;

    private String replacement = "******";

    public PrivilegedDataConverter() {
    }

    public PrivilegedDataConverter(String requiredPrivilege) {
        this.requiredPrivilege = requiredPrivilege;
    }

    @Override
    public Class<?> getInputDataType() {
        return Object.class;
    }

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }

    @Override
    public Object convert(Object original) {
        if (requiredPrivilege == null  || Context.hasPrivilege(requiredPrivilege)) {
            return original;
        } else {
            return replacement;
        }
    }

    public String getRequiredPrivilege() {
        return requiredPrivilege;
    }

    public void setRequiredPrivilege(String requiredPrivilege) {
        this.requiredPrivilege = requiredPrivilege;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

}
