package org.omnifaces.utils;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.omnifaces.utils.Lang.setIfNotEmpty;
import static org.omnifaces.utils.Lang.toTitleCase;

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

}