/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.joda.time.Years;

/**
 * Periods of time during which one might run a report.
 */
public enum TimePeriod {
    DAILY (DateUtil.DAILY, Days.ONE),
    WEEKLY (DateUtil.WEEKLY, Weeks.ONE),
    MONTHLY (DateUtil.MONTHLY, Months.ONE),
    QUARTERLY (DateUtil.QUARTERLY, Months.THREE),
    SEMI_ANNUALLY (null, Months.SIX),
    YEARLY (DateUtil.ANNUALLY, Years.ONE);

    private Integer dateUtilConstant;
    private ReadablePeriod jodaPeriod;

    TimePeriod(Integer dateUtilConstant, ReadablePeriod period) {
        this.dateUtilConstant = dateUtilConstant;
        this.jodaPeriod = period;
    }

    public Integer getDateUtilConstant() {
        return dateUtilConstant;
    }

    public ReadablePeriod getJodaPeriod() {
        return jodaPeriod;
    }

}
