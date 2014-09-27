package org.openmrs.module.reporting.data.converter;

import org.openmrs.api.context.Context;

/**
 * Identical to {@link ObjectFormatter} but requires that the user have a particular privilege.
 *
 * At run-time, if the user does not have #requiredPrivilege then this class returns #replacement instead of the usual
 * formatted value.
 */
public class PrivilegedDataFormatter extends ObjectFormatter {

    private String requiredPrivilege;

    private String replacement = "******";

    public PrivilegedDataFormatter() {
        super();
    }

    public PrivilegedDataFormatter(String requiredPrivilege) {
        super();
        this.requiredPrivilege = requiredPrivilege;
    }

    @Override
    public Object convert(Object o) {
        if (requiredPrivilege == null  || Context.hasPrivilege(requiredPrivilege)) {
            return super.convert(o);
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
