/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReflectionUtilTest {
	
	/**
	 * @see ReflectionUtil#getPropertyValue(Object,String)
	 * @verifies work for string property
	 */
	@Test
	public void getPropertyValue_shouldWorkForStringProperty() throws Exception {
		Bean bean = new Bean();
		bean.setStringProperty("test");
		
		assertThat((String) ReflectionUtil.getPropertyValue(bean, "stringProperty"), is("test"));
	}
	
	/**
	 * @see ReflectionUtil#getPropertyValue(Object,String)
	 * @verifies work for boolean property
	 */
	@Test
	public void getPropertyValue_shouldWorkForBooleanProperty() throws Exception {
		Bean bean = new Bean();
		bean.setBooleanProperty(true);
		
		assertThat((Boolean) ReflectionUtil.getPropertyValue(bean, "booleanProperty"), is(true));
	}
	
	/**
	 * @see ReflectionUtil#getPropertyValue(Object,String)
	 * @verifies work for object property
	 */
	@Test
	public void getPropertyValue_shouldWorkForObjectProperty() throws Exception {
		Bean bean = new Bean();
		Object object = new Object();
		bean.setObjectProperty(object);
		
		assertThat(ReflectionUtil.getPropertyValue(bean, "objectProperty"), is(object));
	}

    /**
     * @see ReflectionUtil#getPropertyValue(Object,String)
     * @verifies work for nested property
     */
    @Test
    public void getPropertyValue_shouldWorkForNestedProperty() throws Exception {
        String expectedValue = "expected value";
        Bean child = new Bean();
        child.setStringProperty(expectedValue);
        Bean parent = new Bean();
        parent.setBeanProperty(child);

        assertThat((String) ReflectionUtil.getPropertyValue(parent, "beanProperty.stringProperty"), is(expectedValue));
    }

    @Test
    public void getPropertyType_shouldWorkForBooleanProperty() throws Exception {
        assertTrue(ReflectionUtil.getPropertyType(Bean.class, "booleanProperty").equals(boolean.class));
    }

    @Test
    public void getPropertyType_shouldWorkForStringProperty() throws Exception {
        assertTrue(ReflectionUtil.getPropertyType(Bean.class, "stringProperty").equals(String.class));
    }


    public static class Bean {
		
		private boolean booleanProperty;
		
		private String stringProperty;
		
		private Object objectProperty;

        private Bean beanProperty;
		
		public boolean isBooleanProperty() {
			return booleanProperty;
		}
		
		public void setBooleanProperty(boolean booleanProperty) {
			this.booleanProperty = booleanProperty;
		}
		
		public String getStringProperty() {
			return stringProperty;
		}
		
		public void setStringProperty(String stringProperty) {
			this.stringProperty = stringProperty;
		}
		
		public Object getObjectProperty() {
			return objectProperty;
		}
		
		public void setObjectProperty(Object objectProperty) {
			this.objectProperty = objectProperty;
		}

        public Bean getBeanProperty() {
            return beanProperty;
        }

        public void setBeanProperty(Bean beanProperty) {
            this.beanProperty = beanProperty;
        }
    }
}
