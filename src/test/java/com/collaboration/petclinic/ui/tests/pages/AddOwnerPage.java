package com.collaboration.petclinic.ui.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

/**
 * Page Object Model for the Add Owner form page
 * Fixed to properly handle Angular form validation by triggering field dirty state
 */
public class AddOwnerPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By firstNameField = By.id("firstName");
    private final By lastNameField = By.id("lastName");
    private final By addressField = By.id("address");
    private final By cityField = By.id("city");
    private final By telephoneField = By.id("telephone");
    private final By submitButton = By.xpath("//button[@type='submit']");
    private final By backButton = By.xpath("//button[@type='button' and contains(text(), 'Back')]");

    public AddOwnerPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Navigate to the Add Owner form page
     */
    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/owners/add");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("firstName")));
    }

    /**
     * Fill in all form fields with valid data
     */
    public void fillFormWithValidData(String firstName, String lastName, String address, String city, String telephone) {
        fillFirstName(firstName);
        fillLastName(lastName);
        fillAddress(address);
        fillCity(city);
        fillTelephone(telephone);
    }

    /**
     * Fill firstName field and trigger Angular validation
     * Click -> Clear -> Type -> Tab (blur) to mark field as dirty and validate
     */
    public void fillFirstName(String value) {
        WebElement field = driver.findElement(firstNameField);
        field.click();  // Make field active
        field.clear();   // Clear existing value
        field.sendKeys(value);  // Enter new value
        field.sendKeys(Keys.TAB);  // Blur field to trigger validation
        waitForValidationToProcess();
    }

    /**
     * Fill lastName field and trigger Angular validation
     */
    public void fillLastName(String value) {
        WebElement field = driver.findElement(lastNameField);
        field.click();
        field.clear();
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
        waitForValidationToProcess();
    }

    /**
     * Fill address field and trigger Angular validation
     */
    public void fillAddress(String value) {
        WebElement field = driver.findElement(addressField);
        field.click();
        field.clear();
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
        waitForValidationToProcess();
    }

    /**
     * Fill city field and trigger Angular validation
     */
    public void fillCity(String value) {
        WebElement field = driver.findElement(cityField);
        field.click();
        field.clear();
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
        waitForValidationToProcess();
    }

    /**
     * Fill telephone field and trigger Angular validation
     */
    public void fillTelephone(String value) {
        WebElement field = driver.findElement(telephoneField);
        field.click();
        field.clear();
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
        waitForValidationToProcess();
    }

    /**
     * Click the submit button
     */
    public void submitForm() {
        driver.findElement(submitButton).click();
    }

    /**
     * Click the back button
     */
    public void clickBack() {
        driver.findElement(backButton).click();
    }

    /**
     * Check if submit button is enabled
     */
    public boolean isSubmitButtonEnabled() {
        return driver.findElement(submitButton).isEnabled();
    }

    /**
     * Check if error message is displayed
     * Properly handles Angular's ngIf validation display
     */
    public boolean isErrorMessageDisplayed(String errorMessage) {
        try {
            // Look for help-block span containing the error message
            WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[@class='help-block' and contains(text(), '" + errorMessage + "')]")));
            return error.isDisplayed();
        } catch (Exception e) {
            // If exact message not found, try partial match
            try {
                WebElement error = driver.findElement(
                        By.xpath("//span[@class='help-block' and contains(., '" + errorMessage + "')]"));
                return error.isDisplayed();
            } catch (Exception e2) {
                return false;
            }
        }
    }

    /**
     * Check if error message is NOT displayed (removed after fix)
     */
    public boolean isErrorMessageNotDisplayed(String errorMessage) {
        try {
            WebElement error = driver.findElement(
                    By.xpath("//span[@class='help-block' and contains(text(), '" + errorMessage + "')]"));
            // If element exists, check if it's actually displayed
            return !error.isDisplayed();
        } catch (Exception e) {
            // Element not found = it's not displayed (good!)
            return true;
        }
    }

    /**
     * Get current page URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Wait for page to redirect to owners list
     */
    public void waitForOwnersListRedirect() {
        wait.until(ExpectedConditions.urlContains("/owners"));
    }

    /**
     * Get the first name field locator
     */
    public By getFirstNameFieldLocator() {
        return firstNameField;
    }

    /**
     * Get the last name field locator
     */
    public By getLastNameFieldLocator() {
        return lastNameField;
    }

    /**
     * Get the address field locator
     */
    public By getAddressFieldLocator() {
        return addressField;
    }

    /**
     * Get the city field locator
     */
    public By getCityFieldLocator() {
        return cityField;
    }

    /**
     * Get the telephone field locator
     */
    public By getTelephoneFieldLocator() {
        return telephoneField;
    }

    /**
     * Check if page title is "New Owner"
     */
    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[contains(text(), 'New Owner')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for Angular validation to process
     * Angular needs a small delay to update the DOM after field changes
     */
    private void waitForValidationToProcess() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Debug method: Get all visible error messages
     * Useful for troubleshooting test failures
     */
    public String getVisibleErrors() {
        try {
            java.util.List<WebElement> errors = driver.findElements(
                    By.xpath("//span[@class='help-block'][normalize-space() != '']"));
            StringBuilder errorText = new StringBuilder();
            for (WebElement error : errors) {
                if (error.isDisplayed()) {
                    errorText.append(error.getText()).append("; ");
                }
            }
            return errorText.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
