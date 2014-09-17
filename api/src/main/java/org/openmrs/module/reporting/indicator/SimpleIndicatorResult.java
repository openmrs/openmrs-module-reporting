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
package org.openmrs.module.reporting.indicator;

import org.openmrs.module.reporting.common.Fraction;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * A class containing generic indicator results.  @see SimpleIndicatorDataSetDefinition
 */
public class SimpleIndicatorResult implements IndicatorResult {
	
	public static final long serialVersionUID = 1L;
	
	private Indicator indicator;
    private EvaluationContext context;
    private Number numeratorResult;
    private Number denominatorResult;
    
    /**
     * Default Constructor
     */
    public SimpleIndicatorResult() {
    	super();
    }
    
	/**
	 * @see Evaluated#getDefinition()
	 */
	public Indicator getDefinition() {
		return indicator;
	}
	
	/**
	 * Returns the result Number if denominator is null, else returns 
	 * @param indicatorResult
	 * @return
	 */
	public static Number getResultValue(SimpleIndicatorResult indicatorResult) {
		if (indicatorResult.getNumeratorResult() == null) {
			return Double.NaN;
		}
		else if (indicatorResult.getDenominatorResult() == null) {
			return indicatorResult.getNumeratorResult();
		}
		else {
			if (Math.rint(indicatorResult.getNumeratorResult().doubleValue()) == indicatorResult.getNumeratorResult().doubleValue() &&
				Math.rint(indicatorResult.getDenominatorResult().doubleValue()) == indicatorResult.getDenominatorResult().doubleValue()) { //check for whole numbers
				return new Fraction(indicatorResult.getNumeratorResult().intValue(), indicatorResult.getDenominatorResult().intValue());
			}
			else {
				throw new RuntimeException("FRACTION indicator type is not currently supported by SimpleIndicatorResult if your numerator and denominator are not whole numbers.  If you are returning decimals in your queries, try changing your queries to use the samllest possible units.");
			}
		}
	}
	
    public Number getValue() {
    	return SimpleIndicatorResult.getResultValue(this);
    }
	
	public Indicator getIndicator() {
		return indicator;
	}
	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}
	public EvaluationContext getContext() {
		return context;
	}
	public void setContext(EvaluationContext context) {
		this.context = context;
	}
	public Number getNumeratorResult() {
		return numeratorResult;
	}
	public void setNumeratorResult(Number numeratorResult) {
		this.numeratorResult = numeratorResult;
	}
	public Number getDenominatorResult() {
		return denominatorResult;
	}
	public void setDenominatorResult(Number denominatorResult) {
		this.denominatorResult = denominatorResult;
	}

}
