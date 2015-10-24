package org.omnifaces.utils.properties;

import static java.lang.System.getProperty;
import static java.util.Collections.unmodifiableMap;
import static java.util.logging.Level.SEVERE;
import static org.omnifaces.utils.properties.PropertiesUtils.PropertiesFormat.XML;
import static org.omnifaces.utils.properties.PropertiesUtils.PropertiesFormat.LIST;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public final class PropertiesUtils {

	private static final Logger logger = Logger.getLogger(PropertiesUtils.class.getName());
	private static final String CONFIGURATION_BASE_DIR = "/conf/";

	public static enum PropertiesFormat {XML, LIST}

	private PropertiesUtils() {
	}

	/**
	 * Loads the properties file in properties list format with the given name from the configuration directory of an EAR with support for staging.
	 *
	 * The properties will loaded from the default properties file and the properties file from the given stage. If both files contain properties with
	 * the same key, the returned Map object will only contain the stage specific ones.
	 *
	 * @param fileName
	 *            the file name of the properties file
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
		URL dummyUrl = Thread.currentThread().getContextClassLoader().getResource("META-INF/dummy.txt");
		if (dummyUrl == null) {
			dummyUrl = PropertiesUtils.class.getClassLoader().getResource("META-INF/dummy.txt");
		}
		String dummyExternalForm = dummyUrl.toExternalForm();

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

			return withoutJar;
		}

		// TODO add support for other servers and JRebel

		throw new IllegalStateException("Can't derive EAR root from: " + dummyExternalForm);
	}

	public static void loadListFromUrl(String url, Map<? super String, ? super String> settings) {
		loadPropertiesFromUrl(url, settings, LIST);
	}

	public static void loadXMLFromUrl(String url, Map<? super String, ? super String> settings) {
		loadPropertiesFromUrl(url, settings, XML);
	}

	public static void loadPropertiesFromUrl(String url, Map<? super String, ? super String> settings, PropertiesFormat propertiesFormat) {
		Properties properties = new Properties();
		try (InputStream in = new URL(url).openStream()) {

			if (propertiesFormat == XML) {
				properties.loadFromXML(in);
			} else {
				properties.load(in);
			}

			logger.info(String.format("Loaded %d settings from %s.", properties.size(), url));

			for (Entry<?, ?> entry : properties.entrySet()) {
				settings.put((String) entry.getKey(), (String) entry.getValue());
			}

		} catch (IOException e) {
			logger.log(SEVERE, "Error while loading settings.", e);
		}
	}

}
