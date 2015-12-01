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
