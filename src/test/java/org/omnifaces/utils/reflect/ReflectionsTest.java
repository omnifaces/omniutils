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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.omnifaces.utils.reflect.Reflections.accessField;
import static org.omnifaces.utils.reflect.Reflections.findClass;
import static org.omnifaces.utils.reflect.Reflections.findConstructor;
import static org.omnifaces.utils.reflect.Reflections.findField;
import static org.omnifaces.utils.reflect.Reflections.findGetter;
import static org.omnifaces.utils.reflect.Reflections.findMethod;
import static org.omnifaces.utils.reflect.Reflections.findSetter;
import static org.omnifaces.utils.reflect.Reflections.getActualTypeArguments;
import static org.omnifaces.utils.reflect.Reflections.getPrimitiveType;
import static org.omnifaces.utils.reflect.Reflections.instantiate;
import static org.omnifaces.utils.reflect.Reflections.invokeGetter;
import static org.omnifaces.utils.reflect.Reflections.invokeMethod;
import static org.omnifaces.utils.reflect.Reflections.invokeSetter;
import static org.omnifaces.utils.reflect.Reflections.listAnnotatedEnumFields;
import static org.omnifaces.utils.reflect.Reflections.listAnnotatedFields;
import static org.omnifaces.utils.reflect.Reflections.map;
import static org.omnifaces.utils.reflect.Reflections.modifyField;
import static org.omnifaces.utils.reflect.Reflections.toClass;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ReflectionsTest {

	@Retention(RetentionPolicy.RUNTIME)
	@interface Marked {}

	enum Color { RED, GREEN, BLUE }

	static class Base {
		private String baseField = "base";
		@Marked int markedBaseField = 1;

		public String getBaseField() { return baseField; }
		public void setBaseField(String v) { this.baseField = v; }
		public boolean isActive() { return true; }
	}

	static class Child extends Base {
		private String childField = "child";
		@Marked String markedChildField = "child";
		@Marked List<Color> colorList;

		public String getChildField() { return childField; }
		public void setChildField(String v) { this.childField = v; }
		public String concat(String a, String b) { return a + b; }
	}

	static class Container {
		Child inner = new Child();
		public Child getInner() { return inner; }
		public void setInner(Child c) { this.inner = c; }
	}

	static class WithEnumFields {
		@Marked Color directEnum;
		@Marked List<Color> parameterizedEnum;
		@Marked String notAnEnum;
	}

	static class NoDefaultConstructor {
		NoDefaultConstructor(String arg) {}
	}

	static abstract class GenericBase<A, B> {}
	static class ConcreteChild extends GenericBase<String, Integer> {}


	@Test
	public void findFieldByObjectFindsOwnField() {
		Optional<Field> field = findField(new Child(), "childField");
		assertTrue(field.isPresent());
		assertEquals("childField", field.get().getName());
	}

	@Test
	public void findFieldByObjectFindsInheritedField() {
		assertTrue(findField(new Child(), "baseField").isPresent());
	}

	@Test
	public void findFieldByObjectReturnsEmptyWhenNotFound() {
		assertFalse(findField(new Child(), "nonExistent").isPresent());
	}

	@Test
	public void findFieldByClassReturnsEmptyWhenNotFound() {
		assertFalse(findField(Child.class, "nonExistent").isPresent());
	}

	@Test
	public void findFieldByNullClassReturnsEmpty() {
		assertFalse(findField((Class<?>) null, "anything").isPresent());
	}


	@Test
	public void listAnnotatedFieldsFindsFieldsAcrossHierarchy() {
		List<Field> fields = listAnnotatedFields(Child.class, Marked.class);
		assertEquals(3, fields.size());
	}

	@Test
	public void listAnnotatedFieldsWithNoAnnotationsThrows() {
		assertThrows(IllegalArgumentException.class, () -> listAnnotatedFields(Child.class));
	}


	@Test
	public void listAnnotatedEnumFieldsFindsDirectAndParameterizedEnumTypes() {
		List<Class<? extends Enum<?>>> enums = listAnnotatedEnumFields(WithEnumFields.class, Marked.class);
		assertEquals(2, enums.size());
		assertTrue(enums.contains(Color.class));
	}

	@Test
	public void listAnnotatedEnumFieldsWithNoAnnotationsThrows() {
		assertThrows(IllegalArgumentException.class, () -> listAnnotatedEnumFields(WithEnumFields.class));
	}


	@Test
	public void findMethodOnInstanceFindsOwnMethod() {
		Optional<Method> method = findMethod(new Child(), "getChildField");
		assertTrue(method.isPresent());
		assertEquals("getChildField", method.get().getName());
	}

	@Test
	public void findMethodOnInstanceReturnsEmptyWhenNotFound() {
		assertFalse(findMethod(new Child(), "nonExistentMethod").isPresent());
	}

	@Test
	public void findMethodOnClassObjectFindsMethod() {
		assertTrue(findMethod(Child.class, "getChildField").isPresent());
	}

	@Test
	public void findMethodFindsInheritedMethod() {
		assertTrue(findMethod(new Child(), "getBaseField").isPresent());
	}

	@Test
	public void findMethodWithParamsFindsCorrectOverload() {
		Optional<Method> method = findMethod(new Child(), "concat", "a", "b");
		assertTrue(method.isPresent());
	}


	@Test
	public void toClassReturnsClassForKnownName() {
		assertEquals(String.class, toClass("java.lang.String"));
	}

	@Test
	public void toClassThrowsForUnknownName() {
		assertThrows(IllegalStateException.class, () -> toClass("com.example.DoesNotExist"));
	}


	@Test
	public void findClassReturnsPresentForKnownName() {
		Optional<Class<String>> cls = findClass("java.lang.String");
		assertTrue(cls.isPresent());
		assertEquals(String.class, cls.get());
	}

	@Test
	public void findClassReturnsEmptyForUnknownName() {
		assertFalse(findClass("com.example.DoesNotExist").isPresent());
	}


	@Test
	public void findConstructorReturnsPresentForPublicDefaultConstructor() {
		assertTrue(findConstructor(StringBuilder.class).isPresent());
	}

	@Test
	public void findConstructorReturnsEmptyWhenNoMatchingConstructor() {
		assertFalse(findConstructor(NoDefaultConstructor.class).isPresent());
	}


	@Test
	public void instantiateByClassCreatesInstance() {
		assertNotNull(instantiate(Child.class));
	}

	@Test
	public void instantiateByClassNameCreatesInstance() {
		Object instance = instantiate("java.lang.StringBuilder");
		assertNotNull(instance);
		assertTrue(instance instanceof StringBuilder);
	}

	@Test
	public void instantiateClassWithNoDefaultConstructorThrows() {
		assertThrows(IllegalStateException.class, () -> instantiate(NoDefaultConstructor.class));
	}


	@Test
	public void accessFieldByNameReturnsValue() {
		assertEquals("child", accessField(new Child(), "childField"));
	}

	@Test
	public void accessFieldByNameWorksForInheritedField() {
		assertEquals("base", accessField(new Child(), "baseField"));
	}

	@Test
	public void accessFieldByNameThrowsWhenNotFound() {
		assertThrows(IllegalStateException.class, () -> accessField(new Child(), "nonExistent"));
	}

	@Test
	public void accessFieldByFieldObjectReturnsValue() throws NoSuchFieldException {
		Field field = Child.class.getDeclaredField("childField");
		assertEquals("child", accessField(new Child(), field));
	}


	@Test
	public void modifyFieldByNameReturnsOldValueAndSetsNew() {
		Child child = new Child();
		String old = modifyField(child, "childField", "updated");
		assertEquals("child", old);
		assertEquals("updated", accessField(child, "childField"));
	}

	@Test
	public void modifyFieldByFieldObjectReturnsOldValueAndSetsNew() throws NoSuchFieldException {
		Child child = new Child();
		Field field = Child.class.getDeclaredField("childField");
		String old = modifyField(child, field, "newValue");
		assertEquals("child", old);
		assertEquals("newValue", accessField(child, "childField"));
	}


	@Test
	public void invokeMethodByNameReturnsResult() {
		assertEquals("child", invokeMethod(new Child(), "getChildField"));
	}

	@Test
	public void invokeMethodByNameThrowsWhenNotFound() {
		assertThrows(IllegalStateException.class, () -> invokeMethod(new Child(), "nonExistentMethod"));
	}

	@Test
	public void invokeMethodByMethodObjectReturnsResult() throws NoSuchMethodException {
		Method method = Child.class.getDeclaredMethod("getChildField");
		assertEquals("child", invokeMethod(new Child(), method));
	}

	@Test
	public void invokeMethodWithParamsReturnsResult() {
		assertEquals("ab", invokeMethod(new Child(), "concat", "a", "b"));
	}


	@Test
	public void findGetterFindsGetPrefixedMethod() {
		Optional<Method> getter = findGetter(Child.class, "childField");
		assertTrue(getter.isPresent());
		assertEquals("getChildField", getter.get().getName());
	}

	@Test
	public void findGetterPrefersIsPrefixedMethod() {
		Optional<Method> getter = findGetter(Base.class, "active");
		assertTrue(getter.isPresent());
		assertEquals("isActive", getter.get().getName());
	}

	@Test
	public void findGetterReturnsEmptyWhenNotFound() {
		assertFalse(findGetter(Child.class, "nonExistent").isPresent());
	}

	@Test
	public void invokeGetterReturnsPropertyValue() {
		assertEquals("child", invokeGetter(new Child(), "childField"));
	}

	@Test
	public void invokeGetterWithDotSeparatedPathTraversesChain() {
		assertEquals("child", invokeGetter(new Container(), "inner.childField"));
	}


	@Test
	public void findSetterFindsSetPrefixedMethod() {
		Optional<Method> setter = findSetter(Child.class, "childField", "someValue");
		assertTrue(setter.isPresent());
		assertEquals("setChildField", setter.get().getName());
	}

	@Test
	public void invokeSetterSetsPropertyValue() {
		Child child = new Child();
		invokeSetter(child, "childField", "updated");
		assertEquals("updated", invokeGetter(child, "childField"));
	}

	@Test
	public void invokeSetterWithDotSeparatedPathSetsNestedValue() {
		Container container = new Container();
		invokeSetter(container, "inner.childField", "nested");
		assertEquals("nested", invokeGetter(container, "inner.childField"));
	}


	@Test
	public void mapFieldCopiesValueBetweenInstances() throws NoSuchFieldException {
		Child from = new Child();
		Child to = new Child();
		modifyField(from, "childField", "fromValue");
		Field field = Child.class.getDeclaredField("childField");
		map(field, from, to);
		assertEquals("fromValue", accessField(to, "childField"));
	}

	@Test
	public void mapWithNonFieldMemberThrows() throws NoSuchMethodException {
		Method method = Child.class.getDeclaredMethod("getChildField");
		assertThrows(UnsupportedOperationException.class, () -> map(method, new Child(), new Child()));
	}


	@Test
	public void getActualTypeArgumentsReturnsCorrectTypesForParameterizedSuperclass() {
		List<Class<?>> types = getActualTypeArguments(ConcreteChild.class, GenericBase.class);
		assertEquals(2, types.size());
		assertEquals(String.class, types.get(0));
		assertEquals(Integer.class, types.get(1));
	}


	@Test
	public void getPrimitiveTypeReturnsCorrectPrimitiveForAllWrappers() {
		assertEquals(int.class, getPrimitiveType(Integer.class));
		assertEquals(boolean.class, getPrimitiveType(Boolean.class));
		assertEquals(long.class, getPrimitiveType(Long.class));
		assertEquals(double.class, getPrimitiveType(Double.class));
		assertEquals(byte.class, getPrimitiveType(Byte.class));
		assertEquals(short.class, getPrimitiveType(Short.class));
		assertEquals(char.class, getPrimitiveType(Character.class));
		assertEquals(float.class, getPrimitiveType(Float.class));
	}

	@Test
	public void getPrimitiveTypeReturnsSelfForPrimitiveType() {
		assertEquals(int.class, getPrimitiveType(int.class));
	}

	@Test
	public void getPrimitiveTypeReturnsNullForNonWrapperType() {
		assertNull(getPrimitiveType(String.class));
	}

}
