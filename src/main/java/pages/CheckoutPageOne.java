package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPageOne extends BasePage {

    private final By firstNameField = By.id("first-name");
    private final By lastNameField = By.id("last-name");
    private final By zipCodeField = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessage = By.xpath("//h3[@data-test='error']");

    public CheckoutPageOne(WebDriver driver) 
    {
        super(driver);
    }


    public boolean isCheckoutOnePageLoaded(String urlPart) 
    {
        return waitForUrlContains(urlPart);
    }


    public void enterInformation(String firstName, String lastName, String zipCode) 
    {
        if (firstName != null && !firstName.isEmpty()) 
        {
            sendKeys(firstNameField, firstName, "First Name Field");
        }
        if (lastName != null && !lastName.isEmpty()) 
        {
            sendKeys(lastNameField, lastName, "Last Name Field");
        }
        if (zipCode != null && !zipCode.isEmpty()) 
        {
            sendKeys(zipCodeField, zipCode, "Zip/Postal Code Field");
        }
        System.out.println("Attempting to enter checkout information: " + firstName + " " + lastName + ", " + zipCode);
    }
    
    public String getErrorMessage() 
    {
        return getText(errorMessage, "Checkout Validation Error Message");
    }

    public boolean isErrorMessageDisplayed() 
    {
        return isDisplayed(errorMessage);
    }


    public CheckoutPageTwo clickContinue() 
    {
        click(continueButton, "Continue Button");
        return new CheckoutPageTwo(driver); 
    }


    public InventoryPage clickCancel() 
    {
        click(cancelButton, "Cancel Button");
        return new InventoryPage(driver);
    }
}
