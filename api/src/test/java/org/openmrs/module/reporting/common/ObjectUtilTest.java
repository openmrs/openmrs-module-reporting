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
package org.openmrs.module.reporting.common;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.ConceptClass;
import org.openmrs.PersonName;
import org.openmrs.User;

/**
 * Tests methods on on ObjectUtil
 */
public class ObjectUtilTest {
	
	@Test
	public void sortShouldSortSimpleStrings() throws Exception {
		List<String> list = Arrays.asList(new String[] { "Daniel", "Abbas", "Kizito" });
		ObjectUtil.sort((list), null);
		Assert.assertEquals("Abbas", list.get(0));
		Assert.assertEquals("Daniel", list.get(1));
		Assert.assertEquals("Kizito", list.get(2));
	}
	
	@Test
	public void shouldSortObjectThatImplementComparable() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleName", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleName", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleName", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), null);
		Assert.assertEquals(personName3, list.get(0));
		Assert.assertEquals(personName2, list.get(1));
		Assert.assertEquals(personName1, list.get(2));
	}
	
	@Test
	public void shouldSortObjectThatImplementComparableAsc() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleName", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleName", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleName", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "asc");
		Assert.assertEquals(personName3, list.get(0));
		Assert.assertEquals(personName2, list.get(1));
		Assert.assertEquals(personName1, list.get(2));
	}
	
	@Test
	public void shouldSortObjectThatImplementComparableDesc() throws Exception {
		PersonName personName1 = new PersonName("givenNameb", "middleName", "familyName");
		PersonName personName2 = new PersonName("givenNamea", "middleName", "familyName");
		PersonName personName3 = new PersonName("givenNamec", "middleName", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "desc");
		Assert.assertEquals(personName3, list.get(0));
		Assert.assertEquals(personName1, list.get(1));
		Assert.assertEquals(personName2, list.get(2));
	}
	
	@Test
	public void shouldSortOnSinglePropertyWithDefaultSortOrder() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "givenName");
		Assert.assertEquals(personName3, list.get(0));
		Assert.assertEquals(personName2, list.get(1));
		Assert.assertEquals(personName1, list.get(2));
	}
	
	@Test
	public void shouldSortOnSinglePropertyAsc() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "givenName asc");
		Assert.assertEquals(personName3, list.get(0));
		Assert.assertEquals(personName2, list.get(1));
		Assert.assertEquals(personName1, list.get(2));
	}
	
	@Test
	public void shouldSortOnSinglePropertyDesc() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "givenName desc");
		Assert.assertEquals(personName1, list.get(0));
		Assert.assertEquals(personName2, list.get(1));
		Assert.assertEquals(personName3, list.get(2));
	}
	
	@Test
	public void shouldSortOnTwoProperties() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "middleName, givenName");
		Assert.assertEquals(personName1, list.get(0));
		Assert.assertEquals(personName2, list.get(1));
		Assert.assertEquals(personName3, list.get(2));
	}
	
	@Test
	public void shouldSortOnThreeProperties() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "familyName, middleName, givenName");
		Assert.assertEquals(personName1, list.get(0));
		Assert.assertEquals(personName2, list.get(1));
		Assert.assertEquals(personName3, list.get(2));
	}
	
	@Test
	public void shouldSortOnNestedProperties() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		personName1.setCreator(new User(3));
		personName2.setCreator(new User(1));
		personName3.setCreator(new User(2));
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "familyName, creator.userId");
		Assert.assertEquals(personName2, list.get(0));
		Assert.assertEquals(personName3, list.get(1));
		Assert.assertEquals(personName1, list.get(2));
	}
	
	@Test
	public void shouldSortOnNestedPropertiesDesc() throws Exception {
		PersonName personName1 = new PersonName("givenNamec", "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		personName1.setCreator(new User(3));
		personName2.setCreator(new User(1));
		personName3.setCreator(new User(2));
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "familyName, creator.userId desc");
		Assert.assertEquals(personName1, list.get(0));
		Assert.assertEquals(personName3, list.get(1));
		Assert.assertEquals(personName2, list.get(2));
	}
	
	@Test
	public void shouldSortNullsLastAsc() throws Exception {
		PersonName personName1 = new PersonName(null, "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "givenName");
		Assert.assertEquals(personName3, list.get(0));
		Assert.assertEquals(personName2, list.get(1));
		Assert.assertEquals(personName1, list.get(2));
	}
	
	@Test
	public void shouldSortNullsLastDesc() throws Exception {
		PersonName personName1 = new PersonName(null, "middleNamea", "familyName");
		PersonName personName2 = new PersonName("givenNameb", "middleNameb", "familyName");
		PersonName personName3 = new PersonName("givenNamea", "middleNamec", "familyName");
		
		List<PersonName> list = Arrays.asList(new PersonName[] { personName1, personName2, personName3 });
		ObjectUtil.sort((list), "givenName desc");
		Assert.assertEquals(personName2, list.get(0));
		Assert.assertEquals(personName3, list.get(1));
		Assert.assertEquals(personName1, list.get(2));
	}
	
	@Test
	public void shouldSortObjectThatDontImplementComparable() throws Exception {
		ConceptClass conceptClass1 = new ConceptClass(3);
		ConceptClass conceptClass2 = new ConceptClass(1);
		ConceptClass conceptClass3 = new ConceptClass(4);
		ConceptClass conceptClass4 = new ConceptClass(2);
		
		List<ConceptClass> list = Arrays.asList(new ConceptClass[] { conceptClass1, conceptClass2, conceptClass3, conceptClass4 });
		ObjectUtil.sort((list), "conceptClassId");
		Assert.assertEquals(conceptClass2, list.get(0));
		Assert.assertEquals(conceptClass4, list.get(1));
		Assert.assertEquals(conceptClass1, list.get(2));
		Assert.assertEquals(conceptClass3, list.get(3));
	}
}
