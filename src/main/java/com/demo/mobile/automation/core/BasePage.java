package com.demo.mobile.automation.core;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/**
 * BasePage initiates the page elements by invoking PageFactory.initElements
 * method. All the test pages should extend this BasePage class.
 * 
 * @author mahesh
 */
public abstract class BasePage {

	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	protected Properties configuration;
	protected AppiumDriver<MobileElement> driver;
	// public WebDriver driver1;

	public BasePage() {
		this.configuration = Configuration.getConfiguration();
//		PageFactory.initElements(new AppiumFieldDecorator(Helpers.driver, 10, TimeUnit.SECONDS), this);
	}

//	public BasePage(AppiumDriver<MobileElement> driver) {
//		this.driver = driver;
//		this.configuration = Configuration.getConfiguration();
//		PageFactory.initElements(new AppiumFieldDecorator(this.driver, 10, TimeUnit.SECONDS), this);
//	}

}
