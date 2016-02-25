/**
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
