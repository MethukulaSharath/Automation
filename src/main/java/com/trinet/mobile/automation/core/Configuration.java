package com.trinet.mobile.automation.core;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Configuration reads various configuration files and provides configuration
 * properties as java.util.Properties. This class is implemented as a Singleton
 * and clients should use the static getConfiguration method to get the
 * configuration.
 * 
 * This class reads the main configuration file
 * src/main/resources/config.properties and then based on the environment and
 * device properties it will read corresponding files.
 * 
 * It is very important to provide the files with same name as listed in
 * environment and device properties to load the configuration properties
 * correctly.
 * 
 * This class also sets the MDC log property and it is important not to write
 * any logs until that property is set appropriately.
 * 
 * @author sthoutam
 *
 */
public class Configuration {

	protected static Logger logger = LoggerFactory.getLogger(Configuration.class);
	private static Properties properties = null;
	private static boolean isLoaded = false;
	public static String device;
	public static String environment;
	private Configuration() {
	}

	/**
	 * Returns the configuration properties after loading them from various
	 * config files.
	 * 
	 * @return java.util.Properties - properties from various configuration
	 *         files.
	 */
	public static Properties getConfiguration() {
		Configuration.initProperties();
		return Configuration.isLoaded ? Configuration.properties : null;
	}

	private static synchronized void initProperties() {
		if (Configuration.isLoaded)
			return;

		try {
			FileInputStream inputStream = new FileInputStream("src/main/resources/config.properties");
			properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			System.out.println("Error reading main.properties file");
			e.printStackTrace();
			return;
		}

		environment = Configuration.properties.getProperty("environment");
		
		if (StringUtils.isBlank(environment)) {
			System.out.println("Environment is not set in main.properties");
			return;
		}

		// Set the logFileName here and don't write any logs before this as logs
		// will
		// be created as root folder and root.log
		// Below lines are Commented by mahesh because of we are maintaining
		// environment details in config.propeties only
		/*
		 * MDC.put("logFileName", environment); String environmentProperties =
		 * environment + ".properties"; try { FileInputStream inputStream = new
		 * FileInputStream("src/main/resources/" + environmentProperties);
		 * Configuration.properties.load(inputStream); inputStream.close(); }
		 * catch (Exception e) { logger.error("Error reading {} file",
		 * environmentProperties); e.printStackTrace(); return; }
		 */

		 device = properties.getProperty("device");

		if (StringUtils.isBlank(device)) {
			logger.error("device is not set in config.properties");
			return;
		}

		String deviceProperties = device + ".properties";
		try {
			FileInputStream inputStream = new FileInputStream("src/main/resources/" + deviceProperties);
			properties.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			logger.error("Error reading {} file", deviceProperties);
			e.printStackTrace();
			return;
		}

		Configuration.isLoaded = true;
		return;
	}
}
