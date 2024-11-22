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

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class AgeTest {

    @Test
    public void testEquals() throws Exception {
        Date d1 = DateUtil.parseYmd("2001-02-03");
        Date d2 = DateUtil.parseYmd("2002-03-04");
        Date d3 = DateUtil.parseYmd("2014-01-19");
        Date d4 = DateUtil.parseYmd("2014-01-20");

        Age age = new Age(d1, d4);
        assertThat(age, is(new Age(d1, d4)));
        assertThat(age, is(not(new Age(d2, d4))));
        assertThat(age, is(not(new Age(d1, d3))));
        assertThat(age, is(not(new Age(d2, d3))));
        assertThat(age, is(not(new Age(null, null))));
        assertFalse(age.equals(null)); // should not throw an exception
    }

	@Test
	public void testGetFullMonths() throws Exception {
		Age age = new Age(DateUtil.getDateTime(1965,3,23), DateUtil.getDateTime(2014,3,6));
		assertThat(age.getFullMonths(), is(587));
	}

	@Test
	public void testGetFullYears() throws Exception {
		Age age = new Age(DateUtil.getDateTime(1965,3,23), DateUtil.getDateTime(2014,3,6));
		assertThat(age.getFullYears(), is(48));
	}

	@Test
	public void testGetFullMonthsSinceLastBirthday() throws Exception {
		Age age = new Age(DateUtil.getDateTime(1965,3,23), DateUtil.getDateTime(2014,3,6));
		assertThat(age.getFullMonthsSinceLastBirthday(), is(11));
	}

    @Test
    public void testHashCode() throws Exception {
        Date d1 = DateUtil.parseYmd("2001-02-03");
        Date d2 = DateUtil.parseYmd("2002-03-04");
        Date d3 = DateUtil.parseYmd("2014-01-19");
        Date d4 = DateUtil.parseYmd("2014-01-20");

        int hashCode = new Age(d1, d4).hashCode();
        assertThat(hashCode, is(new Age(d1, d4).hashCode()));
        assertThat(hashCode, is(not(new Age(d2, d4).hashCode())));
        assertThat(hashCode, is(not(new Age(d1, d3).hashCode())));
        assertThat(hashCode, is(not(new Age(d2, d3).hashCode())));
        assertThat(hashCode, is(not(new Age(null, null).hashCode())));
    }

}
