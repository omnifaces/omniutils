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

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.logging.Level.SEVERE;
import static org.omnifaces.utils.properties.PropertiesUtils.PropertiesFormat.LIST;
import static org.omnifaces.utils.properties.PropertiesUtils.PropertiesFormat.XML;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public final class PropertiesUtils {

	private static final Logger logger = Logger.getLogger(PropertiesUtils.class.getName());
	private static final String CONFIGURATION_BASE_DIR = "/conf/";
	private static final String META_INF_CONFIGURATION_BASE_DIR = "META-INF/conf/";

	public static enum PropertiesFormat {XML, LIST}

	private PropertiesUtils() {
	}

	public static Map<String, String> loadPropertiesFromClasspath(String baseName) {

		Map<String, String> properties = new HashMap<>();

		getResource(baseName + ".xml").ifPresent(url -> loadPropertiesFromUrl(url, properties, XML));
		getResource(baseName + ".properties").ifPresent(url -> loadPropertiesFromUrl(url, properties, LIST));

		return unmodifiableMap(properties);
	}

	/**
	 * Loads the properties file in properties list format with the given name from the configuration directory of META-INF with support for staging.
	 *
	 * The properties will loaded from the default properties file and the properties file from the given stage. If both files contain properties with
	 * the same key, the returned Map object will only contain the stage specific ones.
	 *
	 * @param fileName the file name of the properties file
	 * @param stageSystemPropertyName the name of the system property from which the stage is read
	 * @param defaultStage the default stage
	 * @return an immutable map instance containing the key/value pairs from the given file
	 */
	public static Map<String, String> loadPropertiesListStagedFromClassPath(String fileName, String stageSystemPropertyName, String defaultStage) {
		return loadStagedFromClassPath(PropertiesUtils::loadListFromURL, fileName, stageSystemPropertyName, defaultStage);
	}

	/**
	 * Loads the properties file in XML format with the given name from the configuration directory of META-INF with support for staging.
	 *
	 * The properties will loaded from the default properties file and the  properties file from the given stage. If both files contain properties
	 * with the same key, the returned Map object will only contain the stage specific ones.
	 *
	 * @param fileName
	 * 		the file name of the properties file
	 * @param stageSystemPropertyName
	 * 		the name of the system property from which the stage is read
	 * @param defaultStage
	 * 		the default stage
	 * @return an immutable map instance containing the key/value pairs from the given file
	 */
	public static Map<String, String> loadXMLPropertiesStagedFromClassPath(String fileName, String stageSystemPropertyName, String defaultStage) {
		return loadStagedFromClassPath(PropertiesUtils::loadXMLFromURL, fileName, stageSystemPropertyName, defaultStage);
	}

	public static Map<String, String> loadStagedFromClassPath(BiConsumer<URL, Map<? super String, ? super String>> loadMethod, String fileName, String stageSystemPropertyName, String defaultStage) {

		String stage = getStage(stageSystemPropertyName, defaultStage);

		Map<String, String> settings = new HashMap<>();

		asList(META_INF_CONFIGURATION_BASE_DIR + fileName, META_INF_CONFIGURATION_BASE_DIR + stage + "/" + fileName)
			.forEach(
				path -> getResource(path)
							.ifPresent(
								url -> loadMethod.accept(url, settings)));

		return unmodifiableMap(settings);
	}


	/**
	 * Loads the properties file in properties list format with the given name from the configuration directory of an EAR with support for staging.
	 *
	 * The properties will loaded from the default properties file and the properties file from the given stage. If both files contain properties with
	 * the same key, the returned Map object will only contain the stage specific ones.
	 *
	 * @param fileName
	 * 		the file name of the properties file
	 * @param stageSystemPropertyName
	 * 		the name of the system property from which the stage is read
	 * @return an immutable map instance containing the key/value pairs from the given file
	 */
	public static Map<String, String> loadPropertiesListStagedFromEar(String fileName, String stageSystemPropertyName) {
		return loadStagedFromEar(PropertiesUtils::loadListFromUrl, fileName, stageSystemPropertyName);
	}

	/**
	 * Loads the properties file in XML format with the given name from the configuration directory of an EAR with support for staging.
	 *
	 * The properties will loaded from the default properties file and the  properties file from the given stage. If both files contain properties
	 * with the same key, the returned Map object will only contain the stage specific ones.
	 *
	 * @param fileName
	 *            the file name of the properties file
	 * @param stageSystemPropertyName
	 *            the name of the system property from which the stage is read
	 * @return an immutable map instance containing the key/value pairs from the
	 *         given file
	 */
	public static Map<String, String> loadXMLPropertiesStagedFromEar(String fileName, String stageSystemPropertyName) {
		return loadStagedFromEar(PropertiesUtils::loadXMLFromUrl, fileName, stageSystemPropertyName);
	}

	public static Map<String, String> loadStagedFromEar(BiConsumer<String, Map<? super String, ? super String>> loadMethod, String fileName, String stageSystemPropertyName) {

		String earBaseUrl = getEarBaseUrl();
		String stage = getProperty(stageSystemPropertyName);
		if (stage == null) {
			throw new IllegalStateException(stageSystemPropertyName + " property not found. Please add it to VM arguments, e.g. -D" + stageSystemPropertyName + "=some_stage");
		}

		Map<String, String> settings = new HashMap<>();

		loadMethod.accept(earBaseUrl + CONFIGURATION_BASE_DIR + fileName, settings);
		loadMethod.accept(earBaseUrl + CONFIGURATION_BASE_DIR + stage + "/" + fileName, settings);

		return unmodifiableMap(settings);
	}

	public static String getEarBaseUrl() {
		Optional<URL> dummyUrl = getResource("META-INF/dummy.txt");

		if (dummyUrl.isPresent()) {
			String dummyExternalForm = dummyUrl.get().toExternalForm();

			// Exploded deployment JBoss example
			// vfs:/opt/jboss/standalone/deployments/someapp.ear/someapp.jar/META-INF/dummy.txt

			// Packaged deployment JBoss example
			// vfs:/content/someapp.ear/someapp.jar/META-INF/dummy.txt

			int jarPos = dummyExternalForm.lastIndexOf(".jar");
			if (jarPos != -1) {

				String withoutJar = dummyExternalForm.substring(0, jarPos);
				int lastSlash = withoutJar.lastIndexOf('/');

				withoutJar = withoutJar.substring(0, lastSlash);

				if (withoutJar.endsWith("/lib")) {
					withoutJar = withoutJar.substring(0, withoutJar.length() - 4);
				}

				if (withoutJar.endsWith("/WEB-INF")) {
					withoutJar += "/classes";
				}

				return withoutJar;
			}

			// TODO add support for other servers and JRebel

			throw new IllegalStateException("Can't derive EAR root from: " + dummyExternalForm);
		}

		throw new IllegalStateException("Can't find META-INF/dummy.txt on the classpath. This file should be present in a jar in the ear/lib folder");
	}

	public static void loadListFromUrl(String url, Map<? super String, ? super String> settings) {
		loadPropertiesFromUrl(url, settings, LIST);
	}

	public static void loadXMLFromUrl(String url, Map<? super String, ? super String> settings) {
		loadPropertiesFromUrl(url, settings, XML);
	}

	public static void loadListFromURL(URL url, Map<? super String, ? super String> settings) {
		loadPropertiesFromUrl(url, settings, LIST);
	}

	public static void loadXMLFromURL(URL url, Map<? super String, ? super String> settings) { // TODO name not ideal
		loadPropertiesFromUrl(url, settings, XML);
	}

	public static void loadPropertiesFromUrl(String url, Map<? super String, ? super String> settings, PropertiesFormat propertiesFormat) {
		try {
			loadPropertiesFromUrl(new URL(url), settings, propertiesFormat);
		} catch (MalformedURLException e) {
			logger.log(SEVERE, "Error while loading settings.", e);
		}
	}

	public static void loadPropertiesFromUrl(URL url, Map<? super String, ? super String> settings, PropertiesFormat propertiesFormat) {
		try {
			loadPropertiesFromStream(url.openStream(), url.toString(), settings, propertiesFormat);
		} catch (IOException e) {
			logger.log(SEVERE, "Error while loading settings.", e);
		}
	}
	
	public static void loadPropertiesFromStream(InputStream in, String locationDescription, Map<? super String, ? super String> settings, PropertiesFormat propertiesFormat) {
		Properties properties = new Properties();
		try {

			if (propertiesFormat == XML) {
				properties.loadFromXML(in);
			} else {
				properties.load(in);
			}

			logger.info(String.format("Loaded %d settings from %s.", properties.size(), locationDescription));

			for (Entry<?, ?> entry : properties.entrySet()) {
				settings.put((String) entry.getKey(), (String) entry.getValue());
			}

		} catch (IOException e) {
			logger.log(SEVERE, "Error while loading settings.", e);
		}
	}
	
	public static String getStage(String stageSystemPropertyName, String defaultStage) {
		String stage = getProperty(stageSystemPropertyName);
		if (stage == null) {
			if (defaultStage == null) {
				throw new IllegalStateException(stageSystemPropertyName + " property not found. Please add it to VM arguments, e.g. -D" + stageSystemPropertyName + "=some_stage");
			}

			stage = defaultStage;
		}

		return stage;
	}

	private static Optional<URL> getResource(String name) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(name);
		if (url == null) {
			url = PropertiesUtils.class.getClassLoader().getResource(name);
		}
		return Optional.ofNullable(url);
	}



}
