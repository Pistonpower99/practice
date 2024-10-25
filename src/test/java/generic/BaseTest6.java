package generic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class BaseTest6 {
	public static String CONFIG_PATH="./config.properties";
	public static ExtentReports extent;
	
	public WebDriver driver;
	public WebDriverWait wait;
	public ExtentTest test;
	
	@BeforeSuite
	public void initReport()
	{
		extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter("target/Spark.html");
        extent.attachReporter(spark);
	}
	
	@AfterSuite
	public void generateReport()
	{
		extent.flush();
	}
	
	@BeforeMethod
	public void preCondition(Method method)
	{
		String testName = method.getName();
		test = extent.createTest(testName);
		
		String browser = Utility.getProperty(CONFIG_PATH,"BROWSER");
		String appURL = Utility.getProperty(CONFIG_PATH,"APP_URL");
		String ITO = Utility.getProperty(CONFIG_PATH,"ITO");
		String ETO = Utility.getProperty(CONFIG_PATH,"ETO");
		
		if(browser.equalsIgnoreCase("chrome"))
		{
			test.info("Open Chrome Browser");
			driver=new ChromeDriver();
		}
		else if(browser.equalsIgnoreCase("firefox"))
		{
			test.info("Open Firefox Browser");
			driver=new FirefoxDriver();
		}
		else
		{
			test.info("Open Edge Browser");
			driver=new EdgeDriver();
		}
		
		test.info("Enter the URL:"+appURL);
		driver.get(appURL);
		
		test.info("Maximize the Browser");
		driver.manage().window().maximize();
		
		test.info("Set implicit Wait:"+ITO);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(ITO)));
		
		test.info("Set Explicit Wait:"+ETO);
		wait=new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(ETO)));
	}
	
	@AfterMethod
	public void postCondition(ITestResult testResult) throws IOException
	{
		String testName = testResult.getName();
		
		int status = testResult.getStatus();
		if(status==1)
		{
			test.pass("The Test is Passed");
		}
		else
		{
			LocalDateTime n = LocalDateTime.now();
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd_MM_YYYY_hh_mm_ss");
			String timeStamp = n.format(format);
			String saveLocation="target/"+testName+timeStamp+".png";
			String forAttachment=testName+timeStamp+".png";
			
			TakesScreenshot t=(TakesScreenshot)driver;
			File srcFile = t.getScreenshotAs(OutputType.FILE);
			File dstFile=new File(saveLocation);
			FileUtils.copyFile(srcFile, dstFile);
			
			test.fail(MediaEntityBuilder.createScreenCaptureFromPath(forAttachment).build());
			test.fail("The Test is Failed");
		}
		test.info("Close the Browser");
		driver.quit();
	}
}
