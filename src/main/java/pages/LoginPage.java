package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage{

    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.xpath("//h3[@data-test='error']");

    public LoginPage(WebDriver driver) 
    {
        super(driver);
    }

    public void enterUsername(String username) 
    {
        sendKeys(usernameField, username, "Username Field");
    }

    public void enterPassword(String password) 
    {
        sendKeys(passwordField, password, "Password Field");
    }

    public LoginPage clickLoginButtonExpectingFailure() 
    {
        click(loginButton, "Login Button (Negative Test)"); 
        return this; 
    }

    public InventoryPage login(String username, String password) 
    {
    	enterUsername(username); 
        enterPassword(password);       
        click(loginButton, "Login Button");
        //handleUnexpectedAlert(); 
        return new InventoryPage(driver);
    }

    public boolean isLoginButtonDisplayed() 
    {
        return isDisplayed(loginButton);
    }

    public String getErrorMessage() 
    {
        return getText(errorMessage, "Login Error Message");
    }

    public boolean isErrorMessageDisplayed() 
    {
        return isDisplayed(errorMessage);
    }



}
