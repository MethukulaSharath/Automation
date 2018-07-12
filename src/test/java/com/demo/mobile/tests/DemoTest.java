package com.demo.mobile.tests;
import java.util.LinkedHashMap;
import org.testng.annotations.Test;

import com.demo.mobile.automation.core.BaseTest;
import com.demo.mobile.pages.DemoPage;

public class DemoTest extends BaseTest {
	
	
	
	@Test(dataProvider = "getData")	
	public void Login(LinkedHashMap<String, String> data) throws Exception {
	DemoPage login=new DemoPage(driver);
		login.signIn(data.get("userid"), data.get("password"), data.get("testCaseType"));
		
	}

	

}
