package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import utils.ConfigReader;
import java.time.Duration;

public class BaseTest {

    private WebDriver driver;
    protected ConfigReader config;

    public WebDriver getDriver() 
    {
        return driver;
    }

    @BeforeMethod
    public void setup(ITestContext context) 
    {
        config = new ConfigReader();
        int implicitWaitTime = config.getImplicitWaitTime();
        String baseUrl = config.getBaseUrl();
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitTime));
        driver.get(baseUrl);
        context.setAttribute("WebDriver", driver);
    }

    @AfterMethod
    public void tearDown() 
    {
        if (driver != null) 
        {
            driver.quit();
        }
    }
}
