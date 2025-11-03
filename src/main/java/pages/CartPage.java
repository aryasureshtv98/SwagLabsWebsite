package pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {

    private final By cartItems = By.className("cart_item");
    private final By cartItemName = By.className("inventory_item_name");
    private final By cartItemPrice = By.className("inventory_item_price");
    private final By cartItemQuantity = By.className("cart_quantity");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");


    public CartPage(WebDriver driver) 
    {
        super(driver);
    }

    public boolean isCartPageLoaded(String urlPart) 
    {
        return waitForUrlContains(urlPart);
    }

    public List<String> getCartItemNames() 
    {
        List<String> productNames = new ArrayList<>();
        List<WebElement> itemContainers = driver.findElements(cartItems);
        System.out.println("Found " + itemContainers.size() + " items in the cart.");
        for (WebElement item : itemContainers) 
        {
            try 
            {
                String name = item.findElement(cartItemName).getText().trim();
                productNames.add(name);
            } 
            catch (Exception e) 
            {
                System.err.println("Could not find product name for a cart item. Skipping.");
            }
        }
        return productNames;
    }
    
    public void removeItemByName(String productName) 
    {
        List<WebElement> itemContainers = driver.findElements(cartItems);
        boolean found = false;
        for (WebElement item : itemContainers) 
        {
            String name = item.findElement(cartItemName).getText().trim();
            if (name.equals(productName)) 
            {
                By removeButton = By.cssSelector("button.btn_secondary[name^='remove-']"); 
                item.findElement(removeButton).click();
                System.out.println("Removed item: " + productName + " from the cart.");
                found = true;
                break;
            }
        }
        if (!found) {
            System.err.println("Item '" + productName + "' not found in cart for removal.");
        }
    }
    
    private double extractPrice(String priceText) {
        try 
        {
            return Double.parseDouble(priceText.replace("$", "").trim());
        } 
        catch (NumberFormatException e) 
        {
            System.err.println("Error parsing price text: " + priceText);
            throw new RuntimeException("Invalid price format encountered.", e);
        }
    }
    
    public double getCartItemsPriceSum() 
    {
        double sum = 0.0;
        List<WebElement> itemContainers = driver.findElements(cartItems);
        if (itemContainers.isEmpty()) 
        {
            System.out.println("Cart is empty. Item price sum is 0.0.");
            return 0.0;
        }
        for (WebElement item : itemContainers) 
        {
            try 
            {
                String priceText = item.findElement(cartItemPrice).getText();
                String quantityText = item.findElement(cartItemQuantity).getText();                
                double price = extractPrice(priceText);
                int quantity = Integer.parseInt(quantityText.trim());                
                sum += (price * quantity);
            } 
            catch (Exception e) 
            {
                System.err.println("Error reading price or quantity for a cart item. Skipping. Error: " + e.getMessage());
            }
        }
        sum = Math.round(sum * 100.0) / 100.0;
        System.out.println("Calculated sum of all cart item prices: $" + String.format("%.2f", sum));
        return sum;
    }
    

    public CheckoutPageOne checkout() 
    {
        click(checkoutButton, "Checkout Button");
        return new CheckoutPageOne(driver);
    }


    public InventoryPage continueShopping() 
    {
        click(continueShoppingButton, "Continue Shopping Button");
        return new InventoryPage(driver);
    }
}
