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

import java.io.StringReader;

import org.junit.Assert;

import org.junit.Test;

/**
 * Tests the methods in SqlScriptParser
 */
public class SqlScriptParserTest {
	
	@Test
	public void parse_shouldHandleSingleLineCommentsWithSingleLineDelimiters() {
		String sql = "select * from patient";
		
		StringBuilder sb = new StringBuilder();
		sb.append("-- Some comment \n");
		sb.append("--Another comment\n");
		sb.append("-- Yest another comment \n");
		sb.append(sql);
		
		String[] sqlStatements = SqlScriptParser.parse(new StringReader(sb.toString()));
		Assert.assertEquals(1, sqlStatements.length);
		Assert.assertEquals(sql, sqlStatements[0]);
	}
	
	@Test
	public void parse_shouldHandleMultiLineCommentsWithDelimiterOnSameLineAsComment() {
		String sql = "select * from patient";
		
		//with comment delimiters on same lines as comment.
		StringBuilder sb = new StringBuilder();
		sb.append("/* Some multi \n");
		sb.append("line comment\n");
		sb.append("which ends here */ \n");
		sb.append(sql);
		
		String[] sqlStatements = SqlScriptParser.parse(new StringReader(sb.toString()));
		Assert.assertEquals(1, sqlStatements.length);
		Assert.assertEquals(sql, sqlStatements[0]);
	}
	
	@Test
	public void parse_shouldHandleMultiLineCommentsWithDelimiterOnSeparateLinesFromComment() {
		String sql = "select * from patient";
		
		//with comment delimiters on separate lines from comment
		StringBuilder sb = new StringBuilder();
		sb.append("/* \n");
		sb.append("Some multi \n");
		sb.append("line comment \n");
		sb.append("which ends here \n");
		sb.append("*/ \n");
		sb.append(sql);
		
		String[] sqlStatements = SqlScriptParser.parse(new StringReader(sb.toString()));
		Assert.assertEquals(1, sqlStatements.length);
		Assert.assertEquals(sql, sqlStatements[0]);
	}
	
	@Test
	public void parse_shouldHandleOneLineCommentWithMultiLineDelimiters() {
		String sql = "select * from patient";
		
		//one line comment but with multiple line comment delimiters
		StringBuilder sb = new StringBuilder();
		sb.append("/* one line comment */ \n");
		sb.append(sql);
		
		String[] sqlStatements = SqlScriptParser.parse(new StringReader(sb.toString()));
		Assert.assertEquals(1, sqlStatements.length);
		Assert.assertEquals(sql, sqlStatements[0]);
	}
}
