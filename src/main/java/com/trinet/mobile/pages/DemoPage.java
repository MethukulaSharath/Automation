package com.trinet.mobile.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.trinet.mobile.automation.core.BasePage;
import com.trinet.mobile.automation.core.Helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class DemoPage  extends BasePage{
	

	
	
	@FindBy(xpath = "//*[@resource-id='login.demoapp.com.demoapp:id/email']")
	public MobileElement Email;
	
	@FindBy(xpath = "//*[@resource-id='login.demoapp.com.demoapp:id/password']")
	public MobileElement password;
	
	@FindBy(xpath = "//*[@resource-id='login.demoapp.com.demoapp:id/email_sign_in_button']")
	public MobileElement signIn;
	
	
	@FindBy(xpath = "//android.widget.TextView[@text='Hello world']")
	public MobileElement text;
	
	public DemoPage(AppiumDriver<MobileElement> driver) {
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		this.driver = driver;
	}
	
	public String signIn(String username, String passwd, String tcType) throws InterruptedException {
		String signInResponse =null;
		try {
			Thread.sleep(3000);
			if (tcType.equals("Positive")){		
	Email.sendKeys(username);
	password.sendKeys(passwd);
	Thread.sleep(3000);
	signIn.click();
	Thread.sleep(5000);
			}
			else
				//(tcType.equals("Negative"))		
				{
				Email.sendKeys(username);
				password.sendKeys(passwd);
				Thread.sleep(3000);
				signIn.click();
				Thread.sleep(5000);
			}
	
	String title=text.getText();
	System.out.println(title);
	//Helpers.waitAndClick(timeOffMenu);
		} catch (Exception exception) {
			logger.info("Unable to signIn");
		}
		return signInResponse;
	}
	
	
}
