package com.demo.mobile.automation.core;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite;

import com.gargoylesoftware.htmlunit.javascript.host.Set;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.ReporterType;


/**
 * Generates extent DB reports for each test.
 * Emails the report to the users listed in the configuration if email flag is turned on.
 *  
 * @author mahesh
 *
 */
public class Report implements IReporter {

	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	protected ExtentReports extent;
	protected Properties configuration;
	protected DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
	protected Calendar cal = Calendar.getInstance();
	protected String today = dateFormat.format(cal.getTime());

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {

		configuration = Configuration.getConfiguration();
		String filePath = System.getProperty("user.dir") + configuration.getProperty("reportsLocation") + File.separator + configuration.getProperty("environment") + File.separator + "MobileAutomationReport_" + today + ".html";
		String extentDbPath = System.getProperty("user.dir") + configuration.getProperty("reportsLocation") + File.separator + configuration.getProperty("environment") + File.separator + "extent.db";
		extent = new ExtentReports(filePath, true);
		extent.startReporter(ReporterType.DB, extentDbPath);

		extent.addSystemInfo("Environment", configuration.getProperty("environment"));
		extent.addSystemInfo("Device", configuration.getProperty("deviceName"));
		extent.addSystemInfo("Platform Name", configuration.getProperty("platformName"));
		extent.addSystemInfo("Platform Version", configuration.getProperty("platformVersion"));

		for (ISuite suite : suites) {
			Map<String, ISuiteResult> result = suite.getResults();

			for (ISuiteResult r : result.values()) {
				ITestContext context = r.getTestContext();

				buildTestNodes(context.getPassedTests(), LogStatus.PASS);
				buildTestNodes(context.getFailedTests(), LogStatus.FAIL);
				buildTestNodes(context.getSkippedTests(), LogStatus.SKIP);
			}
		}

		for (String s : Reporter.getOutput()) {
			extent.setTestRunnerOutput(s);
		}

		extent.flush();
		extent.close();
		//Invoke email handler...
		try {
		  String sendEmail = configuration.getProperty("email", "off");
		  if ("on".equalsIgnoreCase(sendEmail)) {
			this.sendMail(filePath);
		  }
		} catch (Exception ex) {
			logger.error("Error occurred while sending email report");
		}
	}
	protected void sendMail(String filePath) {
		Properties props = new Properties();
		//props.put("mail.smtp.auth", "false");
		props.put("mail.user", this.configuration.getProperty("mailfrom"));
		props.put("mail.host", "smtp.1and1.com");
		props.put("mail.port", "25");
		try {
			Session session = Session.getDefaultInstance(props,null);
			MimeMessage msg = new MimeMessage(session);			
			msg.setSubject("Automation Test Report For Project " + this.configuration.getProperty("project"));
			msg.setFrom(new InternetAddress(this.configuration.getProperty("mailfrom"))); //read it from properties
			// Tokenize mail to string on ,mahesh.sudanagunta@trinet.com
			String mailTo = this.configuration.getProperty("mailto", "sharath.methukula@ptgindia.com");
			StringTokenizer multiTokenizer = new StringTokenizer(mailTo, ",");

			while (multiTokenizer.hasMoreTokens())
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(multiTokenizer.nextToken()));

		    MimeBodyPart messageBodyPart = new MimeBodyPart();
		    Multipart multipart = new MimeMultipart();
		    messageBodyPart = new MimeBodyPart();
		    
		    String fileName = "AppiumResults.html";
		    DataSource source = new FileDataSource(filePath);
		    messageBodyPart.setDataHandler(new DataHandler(source));
		    messageBodyPart.setFileName(fileName);
		    multipart.addBodyPart(messageBodyPart);
		    msg.setContent(multipart);
			
			Transport transport = session.getTransport("smtp");
		    transport.connect((String) props.get("mail.smtp.user"),"Mahesh@16s");
		//	transport.connect((String) props.get("mail.smtp.user"), null);
		//	transport.connect();
			transport.sendMessage(msg, msg.getAllRecipients());
			logger.info("Reports has Sent to Mail");
			System.out.println("Reports has Sent to Mail");
			transport.close();
			
			
		} catch(Exception ex) {
			logger.error("Failed to send email " + ex.getMessage());
			ex.printStackTrace();
		}
	}



	private void buildTestNodes(IResultMap tests, LogStatus status) {
		ExtentTest test;
		String screenshot_path, image;

		if (tests.size() > 0) {
			for (ITestResult result : tests.getAllResults()) {
				test = extent.startTest(result.getMethod().getMethodName());

				test.getTest().setStartedTime(getTime(result.getStartMillis()));
				test.getTest().setEndedTime(getTime(result.getEndMillis()));

				for (String group : result.getMethod().getGroups())
					test.assignCategory(group);

				String message = "Test " + status.toString().toLowerCase() + "ed";

				if (result.getThrowable() != null) {

					logger.info(result.getName() + "\n");
					screenshot_path = System.getProperty("user.dir") + configuration.getProperty("reportsLocation") + File.separator + configuration.getProperty("environment") + File.separator + result.getName() + ".png"; 
					image = test.addScreenCapture(screenshot_path);
					test.log(status, result.getName(), image);
					message += "<pre>" + result.getThrowable().getMessage() + "</pre>";

				}
				test.log(status, message);

				extent.endTest(test);
			}
		}
	}

	

	private Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}
	@Test
	public void test() { 
		//sendMail();
		Session session;
	    MimeMultipart multipart = new MimeMultipart();

	    Properties properties = new Properties();

	    properties.put("mail.smtp.host", "smtp.1and1.com");
	    //properties.put("mail.smtp.starttls.enable", "false");
	    properties.put("mail.smtp.port", "25");
	   // properties.put("mail.smtp.user", "venkat.geesidi@ptgindia.com");
	    properties.put("mail.smtp.user", "veerasiva.gangireddy@ptgindia.com");
	    properties.put("mail.smtp.auth", "true");
	    session = Session.getDefaultInstance(properties);
	    session.setDebug(true);
	    try {
	        MimeMessage msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress("nagendar.reddy@ptgindia.com"));
	        msg.setRecipients(Message.RecipientType.TO, "nagendar.reddy@ptgindia.com");
	        msg.setSubject("Email Test");
	        msg.setSentDate(new Date());

	        // BODY
	        MimeBodyPart mbp = new MimeBodyPart();
	        /*if (isHTMLFormat) {
	            mbp.setContent(body.toString(), "text/html");
	        } else {
	            mbp.setText(body.toString());
	        }*/
	        
	        mbp.setText("Mahesh test");

	        multipart.addBodyPart(mbp);

	        msg.setContent(multipart);
	        // Transport.send(msg);

	        Transport t = session.getTransport("smtp");
	     //   t.connect((String) properties.get("mail.smtp.user"), "Mahesh@16s");
	        t.connect();
	        t.sendMessage(msg, msg.getAllRecipients());
	        t.close();
	    } catch (Exception mex) {
	       System.out.println(mex);
	    }

	}

}
