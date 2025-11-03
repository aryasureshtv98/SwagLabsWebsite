package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static ExtentReports extent;
    
    public static class ExtentLogger 
    {
        private static ExtentTest getTest() 
        {
            return extentTest.get();
        }
        
        public static void logInfo(String message) 
        {
            if (getTest() != null) 
            {
                getTest().log(Status.INFO, "INFO: " + message);
            }
        }

        public static void assertTrue(boolean condition, String passMessage, String failMessage) 
        {
            try 
            {
                Assert.assertTrue(condition, failMessage);
                getTest().log(Status.PASS, "ASSERTION PASSED: " + passMessage);
            } 
            catch (AssertionError e) 
            {
                getTest().log(Status.FAIL, "ASSERTION FAILED: " + failMessage + " - Details: " + e.getMessage());
                throw e;
            }
        }

        public static void assertEquals(Object actual, Object expected, String passMessage, String failMessage) 
        {
            try 
            {
                Assert.assertEquals(actual, expected, failMessage);
                getTest().log(Status.PASS, "ASSERTION PASSED: " + passMessage);
            } 
            catch (AssertionError e) 
            {
                getTest().log(Status.FAIL, "ASSERTION FAILED: " + failMessage + " - Details: " + e.getMessage());
                throw e;
            }
        }
        
        public static void assertFalse(boolean condition, String passMessage, String failMessage) 
        {
            try 
            {
                Assert.assertFalse(condition, failMessage);
                getTest().log(Status.PASS, "ASSERTION PASSED: " + passMessage);
            } 
            catch (AssertionError e) 
            {
                getTest().log(Status.FAIL, "ASSERTION FAILED: " + failMessage + " - Details: " + e.getMessage());
                throw e;
            }
        }
    }
    
    public static ExtentTest getExtentTest() 
    {
        return extentTest.get();
    }

    private ExtentReports setupExtentReports() 
    {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String fileName = "ExtentReport_" + timestamp + ".html";
        String targetDir = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "reports" + File.separator;
        try 
        {
            Files.createDirectories(Paths.get(targetDir));
        } 
        catch (IOException e) 
        {
            System.err.println("Failed to create report directory: " + targetDir + ". Error: " + e.getMessage());
        }
        String reportPath = System.getProperty("user.dir") + "/test-output/reports/" + fileName;
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setReportName("Sauce Labs Automation Test Report");
        sparkReporter.config().setDocumentTitle("Test Results - SwagLabs");
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application", "Sauce Labs Demo");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        return extent;
    }

    @Override
    public void onStart(ITestContext context) 
    {
        if (extent == null) 
        {
            extent = setupExtentReports();
        }
        System.out.println("\n----------------------------------------------------------------------------------------------");
        System.out.println("Starting Test Suite: " + context.getName());
        System.out.println("----------------------------------------------------------------------------------------------\n");
    }

    @Override
    public void onFinish(ITestContext context) 
    {
        if (extent != null) 
        {
            extent.flush();
        }
        System.out.println("\n----------------------------------------------------------------------------------------------");
        System.out.println("Finished Test Suite: " + context.getName());
        System.out.println("----------------------------------------------------------------------------------------------\n");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName;
        String methodName = result.getMethod().getMethodName();
        Object[] parameters = result.getParameters();
        org.testng.annotations.Test testAnnotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(org.testng.annotations.Test.class);
        if (testAnnotation != null && !testAnnotation.testName().isEmpty()) 
        {
            testName = testAnnotation.testName();
        } 
        else 
        {
            testName = methodName;
        }

        if (methodName.equals("testLoginAndLogoutScenarios") && parameters.length > 0) 
        {
            String username = parameters[0].toString();
            String password = parameters[1].toString();
            testName += " [User with username : " + username + " and password : " + password + "]";
        }
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().log(Status.PASS, "Test Finished Successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) 
    {
        extentTest.get().log(Status.FAIL, result.getThrowable());
        WebDriver driver = (WebDriver) result.getTestContext().getAttribute("WebDriver");
        if (driver != null) 
        {
            String screenshotPath = captureScreenshot(driver, result.getMethod().getMethodName());
            if (screenshotPath != null) 
            {
                String relativePath = ".." + File.separator + "screenshots" + File.separator + new File(screenshotPath).getName();
                extentTest.get().addScreenCaptureFromPath(relativePath, "Test Failed Screenshot");
            }
        } 
        else 
        {
            extentTest.get().log(Status.WARNING, "WebDriver instance not found to capture screenshot.");
        }
        extentTest.get().log(Status.FAIL, "Test Failed");
        extentTest.remove(); 
    }

    @Override
    public void onTestSkipped(ITestResult result) 
    {
        extentTest.get().log(Status.SKIP, "Test Skipped");
    }

    public String captureScreenshot(WebDriver driver, String screenshotName) {
        try 
        {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String targetDir = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "screenshots" + File.separator;
            Files.createDirectories(Paths.get(targetDir));
            String fileName = screenshotName + "_" + new Date().getTime() + ".png";
            String destinationPath = targetDir + fileName;
            Files.copy(source.toPath(), Paths.get(destinationPath));
            return destinationPath;
        } 
        catch (Exception e) 
        {
            System.err.println("Exception while taking screenshot: " + e.getMessage());
            return null;
        }
    }
}