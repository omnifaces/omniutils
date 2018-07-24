/*
 * Copyright 2018 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.omnifaces.utils.Lang.setIfNotEmpty;
import static org.omnifaces.utils.Lang.toTitleCase;
import static org.omnifaces.utils.Lang.toUrlSafe;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class LangTest {

	@Test
	public void setIfNotEmptyTest() {
		setIfNotEmpty(emptyList(), collection -> fail());

		AtomicReference<List<String>> reference = new AtomicReference<>();

		setIfNotEmpty(singletonList(""), reference::set);

		assertNotNull(reference.get());
	}

	@Test
	public void titleCaseTest() {
		assertNull(toTitleCase(null));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("lorem ipsum dolor sit amet"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("LOREM IPSUM DOLOR SIT AMET"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("lOREM iPSUM dOLOR sIT aMET"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("LOREm IPSUm DOLOr SIt AMEt"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("LoReM IpSuM DoLoR SiT AmEt"));
		assertEquals("Lorem Ipsum Dolor Sit Amet", toTitleCase("Lorem Ipsum Dolor Sit Amet"));
	}

	@Test
	public void urlSafeTest() {
		assertNull(toUrlSafe(null));
		assertEquals("lorem-ipsum-dolor-sit-amet", toUrlSafe("lorem ipsum dolor sit amet"));
		assertEquals("LOREM-IPSUM-DOLOR-SIT-AMET", toUrlSafe("LOREM IPSUM DOLOR SIT AMET"));
		assertEquals("lorem-ipsum-dOlor-sIt-amEt", toUrlSafe("lórém ípsúm dÓlor sÍt ámÉt"));
		assertEquals("lorem-ipsum-dolor-sit-amet", toUrlSafe("lorem--ipsum--dolor--sit--amet"));
		assertEquals("lorem-ipsum-dolor-sit-amet", toUrlSafe("?lorem&ipsum%dolor_sit amet-"));
	}

}