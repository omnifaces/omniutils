package org.omnifaces.utils;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.omnifaces.utils.Lang.ifNotEmptySet;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class LangTest {

	@Test
	public void ifNotEmptySetTest() {
		ifNotEmptySet(emptyList(), collection -> fail());

		AtomicReference<List<String>> reference = new AtomicReference<>();

		ifNotEmptySet(singletonList(""), reference::set);

		assertNotNull(reference.get());
	}
}
