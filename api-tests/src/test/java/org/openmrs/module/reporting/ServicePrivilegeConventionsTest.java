/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting;

import org.junit.Test;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.util.PrivilegeConstants;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Enforces this module's privilege conventions across every service it registers, rather than one service
 * at a time. Adding a service, or a method to one, without a privilege fails here.
 * <p/>
 * The services are discovered from moduleApplicationContext.xml so that a newly registered service is
 * covered automatically. Privileges are resolved through {@link Class#getMethods()} on the concrete
 * service interface, which is how the Spring proxy resolves the method the authorization advice sees. That
 * detail matters: an <em>unannotated</em> redeclaration of an inherited method in a sub-interface shadows
 * the annotation on the base interface and leaves the method completely unguarded, and inspecting only
 * declared methods would not notice.
 */
public class ServicePrivilegeConventionsTest {

	/** Privileges this module declares in its omod's config.xml. */
	private static final Set<String> MODULE_PRIVILEGES = new HashSet<String>(Arrays.asList(
	    ReportingConstants.PRIV_MANAGE_REPORTS, ReportingConstants.PRIV_MANAGE_REPORT_DEFINITIONS,
	    ReportingConstants.PRIV_MANAGE_DATA_SET_DEFINITIONS, ReportingConstants.PRIV_MANAGE_INDICATOR_DEFINITIONS,
	    ReportingConstants.PRIV_MANAGE_DIMENSION_DEFINITIONS, ReportingConstants.PRIV_MANAGE_COHORT_DEFINITIONS,
	    ReportingConstants.PRIV_MANAGE_REPORT_DESIGNS, ReportingConstants.PRIV_MANAGE_SCHEDULED_REPORT_TASKS,
	    ReportingConstants.PRIV_RUN_REPORTS));

	/** Core privileges this module's services rely on, none of which are declared in config.xml. */
	private static final Set<String> CORE_PRIVILEGES = new HashSet<String>(Arrays.asList(
	    ReportingConstants.PRIV_VIEW_REPORTS, ReportingConstants.PRIV_DELETE_REPORTS, PrivilegeConstants.GET_PATIENTS,
	    PrivilegeConstants.GET_PERSONS, PrivilegeConstants.GET_ENCOUNTERS, PrivilegeConstants.GET_OBS,
	    PrivilegeConstants.GET_VISITS));

	/**
	 * Method name prefixes that mutate state. Kept deliberately broad so that a future method named, say,
	 * retireDefinition is treated as a write even though nothing is named that today.
	 */
	private static final List<String> WRITE_PREFIXES = Arrays.asList("save", "purge", "delete", "run", "queue",
	    "process", "log", "persist", "retire", "unretire", "void", "unvoid", "update", "create", "cancel", "set");

	private List<Class<?>> serviceInterfaces() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("moduleApplicationContext.xml");
		assertTrue("moduleApplicationContext.xml must be on the test classpath", in != null);
		Document doc;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			doc = factory.newDocumentBuilder().parse(in);
		}
		finally {
			in.close();
		}
		Set<String> names = new HashSet<String>();
		NodeList values = doc.getElementsByTagName("value");
		for (int i = 0; i < values.getLength(); i++) {
			Node node = values.item(i);
			String text = node.getTextContent() == null ? "" : node.getTextContent().trim();
			if (text.startsWith("org.openmrs.module.reporting") && text.endsWith("Service")) {
				names.add(text);
			}
		}
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		for (String name : new ArrayList<String>(new java.util.TreeSet<String>(names))) {
			Class<?> c = Class.forName(name);
			if (c.isInterface()) {
				interfaces.add(c);
			}
		}
		assertTrue("expected to discover the module's services, found " + interfaces.size(), interfaces.size() >= 15);
		return interfaces;
	}

	/**
	 * Methods that intentionally carry no privilege. Both return only type metadata, and
	 * DefinitionContext.getDefinitionService calls getDefinitionType() on every registered service to
	 * dispatch by definition type. That dispatch runs beneath every Hibernate load of a mapped definition,
	 * so gating it would make one service's privilege a prerequisite for loading any report request.
	 */
	private static final Set<String> EXEMPT = new HashSet<String>(Arrays.asList("getDefinitionType",
	    "getDefinitionTypes", "setCohortQueryDAO"));

	/**
	 * Lifecycle callbacks on {@link OpenmrsService} are infrastructure, not API, and core invokes them on a
	 * daemon thread at startup.
	 */
	private boolean isLifecycle(Method method) {
		return OpenmrsService.class.equals(method.getDeclaringClass())
		        || Object.class.equals(method.getDeclaringClass()) || EXEMPT.contains(method.getName());
	}

	private List<String> privilegesOf(Method method) {
		Authorized authorized = method.getAnnotation(Authorized.class);
		return authorized == null ? null : Arrays.asList(authorized.value());
	}

	@Test
	public void everyServiceMethodShouldRequireAPrivilege() throws Exception {
		List<String> offenders = new ArrayList<>();
		for (Class<?> service : serviceInterfaces()) {
			for (Method method : service.getMethods()) {
				if (isLifecycle(method)) {
					continue;
				}
				List<String> privileges = privilegesOf(method);
				if (privileges == null || privileges.isEmpty()) {
					offenders.add(service.getSimpleName() + "." + method.getName()
					        + (method.isBridge() ? " (bridge)" : ""));
				}
			}
		}
		Collections.sort(offenders);
		assertTrue("service methods reachable through the proxy with no privilege: " + offenders, offenders.isEmpty());
	}

	@Test
	public void everyServiceMethodShouldRequireExactlyOnePrivilege() throws Exception {
		List<String> offenders = new ArrayList<>();
		for (Class<?> service : serviceInterfaces()) {
			for (Method method : service.getMethods()) {
				if (isLifecycle(method)) {
					continue;
				}
				List<String> privileges = privilegesOf(method);
				if (privileges != null && privileges.size() > 1) {
					offenders.add(service.getSimpleName() + "." + method.getName() + " -> " + privileges);
				}
			}
		}
		Collections.sort(offenders);
		assertTrue("methods requiring more than one privilege: " + offenders, offenders.isEmpty());
	}

	@Test
	public void readPrivilegesShouldNeverGuardAWriteMethod() throws Exception {
		Set<String> readOnly = new HashSet<>(Arrays.asList(ReportingConstants.PRIV_VIEW_REPORTS));
		List<String> offenders = new ArrayList<>();
		for (Class<?> service : serviceInterfaces()) {
			for (Method method : service.getMethods()) {
				if (isLifecycle(method)) {
					continue;
				}
				List<String> privileges = privilegesOf(method);
				if (privileges == null || !isWrite(method.getName())) {
					continue;
				}
				for (String privilege : privileges) {
					if (readOnly.contains(privilege)) {
						offenders.add(service.getSimpleName() + "." + method.getName() + " -> " + privilege);
					}
				}
			}
		}
		Collections.sort(offenders);
		assertTrue("write methods guarded by a read-only privilege: " + offenders, offenders.isEmpty());
	}

	private boolean isWrite(String methodName) {
		for (String prefix : WRITE_PREFIXES) {
			if (methodName.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void everyDeclaredPrivilegeShouldExist() throws Exception {
		Set<String> known = new HashSet<>(MODULE_PRIVILEGES);
		known.addAll(CORE_PRIVILEGES);
		List<String> offenders = new ArrayList<String>();
		for (Class<?> service : serviceInterfaces()) {
			for (Method method : service.getMethods()) {
				List<String> privileges = privilegesOf(method);
				if (privileges == null) {
					continue;
				}
				for (String privilege : privileges) {
					if (!known.contains(privilege)) {
						offenders.add(service.getSimpleName() + "." + method.getName() + " -> " + privilege);
					}
				}
			}
		}
		Collections.sort(offenders);
		assertTrue("privileges that are neither this module's nor a known core one: " + offenders, offenders.isEmpty());
	}

	/**
	 * A sanity check on the discovery above: if the parsing silently found nothing, every other test here
	 * would vacuously pass.
	 */
	@Test
	public void serviceDiscoveryShouldFindAnnotatedMethods() throws Exception {
		int annotated = 0;
		for (Class<?> service : serviceInterfaces()) {
			for (Method method : service.getMethods()) {
				if (!isLifecycle(method) && privilegesOf(method) != null) {
					annotated++;
				}
			}
		}
        assertNotEquals("discovery found no annotated methods at all", 0, annotated);
		assertTrue("expected well over a hundred guarded methods, found " + annotated, annotated > 100);
	}
}
