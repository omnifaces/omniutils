/*
 * Copyright 2020 OmniFaces
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
package org.omnifaces.utils.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * An X509TrustManager with provided default methods, so these don't need to be needlessly 
 * defined by implementations.
 * 
 * @author Arjan Tijms
 *
 */
public interface DefaultX509TrustManager extends X509TrustManager {

	@Override
	default X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[] {};
	}
	
	@Override
	default void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// Do nothing
	}
	
	@Override
	default void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// Do nothing
	}

}
