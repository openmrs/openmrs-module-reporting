package org.openmrs.module.reporting.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlRunnerTest {

    @Test
    public void parseParametersIntoStatements_shouldEscapeParametersWithSingleQuotes() throws Exception {
        SqlRunner sqlRunner = new SqlRunner(null);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("generatedBy", "Fredrick 'Fred' Flintstone");
        List<String> results = sqlRunner.parseParametersIntoStatements(parameters);
        Assert.assertEquals("set @generatedBy='Fredrick ''Fred'' Flintstone'", results.get(0));
    }

}
