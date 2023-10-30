package testCases;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import capstoneProject.Pages.LoginPage;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import utils.ExcelDataReader;

public class LoginTest {

	private WebDriver driver;
	private ExtentSparkReporter htmlReporter;
	private ExtentReports extent;
	private ExtentTest test;

	@BeforeClass
	public void setup() {
		System.setProperty("webdriver.edge.driver", "D:\\All jar files\\edgedriver_win64\\msedgedriver.exe");
		driver = new EdgeDriver();

		// Initialize Extent Reports
		ExtentSparkReporter spark = new ExtentSparkReporter("Spark.html");
		extent = new ExtentReports();
		extent.attachReporter(spark);
	}

	@BeforeMethod
	public void navigateToLoginPage() {
		driver.get("https://www.saucedemo.com/");
	}

	@AfterMethod
	public void handleTestResults(ITestResult result) {
		// Capture and log test status and screenshot to Extent Report
		if (result.getStatus() == ITestResult.SUCCESS) {
			test.log(Status.PASS, MarkupHelper.createLabel("Test Case Passed", ExtentColor.GREEN));
		} else if (result.getStatus() == ITestResult.FAILURE) {
			test.log(Status.FAIL, MarkupHelper.createLabel("Test Case Failed", ExtentColor.RED));
			captureScreenshot(result.getName());
		}

		// End the test and add it to the Extent Report
		extent.flush();
	}

	@Test(dataProvider = "loginData", dataProviderClass = ExcelDataReader.class)
	public void loginTest(String username, String password, String expectedResult) {
		// Initialize the Extent Test
		test = extent.createTest("loginTest: " + username);

		// Perform login with the provided credentials
		LoginPage loginPage = new LoginPage(driver);
		loginPage.enterCredentials(username, password);
		loginPage.clickLoginButton();

		// Validate the result based on the "Expected Result"
		if (expectedResult.equals("Login Successful")) {
			Assert.assertEquals(driver.getTitle(), "Swag Labs");
			test.pass("Login successful");
			captureScreenshot("loginTest");
		} else if (expectedResult.equals("Error Message")) {
			Assert.assertTrue(loginPage.isErrorMessageDisplayed());
			test.fail("Login failed");
			captureScreenshot("loginTest");
		}

		System.out.println("Username: " + username);
		System.out.println("Password: " + password);
		System.out.println("Expected Result: " + expectedResult);
	}

	@Test
	public void verifyTitleAfterSuccessfulLogin() {
		// Initialize the Extent Test
		test = extent.createTest("verifyTitleAfterSuccessfulLogin");

		// Perform a successful login
		LoginPage loginPage = new LoginPage(driver);
		loginPage.enterCredentials("standard_user", "secret_sauce");
		loginPage.clickLoginButton();

		// Verify the title after a successful login
		Assert.assertEquals(driver.getTitle(), "Swag Labs");
		test.pass("Title verified after a successful login");
		captureScreenshot("verifyTitleAfterSuccessfulLogin");
	}

	@Test
	public void verifyErrorMessage() {
		// Initialize the Extent Test
		test = extent.createTest("verifyErrorMessage");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.enterCredentials("standard_user", "Wrong_Password");
		loginPage.clickLoginButton();
		Assert.assertTrue(loginPage.isErrorMessageDisplayed());
		test.fail("Error message displayed: " + loginPage.getErrorMessage());
		captureScreenshot("verifyErrorMessage");
	}

	@AfterClass
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	// Method to capture and save a screenshot
	public void captureScreenshot(String methodName) {
		// Add your screenshot capture logic here
		// You can use the code provided earlier in this conversation.
		if (driver instanceof TakesScreenshot) {
			try {
				File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				String screenshotName = methodName + "_" + System.currentTimeMillis() + ".png";
				String screenshotPath = "screenshots/" + screenshotName;
				File destination = new File(screenshotPath);
				FileUtils.copyFile(screenshot, destination);
				test.addScreenCaptureFromPath(screenshotPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
