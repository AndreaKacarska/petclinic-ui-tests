package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AddOwnerTest extends BaseTest {

  private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(8);

  private static final By FIRST_NAME = By.id("firstName");
  private static final By LAST_NAME = By.id("lastName");
  private static final By ADDRESS = By.id("address");
  private static final By CITY = By.id("city");
  private static final By TELEPHONE = By.id("telephone");
  private static final By SUBMIT_BUTTON = By.xpath("//button[@type='submit']");
  private static final By REQUIRED_FIRST_NAME_ERROR =
      By.xpath("//span[contains(text(),'First name is required')]");

  private static final String OWNER_DETAILS_URL_FRAGMENT = "owners";
  private static final String MSG_PHONE_DIGITS_ONLY = "only accept digits";
  private static final String MSG_PHONE_MAX_LENGTH = "cannot be more than 10";
  private static final String MSG_LETTERS_ONLY = "letters only";
  private static final String MSG_MUST_BE_LETTERS = "must consist of letters";
  private static final String MSG_CITY_REQUIRED = "City is required";
  private static final String MSG_ADDRESS_MAX_LENGTH = "at most 255";

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

  private void submitFormAndWaitForOwnersPage() {
    submitButton().click();
    driverWait().until(ExpectedConditions.urlContains(OWNER_DETAILS_URL_FRAGMENT));
  }

  private void waitForPageSourceContains(String expectedText) {
    driverWait().until(driver -> driver.getPageSource().contains(expectedText));
  }

  private void waitForPageSourceNotContains(String text) {
    driverWait().until(driver -> !driver.getPageSource().contains(text));
  }

  private void assertPageContains(String text) {
    waitForPageSourceContains(text);
    assertTrue(driver.getPageSource().contains(text), "Expected page to contain: " + text);
  }

  private void assertPageNotContains(String text) {
    waitForPageSourceNotContains(text);
    assertFalse(driver.getPageSource().contains(text), "Expected page not to contain: " + text);
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

    assertTrue(driver.getCurrentUrl().contains(OWNER_DETAILS_URL_FRAGMENT));
  }

  // ============================
  // TS-02
  // ============================
  @Test
  void TS02_redirectAfterCreation() {
    navigateToAddOwner();
    fillValidForm();

    submitFormAndWaitForOwnersPage();

    assertTrue(driver.getCurrentUrl().contains(OWNER_DETAILS_URL_FRAGMENT));
  }

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
  // TS-04
  // ============================
  @Test
  void TS04_createMultipleOwners() {
    for (int i = 0; i < 3; i++) {
      navigateToAddOwner();

      fillForm("Andrej", "Marinov", "Ulica " + i, "Skopje" + i, "123456789" + i);

      submitFormAndWaitForOwnersPage();

      assertPageContains("123456789" + i);
    }
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

    assertPageContains(MSG_PHONE_DIGITS_ONLY);
  }

  // ============================
  // TS-09
  // ============================
  @Test
  void TS09_invalidInputShowsErrors() {
    navigateToAddOwner();

    element(FIRST_NAME).click();
    element(FIRST_NAME).sendKeys("1");

    assertPageContains(MSG_MUST_BE_LETTERS);
  }

  // ============================
  // TS-10
  // ============================
  @Test
  void TS10_dynamicValidation() {
    navigateToAddOwner();

    WebElement firstName = element(FIRST_NAME);
    firstName.sendKeys("1");

    assertPageContains(MSG_LETTERS_ONLY);

    firstName.clear();
    firstName.sendKeys("Andrej");

    assertPageNotContains(MSG_LETTERS_ONLY);
  }

  // ============================
  // TS-11
  // ============================
  @Test
  void TS11_nameFieldValidation() {
    navigateToAddOwner();

    element(FIRST_NAME).sendKeys("@@@");

    assertPageContains(MSG_LETTERS_ONLY);
  }

  // ============================
  // TS-12
  // ============================
  @Test
  void TS12_telephoneNumericOnly() {
    navigateToAddOwner();

    element(TELEPHONE).sendKeys("abc");

    assertPageContains(MSG_PHONE_DIGITS_ONLY);
  }

  // ============================
  // TS-13
  // ============================
  @Test
  void TS13_telephoneLengthValidation() {
    navigateToAddOwner();

    element(TELEPHONE).sendKeys("123456789012345678901");

    assertPageContains(MSG_PHONE_MAX_LENGTH);
  }

  // ============================
  // TS-14
  // ============================
  @Test
  void TS14_cityValidation() {
    navigateToAddOwner();

    WebElement field = element(CITY);
    field.sendKeys(" ");
    field.clear();

    assertPageContains(MSG_CITY_REQUIRED);
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

  // ============================
  // TS-16
  // ============================
  @Test
  void TS16_specialCharactersAddress() {
    navigateToAddOwner();

    WebElement addressField = element(ADDRESS);
    addressField.sendKeys("Street #12 @ Skopje");

    assertTrue(addressField.isDisplayed());
  }

  // ============================
  // TS-17
  // ============================
  @Test
  void TS17_largeTextInput() {
    navigateToAddOwner();

    String longText = "a".repeat(300);
    element(ADDRESS).sendKeys(longText);

    assertPageContains(MSG_ADDRESS_MAX_LENGTH);
  }

  // ============================
  // TS-18
  // ============================
  @Test
  void TS18_errorMessagesVisible() {
    navigateToAddOwner();

    WebElement field = element(FIRST_NAME);
    field.sendKeys(" ");
    field.clear();

    assertPageContains(MSG_LETTERS_ONLY);
  }

  // ============================
  // TS-19
  // ============================
  @Test
  void TS19_errorMessagePlacement() {
    navigateToAddOwner();

    WebElement field = element(FIRST_NAME);
    field.sendKeys("Andrej");
    field.clear();

    WebElement error = driverWait().until(ExpectedConditions.visibilityOfElementLocated(REQUIRED_FIRST_NAME_ERROR));
    assertTrue(error.isDisplayed());
  }

  // ============================
  // TS-20
  // ============================
  @Test
  void TS20_errorClearedAfterFix() {
    navigateToAddOwner();

    WebElement field = element(FIRST_NAME);
    field.sendKeys("1");
    assertPageContains(MSG_LETTERS_ONLY);

    field.clear();
    field.sendKeys("Andrej");

    assertPageNotContains(MSG_LETTERS_ONLY);
  }

  // ============================
  // TS-21
  // ============================
  @Test
  void TS21_noSilentFailures() {
    navigateToAddOwner();

    element(TELEPHONE).sendKeys("abc");

    assertPageContains("Phone number");
  }
}