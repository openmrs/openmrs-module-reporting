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
package org.openmrs.module.reporting.logic;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicExpression;
import org.openmrs.logic.op.Operand;
import org.openmrs.logic.op.Operator;

/**
 * Mock implementation of Logic Criteria to get around issues using the actual Logic Service implementations
 */
public class MockLogicCriteria implements LogicCriteria {
	
	private String token;
	
	public MockLogicCriteria(String token) {
		this.token = token;
	}

	/**
	 * @see LogicCriteria#getRootToken()
	 */
	public String getRootToken() {
		return token;
	}
	
	/**
	 * @see LogicCriteria#getExpression()
	 */
	public LogicExpression getExpression() {
		return null;
	}

	/**
	 * @see LogicCriteria#getLogicParameters()
	 */
	public Map<String, Object> getLogicParameters() {
		return null;
	}

	/**
	 * @see LogicCriteria#after(java.util.Date)
	 */
	public LogicCriteria after(Date arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#and(LogicCriteria)
	 */
	public LogicCriteria and(LogicCriteria arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#appendCriteria(op.Operator, LogicCriteria)
	 */
	public LogicCriteria appendCriteria(Operator arg0, LogicCriteria arg1) {
		return null;
	}

	/**
	 * @see LogicCriteria#appendExpression(op.Operator, double)
	 */
	public LogicCriteria appendExpression(Operator arg0, double arg1) {
		return null;
	}

	/**
	 * @see LogicCriteria#appendExpression(op.Operator, op.Operand)
	 */
	public LogicCriteria appendExpression(Operator arg0, Operand arg1) {
		return null;
	}

	/**
	 * @see LogicCriteria#appendExpression(op.Operator, java.lang.String)
	 */
	public LogicCriteria appendExpression(Operator arg0, String arg1) {
		return null;
	}

	/**
	 * @see LogicCriteria#applyTransform(op.Operator)
	 */
	public LogicCriteria applyTransform(Operator arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#asOf(java.util.Date)
	 */
	public LogicCriteria asOf(Date arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#average()
	 */
	public LogicCriteria average() {
		return null;
	}

	/**
	 * @see LogicCriteria#before(java.util.Date)
	 */
	public LogicCriteria before(Date arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#contains(double)
	 */
	public LogicCriteria contains(double arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#contains(float)
	 */
	public LogicCriteria contains(float arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#contains(int)
	 */
	public LogicCriteria contains(int arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#contains(op.Operand)
	 */
	public LogicCriteria contains(Operand arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#contains(java.lang.String)
	 */
	public LogicCriteria contains(String arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#count()
	 */
	public LogicCriteria count() {
		return null;
	}

	/**
	 * @see LogicCriteria#distinct()
	 */
	public LogicCriteria distinct() {
		return null;
	}

	/**
	 * @see LogicCriteria#equalTo(double)
	 */
	public LogicCriteria equalTo(double arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#equalTo(float)
	 */
	public LogicCriteria equalTo(float arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#equalTo(int)
	 */
	public LogicCriteria equalTo(int arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#equalTo(op.Operand)
	 */
	public LogicCriteria equalTo(Operand arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#equalTo(java.lang.String)
	 */
	public LogicCriteria equalTo(String arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#exists()
	 */
	public LogicCriteria exists() {
		return null;
	}

	/**
	 * @see LogicCriteria#first()
	 */
	public LogicCriteria first() {
		return null;
	}

	/**
	 * @see LogicCriteria#first(java.lang.Integer, java.lang.String)
	 */
	public LogicCriteria first(Integer arg0, String arg1) {
		return null;
	}

	/**
	 * @see LogicCriteria#first(java.lang.Integer)
	 */
	public LogicCriteria first(Integer arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#first(java.lang.String)
	 */
	public LogicCriteria first(String arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#gt(double)
	 */
	public LogicCriteria gt(double arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#gt(float)
	 */
	public LogicCriteria gt(float arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#gt(int)
	 */
	public LogicCriteria gt(int arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#gt(op.Operand)
	 */
	public LogicCriteria gt(Operand arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#gte(double)
	 */
	public LogicCriteria gte(double arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#gte(float)
	 */
	public LogicCriteria gte(float arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#gte(int)
	 */
	public LogicCriteria gte(int arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#gte(op.Operand)
	 */
	public LogicCriteria gte(Operand arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#in(java.util.Collection)
	 */
	public LogicCriteria in(Collection<?> arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#last()
	 */
	public LogicCriteria last() {
		return null;
	}

	/**
	 * @see LogicCriteria#last(java.lang.Integer)
	 */
	public LogicCriteria last(Integer arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#lt(double)
	 */
	public LogicCriteria lt(double arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#lt(float)
	 */
	public LogicCriteria lt(float arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#lt(int)
	 */
	public LogicCriteria lt(int arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#lt(op.Operand)
	 */
	public LogicCriteria lt(Operand arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#lte(double)
	 */
	public LogicCriteria lte(double arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#lte(float)
	 */
	public LogicCriteria lte(float arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#lte(int)
	 */
	public LogicCriteria lte(int arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#lte(op.Operand)
	 */
	public LogicCriteria lte(Operand arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#not()
	 */
	public LogicCriteria not() {
		return null;
	}

	/**
	 * @see LogicCriteria#notExists()
	 */
	public LogicCriteria notExists() {
		return null;
	}

	/**
	 * @see LogicCriteria#or(LogicCriteria)
	 */
	public LogicCriteria or(LogicCriteria arg0) {
		return null;
	}

	/**
	 * @see LogicCriteria#setLogicParameters(java.util.Map)
	 */
	public void setLogicParameters(Map<String, Object> arg0) {
	}

	/**
	 * @see LogicCriteria#within(Duration)
	 */
	public LogicCriteria within(Duration arg0) {
		return null;
	}
}
