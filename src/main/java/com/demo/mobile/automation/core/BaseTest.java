package com.demo.mobile.automation.core;


import java.net.URL;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;


//import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import com.demo.mobile.automation.core.Configuration;
import com.demo.mobile.automation.core.Constants;
import com.demo.mobile.automation.core.Helpers;

import io.appium.java_client.AppiumDriver;
//import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.appium.java_client.service.local.AppiumDriverLocalService;

/**
 * BaseTest instantiates the appropriate driver based on the configuration and will set the desired capabilities.
 * All the test classes should extend from BaseTest to setup the driver appropriately.
 * For all the failed tests, screenshots will be taken and stored at the location
 * configured in configuration file.
 * @author Mahesh
 *
 */
public abstract class BaseTest implements ITest {
	protected static AppiumDriver driver;
	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	protected Properties configuration;
	protected String description;
	public static String actualMessage;
	public static String device;
	private static AppiumDriverLocalService service;

	@BeforeMethod
	public void setUp() throws Exception {
		configuration = Configuration.getConfiguration();

		if (null == configuration || configuration.size() == 0) {
			logger.error("Error reading configuration files.");
			throw new Exception("Error reading configuration files.");
		}

		String platformName = configuration.getProperty(MobileCapabilityType.PLATFORM_NAME);

		switch (platformName) 
		{
		case MobilePlatform.ANDROID:
			this.setupAndroidDriver();
			break;
		case MobilePlatform.IOS:
			this.setupIosDriver();
			break;
		default:
			logger.error("Invalid MobilePlatform provided in configuration file.");
			throw new Exception("Invalid MobilePlatform provided in configuration file.");
		}
	}
//	@DataProvider
//	public Object[][] getData(ITestContext context) throws Exception {
//
//		Object[][] testObjArray = ExcelUtils.getData(context);
//
//		return (testObjArray);
//	}
	
	private void setupAndroidDriver() throws Exception {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,configuration.getProperty(MobileCapabilityType.PLATFORM_NAME));
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,configuration.getProperty(MobileCapabilityType.PLATFORM_VERSION));
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,configuration.getProperty(MobileCapabilityType.DEVICE_NAME));
		// capabilities.setCapability("autoAcceptAlerts", true);
		capabilities.setCapability(MobileCapabilityType.APP,System.getProperty("user.dir") + configuration.getProperty(Constants.APP_ANDROID));
		capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
		capabilities.setCapability(MobileCapabilityType.FULL_RESET, false);
		String urlString = new StringBuffer(configuration.getProperty(Constants.PROTOCOL)).append("://")
				.append(configuration.getProperty(Constants.HOST_NAME)).append(":")
				.append(configuration.getProperty(Constants.PORT)).append("/wd/hub").toString();

		driver = new AndroidDriver<WebElement>(new URL(urlString), capabilities);
		
//	logger.debug("BaseTest::setupAndroidDriver done...");
		
	}

	protected void setupIosDriver() throws Exception {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,
				configuration.getProperty(MobileCapabilityType.PLATFORM_NAME));
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,
				configuration.getProperty(MobileCapabilityType.PLATFORM_VERSION));
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,
				configuration.getProperty(MobileCapabilityType.DEVICE_NAME));
		capabilities.setCapability(MobileCapabilityType.UDID, configuration.getProperty(Constants.UDID));
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 8000);
		capabilities.setCapability(MobileCapabilityType.APP,
				System.getProperty("user.dir") + configuration.getProperty(Constants.APP_IOS));
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.IOS_XCUI_TEST);
		 capabilities.setCapability(MobileCapabilityType.AUTO_WEBVIEW, true);
		capabilities.setCapability("autoAcceptAlerts", true);
		// Helpers.setContext(Constants.WEBVIEW_CONTEXT);

		String urlString = new StringBuffer(configuration.getProperty(Constants.PROTOCOL)).append("://")
				.append(configuration.getProperty(Constants.HOST_NAME)).append(":")
				.append(configuration.getProperty(Constants.PORT)).append("/wd/hub").toString();
		driver = new IOSDriver<WebElement>(new URL(urlString), capabilities);
		driver.manage().timeouts().implicitlyWait(Constants.TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

		// TODO (alin): Dynamically switch contexts
		Set<String> contextNames = driver.getContextHandles();
		driver.context((String) contextNames.toArray()[1]); // set context to
															// WEBVIEW_1
		// switchContext(true); //this will enable webview

		Helpers.init(driver);
		logger.debug("BaseTest::setupIosDriver done...");
		
	}

// 
	/**
	  * Dataprovider & Login
	  * 
	  */
	@BeforeClass
	@DataProvider
	public Object[][] getData(ITestContext context) throws Exception {

		Object[][] testObjArray = ExcelUtils.getData(context);

		return (testObjArray);
	}

	public static void switchContext(boolean nativeView) {
		try {
			Set<String> context = ((AppiumDriver<WebElement>) driver).getContextHandles();
			for (String str : context) {
				if(nativeView){
					if (str.contains("WEB")) {
						System.out.println(str);
						driver.context(str);
						break;
					}
				} else{
					System.out.println(str);
					driver.context(str);
					break;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
//Kill the driver After each '@Test' in TestClass  
	@AfterMethod
	public void tearDown() throws InterruptedException {
		if (driver != null) {
			driver.quit();
   }
}
	
	/**
	 * The name of test instance(s). This will be used for data driven tests to provide
	 * meaningful names.
	 * 
	 * @return name associated with a particular instance of a test.
	 */
	public String getTestName() {
		return null != this.description ? this.description : this.getClass().getSimpleName();
	}
}
