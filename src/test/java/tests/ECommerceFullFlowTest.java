package tests;

import static listeners.TestListener.ExtentLogger.*; 
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import listeners.RetryAnalyzer;
import pages.CartPage;
import pages.CheckoutPageOne;
import pages.CheckoutPageTwo;
import pages.CheckoutFinishPage;
import pages.InventoryPage;
import pages.LoginPage;
import utils.ExcelUtils;
import java.util.List;

public class ECommerceFullFlowTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData()
    {
        return ExcelUtils.getTestDataAsDataProvider();
    }


    @Test(dataProvider = "loginData", retryAnalyzer = RetryAnalyzer.class, priority=1, testName = "1. Data-Driven Login Scenarios Check") 
    public void testLoginAndLogoutScenarios(String username, String password)
    {
        System.out.println("\n------------------------------------------------------------------------------------------------");
        System.out.println(">>> STARTING TEST: 1. Data-Driven Login Scenarios Check [Username: " + username + " and Password: " + password +"] <<<");
        System.out.println("------------------------------------------------------------------------------------------------\n");
        
        LoginPage loginPage = new LoginPage(getDriver());
        logInfo("Attempting login for User with username : " + username + " and password : " + password);
        System.out.println("STEP: Attempting login...");
        
        if ("standard_user".equalsIgnoreCase(username) && "secret_sauce".equalsIgnoreCase(password))
        {
            InventoryPage inventoryPage = loginPage.login(username, password);
            System.out.println("INFO: Positive flow expected.");
            
            assertTrue(inventoryPage.isInventoryPageLoaded("inventory"), 
                "Positive Login Successful : User landed on the Inventory Page.", 
                "Positive Login Failed : URL does not contain 'inventory'.");
            System.out.println("ASSERT: Inventory page loaded successfully.");
            
            LoginPage postLogoutPage = inventoryPage.logout();
            System.out.println("STEP: Logging out.");
            
            assertTrue(postLogoutPage.isLoginButtonDisplayed(), 
                "Logout Successful : Login button is visible after logging out.", 
                "Logout Failed: Login button is NOT visible.");
            System.out.println("ASSERT: Logout successful, returned to login page.");
        }
        else
        {
            System.out.println("INFO: Negative flow expected.");
            loginPage.enterUsername(username);
            loginPage.enterPassword(password);
            loginPage.clickLoginButtonExpectingFailure();
            
            assertTrue(loginPage.isErrorMessageDisplayed(),
                "Negative test with invalid credentials: Login error message was successfully displayed.",
                "Negative Login Failed with invalid credentials: Error message was NOT displayed for user");
            System.out.println("ASSERT: Error message displayed successfully.");
            
            String actualError = loginPage.getErrorMessage();
            logInfo("Retrieved Error Message: " + actualError);
            System.out.println("INFO: Retrieved Error: " + actualError);
            
            String expectedErrorText;
            if ("locked_out_user".equalsIgnoreCase(username)) 
            {
                expectedErrorText = "Epic sadface: Sorry, this user has been locked out.";
            } 
            else if (username.isEmpty()) 
            {
                expectedErrorText = "Epic sadface: Username is required";
            } 
            else if (password.isEmpty()) 
            {
                expectedErrorText = "Epic sadface: Password is required";
            } 
            else 
            {
                expectedErrorText = "Epic sadface: Username and password do not match any user in this service";
            }
            
            assertTrue(actualError.contains(expectedErrorText.split(":")[0]), 
                "Validated that the error message contains the expected text for failure.",
                "Error Message Mismatch. Expected to contain: '" + expectedErrorText + "', but found: '" + actualError + "'.");
            System.out.println("ASSERT: Error message content validated successfully.");
        }
        System.out.println("\n----------------------------------------------------------------------------------------------");
        System.out.println("<<< ENDING TEST: 1. Data-Driven Login Scenarios Check [Username: " + username + " and Password: " + password +"] <<<");
        System.out.println("----------------------------------------------------------------------------------------------\n");
    }

    @Parameters({"username", "password"})
    @Test(groups = {"ECommerceFlow"}, dependsOnMethods = {"testLoginAndLogoutScenarios"}, priority=2, testName = "2. Full Checkout Flow: Price Range Addition & Verification")
    public void testAddItemByPriceAndVerifyCartTotal(String username, String password)
    {
        System.out.println("\n----------------------------------------------------------------------------------------------");
        System.out.println(">>> STARTING TEST: 2. Full Checkout Flow: Price Range Addition & Verification <<<");
        System.out.println("----------------------------------------------------------------------------------------------\n");
        logInfo("Starting E-Commerce Flow: Add Items by Price and Verify Cart Total");
        
        LoginPage loginPage = new LoginPage(getDriver());
        getDriver().get(config.getBaseUrl());
        InventoryPage inventoryPage = loginPage.login(username, password);
        
        assertTrue(inventoryPage.isInventoryPageLoaded("inventory"), 
            "Pre-condition Login Successful: Inventory Page loaded.", 
            "Pre-condition Login Failed: Inventory Page NOT loaded.");
        System.out.println("STEP: Logged in successfully as " + username + ".");
            
        List<String> addedProducts = inventoryPage.addItemsToCartByPriceRange(10.00, 40.00);
        
        assertTrue(addedProducts.size() > 0, 
            "Items Added: " + addedProducts.size() + ". Successfully added items within the price range.", 
            "No items were added within the specified price range.");
        System.out.println("STEP: Successfully added " + addedProducts.size() + " items to cart: " + addedProducts);
            
        assertEquals(inventoryPage.getCartItemCount(), addedProducts.size(), 
            "Cart badge count (" + inventoryPage.getCartItemCount() + ") matches the number of added products.", 
            "Cart badge count mismatch.");
        System.out.println("ASSERT: Cart badge count matches added products count.");
        
        CartPage cartPage = inventoryPage.goToCart();
        
        assertTrue(cartPage.isCartPageLoaded("cart"), 
            "Successfully navigated to Cart Page.", 
            "Failed to navigate to Cart Page.");
        System.out.println("STEP: Navigated to Cart Page.");
            
        List<String> cartItemNames = cartPage.getCartItemNames();
        
        assertEquals(cartItemNames.size(), addedProducts.size(), 
            "Number of items displayed in cart matches the number added.", 
            "Number of items in cart mismatch.");
            
        assertTrue(cartItemNames.containsAll(addedProducts), 
            "Cart contents successfully verified against the added list.", 
            "Some added items are missing from the cart.");
        System.out.println("ASSERT: Cart contents verified successfully.");
            
        double calculatedSubtotal = cartPage.getCartItemsPriceSum();
        logInfo("Calculated Subtotal from CartPage: $" + String.format("%.2f", calculatedSubtotal));
        System.out.println("INFO: Calculated Subtotal: $" + String.format("%.2f", calculatedSubtotal));
        
        CheckoutPageOne checkoutPageOne = cartPage.checkout();
        
        assertTrue(checkoutPageOne.isCheckoutOnePageLoaded("checkout-step-one"), 
            "Successfully navigated to Checkout Step One (User Info).", 
            "Did not navigate to Checkout Step One.");
        System.out.println("STEP: Navigated to Checkout Step 1.");
            
        logInfo("Entering user information (First Name: Test, Last Name: User, Zip: 12345).");
        checkoutPageOne.enterInformation("Test", "User", "12345");
        CheckoutPageTwo checkoutPageTwo = checkoutPageOne.clickContinue();
        System.out.println("STEP: Entered user info and proceeded to Checkout Step 2.");
        
        assertTrue(checkoutPageTwo.isCheckoutTwoPageLoaded("checkout-step-two"), 
            "Successfully navigated to Checkout Step Two (Overview).", 
            "Did not navigate to Checkout Step Two.");
            
        String itemTotalText = checkoutPageTwo.getItemTotal(); 
        double actualItemTotal = Double.parseDouble(itemTotalText.replaceAll("[^0-9.]", ""));
        
        assertEquals(actualItemTotal, calculatedSubtotal, 
            "Checkout Item Total matches calculated Cart Sum.", 
            "Checkout Item Total (" + actualItemTotal + ") does not match calculated Cart Sum (" + calculatedSubtotal + ").");
        System.out.println("ASSERT: Checkout Item Total ($" + actualItemTotal + ") matches Cart Sum.");
        
        checkoutPageTwo.clickFinish();
        CheckoutFinishPage finishPage = new CheckoutFinishPage(getDriver());
        
        assertTrue(finishPage.isCheckoutCompletePageLoaded(), 
            "Successfully reached Checkout Complete Page.", 
            "Checkout Finish Page not loaded.");
        System.out.println("STEP: Checkout completed successfully.");
        
        String actualSuccessMsg = finishPage.getSuccessMessage();
        assertEquals(actualSuccessMsg, "Thank you for your order!",
            "Validated main success message on finish page.",
            "Main success message text mismatch. Found: " + actualSuccessMsg);
        logInfo("Validated main success message: " + actualSuccessMsg);
        System.out.println("ASSERT: Main success message validated.");
        
        String actualDispatchMsg = finishPage.getOrderDispatchMessage();
        assertTrue(actualDispatchMsg.contains("Your order has been dispatched"),
            "Validated order dispatch detailed message.",
            "Order dispatch detailed message text mismatch. Found: " + actualDispatchMsg);
        logInfo("Validated order dispatch detail: " + actualDispatchMsg);
        System.out.println("ASSERT: Order dispatch message validated.");
            
        finishPage.clickBackHome();
        
        assertTrue(inventoryPage.isInventoryPageLoaded("inventory"), 
            "Returned to inventory page after checkout.", 
            "Failed to return to inventory page after checkout.");
        System.out.println("STEP: Returned to Inventory Page.");
            
        inventoryPage.logout();
        logInfo("Logout Successful. Test Flow 2 Finished.");
        System.out.println("STEP: Logged out successfully.");

        System.out.println("\n----------------------------------------------------------------------------------------------");
        System.out.println("<<< ENDING TEST: 2. Full Checkout Flow: Price Range Addition & Verification <<<");
        System.out.println("----------------------------------------------------------------------------------------------\n");
    }

    @Parameters({"username", "password"})
    @Test(groups = {"ECommerceFlow"}, dependsOnMethods = {"testLoginAndLogoutScenarios"}, priority=3,testName = "3. Cart Total Update Verification on Item Modification")
    public void testCartTotalUpdateOnItemModification(String username, String password)
    {
        System.out.println("\n----------------------------------------------------------------------------------------------");
        System.out.println(">>> STARTING TEST: 3. Cart Total Update Verification on Item Modification <<<");
        System.out.println("----------------------------------------------------------------------------------------------\n");
        logInfo("Starting Test: Verify Cart Total Update on Item Modification");
        
        LoginPage loginPage = new LoginPage(getDriver());
        getDriver().get(config.getBaseUrl());
        InventoryPage inventoryPage = loginPage.login(username, password);
        
        assertTrue(inventoryPage.isInventoryPageLoaded("inventory"), 
            "Pre-condition Login Successful: Inventory Page loaded.", 
            "Pre-condition Login Failed.");
        System.out.println("STEP: Logged in successfully as " + username + ".");
            
        final String item1 = "Sauce Labs Backpack";
        final String item2 = "Sauce Labs Bike Light";
        final String item3 = "Sauce Labs Bolt T-Shirt";
        inventoryPage.addProductToCartByName(item1);
        inventoryPage.addProductToCartByName(item2);
        inventoryPage.addProductToCartByName(item3);
        logInfo("Added three items: " + item1 + ", " + item2 + ", " + item3);
        System.out.println("STEP: Added 3 initial items to cart.");
        
        assertEquals(inventoryPage.getCartItemCount(), 3, 
            "Cart count is 3 after adding three items.", 
            "Cart count is NOT 3 after adding three items.");
        System.out.println("ASSERT: Cart badge count is 3.");
        
        CartPage cartPage = inventoryPage.goToCart();
        
        assertTrue(cartPage.isCartPageLoaded("cart"), 
            "Successfully navigated to Cart Page.", 
            "Did not navigate to Cart Page.");
        System.out.println("STEP: Navigated to Cart Page.");
            
        double initialSum = cartPage.getCartItemsPriceSum();
        logInfo("Initial Cart Item Sum (3 items): $" + String.format("%.2f", initialSum));
        System.out.println("INFO: Initial Cart Sum: $" + String.format("%.2f", initialSum));
        
        List<String> cartItemNamesInitial = cartPage.getCartItemNames();
        
        assertTrue(cartItemNamesInitial.contains(item1) && cartItemNamesInitial.contains(item2) && cartItemNamesInitial.contains(item3), 
            "Initial cart contents verified: all 3 items are present.", 
            "Initial cart contents mismatch. Expected all 3 items.");
        System.out.println("ASSERT: Initial cart contents verified.");
        
        CheckoutPageOne checkoutPageOne = cartPage.checkout();
        logInfo("Checking out to verify item total.");
        checkoutPageOne.enterInformation("Test", "User", "12345");
        CheckoutPageTwo checkoutPageTwo = checkoutPageOne.clickContinue();
        System.out.println("STEP: Proceeded to Checkout Overview.");
        
        String itemTotalText = checkoutPageTwo.getItemTotal(); 
        double actualItemTotalCheckout = Double.parseDouble(itemTotalText.replaceAll("[^0-9.]", ""));
        
        assertEquals(actualItemTotalCheckout, initialSum, 
            "Checkout Item Total verified against Initial Cart Sum.", 
            "Checkout Item Total mismatch.");
        System.out.println("ASSERT: Checkout Item Total ($" + actualItemTotalCheckout + ") matches Initial Cart Sum.");
        
        inventoryPage = checkoutPageTwo.clickCancel();
        cartPage = inventoryPage.goToCart();
        System.out.println("\nSUB-FLOW: Starting removal verification.");
        
        assertTrue(cartPage.isCartPageLoaded("cart"), 
            "Successfully returned to Cart Page.", 
            "Failed to return to Cart Page.");
            
        double sumBeforeRemoval = cartPage.getCartItemsPriceSum(); 
        cartPage.removeItemByName(item2);
        logInfo("Removed item: " + item2);
        System.out.println("STEP: Removed item: " + item2);
        
        List<String> cartItemNamesAfterRemoval = cartPage.getCartItemNames();
        
        assertEquals(cartItemNamesAfterRemoval.size(), 2, 
            "Cart item count is 2 after removal.", 
            "Cart item count is NOT 2 after removal.");
            
        assertFalse(cartItemNamesAfterRemoval.contains(item2), 
            item2 + " successfully verified as removed from cart.",
            item2 + " was NOT removed from cart.");
        
        double sumAfterRemoval = cartPage.getCartItemsPriceSum();
        logInfo("Cart Item Sum after removal: $" + String.format("%.2f", sumAfterRemoval));
        
        assertTrue(sumAfterRemoval < sumBeforeRemoval, 
            "Cart total successfully decreased after item removal.", 
            "Cart total did NOT decrease after item removal.");
        System.out.println("ASSERT: Cart total decreased to $" + String.format("%.2f", sumAfterRemoval) + " after removal.");
        
        inventoryPage = cartPage.continueShopping();
        System.out.println("\nSUB-FLOW: Starting addition verification.");
        
        assertEquals(inventoryPage.getCartItemCount(), 2, 
            "Cart badge count is correct (2 items) after continuing shopping.", 
            "Cart badge count is incorrect.");
            
        final String item4 = "Sauce Labs Fleece Jacket"; 
        inventoryPage.addProductToCartByName(item4);
        logInfo("Added new item: " + item4);
        System.out.println("STEP: Added new item: " + item4);
        
        assertEquals(inventoryPage.getCartItemCount(), 3, 
            "Cart count is 3 after adding a new item.", 
            "Cart count is NOT 3 after adding a new item.");
            
        cartPage = inventoryPage.goToCart();
        double sumAfterAddition = cartPage.getCartItemsPriceSum();
        logInfo("Cart Item Sum after addition: $" + String.format("%.2f", sumAfterAddition));
        
        assertTrue(sumAfterAddition > sumAfterRemoval, 
            "Cart total successfully increased after adding a new item.", 
            "Cart total did NOT increase after adding a new item.");
        System.out.println("ASSERT: Cart total increased to $" + String.format("%.2f", sumAfterAddition) + " after addition.");
        
        cartPage.removeItemByName(item1);
        cartPage.removeItemByName(item3);
        cartPage.removeItemByName(item4);
        
        assertEquals(cartPage.getCartItemNames().size(), 0, 
            "Cart was successfully emptied for cleanup.", 
            "Cart was NOT emptied for cleanup.");
        System.out.println("STEP: Cart cleaned up successfully.");
            
        cartPage.continueShopping();
        inventoryPage.logout();
        logInfo("Logout Successful. Test Flow 3 Finished.");
        System.out.println("STEP: Logged out successfully.");
        
        System.out.println("\n----------------------------------------------------------------------------------------------");
        System.out.println("<<< ENDING TEST: 3. Cart Total Update Verification on Item Modification <<<");
        System.out.println("----------------------------------------------------------------------------------------------\n");
    }
}