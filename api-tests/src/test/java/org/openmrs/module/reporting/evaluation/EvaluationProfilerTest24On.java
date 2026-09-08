package org.openmrs.module.reporting.evaluation;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.logging.MemoryAppender;
import org.openmrs.logging.OpenmrsLoggingUtil;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.test.OpenmrsVersionTestListener;
import org.openmrs.module.reporting.test.RequiresVersion;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.test.context.TestExecutionListeners;

/**
 * Tests for {@link EvaluationProfiler}
 */
@TestExecutionListeners(OpenmrsVersionTestListener.class)
@RequiresVersion("2.4.* - 2.*")
public class EvaluationProfilerTest24On extends BaseModuleContextSensitiveTest {
	
	protected EvaluationProfiler profiler1, profiler2;
	
	private static MemoryAppender appender;
	
	@BeforeClass
	public static void beforeClass() {
		appender = OpenmrsLoggingUtil.getMemoryAppender();
		
		if (appender == null) {
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			Configuration config = context.getConfiguration();
			
			appender = MemoryAppender.newBuilder().setName("MEMORY_APPENDER").setLayout(PatternLayout.createDefaultLayout())
					.setConfiguration(config).build();
			appender.start();
			
			config.addAppender(appender);
			
			AppenderRef appenderRef = AppenderRef.createAppenderRef("MEMORY_APPENDER", Level.ALL, null);
			LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.ALL, EvaluationProfiler.class.getName(), null,
					new AppenderRef[] { appenderRef }, null, config, null);
			loggerConfig.addAppender(appender, Level.ALL, null);
			config.addLogger(EvaluationProfiler.class.getName(), loggerConfig);
			context.updateLoggers();
		}
		
	}
	
	@AfterClass
	public static void afterClass() {
		((LoggerContext) LogManager.getContext()).updateLoggers();
	}
	
	/**
	 * Setup each test by configuring AOP on the relevant services and logging for the profiler class
	 */
	@Before
	public void setup() {
		profiler1 = new EvaluationProfiler(new EvaluationContext());
		profiler2 = new EvaluationProfiler(new EvaluationContext());
	}
	
	@Test
	public void integration() throws EvaluationException {
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("males");
		males.setMaleIncluded(true);
		
		CohortIndicator count = new CohortIndicator(); // No name, log message should use "?"
		count.setCohortDefinition(males, "");
		
		Context.getService(IndicatorService.class).evaluate(count, null);
		
		List<String> split = appender.getLogLines();
		Assert.assertEquals(6, split.size());
		Assert.assertTrue(split.get(0).contains("EVALUATION_STARTED"));
		Assert.assertTrue(split.get(1).contains(">"));
		Assert.assertTrue(split.get(1).contains("CohortIndicator"));
		Assert.assertTrue(split.get(2).contains(">>"));
		Assert.assertTrue(split.get(2).contains("GenderCohortDefinition[males]"));
		Assert.assertTrue(split.get(5).contains("EVALUATION_COMPLETED"));
	}
}
