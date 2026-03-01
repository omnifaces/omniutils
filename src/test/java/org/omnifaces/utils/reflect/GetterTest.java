/*
 * Copyright 2021 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GetterTest {

	static class Person {
		private final String name;
		private final boolean active;

		Person(String name, boolean active) {
			this.name = name;
			this.active = active;
		}

		public String getName() { return name; }
		public boolean isActive() { return active; }
	}

	@Test
	public void getPropertyNameReturnsCorrectNameForGetPrefixedGetter() {
		Getter<Person> getter = Person::getName;
		assertEquals("name", getter.getPropertyName());
	}

	@Test
	public void getPropertyNameReturnsCorrectNameForIsPrefixedGetter() {
		Getter<Person> getter = Person::isActive;
		assertEquals("active", getter.getPropertyName());
	}

	@Test
	public void getBaseTypeReturnsDeclaringClass() {
		Getter<Person> getter = Person::getName;
		assertEquals(Person.class, getter.getBaseType());
	}

	@Test
	public void getReturnTypeReturnsCorrectTypeForStringGetter() {
		Getter<Person> getter = Person::getName;
		assertEquals(String.class, getter.getReturnType());
	}

	@Test
	public void getReturnTypeReturnsCorrectTypeForBooleanGetter() {
		Getter<Person> getter = Person::isActive;
		assertEquals(boolean.class, getter.getReturnType());
	}

}
