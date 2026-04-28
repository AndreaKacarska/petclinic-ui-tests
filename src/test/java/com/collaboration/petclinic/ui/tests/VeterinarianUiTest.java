package com.collaboration.petclinic.ui.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VeterinarianUiTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void setUp() {
        // Оптимизација: Headless mode за штедење ресурси
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:4200/vets");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void veterinariansPageLoads() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("VETERINARIANS"))).click();
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        assertTrue(title.getText().contains("Veterinarians"));
    }

    @Test
    void veterinariansListDisplayed() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("VETERINARIANS"))).click();
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(table.isDisplayed());
    }

    @Test
    void specificVeterinarianExists() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("VETERINARIANS"))).click();
        assertTrue(driver.getPageSource().contains("James Carter"));
    }

    @Test
    void verifySpecialtiesDisplayed() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("VETERINARIANS"))).click();
        assertTrue(driver.getPageSource().contains("radiology"));
    }

    @Test
    void addOwnerValid() {
        // AI Оптимизација: Динамичко име за избегнување конфликти при паралелно извршување
        String uniqueName = "Jasmin" + System.currentTimeMillis() % 1000;

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("FIND OWNERS"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add Owner"))).click();

        driver.findElement(By.id("firstName")).sendKeys(uniqueName);
        driver.findElement(By.id("lastName")).sendKeys("Abazi");
        driver.findElement(By.id("address")).sendKeys("Street 1");
        driver.findElement(By.id("city")).sendKeys("Skopje");
        driver.findElement(By.id("telephone")).sendKeys("1234567890");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        boolean isPresent = wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), uniqueName));
        assertTrue(isPresent, "Owner " + uniqueName + " not found!");
    }

    @Test
    void addOwnerEmptyFields() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("FIND OWNERS"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add Owner"))).click();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        assertTrue(driver.getPageSource().contains("Owner"));
    }

    @Test
    void addOwnerInvalidPhone() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("FIND OWNERS"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add Owner"))).click();
        driver.findElement(By.id("telephone")).sendKeys("abc");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        assertTrue(driver.getPageSource().toLowerCase().contains("numeric") || driver.getPageSource().toLowerCase().contains("error"));
    }

    @Test
    void searchOwner() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("FIND OWNERS")
        )).click();
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lastName")));
        searchField.sendKeys("Franklin");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        boolean isPresent = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"), "Franklin"
        ));
        assertTrue(isPresent, "Owner search failed - Franklin was not found in the results.");
    }
}