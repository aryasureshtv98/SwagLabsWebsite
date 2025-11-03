package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutFinishPage extends BasePage {


    private final By successMessage = By.className("complete-header");
    private final By backHomeButton = By.id("back-to-products");
    private final By successText=By.className("complete-text");

    public CheckoutFinishPage(WebDriver driver) 
    {
        super(driver);
    }


    public boolean isCheckoutCompletePageLoaded() 
    {
        boolean isUrlCorrect = waitForUrlContains("checkout-complete.html");
        boolean isSuccessMessageDisplayed = isDisplayed(successMessage);        
        return isUrlCorrect && isSuccessMessageDisplayed;
    }


    public String getSuccessMessage() 
    {
        return getText(successMessage, "Order Success Message");
    }
    
    public String getOrderDispatchMessage() 
    {
        return getText(successText, "Order Dispatch Detailed Message");
    }


    public InventoryPage clickBackHome() 
    {
        click(backHomeButton, "Back Home Button");
        return new InventoryPage(driver);
    }
}
