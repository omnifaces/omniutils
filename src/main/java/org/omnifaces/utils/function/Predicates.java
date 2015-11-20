package org.omnifaces.utils.function;

import java.util.function.Predicate;

public final class Predicates {

	private Predicates() {
	}

	public static <T> Predicate<T> always() {
		return t -> true;
	}

	public static <T> Predicate<T> never() {
		return t -> false;
	}
}
