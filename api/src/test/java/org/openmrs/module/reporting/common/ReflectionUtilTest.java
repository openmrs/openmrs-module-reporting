package org.openmrs.module.reporting.common;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
	
	public static class Bean {
		
		private boolean booleanProperty;
		
		private String stringProperty;
		
		private Object objectProperty;
		
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
	}
}
