package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class HomeTest extends BaseTest {

  private void navigateToAddOwner() throws InterruptedException {
    driver.get(BASE_URL + "/owners/add");
    Thread.sleep(2000);
  }

  private void fillValidForm() {
    driver.findElement(By.id("firstName")).sendKeys("Andrej");
    driver.findElement(By.id("lastName")).sendKeys("Marinov");
    driver.findElement(By.id("address")).sendKeys("Main Street 1");
    driver.findElement(By.id("city")).sendKeys("Skopje");
    driver.findElement(By.id("telephone")).sendKeys("1234567890");
  }

  // ============================
  // TS-01
  // ============================
  @Test
  void TS01_createOwnerSuccessfully() throws InterruptedException {
    navigateToAddOwner();
    fillValidForm();

    driver.findElement(By.xpath("//button[@type='submit']")).click();
    Thread.sleep(2000);

    assertTrue(driver.getCurrentUrl().contains("owners"));
  }

  // ============================
  // TS-02
  // ============================
  @Test
  void TS02_redirectAfterCreation() throws InterruptedException {
    navigateToAddOwner();
    fillValidForm();

    driver.findElement(By.xpath("//button[@type='submit']")).click();
    Thread.sleep(2000);

    assertTrue(driver.getCurrentUrl().contains("owners"));
  }

  // ============================
  // TS-03
  // ============================
  @Test
  void TS03_verifyCreatedOwnerDataDisplayed() throws InterruptedException {
    navigateToAddOwner();
    fillValidForm();

    driver.findElement(By.xpath("//button[@type='submit']")).click();
    Thread.sleep(2000);

    String pageSource = driver.getPageSource();

    assertTrue(pageSource.contains("Andrej"));
    assertTrue(pageSource.contains("Marinov"));
    assertTrue(pageSource.contains("Skopje"));
  }

  // ============================
  // TS-04
  // ============================
  @Test
  void TS04_createMultipleOwners() throws InterruptedException {
    for (int i = 0; i < 3; i++) {
      navigateToAddOwner();

      driver.findElement(By.id("firstName")).sendKeys("Andrej");
      driver.findElement(By.id("lastName")).sendKeys("Marinov");
      driver.findElement(By.id("address")).sendKeys("Ulica " + i);
      driver.findElement(By.id("city")).sendKeys("Skopje" + i);
      driver.findElement(By.id("telephone")).sendKeys("123456789" + i);

      driver.findElement(By.xpath("//button[@type='submit']")).click();
      Thread.sleep(2000);

      assertTrue(driver.getPageSource().contains("123456789" + i));
    }
  }

  // ============================
  // TS-05
  // ============================
  @Test
  void TS05_blockSubmitWhenEmpty() throws InterruptedException {
    navigateToAddOwner();

    WebElement submitBtn = driver.findElement(By.xpath("//button[@type='submit']"));
    assertFalse(submitBtn.isEnabled());
  }

  // ============================
  // TS-06
  // ============================
  @Test
  void TS06_submitEnabledOnlyWhenValid() throws InterruptedException {
    navigateToAddOwner();

    WebElement submitBtn = driver.findElement(By.xpath("//button[@type='submit']"));
    assertFalse(submitBtn.isEnabled());

    fillValidForm();
    Thread.sleep(1000);

    assertTrue(submitBtn.isEnabled());
  }

  // ============================
  // TS-07
  // ============================
  @Test
  void TS07_invalidDataPreventsSubmit() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("firstName")).sendKeys("123"); // invalid
    fillValidForm();

    WebElement submitBtn = driver.findElement(By.xpath("//button[@type='submit']"));
            assertFalse(submitBtn.isEnabled());
  }

  // ============================
  // TS-08
  // ============================
  @Test
  void TS08_inputFormatValidation() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("telephone")).sendKeys("abc");
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("Phone number only accept digits"));
  }

  // ============================
  // TS-09
  // ============================
  @Test
  void TS09_invalidInputShowsErrors() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("firstName")).click();
    driver.findElement(By.id("firstName")).sendKeys("1");
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("must consist of letters"));
  }

  // ============================
  // TS-10
  // ============================
  @Test
  void TS10_dynamicValidation() throws InterruptedException {
    navigateToAddOwner();

    WebElement firstName = driver.findElement(By.id("firstName"));
    firstName.sendKeys("1");
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("letters only"));

    firstName.clear();
    firstName.sendKeys("Andrej");
    Thread.sleep(1000);

    assertFalse(driver.getPageSource().contains("letters only"));
  }

  // ============================
  // TS-11
  // ============================
  @Test
  void TS11_nameFieldValidation() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("firstName")).sendKeys("@@@");
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("letters only"));
  }

  // ============================
  // TS-12
  // ============================
  @Test
  void TS12_telephoneNumericOnly() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("telephone")).sendKeys("abc");
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("only accept digits"));
  }

  // ============================
  // TS-13
  // ============================
  @Test
  void TS13_telephoneLengthValidation() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("telephone")).sendKeys("123456789012345678901");
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("cannot be more than 10"));
  }

  // ============================
  // TS-14
  // ============================
  @Test
  void TS14_cityValidation() throws InterruptedException {
    navigateToAddOwner();

    WebElement field = driver.findElement(By.id("city"));
    field.sendKeys(" ");
    field.clear();

    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("City is required"));
  }

  // ============================
  // TS-15
  // ============================
  @Test
  void TS15_trimSpaces() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("firstName")).sendKeys("  Andrej  ");
    fillValidForm();

    driver.findElement(By.xpath("//button[@type='submit']")).click();
    Thread.sleep(2000);

    assertTrue(driver.getPageSource().contains("Andrej"));
  }

  // ============================
  // TS-16
  // ============================
  @Test
  void TS16_specialCharactersAddress() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("address")).sendKeys("Street #12 @ Skopje");
    Thread.sleep(1000);

    assertTrue(driver.findElement(By.id("address")).isDisplayed());
  }

  // ============================
  // TS-17
  // ============================
  @Test
  void TS17_largeTextInput() throws InterruptedException {
    navigateToAddOwner();

    String longText = "a".repeat(300);
    driver.findElement(By.id("address")).sendKeys(longText);
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("at most 255"));
  }

  // ============================
  // TS-18
  // ============================
  @Test
  void TS18_errorMessagesVisible() throws InterruptedException {
    navigateToAddOwner();

    WebElement field = driver.findElement(By.id("firstName"));
    field.sendKeys(" ");
    field.clear();
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("letters only"));
  }

  // ============================
  // TS-19
  // ============================
  @Test
  void TS19_errorMessagePlacement() throws InterruptedException {
    navigateToAddOwner();

    WebElement field = driver.findElement(By.id("firstName"));
    field.sendKeys("Andrej");
    field.clear();

    Thread.sleep(4000);

    WebElement error = driver.findElement(By.xpath("//span[contains(text(),'First name is required')]"));
    assertTrue(error.isDisplayed());
  }

  // ============================
  // TS-20
  // ============================
  @Test
  void TS20_errorClearedAfterFix() throws InterruptedException {
    navigateToAddOwner();

    WebElement field = driver.findElement(By.id("firstName"));
    field.sendKeys("1");
    Thread.sleep(1000);

    field.clear();
    field.sendKeys("Andrej");
    Thread.sleep(1000);

    assertFalse(driver.getPageSource().contains("letters only"));
  }

  // ============================
  // TS-21
  // ============================
  @Test
  void TS21_noSilentFailures() throws InterruptedException {
    navigateToAddOwner();

    driver.findElement(By.id("telephone")).sendKeys("abc");
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains("Phone number"));
  }
}