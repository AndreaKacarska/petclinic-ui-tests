package com.collaboration.petclinic.ui.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        // Sustainability: One resilient interaction path reduces flaky retries and wasted reruns.
        setFieldValue(firstNameField, value);
    }

    /**
     * Fill lastName field and trigger Angular validation
     */
    public void fillLastName(String value) {
        setFieldValue(lastNameField, value);
    }

    /**
     * Fill address field and trigger Angular validation
     */
    public void fillAddress(String value) {
        setFieldValue(addressField, value);
    }

    /**
     * Fill city field and trigger Angular validation
     */
    public void fillCity(String value) {
        setFieldValue(cityField, value);
    }

    /**
     * Fill telephone field and trigger Angular validation
     */
    public void fillTelephone(String value) {
        setFieldValue(telephoneField, value);
    }

    /**
     * Click the submit button
     */
    public void submitForm() {
        safeClick(submitButton);
    }

    /**
     * Click the back button
     */
    public void clickBack() {
        safeClick(backButton);
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
        // Sustainability: Accurate redirect detection prevents false positives and expensive flaky retries.
        wait.until(driver -> {
            String currentUrl = driver.getCurrentUrl();
            return currentUrl.contains("/owners") && !currentUrl.contains("/owners/add");
        });
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

    private void setFieldValue(By locator, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", field);
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", field);

        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);

        // Sustainability: Event-based wait avoids fixed delays and keeps tests fast on responsive runs.
        wait.until(driver -> {
            String cssClasses = field.getDomProperty("className");
            return cssClasses != null && (cssClasses.contains("ng-dirty") || cssClasses.contains("ng-touched"));
        });
    }

    private void safeClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);

        try {
            element.click();
        } catch (Exception ignored) {
            // Sustainability: JS fallback avoids reruns caused by intermittent footer/image overlap.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
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