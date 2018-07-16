package com.demo.mobile.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.demo.mobile.automation.core.BasePage;
import com.demo.mobile.automation.core.Helpers;

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
		
			Thread.sleep(3000);
			if (tcType.equals("Positive")){		
			
				logger.info("Enter Valid Credentials" );
	Email.sendKeys(username);
	password.sendKeys(passwd);
	logger.info("Entered Password : "+ passwd );
	Thread.sleep(2000);
	signIn.click();
	logger.info("tapped on signIn  " );
	Thread.sleep(3000);
			}
			else if(tcType.equals("Negative"))		
				{
				logger.info("Enter InValid Credentials" );
				Email.sendKeys(username);
				logger.info("Entered Invalid Email : "+ username );
				password.sendKeys(passwd);
				logger.info("Entered Invalid Password : "+ passwd );
				Thread.sleep(2000);
				signIn.click();
				logger.info("tapped on signIn  " );
				Thread.sleep(3000);
				logger.info("Logged in Successfully");
			}
	
	String title=text.getText();
	logger.info("Tittle of the page : "+ title );
	//System.out.println(title);
	//Helpers.waitAndClick(timeOffMenu);
	logger.info("test for GIT ::::::::");
		return signInResponse;
	}
	
	
}
