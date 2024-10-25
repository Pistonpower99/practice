package generic;

import java.lang.reflect.Method;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class BaseTest3 {
	public WebDriver driver;
	public WebDriverWait wait;
	public static ExtentReports extent;
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
		
		test.info("Open Chrome Browser");
		driver=new ChromeDriver();
		
		test.info("Enter the URL");
		driver.get("http://www.google.com");
		
		test.info("Maximize the Browser");
		driver.manage().window().maximize();
		
		test.info("Set implicit Wait");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		
		test.info("Set Explicit Wait");
		wait=new WebDriverWait(driver, Duration.ofSeconds(10));
	}
	
	@AfterMethod
	public void postCondition(ITestResult testResult)
	{
		int status = testResult.getStatus();
		if(status==1)
		{
			test.pass("The Test is Passed");
		}
		else
		{
			test.fail("The Test is Failed");
		}
		test.info("Close the Browser");
		driver.quit();
	}
}
