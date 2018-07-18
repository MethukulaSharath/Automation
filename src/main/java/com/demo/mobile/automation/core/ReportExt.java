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
import org.testng.xml.XmlSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.relevantcodes.extentreports.ReporterType;




public class ReportExt implements IReporter{

	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	protected ExtentReports extent;
	protected Properties configuration = Configuration.getConfiguration();
	protected DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
	protected Calendar cal = Calendar.getInstance();
	protected String today = dateFormat.format(cal.getTime());
	
	private int hh = cal.get(Calendar.HOUR_OF_DAY);
	private int mi = cal.get(Calendar.MINUTE);
	private int ss = cal.get(Calendar.SECOND);
	
	
	
	String filePath = "C:\\reports\\Demo" + File.separator + "MobileAutomation_" + today +"_"+hh+"_"+mi+"_"+ss + ".html";
	
	String extentDbPath = System.getProperty("user.dir") + configuration.getProperty("reportsLocation") + File.separator + configuration.getProperty("environment") + File.separator + "extent.db";
	
	
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		// TODO Auto-generated method stub
		init();
		
		for(ISuite suite : suites) {
			Map<String, ISuiteResult> result = suite.getResults();
			
			for(ISuiteResult r : result.values()) {
				ITestContext context = r.getTestContext();
				
				buildTestNodes(context.getPassedTests(), Status.PASS);
				 buildTestNodes(context.getFailedTests(), Status.FAIL);
				buildTestNodes(context.getSkippedTests(), Status.SKIP);
               
			}
			
		}
		
		extent.flush();
		
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
		System.out.println("Start sendEmail Method");
		
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
		    
			System.out.println("Reports File Path :" + filePath );
			 
		    String fileName = "AppiumResults.html";
		    DataSource source = new FileDataSource(filePath);
		    messageBodyPart.setDataHandler(new DataHandler(source));
		    messageBodyPart.setFileName(fileName);
		    multipart.addBodyPart(messageBodyPart);
		    msg.setContent(multipart);
			
			Transport transport = session.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.user"),"Value*123");
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
	
	private void buildTestNodes(IResultMap tests, Status status) {
		// TODO Auto-generated method stub
	
		ExtentTest test;
		String screenshot_path, image;
		
		if(tests.size() > 0) {
			
			for(ITestResult result : tests.getAllResults()) {
				test = extent.createTest(result.getMethod().getMethodName());
				
				for(String group : result.getMethod().getGroups()) {
					test.assignCategory(group);		
				}
				
				if(result.getThrowable()!= null) {
					
					test.log(status, result.getThrowable());
				}
				else {
	                    test.log(status, "Test " + status.toString().toLowerCase() + "ed");
	                    
					}
				
				test.getModel().setStartTime(getTime(result.getStartMillis()));
	           test.getModel().setEndTime(getTime(result.getEndMillis()));
				
			}
			
		}
		
	}

	private void init() {
		// TODO Auto-generated method stub
		
		
		System.out.println("File path " + filePath);
		
	//	extent = new ExtentReports(filePath, true);
	//	((Object) extent).startReporter(ReporterType.DB, extentDbPath);

	
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(filePath);
		htmlReporter.config().setDocumentTitle("ExtentReports - Demo App Reports");
		htmlReporter.config().setReportName("ExtentReports - Test Results");
		htmlReporter.config().setTestViewChartLocation(ChartLocation.BOTTOM);
		htmlReporter.config().setTheme(Theme.DARK);
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
	
		extent.setSystemInfo("Environment", configuration.getProperty("environment"));
		extent.setSystemInfo("Device", configuration.getProperty("deviceName"));
		extent.setSystemInfo("Platform Name", configuration.getProperty("platformName"));
		extent.setSystemInfo("Platform Version", configuration.getProperty("platformVersion"));
	}
	
	private Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}
	
	
}
