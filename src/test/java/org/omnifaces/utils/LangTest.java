package org.omnifaces.utils;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.omnifaces.utils.Lang.setIfNotEmpty;

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
}
