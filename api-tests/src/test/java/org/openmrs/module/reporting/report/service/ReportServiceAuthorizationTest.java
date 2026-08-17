/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.processor.LoggingReportProcessor;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.PrivilegeConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Verifies that the {@link Authorized} annotations on {@link ReportService} are actually enforced by
 * the OpenMRS authorization advice.
 * <p/>
 * Each test authenticates as a purpose-built user, since the user that
 * {@link BaseModuleContextSensitiveTest} authenticates by default is a superuser and would bypass
 * every check. That user holds only the reporting privileges the test asks for, plus every core
 * OpenMRS privilege. Granting the core privileges keeps the tests focused on the reporting privileges:
 * without them, calls would fail with an unrelated {@link APIAuthenticationException} (for example
 * when the serializer loads the creator of a definition, which needs "Get Users").
 */
public class ReportServiceAuthorizationTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	/**
	 * Every privilege this module declares in its config.xml. Together with core's "View Reports" these
	 * form {@link #GRANTABLE_PRIVILEGES}, and are withheld from the test user unless a test asks for them.
	 */
	private static final Set<String> REPORTING_PRIVILEGES = new HashSet<String>(Arrays.asList(
	    ReportingConstants.PRIV_MANAGE_REPORTS, ReportingConstants.PRIV_MANAGE_REPORT_DEFINITIONS,
	    ReportingConstants.PRIV_MANAGE_DATA_SET_DEFINITIONS, ReportingConstants.PRIV_MANAGE_INDICATOR_DEFINITIONS,
	    ReportingConstants.PRIV_MANAGE_DIMENSION_DEFINITIONS, ReportingConstants.PRIV_MANAGE_COHORT_DEFINITIONS,
	    ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS, ReportingConstants.PRIV_MANAGE_SCHEDULED_REPORT_TASKS,
	    ReportingConstants.PRIV_RUN_REPORTS));

	/**
	 * Everything a @Authorized annotation on this service is allowed to name: the module's own privileges
	 * plus the two core privileges the service relies on, "View Reports" for reads and "Delete Reports" for
	 * purges.
	 */
	private static final Set<String> GRANTABLE_PRIVILEGES = new HashSet<String>(REPORTING_PRIVILEGES);

	static {
		GRANTABLE_PRIVILEGES.add(ReportingConstants.PRIV_VIEW_REPORTS);
		GRANTABLE_PRIVILEGES.add(ReportingConstants.PRIV_DELETE_REPORTS);
	}

	/** Static so that repeated calls within one test still produce unique role and user names. */
	private static int userCount = 0;

	private final List<String> proxyPrivileges = new ArrayList<String>();

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@After
	public void restoreSuperUser() {
		for (String privilege : proxyPrivileges) {
			Context.removeProxyPrivilege(privilege);
		}
		proxyPrivileges.clear();
		// Drop the restricted user so the next test does not inherit it. Deliberately no re-authentication
		// here: the framework's own setup authenticates before every test, on a fresh transaction. Doing
		// it here instead runs against this test's session, where deserializing a definition has left a
		// modified User attached, so authenticating as admin fails - and the inherited authenticate()
		// reacts to that failure by prompting for credentials on stdin, which would hang the build.
		Context.logout();
	}

	private ReportService getReportService() {
		return Context.getService(ReportService.class);
	}

	/**
	 * Creates a role holding exactly the passed reporting privileges, assigns it to a new user, and
	 * authenticates as that user, then tops the user up with the core privileges described on
	 * {@link #grantCoreOpenmrsPrivileges()}. The reporting privileges are created on demand because the
	 * module's config.xml, which is what normally creates them, is not processed in the api-tests
	 * context.
	 */
	private void authenticateWithReportingPrivileges(String... reportingPrivileges) {
		UserService userService = Context.getUserService();

		String suffix = getClass().getSimpleName() + (++userCount);
		Role role = new Role("Role " + suffix, "Role for " + suffix);
		for (String privilegeName : reportingPrivileges) {
			Privilege privilege = userService.getPrivilege(privilegeName);
			if (privilege == null) {
				privilege = userService.savePrivilege(new Privilege(privilegeName, privilegeName));
			}
			role.addPrivilege(privilege);
		}
		userService.saveRole(role);

		Person person = new Person();
		person.setGender("F");
		person.addName(new PersonName("Test", null, suffix));

		User user = new User(person);
		user.setUsername("user" + suffix);
		user.addRole(role);

		String password = "Test1234";
		userService.createUser(user, password);
		Context.flushSession();

		Context.logout();
		Context.authenticate(user.getUsername(), password);
		assertEquals(user.getUsername(), Context.getAuthenticatedUser().getUsername());
		grantCoreOpenmrsPrivileges();

		// Pin down exactly which reporting privileges the user has. Without this the negative tests could
		// pass for the wrong reason - an APIAuthenticationException raised by some unrelated check.
		Set<String> granted = new HashSet<String>(Arrays.asList(reportingPrivileges));
		for (String privilegeName : granted) {
			assertTrue("test user should hold " + privilegeName, Context.hasPrivilege(privilegeName));
		}
		for (String privilegeName : GRANTABLE_PRIVILEGES) {
			if (!granted.contains(privilegeName)) {
				assertFalse("test user should not hold " + privilegeName, Context.hasPrivilege(privilegeName));
			}
		}
	}

	/**
	 * Grants the authenticated user every privilege core declares in {@link PrivilegeConstants}, other than
	 * any privilege this service's annotations may name (see {@link #GRANTABLE_PRIVILEGES}, which includes
	 * core's "View Reports"), so that the tests only ever fail on a privilege under test. Without this,
	 * calls fail on unrelated core checks - for example the serializer looks up the creator of a
	 * definition, which needs "Get Users".
	 * <p/>
	 * These are granted as proxy privileges rather than as real role privileges because the in-memory
	 * test database contains very few privilege rows, and creating a couple of hundred of them per test
	 * is prohibitively slow.
	 */
	private void grantCoreOpenmrsPrivileges() {
		for (Field field : PrivilegeConstants.class.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()) || !String.class.equals(field.getType())) {
				continue;
			}
			try {
				String value = (String) field.get(null);
				// PrivilegeConstants also holds a couple of string constants that are not privileges
				if (value != null && !value.contains(".") && !GRANTABLE_PRIVILEGES.contains(value)) {
					Context.addProxyPrivilege(value);
					proxyPrivileges.add(value);
				}
			}
			catch (IllegalAccessException e) {
				throw new IllegalStateException("Unable to read " + field.getName(), e);
			}
		}
	}

	//***** METADATA COVERAGE *****

	/**
	 * Guards against new service methods being added without any privilege requirement at all.
	 */
	@Test
	public void everyReportServiceMethodShouldDeclareRequiredPrivileges() {
		List<String> unannotated = new ArrayList<String>();
		for (Method method : ReportService.class.getDeclaredMethods()) {
			Authorized authorized = method.getAnnotation(Authorized.class);
			if (authorized == null || authorized.value().length == 0) {
				unannotated.add(method.getName());
			}
		}
		assertTrue("ReportService methods without @Authorized privileges: " + unannotated, unannotated.isEmpty());
	}

	/**
	 * Every privilege named in an @Authorized annotation must be one that actually exists, otherwise the
	 * annotation guards against a privilege no administrator can grant.
	 */
	@Test
	public void everyDeclaredPrivilegeShouldBeAReportingPrivilege() {
		List<String> unknown = new ArrayList<String>();
		for (Method method : ReportService.class.getDeclaredMethods()) {
			Authorized authorized = method.getAnnotation(Authorized.class);
			if (authorized != null) {
				for (String privilege : authorized.value()) {
					if (!GRANTABLE_PRIVILEGES.contains(privilege)) {
						unknown.add(method.getName() + " -> " + privilege);
					}
				}
			}
		}
		assertTrue("@Authorized privileges that are neither this module's nor core's: " + unknown, unknown.isEmpty());
	}

	/**
	 * "View Reports" is a read-only privilege: it must never appear on a method that creates, modifies or
	 * deletes anything, otherwise a report consumer could mutate reporting data.
	 */
	@Test
	public void viewReportsShouldNeverBeAcceptedByAWriteMethod() {
		List<String> offenders = new ArrayList<String>();
		for (Method method : ReportService.class.getDeclaredMethods()) {
			Authorized authorized = method.getAnnotation(Authorized.class);
			if (authorized == null || !Arrays.asList(authorized.value()).contains(ReportingConstants.PRIV_VIEW_REPORTS)) {
				continue;
			}
			String name = method.getName();
			if (name.startsWith("save") || name.startsWith("purge") || name.startsWith("delete")
			        || name.startsWith("run") || name.startsWith("queue") || name.startsWith("process")
			        || name.startsWith("log") || name.startsWith("persist")) {
				offenders.add(name);
			}
		}
		assertTrue("write methods must not accept View Reports: " + offenders, offenders.isEmpty());
	}

	/**
	 * This module follows the usual OpenMRS convention of one privilege per service method. A method listing
	 * several privileges would be satisfied by any one of them, since {@link Authorized} defaults to
	 * {@code requireAll = false}, which quietly widens access.
	 */
	@Test
	public void everyMethodShouldRequireExactlyOnePrivilege() {
		List<String> offenders = new ArrayList<String>();
		for (Method method : ReportService.class.getDeclaredMethods()) {
			Authorized authorized = method.getAnnotation(Authorized.class);
			if (authorized != null && authorized.value().length != 1) {
				offenders.add(method.getName() + " -> " + Arrays.toString(authorized.value()));
			}
		}
		assertTrue("methods requiring more than one privilege: " + offenders, offenders.isEmpty());
	}

	//***** VIEW REPORTS (report consumer) *****

	@Test
	public void getReportRequests_shouldBeAllowedForRunReports() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS, ReportingConstants.PRIV_RUN_REPORTS);
		assertNotNull(getReportService().getReportRequests(null, null, null));
	}

	@Test
	public void loadReportLog_shouldBeAllowedForViewReports() {
		ReportRequest request = newReportRequest();
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS);
		// a request that was never run has no log file, but the call must not be rejected
		getReportService().loadReportLog(request);
	}

	@Test
	public void getRenderingModes_shouldBeAllowedForManageReports() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS, ReportingConstants.PRIV_MANAGE_REPORTS);
		assertNotNull(getReportService().getRenderingModes(new ReportDefinition()));
	}

	@Test
	public void getAllReportDesigns_shouldBeAllowedForManageReportDesigns() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS, ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS);
		assertNotNull(getReportService().getAllReportDesigns(true));
	}

	@Test
	public void saveReport_shouldNotBeAllowedForViewReports() {
		final ReportRequest request = newReportRequest();
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().saveReport(new Report(request), "should not be permitted");
			}
		});
	}

	@Test
	public void purgeReportRequest_shouldNotBeAllowedForViewReports() {
		final ReportRequest request = newReportRequest();
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().purgeReportRequest(request);
			}
		});
	}

	@Test
	public void queueReport_shouldNotBeAllowedForViewReports() {
		final ReportRequest request = newReportRequest();
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().queueReport(request);
			}
		});
	}

	//***** REPORT DESIGNS *****

	@Test
	public void saveReportDesign_shouldRequireManageReportDesigns() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_RUN_REPORTS,
		    ReportingConstants.PRIV_VIEW_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().saveReportDesign(newReportDesign());
			}
		});
	}

	@Test
	public void saveReportDesign_shouldBeAllowedForManageReportDesigns() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS);
		assertNotNull(getReportService().saveReportDesign(newReportDesign()).getId());
	}

	@Test
	public void getAllReportDesigns_shouldNotBeAllowedForRunReportsAlone() {
		// reads are gated on View Reports alone, so running reports does not imply reading designs
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_RUN_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().getAllReportDesigns(true);
			}
		});
	}

	@Test
	public void getAllReportDesigns_shouldRequireAReportingPrivilege() {
		authenticateWithReportingPrivileges();
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().getAllReportDesigns(true);
			}
		});
	}

	@Test
	public void purgeReportDesignsForReportDefinition_shouldBeAllowedForManageReports() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORTS);
		getReportService().purgeReportDesignsForReportDefinition("c11f5354-9567-4cc5-b3ef-163e28873926");
	}

	@Test
	public void purgeReportDesignsForReportDefinition_shouldNotBeAllowedForManageReportDefinitions() {
		// the cascade from purging a report definition now needs the delete privilege as well
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORT_DEFINITIONS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().purgeReportDesignsForReportDefinition("c11f5354-9567-4cc5-b3ef-163e28873926");
			}
		});
	}

	//***** REPORT REQUESTS *****

	@Test
	public void getReportRequests_shouldRequireAReportingPrivilege() {
		authenticateWithReportingPrivileges();
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().getReportRequests(null, null, null);
			}
		});
	}

	@Test
	public void purgeReportRequestsForReportDefinition_shouldRequireDeleteReports() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().purgeReportRequestsForReportDefinition("c11f5354-9567-4cc5-b3ef-163e28873926");
			}
		});
	}

	//***** REPORT PROCESSOR CONFIGURATIONS *****

	@Test
	public void saveReportProcessorConfiguration_shouldRequireManageReportDesigns() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORTS, ReportingConstants.PRIV_RUN_REPORTS,
		    ReportingConstants.PRIV_VIEW_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().saveReportProcessorConfiguration(newProcessorConfiguration());
			}
		});
	}

	@Test
	public void saveReportProcessorConfiguration_shouldBeAllowedForManageReportDesigns() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS);
		assertNotNull(getReportService().saveReportProcessorConfiguration(newProcessorConfiguration()).getId());
	}

	@Test
	public void purgeReportProcessorConfiguration_shouldRequireManageReports() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS, ReportingConstants.PRIV_VIEW_REPORTS);
		final ReportProcessorConfiguration config;

		Context.addProxyPrivilege(ReportingConstants.PRIV_MANAGE_REPORTS);
		try {
			config = getReportService().getReportProcessorConfiguration(1);
		} finally {
			Context.removeProxyPrivilege(ReportingConstants.PRIV_MANAGE_REPORTS);
		}

		assertNotNull(config);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().purgeReportProcessorConfiguration(config);
			}
		});
	}

	//***** RUNNING REPORTS *****

	@Test
	public void runReport_shouldRequireRunReports() {
		final ReportRequest request = newReportRequest();
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS,
		    ReportingConstants.PRIV_MANAGE_REPORTS, ReportingConstants.PRIV_MANAGE_REPORT_DEFINITIONS,
		    ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().runReport(request);
			}
		});
	}

	@Test
	public void queueReport_shouldRequireRunReports() {
		final ReportRequest request = newReportRequest();
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_VIEW_REPORTS,
		    ReportingConstants.PRIV_MANAGE_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().queueReport(request);
			}
		});
	}

	@Test
	public void queueReport_shouldBeAllowedForRunReports() {
		ReportRequest request = newReportRequest();
		// Loading a persisted ReportRequest resolves its mapped definition through
		// ReportDefinitionService.getDefinitionByUuid, so View Reports is needed on top of the privilege
		// under test. That is the baseline-privilege model, not a gap in the annotation being exercised.
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_RUN_REPORTS,
		    ReportingConstants.PRIV_VIEW_REPORTS);
		assertNotNull(getReportService().queueReport(request).getId());
	}

	@Test
	public void loadReportLog_shouldRequireViewReports() {
		// managing designs and definitions is about authoring reports, not reading what a run produced
		final ReportRequest request = newReportRequest();
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS,
		    ReportingConstants.PRIV_MANAGE_REPORT_DEFINITIONS, ReportingConstants.PRIV_RUN_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().loadReportLog(request);
			}
		});
	}

	@Test
	public void saveReport_shouldBeAllowedForRunReportsPlusViewReports() {
		// saveReport reads the request back through the proxy, so it needs the read privilege as well
		ReportRequest request = newReportRequest();
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_RUN_REPORTS,
		    ReportingConstants.PRIV_VIEW_REPORTS);
		request = getReportService().queueReport(request);
		Report saved = getReportService().saveReport(new Report(request), "saved by authorization test");
		assertEquals(ReportRequest.Status.SAVED, saved.getRequest().getStatus());
	}

	@Test
	public void purgeReportRequest_shouldBeAllowedForManageReports() {
		// the scheduled reports screen's delete action now needs the delete privilege
		ReportRequest request = newReportRequest();
		// Loading a persisted ReportRequest resolves its mapped definition through
		// ReportDefinitionService.getDefinitionByUuid, so View Reports is needed on top of the privilege
		// under test. That is the baseline-privilege model, not a gap in the annotation being exercised.
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_RUN_REPORTS,
		    ReportingConstants.PRIV_MANAGE_REPORTS, ReportingConstants.PRIV_VIEW_REPORTS);
		request = getReportService().queueReport(request);
		getReportService().purgeReportRequest(request);
	}

	@Test
	public void deleteOldReportRequests_shouldRequireManageReports() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_RUN_REPORTS,
		    ReportingConstants.PRIV_VIEW_REPORTS);
		assertNotAuthorized(new Invocation() {

			public void run() {
				getReportService().deleteOldReportRequests();
			}
		});
	}

	@Test
	public void deleteOldReportRequests_shouldBeAllowedForManageReports() {
		authenticateWithReportingPrivileges(ReportingConstants.PRIV_MANAGE_REPORTS,
		    ReportingConstants.PRIV_VIEW_REPORTS);
		getReportService().deleteOldReportRequests();
	}

	//***** HELPERS *****

	private ReportDesign newReportDesign() {
		ReportDefinition definition = new ReportDefinition();
		definition.setName("Authorization test report");
		ReportDesign design = new ReportDesign();
		design.setName("Authorization test design");
		design.setReportDefinition(definition);
		design.setRendererType(TsvReportRenderer.class);
		return design;
	}

	private ReportProcessorConfiguration newProcessorConfiguration() {
		ReportProcessorConfiguration c = new ReportProcessorConfiguration();
		c.setName("Authorization test processor");
		c.setProcessorType(LoggingReportProcessor.class.getName());
		return c;
	}

	/**
	 * Builds a report request against a saved report definition. This must be called before
	 * authenticating as a restricted user, so that the setup itself is performed as the superuser.
	 */
	private ReportRequest newReportRequest() {
		ReportDefinition definition = new ReportDefinition();
		definition.setName("Authorization test report");
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery("select count(*) from patient");
		definition.addDataSetDefinition("patients", dsd, null);
		Context.getService(ReportDefinitionService.class).saveDefinition(definition);

		RenderingMode mode = new RenderingMode(new TsvReportRenderer(), "TSV", null, 100);
		return new ReportRequest(new Mapped<>(definition, null), null, mode, Priority.NORMAL, null);
	}

	private void assertNotAuthorized(Invocation invocation) {
		try {
			invocation.run();
			fail("Expected an APIAuthenticationException for user " + Context.getAuthenticatedUser().getUsername());
		}
		catch (APIAuthenticationException e) {
			// expected
		}
	}

	private interface Invocation {

		void run();
	}
}
