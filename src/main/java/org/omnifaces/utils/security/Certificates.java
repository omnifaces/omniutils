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
package org.omnifaces.utils.security;

import static org.omnifaces.utils.Lang.isEmpty;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

/**
 * Collection of utility methods for working with Certificates and SSL.
 * 
 * @author Arjan Tijms
 *
 */
public final class Certificates {
	
	private Certificates() {
	}
	
	/**
	 * Attempts to query a server for the X509 certificate chain it will
	 * use in the SSL handshake.
	 * 
	 * <p>
	 * This method uses a default timeout of 15 seconds.
	 * 
	 * @param host the server's host
	 * @param port the server's port
	 * @return The certificate chain, or null if it could not be obtained.
	 */
	public static X509Certificate[] getCertificateChainFromServer(String host, int port) {
		return getCertificateChainFromServer(host, port, 15000);
	}
	
	/**
	 * Attempts to query a server for the X509 certificate chain it will
	 * use in the SSL handshake.
	 * 
	 * @param host the server's host
	 * @param port the server's port
	 * @param timeout the socket timeout, in milliseconds.
	 * @return The certificate chain, or null if it could not be obtained.
	 */
	public static X509Certificate[] getCertificateChainFromServer(String host, int port, int timeout) {

		InterceptingX509TrustManager interceptingTrustManager = new InterceptingX509TrustManager();

		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] { interceptingTrustManager }, null);

			try (SSLSocket socket = (SSLSocket) context.getSocketFactory().createSocket(host, port)) {
				socket.setSoTimeout(timeout);
				socket.startHandshake();
			}

		} catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
			e.printStackTrace();
		}
		
		if (interceptingTrustManager.getX509ServerCertificates().isEmpty()) {
			return null;
		}

		return interceptingTrustManager.getX509ServerCertificates().get(0);
	}
	
	/**
	 * Extracts the host name from the first X509 certificate in a chain. 
	 * 
	 * <p>
	 * This method assumes RFC 2253 format of the distinguished named, and will take the CN name
	 * to be representative of the host name.
	 * 
	 * @param serverCertificateChain the chain from which to extract the host name
	 * @return the CN from the first certificate corresponding to the host name
	 */
	public static String getHostFromCertificate(X509Certificate[] serverCertificateChain) {
		String[] names = serverCertificateChain[0]
							   .getIssuerX500Principal()
							   .getName()
							   .split(",");
		
		if (isEmpty(names)) {
			throw new IllegalStateException("No CN name found");
		}
		
		// In the X.500 distinguished name using the format defined in RFC 2253, CN is the first
		// element and represents the host
		String cn = names[0];
		
		return cn.substring(cn.indexOf('=') + 1).trim();
	}
	
	/**
	 * Generates a random RSA keypair with a keysize of 2048 bits.
	 * 
	 * @return a random RSA keypair
	 */
	public static KeyPair generateRandomRSAKeys() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
			keyPairGenerator.initialize(2048);

			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Creates a temporary JKS key store on disk initialized with the given private key and 
	 * certificate and the well known default password "changeit" (without quotes).
	 * 
	 * @param privateKey the key used to initialize the key store
	 * @param certificate the certificate used to initialize the key store
	 * @return the path on disk to the temporary key store
	 */
	public static String createTempJKSKeyStore(PrivateKey privateKey, X509Certificate certificate) {
		try {
			Path tmpKeyStorePath = Files.createTempFile("trustStore", ".jks");
			
			createJKSKeyStore(tmpKeyStorePath, "changeit".toCharArray(), privateKey, certificate);

			return tmpKeyStorePath.toString();
		   
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}
	
	/**
	 * Creates a JKS key store on disk initialized with the given private key and 
	 * certificate, at the given location and with the given password.
	 * 
	 * @param path the full path (directory and file name) where the key store is created
	 * @param password the password used to protect the key store
	 * @param privateKey the key used to initialize the key store
	 * @param certificate the certificate used to initialize the key store
	 */
	public static void createJKSKeyStore(Path path, char[] password, PrivateKey privateKey, X509Certificate certificate) {
		try {
			KeyStore keyStore = KeyStore.getInstance("jks");
			keyStore.load(null, null);

			keyStore.setEntry(
					"omniKey",
					new PrivateKeyEntry(privateKey, new Certificate[] { certificate }),
					new PasswordProtection(password));

			keyStore.store(new FileOutputStream(path.toFile()), password);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Creates a temporary JKS trust store on disk initialized with the given
	 * certificates and the well known default password "changeit" (without quotes).
	 * 
	 * @param certificates the certificates used to initialize the trust store
	 * @return the path on disk to the temporary trust store
	 */
	public static String createTempJKSTrustStore(X509Certificate[] certificates) {
		try {
			Path tmpTrustStorePath = Files.createTempFile("trustStore", ".jks");
			
			createJKSTrustStore(tmpTrustStorePath, "changeit".toCharArray(), certificates);

			return tmpTrustStorePath.toString();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}
	
	/**
	 * Creates a JKS key trust on disk initialized with the given 
	 * certificates, at the given location and with the given password.
	 * 
	 * @param path the full path (directory and file name) where the trust store is created
	 * @param password the password used to protect the trust store
	 * @param certificates the certificates used to initialize the trust store
	 */
	public static void createJKSTrustStore(Path path, char[] password, X509Certificate[] certificates) {
		try {
			KeyStore trustStore = KeyStore.getInstance("jks");
			trustStore.load(null, null);

			for (int i = 0; i < certificates.length; i++) {
				trustStore.setCertificateEntry("omniCert" + i, certificates[i]);
			}

			trustStore.store(new FileOutputStream(path.toFile()), password);
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Sets the system-wide (JVM) trust store to the one referenced by the
	 * given path.
	 * 
	 * <p>
	 * The default password "changeit" is used.
	 * 
	 * @param path the path on disk where the trust store is located
	 */
	public static void setSystemTrustStore(String path) {
		setSystemTrustStore(path, "changeit");
	}
	
	/**
	 * Sets the system-wide (JVM) trust store to the one referenced by the
	 * given path.
	 * 
	 * <p>
	 * The default password "changeit" is used.
	 * 
	 * @param path the path on disk where the trust store is located
	 * @param password the password to access the trust store
	 */
	public static void setSystemTrustStore(String path, String password) {
		System.setProperty("javax.net.ssl.trustStore", path);
		System.setProperty("javax.net.ssl.trustStorePassword", password);
	}
	

}
