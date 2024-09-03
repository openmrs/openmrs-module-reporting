/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the methods in SqlScriptParser
 */
public class SqlUtilsTest {

	@Test
	public void isSelectQuery_shouldAllowSelectStatements() {
		Assert.assertTrue(SqlUtils.isSelectQuery("select * from foo where 1=1"));
		Assert.assertTrue(SqlUtils.isSelectQuery("select * from foo_alter where bardrop = 1"));
	}

	@Test
	public void isSelectQuery_shouldNotAllowInvalidKeywords() {
		Assert.assertFalse(SqlUtils.isSelectQuery("alter foo add column bar"));
		Assert.assertFalse(SqlUtils.isSelectQuery("insert into foo (bar) values (1, 2)"));
		Assert.assertFalse(SqlUtils.isSelectQuery("update foo set bar = 1;"));
		Assert.assertFalse(SqlUtils.isSelectQuery("delete bar from foo"));
		Assert.assertFalse(SqlUtils.isSelectQuery("drop table foo"));
		Assert.assertFalse(SqlUtils.isSelectQuery("create table bar"));
		Assert.assertFalse(SqlUtils.isSelectQuery("rename table foo bar"));
		Assert.assertFalse(SqlUtils.isSelectQuery("select foo from bar into foo2"));
	}

	@Test
	public void isSelectQuery_shouldHandleMultipleStatements() {
		Assert.assertFalse(SqlUtils.isSelectQuery("select * from foo; delete bar from foo"));
		Assert.assertTrue(SqlUtils.isSelectQuery("select * from foo;  select * from bar;"));
	}
}
