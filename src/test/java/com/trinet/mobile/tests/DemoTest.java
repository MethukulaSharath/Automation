package com.trinet.mobile.tests;
import java.util.LinkedHashMap;
import org.testng.annotations.Test;
import com.trinet.mobile.automation.core.BaseTest;
import com.trinet.mobile.pages.DemoPage;

public class DemoTest extends BaseTest {
	
	@Test(dataProvider = "getData")	
	public void Login(LinkedHashMap<String, String> data) throws Exception {
	DemoPage login=new DemoPage(driver);
		login.signIn(data.get("userid"), data.get("password"), data.get("testCaseType"));
		driver.quit();
	}

	

}
