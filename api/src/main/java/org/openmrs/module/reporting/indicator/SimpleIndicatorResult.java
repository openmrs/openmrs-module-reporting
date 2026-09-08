/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
