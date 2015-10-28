package org.omnifaces.utils.security;

import java.security.NoSuchAlgorithmException;

public class UncheckedNoSuchAlgorithmException extends RuntimeException {

	private static final long serialVersionUID = 6300183450203082745L;

	public UncheckedNoSuchAlgorithmException(NoSuchAlgorithmException e) {
		super(e);
	}
}
