/*
 * Copyright 2019 OmniFaces
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
package org.omnifaces.utils.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * A trust manager implementation that doesn't do anything other than 
 * 
 * @author Arjan Tijms
 *
 */
public class InterceptingX509TrustManager implements DefaultX509TrustManager {

	private List<X509Certificate[]> x509ClientCertificates = new ArrayList<>();
	private List<X509Certificate[]> x509ServerCertificates = new ArrayList<>();
	
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		x509ClientCertificates.add(chain);
	}
	
	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		x509ServerCertificates.add(chain);
	}
	
	/**
	 * Returns the client certificates that have been collected
	 *  
	 * @return the client certificates
	 */
	public List<X509Certificate[]> getX509ClientCertificates() {
		return x509ClientCertificates;
	}

	/**
	 * Returns the server certificates that have been collected
	 * 
	 * @return the server certificates
	 */
	public List<X509Certificate[]> getX509ServerCertificates() {
		return x509ServerCertificates;
	}
	
}
