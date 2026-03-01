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
package org.omnifaces.utils.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.omnifaces.utils.properties.PropertiesUtils.PropertiesFormat.LIST;
import static org.omnifaces.utils.properties.PropertiesUtils.PropertiesFormat.XML;
import static org.omnifaces.utils.properties.PropertiesUtils.getStage;
import static org.omnifaces.utils.properties.PropertiesUtils.loadPropertiesFromStream;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class PropertiesUtilsTest {

	private static final String STAGE_PROPERTY = "omniutils.test.stage";

	@Test
	public void getStageReturnsSystemPropertyValueWhenSet() {
		System.setProperty(STAGE_PROPERTY, "production");
		try {
			assertEquals("production", getStage(STAGE_PROPERTY, "default"));
		}
		finally {
			System.clearProperty(STAGE_PROPERTY);
		}
	}

	@Test
	public void getStageReturnsDefaultWhenSystemPropertyIsNotSet() {
		System.clearProperty(STAGE_PROPERTY);
		assertEquals("development", getStage(STAGE_PROPERTY, "development"));
	}

	@Test
	public void getStageThrowsWhenSystemPropertyIsNotSetAndDefaultIsNull() {
		System.clearProperty(STAGE_PROPERTY);
		assertThrows(IllegalStateException.class, () -> getStage(STAGE_PROPERTY, null));
	}

	@Test
	public void loadPropertiesFromStreamInListFormatParsesKeyValuePairs() {
		String content = "key1=value1\nkey2=value2\n";
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		Map<String, String> settings = new HashMap<>();
		loadPropertiesFromStream(in, "test", settings, LIST);
		assertEquals("value1", settings.get("key1"));
		assertEquals("value2", settings.get("key2"));
	}

	@Test
	public void loadPropertiesFromStreamInXmlFormatParsesKeyValuePairs() {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n"
				+ "<properties>\n"
				+ "  <entry key=\"xml.key\">xml.value</entry>\n"
				+ "</properties>";
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		Map<String, String> settings = new HashMap<>();
		loadPropertiesFromStream(in, "test", settings, XML);
		assertEquals("xml.value", settings.get("xml.key"));
	}

	@Test
	public void loadPropertiesFromStreamMergesIntoExistingMap() {
		String content = "new.key=new.value\n";
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		Map<String, String> settings = new HashMap<>();
		settings.put("existing.key", "existing.value");
		loadPropertiesFromStream(in, "test", settings, LIST);
		assertEquals("existing.value", settings.get("existing.key"));
		assertEquals("new.value", settings.get("new.key"));
	}

}
