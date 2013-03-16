package org.openmrs.module.reporting.evaluation;

import junit.framework.Assert;
import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.evaluation.EvaluationUtil.evaluateParameterExpression;

/**
 *
 */
public class EvaluationUtilTest {

    private Date date = DateUtil.parseDate("2012-03-04", "yyyy-MM-dd");
    private Integer anInteger = 17;
    private Double aDouble = 5d;

    private Map<String,Object> parameters;

    @Before
    public void setUp() throws Exception {
        parameters = new HashMap<String, Object>();
        parameters.put("date", date);
        parameters.put("report.date", date);
        parameters.put("integer", anInteger);
        parameters.put("report.integer", anInteger);
        parameters.put("double", aDouble);
    }

    @Test
    public void evaluateParameterExpression_shouldSupportDateSubtraction() throws Exception {
        assertThat((Date) evaluateParameterExpression("date - 12h", parameters), is(DateUtils.addHours(date, -12)));
        assertThat((Date) evaluateParameterExpression("report.date - 4d", parameters), is(DateUtils.addDays(date, -4)));
        assertThat((Date) evaluateParameterExpression("report.date-1w", parameters), is(DateUtils.addDays(date, -7)));
        assertThat((Date) evaluateParameterExpression("date -3m", parameters), is(DateUtils.addMonths(date, -3)));
        assertThat((Date) evaluateParameterExpression("date-2y", parameters), is(DateUtils.addYears(date, -2)));
    }

    @Test
    public void evaluateParameterExpression_shouldSupportDateAddition() throws Exception {
        assertThat((Date) evaluateParameterExpression("date + 3", parameters), is(DateUtils.addDays(date, 3)));
        assertThat((Date) evaluateParameterExpression("date+ 12h", parameters), is(DateUtils.addHours(date, 12)));
        assertThat((Date) evaluateParameterExpression("date+ 5d", parameters), is(DateUtils.addDays(date, 5)));
        assertThat((Date) evaluateParameterExpression("date +1w", parameters), is(DateUtils.addDays(date, 7)));
        assertThat((Date) evaluateParameterExpression("date+6m", parameters), is(DateUtils.addMonths(date, 6)));
        assertThat((Date) evaluateParameterExpression("date + 7y", parameters), is(DateUtils.addYears(date, 7)));
    }

    @Test
    public void evaluateParameterExpression_shouldSupportMultipleOpsOnDate() throws Exception {
        Date expected = DateUtils.addDays(DateUtils.addMonths(DateUtils.addYears(date, 1), 6), -7);
        assertThat((Date) evaluateParameterExpression("date+1y+6m- 7d", parameters), is(expected));
    }

    @Test
    public void evaluateParameterExpression_shouldSupportIntegerArithmetic() throws Exception {
        assertThat((Integer) evaluateParameterExpression("integer + 1", parameters), is(anInteger + 1));
        assertThat((Integer) evaluateParameterExpression("report.integer- 10", parameters), is(anInteger - 10));
        assertThat((Integer) evaluateParameterExpression("integer *2", parameters), is(anInteger * 2));
        assertThat((Integer) evaluateParameterExpression("integer/3", parameters), is(anInteger / 3));
        assertThat((Integer) evaluateParameterExpression("report.integer +1 *2", parameters), is((anInteger + 1) * 2)); // note these are applied left-to-right
    }

    @Test
    public void evaluateParameterExpression_shouldSupportDoubleArithmetic() throws Exception {
        assertThat((Double) evaluateParameterExpression("double+1", parameters), is(aDouble + 1));
        assertThat((Double) evaluateParameterExpression("double+2.0", parameters), is(aDouble + 2d));
        assertThat((Double) evaluateParameterExpression("double -10", parameters), is(aDouble - 10));
        assertThat((Double) evaluateParameterExpression("double -12.5", parameters), is(aDouble - 12.5));
        assertThat((Double) evaluateParameterExpression("double*2", parameters), is(aDouble * 2));
        assertThat((Double) evaluateParameterExpression("double*3.2", parameters), is(aDouble * 3.2));
        assertThat((Double) evaluateParameterExpression("double / 3", parameters), is(aDouble / 3));
        assertThat((Double) evaluateParameterExpression("double / 1.5", parameters), is(aDouble / 1.5));
    }

    @Test
    public void evaluateParameterExpression_shouldFailForBadExpressions() throws Exception {
        String[] badExpressions = new String[]{
            "integer - 1y",
            "double + 2d",
            "integer + 1.5.2",
            "date + 1.5h",
            "date + 7x",
            "date / 2",
            "date * 3",
            "integer + x",
            "integer + double"
        };
        for (String badExpression : badExpressions) {
            try {
                Object actual = evaluateParameterExpression(badExpression, parameters);
                if (!actual.equals(badExpression)) {
                    Assert.fail("Expression should have failed: " + badExpression + " => " + actual);
                }
            } catch (ParameterException ex) {
                // expected
            }
        }

    }

}
