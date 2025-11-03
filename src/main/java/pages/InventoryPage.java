package pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {

    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By sortDropdown = By.className("product_sort_container");
    private final By shoppingCartLink = By.className("shopping_cart_link");
    private final By shoppingCartBadge = By.className("shopping_cart_badge");
    private final By inventoryItems = By.className("inventory_item"); 
    private final By inventoryItemPrice = By.className("inventory_item_price");
    private final By addToCartButton = By.cssSelector("button[class*='btn_inventory']");
    private final By inventoryItemName = By.className("inventory_item_name");

    public InventoryPage(WebDriver driver) 
    {
       super(driver);
    }

    public boolean isInventoryPageLoaded(String urlPart) 
    {
        return waitForUrlContains(urlPart);
    }

    public void openSidebar() 
    {
        click(menuButton, "Menu Button");
    }

    public LoginPage logout() 
    {
        System.out.println("Executing logout sequence...");
        openSidebar();
        click(logoutLink, "Logout Link");
        return new LoginPage(driver);
    }

    public int getCartItemCount() 
    {
        try 
        {
            String countText = getText(shoppingCartBadge, "Cart Badge Count");
            return Integer.parseInt(countText);
        } 
        catch (Exception e) 
        {
            return 0;
        }
    }

    private double extractPrice(String priceText) 
    {
        return Double.parseDouble(priceText.replace("$", "").trim());
    }

    public List<String> addItemsToCartByPriceRange(double minPrice, double maxPrice) 
    {
        List<String> addedProducts = new ArrayList<>();
        System.out.println("Starting to filter and add items between $" + minPrice + " and $" + maxPrice);
        List<WebElement> items = driver.findElements(inventoryItems);  
        for (WebElement itemContainer : items) 
        {
            try 
            {
                String priceText = itemContainer.findElement(inventoryItemPrice).getText();
                double price = extractPrice(priceText);
                if (price > minPrice && price < maxPrice) 
                {
                    String productName = itemContainer.findElement(inventoryItemName).getText();
                    WebElement addButton = itemContainer.findElement(addToCartButton);                    
                    if (addButton.getText().trim().equalsIgnoreCase("ADD TO CART")) 
                    {
                        addButton.click();
                        addedProducts.add(productName);
                        System.out.println("Added: " + productName + " - $" + price);
                    }
                }
            } 
            catch (Exception e) 
            {
                System.err.println("Error processing item. Skipping. Error: " + e.getMessage());
            }
        }
        
        if (addedProducts.isEmpty()) 
        {
            System.out.println("Warning: No items were added within the price range.");
        } 
        else 
        {
            System.out.println("Total items added to cart: " + addedProducts.size());
        }
        return addedProducts;
    }

    public void addProductToCartByName(String productName) 
    {
        List<WebElement> items = driver.findElements(inventoryItems);
        for (WebElement item : items) 
        {
            String name = item.findElement(inventoryItemName).getText();
            if (name.equals(productName)) 
            {
                WebElement addButton = item.findElement(addToCartButton);
                if (addButton.getText().trim().equalsIgnoreCase("ADD TO CART")) 
                {
                    addButton.click();
                    System.out.println("Added specific product by name: " + productName);
                    break;
                }
            }
        }
    }


    public CartPage goToCart() 
    {
        click(shoppingCartLink, "Shopping Cart Icon");
        return new CartPage(driver);
    }

    public void selectSortOption(String sortOption) 
    {
        WebElement element = driver.findElement(sortDropdown); 
        Select select = new Select(element);
        select.selectByVisibleText(sortOption);
        System.out.println("Selected sort option: " + sortOption);
    }

    








}
