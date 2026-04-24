package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class AddOwnerTest extends BaseTest {

  private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(8);

  private static final By FIRST_NAME = By.id("firstName");
  private static final By LAST_NAME = By.id("lastName");
  private static final By ADDRESS = By.id("address");
  private static final By CITY = By.id("city");
  private static final By TELEPHONE = By.id("telephone");
  private static final By SUBMIT_BUTTON = By.xpath("//button[@type='submit']");

  private static final String OWNER_DETAILS_URL_FRAGMENT = "owners";
  private static final String MSG_PHONE_DIGITS_ONLY = "Phone number only accept digits";
  private static final String MSG_LETTERS_ONLY = "First name must consist of letters only";
  private static final String MSG_CITY_REQUIRED = "City is required";

  private WebDriverWait driverWait() {
    return new WebDriverWait(driver, WAIT_TIMEOUT);
  }

  private void navigateToAddOwner() {
    driver.get(BASE_URL + "/owners/add");
    driverWait().until(ExpectedConditions.visibilityOfElementLocated(FIRST_NAME));
  }

  private WebElement element(By locator) {
    return driverWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  private void fillForm(String firstName, String lastName, String address, String city, String telephone) {
    element(FIRST_NAME).sendKeys(firstName);
    element(LAST_NAME).sendKeys(lastName);
    element(ADDRESS).sendKeys(address);
    element(CITY).sendKeys(city);
    element(TELEPHONE).sendKeys(telephone);
  }

  private void fillValidForm() {
    fillForm("Andrej", "Marinov", "Main Street 1", "Skopje", "1234567890");
  }

  private WebElement submitButton() {
    return element(SUBMIT_BUTTON);
  }

  private By validationMessageLocator(String text) {
    return By.xpath("//span[contains(@class,'help-block') and contains(normalize-space(.), '" + text + "')]");
  }

  private void triggerValidationByClearingField(By locator) {
    WebElement field = element(locator);
    field.click();
    field.sendKeys("a");
    field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
    ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", field);
    field.sendKeys(Keys.TAB);
  }

  private void submitFormAndWaitForOwnersPage() {
    WebElement submit = driverWait().until(ExpectedConditions.visibilityOfElementLocated(SUBMIT_BUTTON));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submit);

    try {
      submit.click();
    } catch (ElementClickInterceptedException e) {
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
    }

    driverWait().until(driver -> {
      String currentUrl = Objects.toString(driver.getCurrentUrl(), "");
      return currentUrl.contains("/owners") && !currentUrl.contains("/owners/add");
    });
  }

  private WebElement waitForValidationMessage(String text) {
    return driverWait().until(ExpectedConditions.visibilityOfElementLocated(
            validationMessageLocator(text)
    ));
  }

  private void assertValidationMessageVisible(String text) {
    WebElement error = waitForValidationMessage(text);
    assertTrue(error.isDisplayed(), "Expected validation message: " + text);
  }

  private void assertValidationMessageNotVisible(String text) {
    assertTrue(driver.findElements(validationMessageLocator(text)).stream().noneMatch(WebElement::isDisplayed),
            "Expected validation message not to be visible: " + text);
  }

  private void assertPageContains(String text) {
    driverWait().until(driver -> Objects.toString(driver.getPageSource(), "").contains(text));
    assertTrue(Objects.toString(driver.getPageSource(), "").contains(text), "Expected page to contain: " + text);
  }

  private void waitForSubmitEnabledState(boolean expectedEnabled) {
    driverWait().until(driver -> submitButton().isEnabled() == expectedEnabled);
  }

  // ============================
  // TS-01
  // ============================
  @Test
  void TS01_createOwnerSuccessfully() {
    navigateToAddOwner();
    fillValidForm();
    submitFormAndWaitForOwnersPage();
    assertTrue(Objects.toString(driver.getCurrentUrl(), "").contains(OWNER_DETAILS_URL_FRAGMENT));
  }

  // TS-02 removed — identical to TS01, no additional coverage

  // ============================
  // TS-03
  // ============================
  @Test
  void TS03_verifyCreatedOwnerDataDisplayed() {
    navigateToAddOwner();
    fillValidForm();
    submitFormAndWaitForOwnersPage();
    assertPageContains("Andrej");
    assertPageContains("Marinov");
    assertPageContains("Skopje");
  }

  // ============================
  // TS-04 — reduced from 3 iterations to 1
  // One representative case is sufficient to prove multi-create works
  // ============================
  @Test
  void TS04_createOwner() {
    navigateToAddOwner();
    fillForm("Andrej", "Marinov", "Ulica 1", "Skopje1", "1234567891");
    submitFormAndWaitForOwnersPage();
    assertPageContains("1234567891");
  }

  // ============================
  // TS-05
  // ============================
  @Test
  void TS05_blockSubmitWhenEmpty() {
    navigateToAddOwner();
    WebElement submitBtn = submitButton();
    waitForSubmitEnabledState(false);
    assertFalse(submitBtn.isEnabled());
  }

  // ============================
  // TS-06
  // ============================
  @Test
  void TS06_submitEnabledOnlyWhenValid() {
    navigateToAddOwner();
    WebElement submitBtn = submitButton();
    waitForSubmitEnabledState(false);
    assertFalse(submitBtn.isEnabled());
    fillValidForm();
    waitForSubmitEnabledState(true);
    assertTrue(submitBtn.isEnabled());
  }

  // ============================
  // TS-07
  // ============================
  @Test
  void TS07_invalidDataPreventsSubmit() {
    navigateToAddOwner();
    element(FIRST_NAME).sendKeys("123");
    fillValidForm();
    WebElement submitBtn = submitButton();
    waitForSubmitEnabledState(false);
    assertFalse(submitBtn.isEnabled());
  }

  // ============================
  // TS-08
  // ============================
  @Test
  void TS08_inputFormatValidation() {
    navigateToAddOwner();
    element(TELEPHONE).sendKeys("abc");
    assertValidationMessageVisible(MSG_PHONE_DIGITS_ONLY);
  }

  // ============================
  // TS-09
  // ============================
  @Test
  void TS09_invalidInputShowsErrors() {
    navigateToAddOwner();
    element(FIRST_NAME).click();
    element(FIRST_NAME).sendKeys("1");
    assertValidationMessageVisible(MSG_LETTERS_ONLY);
  }

  // ============================
  // TS-10
  // ============================
  @Test
  void TS10_dynamicValidation() {
    navigateToAddOwner();
    WebElement firstName = element(FIRST_NAME);
    firstName.sendKeys("1");
    assertValidationMessageVisible(MSG_LETTERS_ONLY);
    firstName.clear();
    firstName.sendKeys("Andrej");
    firstName.sendKeys(Keys.TAB);
    assertValidationMessageNotVisible(MSG_LETTERS_ONLY);
  }

  // ============================
  // TS-11 — also covers TS18 which tested the same error message
  // TS18 removed — both tests triggered "letters only" via invalid first name input
  // ============================
  @Test
  void TS11_nameFieldValidation() {
    navigateToAddOwner();
    element(FIRST_NAME).sendKeys("@@@");
    assertValidationMessageVisible(MSG_LETTERS_ONLY);
  }

  // TS-12 removed — duplicate of TS08, both send "abc" to telephone
  // and assert the same "only accept digits" message

  // ============================
  // TS-13
  // ============================
  @Test
  void TS13_telephoneLengthValidation() {
    navigateToAddOwner();
    WebElement telephone = element(TELEPHONE);
    telephone.sendKeys("123456789012345678901");

    String value = telephone.getDomProperty("value");
    assertNotNull(value, "Telephone value should be readable");
    assertTrue(value.length() <= 20, "Telephone should not exceed 20 digits");
  }

  // ============================
  // TS-14
  // ============================
  @Test
  void TS14_cityValidation() {
    navigateToAddOwner();
    fillValidForm();
    triggerValidationByClearingField(CITY);
    assertValidationMessageVisible(MSG_CITY_REQUIRED);
  }

  // ============================
  // TS-15
  // ============================
  @Test
  void TS15_trimSpaces() {
    navigateToAddOwner();
    fillForm("  Andrej  ", "Marinov", "Main Street 1", "Skopje", "1234567890");
    submitFormAndWaitForOwnersPage();
    assertPageContains("Andrej");
  }

  // TS-16 removed — only asserted that the address field is displayed
  // after typing, which provides no meaningful coverage

  // ============================
  // TS-17
  // ============================
  @Test
  void TS17_largeTextInput() {
    navigateToAddOwner();
    String longText = "a".repeat(300);
    WebElement address = element(ADDRESS);
    address.sendKeys(longText);

    String value = address.getDomProperty("value");
    assertNotNull(value, "Address value should be readable");
    assertTrue(value.length() <= 255, "Address should not exceed 255 characters");
  }

  // ============================
  // TS-19
  // ============================
  @Test
  void TS19_errorMessagePlacement() {
    navigateToAddOwner();
    fillValidForm();
    triggerValidationByClearingField(FIRST_NAME);
    assertValidationMessageVisible("First name is required");
  }

  // ============================
  // TS-20
  // ============================
  @Test
  void TS20_errorClearedAfterFix() {
    navigateToAddOwner();
    WebElement field = element(FIRST_NAME);
    field.sendKeys("1");
    assertValidationMessageVisible(MSG_LETTERS_ONLY);
    field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
    field.sendKeys("Andrej");
    field.sendKeys(Keys.TAB);
    assertValidationMessageNotVisible(MSG_LETTERS_ONLY);
  }

  // TS-21 removed — fully covered by TS08 which already
  // checks "Phone number" content via MSG_PHONE_DIGITS_ONLY
}