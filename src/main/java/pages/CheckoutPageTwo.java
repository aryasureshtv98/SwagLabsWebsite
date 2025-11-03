package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class CheckoutPageTwo extends BasePage {


    private final By finishButton = By.id("finish");
    private final By cancelButton = By.id("cancel");
    private final By cartItem = By.className("cart_item");
    private final By cartItemName = By.className("inventory_item_name");
    private final By itemTotalLabel = By.className("summary_subtotal_label");
    private final By taxLabel = By.className("summary_tax_label");
    private final By totalLabel = By.cssSelector(".summary_info_label.summary_total_label");

    public CheckoutPageTwo(WebDriver driver) 
    {
        super(driver);
    }

 
    public boolean isCheckoutTwoPageLoaded(String urlPart) 
    {
        return waitForUrlContains(urlPart);
    }

    public CheckoutFinishPage clickFinish() 
    {
        click(finishButton, "Finish Button");
        return new CheckoutFinishPage(driver);
    }

    public InventoryPage clickCancel() 
    {
        click(cancelButton, "Cancel Button");
        return new InventoryPage(driver);
    }

    public List<WebElement> getItemsInOverview() 
    {
        waitForVisibility(cartItem);
        return driver.findElements(cartItem);
    }

    public boolean isProductPresent(String productName) 
    {
        for (WebElement item : getItemsInOverview()) 
        {
            if (item.findElement(cartItemName).getText().equals(productName)) {
                System.out.println("Product found in checkout overview: " + productName);
                return true;
            }
        }
        System.out.println("Product NOT found in checkout overview: " + productName);
        return false;
    }

    public String getItemTotal() 
    {
        return getText(itemTotalLabel, "Item Subtotal").replace("Item total: ", "");
    }

    public String getTaxAmount() 
    {
        return getText(taxLabel, "Tax Amount").replace("Tax: ", "");
    }

    public String getTotalAmount() 
    {
        return getText(totalLabel, "Total Amount").replace("Total: ", "");
    }
}
