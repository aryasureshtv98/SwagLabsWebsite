package pages;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {

    protected WebDriver driver;
    private static final Duration EXPLICIT_WAIT_TIMEOUT = Duration.ofSeconds(20);

    public BasePage(WebDriver driver) 
    {
        this.driver = driver;
    }

    public void handleUnexpectedAlert() {
        WebDriverWait shortWait = new WebDriverWait(driver, EXPLICIT_WAIT_TIMEOUT);
        try {
            shortWait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            alert.accept();
            System.out.println("Handled and accepted unexpected alert with text: " + alertText);
        } 
        catch (TimeoutException e) 
        {
            System.out.println("No unexpected alert appeared within " + EXPLICIT_WAIT_TIMEOUT.getSeconds() + " seconds. Continuing...");
        } 
        catch (Exception e) 
        {
             System.err.println("Error while trying to handle alert: " + e.getMessage());
        }
    }

    public WebElement waitForVisibility(By locator) {
        try 
        {
            return new WebDriverWait(driver, EXPLICIT_WAIT_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(locator));
        } 
        catch (Exception e) 
        {
            System.err.println("Element not visible after " + EXPLICIT_WAIT_TIMEOUT.getSeconds() + " seconds: " + locator.toString());
            throw new RuntimeException("Element visibility failed for: " + locator.toString(), e);
        }
    }

    public void click(By locator, String elementName) 
    {
        try 
        {
            WebElement element = waitForClickability(locator);
            element.click();
            System.out.println("Clicked on element: " + elementName);
        } 
        catch (Exception e) 
        {
            System.err.println("Failed to click on " + elementName + " located by " + locator + ". Error: " + e.getMessage());
            throw e;
        }
    }

    public void sendKeys(By locator, String text, String elementName) {

        try 
        {
            WebElement element = waitForVisibility(locator);
            element.clear();
            element.sendKeys(text);
            System.out.println("Entered text '" + text + "' into " + elementName);
        } 
        catch (Exception e) 
        {
            System.err.println("Failed to enter text into " + elementName + " located by " + locator + ". Error: " + e.getMessage());
            throw e;
        }
    }

    public String getText(By locator, String elementName) 
    {
        WebElement element = waitForVisibility(locator);
        String text = element.getText().trim();
        System.out.println("Retrieved text from " + elementName + ": " + text);
        return text;
    }

    public boolean isDisplayed(By locator) 
    {
        try 
        {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } 
        catch (Exception e) 
        {
            System.out.println("No element of locator :" + locator + "is dispayed");
            return false;
        }
    }

    private WebElement waitForClickability(By locator) 
    {
        try 
        {
            return new WebDriverWait(driver, EXPLICIT_WAIT_TIMEOUT).until(ExpectedConditions.elementToBeClickable(locator));
        } 
        catch (Exception e) 
        {
            System.err.println("Element not clickable after " + EXPLICIT_WAIT_TIMEOUT.getSeconds() + " seconds: " + locator.toString());
            throw new RuntimeException("Element clickability failed for: " + locator.toString(), e);
        }
    }
    
    public boolean waitForUrlContains(String urlSegment) 
    {
        try 
        {
            new WebDriverWait(driver, EXPLICIT_WAIT_TIMEOUT).until(ExpectedConditions.urlContains(urlSegment));
            System.out.println("Successfully navigated to URL containing: " + urlSegment);
            return true;
        } 
        catch (TimeoutException e) 
        {
            System.err.println("Navigation failed. Current URL does not contain '" + urlSegment + "' after " + EXPLICIT_WAIT_TIMEOUT.getSeconds() + " seconds. Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }

}
