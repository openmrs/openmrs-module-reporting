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
