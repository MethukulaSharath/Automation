package com.trinet.mobile.automation.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import com.trinet.mobile.automation.core.Constants;
import static com.trinet.mobile.automation.core.BaseTest.*;

/**
 * This class contains all the reusable methods that can be used across iOS and
 * Android Page or test classes
 *
 * @author sthoutam
 *
 */
public abstract class Helpers {
	public static AppiumDriver<WebElement> driver;
	private static WebDriverWait driverWait;
	protected static Logger logger = LoggerFactory.getLogger(Helpers.class);
	// Added below variabel as class level --mahesh
	private static Dimension size;
	private static int count;
	private WebElement element;

	/**
	 * Initialize the Appiumdriver. Must be invoked before using any helper
	 * methods. *
	 */
	public static void init(AppiumDriver<WebElement> driver) {
		Helpers.driver = driver;
		driverWait = new WebDriverWait(driver, Constants.TIMEOUT_IN_SECONDS);
	}

	public static String[] generateFutureDate(String format, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, days);
		DateFormat dateFormat = new SimpleDateFormat(format);
		String[] dayMonthYear = dateFormat.format(calendar.getTime()).toString().split("-");

		return dayMonthYear;
	}

	public static boolean extractCheckboxsElement(WebElement ele) {

		try {
			WebElement checkboxs = ele.findElement(By.xpath("preceding::input"));
			if (checkboxs.isSelected()) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Generating Random name Generating Random Names
	 * 
	 * @return randomName
	 */
	public static String generateRandomName(int count) {
		String randomName = null;
		try {
			randomName = RandomStringUtils.randomAlphabetic(count);
		} catch (Exception exception) {
			logger.info("Exception Occured --Random Name generator method ::", exception);
		}
		return randomName;
	}
	public static void assertEquals(String actual, String expected) {
		try {
			logger.info("From UI:--" + actual);
			logger.info("From Excel:--" + expected);
			Assert.assertEquals(actual, expected);
			logger.info("Sucessfully Asserted");
		} catch (Exception e) {
			logger.info("data is invalid");
		}

	}

	public static boolean isEnabled(WebElement ele) {
		try {
			if (ele.isEnabled()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Set implicit wait in seconds *
	 */
	public static void setWait(int seconds) {
		driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
	}

	/**
	 * Return an element by locator *
	 */
	public static WebElement element(By locator) {
		return driver.findElement(locator);
	}

	/**
	 * Return an element by accessibility id *
	 */
	public static WebElement elementByAccessibilityId(String id) {
		return driver.findElementByAccessibilityId(id);
	}

	/**
	 * Return a list of elements by locator *
	 */
	public static List<WebElement> elements(By locator) {
		return driver.findElements(locator);
	}

	/**
	 * Press the back button *
	 */
	public static void back() {
		driver.navigate().back();
	}

	/**
	 * Press the refresh button *
	 */
	public static void refresh() {
		driver.navigate().refresh();
	}

	/**
	 * Press the forward button *
	 */
	public static void forward() {
		driver.navigate().forward();
	}

	/**
	 * Return a list of elements by tag name *
	 */
	public static List<WebElement> tags(String tagName) {
		return elements(forTags(tagName));
	}

	/**
	 * Return a tag name locator *
	 */
	public static By forTags(String tagName) {
		return By.className(tagName);
	}

	/**
	 * Return a static text element by xpath index *
	 */
	public static WebElement staticText(int xpathIndex) {
		return element(forText(xpathIndex));
	}

	/**
	 * Return a static text locator by xpath index *
	 */
	public static By forText(int xpathIndex) {
		return By.xpath("//android.widget.TextView[" + xpathIndex + "]");
	}

	/**
	 * Return a static text element that contains text *
	 */
	public static WebElement text(String text) {
		return element(forText(text));
	}

	/**
	 * Return a static text locator that contains text *
	 */
	public static By forText(String text) {
		return By.xpath("//android.widget.TextView[contains(@text, '" + text + "')]");
	}

	/**
	 * Return a static text element by exact text *
	 */
	public static WebElement textExact(String text) {
		return element(forTextExact(text));
	}

	/**
	 * Return a static text locator by exact text *
	 */
	public static By forTextExact(String text) {
		return By.xpath("//android.widget.TextView[@text='" + text + "']");
	}

	public static By forFind(String value) {
		return By.xpath("//*[@content-desc=\"" + value + "\" or @resource-id=\"" + value + "\" or @text=\"" + value
				+ "\"] | //*[contains(translate(@content-desc,\"" + value + "\",\"" + value + "\"), \"" + value
				+ "\") or contains(translate(@text,\"" + value + "\",\"" + value + "\"), \"" + value
				+ "\") or @resource-id=\"" + value + "\"]");
	}

	public static WebElement find(String value) {
		return element(forFind(value));
	}

	/**
	 * Wait 60 seconds for locator to find an element *
	 */
	public static WebElement wait(By locator) {
		return driverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Wait 60 seconds for locator to find all elements *
	 */
	public static List<WebElement> waitAll(By locator) {
		return driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
	}

	/**
	 * Wait 60 seconds for locator to not find a visible element *
	 */
	public static boolean waitInvisible(By locator) {
		return driverWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	/**
	 * Return true if scrolled to an element that exactly matches given text
	 * value and clicked successfully.
	 */
	public static boolean scrollToExact(List<WebElement> list, String value) {
		if (null == list || list.size() == 0 || StringUtils.isEmpty(value))
			return false;

		Iterator<WebElement> iterator = list.iterator();
		WebElement element;
		while (iterator.hasNext()) {
			element = iterator.next();
			if (value.equalsIgnoreCase(element.getText())) {
				element.click();
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if scrolled to an element that either matches exactly or
	 * contains given text value and clicked successfully.
	 */
	public static boolean scrollTo(List<WebElement> list, String value) {
		if (null == list || list.size() == 0 || StringUtils.isEmpty(value))
			return false;

		Iterator<WebElement> iterator = list.iterator();
		WebElement element;
		while (iterator.hasNext()) {
			element = iterator.next();
			if (value.equalsIgnoreCase(element.getText())) {
				element.click();
				return true;
			}
		}

		// If exact match is not found, check contains...
		iterator = list.iterator();
		while (iterator.hasNext()) {
			element = iterator.next();
			if (value.contains(element.getText())) {
				element.click();
				return true;
			}
		}
		return false;
	}

	public static void swipeDown() {
		// Dimension size;
		// Get the size of screen.
		size = driver.manage().window().getSize();

		// Find swipe start and end point from screen's width and height.
		// Find starting point which is at bottom side of screen.
		int start = (int) (size.height * 0.97);

		// Find end point which is at top side of screen.
		int end = (int) (size.height * 0.05);

		// Find horizontal point where you wants to swipe. It is in middle of
		// screen width.
		int startx = size.width / 2;

		// Swipe from Bottom to Top.
		driver.swipe(startx, start, startx, end, 3000);
	}

	// Added swipeUp & swipeHorizontal methods --- Mahesh
	public static void swipeUp() throws InterruptedException {
		// Get the size of screen.
		size = driver.manage().window().getSize();
		System.out.println(size);

		// Find swipe start and end point from screen's with and height.
		// Find starty point which is at bottom side of screen.
		int starty = (int) (size.height * 0.97);
		// Find endy point which is at top side of screen.
		int endy = (int) (size.height * 0.05);
		// Find horizontal point where you wants to swipe. It is in middle of
		// screen width.
		int startx = size.width / 2;
		System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
		Thread.sleep(2000);
		// Swipe from Top to Bottom.
		driver.swipe(startx, endy, startx, starty, 3000);
		Thread.sleep(2000);
	}

	public static void swipeHorizontal() throws InterruptedException {
		// Get the size of screen.
		size = driver.manage().window().getSize();
		System.out.println(size);

		// Find swipe start and end point from screen's with and height.
		// Find startx point which is at right side of screen.
		int startx = (int) (size.width * 0.70);
		// Find endx point which is at left side of screen.
		int endx = (int) (size.width * 0.30);
		// Find vertical point where you wants to swipe. It is in middle of
		// screen height.
		int starty = size.height / 2;
		System.out.println("startx = " + startx + " ,endx = " + endx + " , starty = " + starty);

		// Swipe from Right to Left.
		driver.swipe(startx, starty, endx, starty, 3000);
		Thread.sleep(2000);

		// Swipe from Left to Right.
		driver.swipe(endx, starty, startx, starty, 3000);
		Thread.sleep(2000);
	}

	/* This method is used to scroll down vertically */
	/*
	 * public void scrolldown(int x, int y){ try{
	 * js.executeScript("window.scrollBy("+x +","+y+")", ""); }catch(Exception
	 * e){ logger.error("Exception {} Details : {}", e, e.getMessage());
	 * Assert.fail("Failed to scroll down " +e.getMessage()); }
	 * 
	 * /** Wait until an element is available for clicking *
	 */
	public static WebElement waitForClickable(WebElement element) {
		driverWait.until(ExpectedConditions.elementToBeClickable(element));
		return element;
	}

	/**
	 * Wait until an element is displayed
	 * 
	 * @return *
	 */
	public static WebElement waitForDisplay(WebElement element) {
		driverWait.until(ExpectedConditions.visibilityOf(element));
		return element;
	}

	/**
	 * Wait until a list of elements are displayed *
	 */
	public static List<WebElement> waitForDisplay(List<WebElement> element) {
		driverWait.until(ExpectedConditions.visibilityOfAllElements(element));
		return element;
	}

	/**
	 * Clear input element and type *
	 */
	public static void clearAndType(WebElement element, String text) {
		waitForClickable(element);
		element.click();
		element.clear();
		Helpers.sleep(1000);
		element.sendKeys(text);
	}

	/**
	 * Type the given text into the given element *
	 */
	public static void type(WebElement element, String text) {
		waitForClickable(element);
		element.click();
		Helpers.sleep(1000);
		element.sendKeys(text);
		Helpers.sleep(1000);

	}

	/**
	 * Wait for visibility of given element and Click *
	 */
	public static void waitAndClick(WebElement element) {
		waitForDisplay(element);
		waitForClickable(element);
		element.click();
	}

	/**
	 * Clicks the given element *
	 */
	public static void click(WebElement element) {
		element.click();
	}

	/**
	 * Current thread will sleep for given milliseconds *
	 */
	public static void sleep(long milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			logger.error("Caught exception while trying to sleep " + e.getMessage());
		}
	}

	/**
	 * Set driver context to the given context. *
	 */
	public static boolean setContext(String context) {

		if (StringUtils.isBlank(context)) {
			logger.error("Invalid context provided to setContext");
			return false;
		}

		Set<String> contextNames = driver.getContextHandles();

		// First check if there is a match.
		for (String contextName : contextNames) {
			if (contextName.equalsIgnoreCase(context)) {
				driver.context(contextName);
				logger.info(contextName);
				return true;
			}
		}

		// If there is no exact match then check for contains
		for (String contextName : contextNames) {
			if (contextName.contains(context)) {
				driver.context(contextName);
				logger.info(contextName);
				return true;
			}
		}
		return false;
	}

	/**
	 * Prints driver context to log. *
	 */
	public static void printContext() {
		logger.debug("Helpers::printContext context " + driver.getContext());

		Set<String> contextNames = driver.getContextHandles();

		logger.debug("Helpers::printContext Available Contexts ");
		for (String contextName : contextNames) {
			logger.debug("Helpers::printContext context " + contextName);
		}
	}

	/**
	 * Takes the current screenshot and stores the file whose name includes the
	 * given testName.
	 * 
	 * @param testName
	 * @throws IOException
	 */
	public static void takeScreenShot(String testName) throws IOException {
		logger.debug("Helpers::takeScreenShot taking screenshot. " + testName);
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String filePath = Helpers.getScreenShotPath(testName);
		FileUtils.copyFile(srcFile, new File(filePath));
	}

	/**
	 * Returns the screenshot path based on the configuration and given test
	 * name. <user_dir>/<screenshot_location_in_configuration>/
	 * <environment_in_configuration>/<device_in_configuration>/<testName>/
	 * <current_time_in_millis>.jpg
	 * 
	 * @param testName
	 * @return screenShotPath - <user_dir>/
	 *         <screenshot_location_in_configuration>/
	 *         <environment_in_configuration>/<device_in_configuration>/
	 *         <testName>/<current_time_in_millis>.jpg
	 */
	public static String getScreenShotPath(String testName) {
		Properties configuration = Configuration.getConfiguration();
		String screenShotPath = System.getProperty("user.dir") + File.separator
				+ configuration.getProperty("screenshotsLocation") + File.separator
				+ configuration.getProperty("environment") + File.separator + configuration.getProperty("device")
				+ File.separator + testName + "_" + System.currentTimeMillis() + ".jpg";

		return screenShotPath;
	}

	public static String getDataFilePath(String module, String filename) {
		return System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
				+ "resources" + File.separator + "com" + File.separator + "trinet" + File.separator + "mobile"
				+ File.separator + module + File.separator + "testdata" + File.separator + filename;
	}

	// Handles Alerts -- @Author -- Mahesh
	public static boolean isAlertExists() {
		boolean isAlertExists = false;

		try {
			driver.switchTo().alert();
			isAlertExists = true;
		} catch (NoAlertPresentException exception) {
			isAlertExists = false;
		}
		return isAlertExists;
	}

	public static void getText(WebElement element) {
		try {
			String text = element.getText();
			System.out.println(text);
			logger.info("get text ::-" + text);
		} catch (Exception exception) {
			logger.info("Exception occued in getText method " + exception);
		}
	}

	public static void navigateToBack() {
		try {
			Thread.sleep(5000);
			((AndroidDriver) driver).pressKeyCode(AndroidKeyCode.BACK);
			logger.info("Navigate to Pervious Appliaction Page ");
		} catch (Exception exception) {
			logger.info("Exception occued in navigateToBack method " + exception);
		}

	}

	public static void scrollDown(By xpath) {

		for (int i = 0; i <= count; i++) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			HashMap<String, String> scrollObject = new HashMap<String, String>();
			scrollObject.put("direction", "down");
			// scrollObject.put("element", ele1);
			js.executeScript("mobile: scroll", scrollObject);
			if (driver.findElements(xpath).size() != 0) {
				break;
			}
			count++;
		}
	}

	public static int generateRandomNumberWithinRange(int min, int max) {
		Random random = new Random();
		return random.nextInt((max - min) + 1) + min;

	}

	public static void switchtoBack() {
		try {

			Thread.sleep(5000);
			driver.navigate().back();
			logger.info("swift to Appliaction Pervious Page ");
		} catch (Exception exception) {
			logger.info("Exception occued in switchtoBack method " + exception);
		}
	}

	// if Element Present to perform Action
	public static boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
}