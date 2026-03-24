package com.collaboration.petclinic.ui.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class VisitsUITests {

    private static WebDriver driver;
    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:4200/petclinic/owners/3");
    }

    @AfterAll
    public static void teardown() {
        driver.quit();
    }

    @Test
    public void testAddVisit() {
        driver.findElement(By.xpath("//dd[text()='Rosy']/following::button[text()='Add Visit']")).click();
        driver.findElement(By.xpath("//input[@ng-reflect-name='date']")).sendKeys("2026-03-21");
        driver.findElement(By.xpath("//input[@ng-reflect-name='description']")).sendKeys("Routine checkup");
        driver.findElement(By.xpath("//button[text()='Add Visit']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement addedVisit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//td[contains(text(),'Routine checkup')]")
                ));
        assertNotNull(addedVisit);
    }

    @Test
    public void testAddInvalidVisit1() {
        driver.findElement(By.xpath("//dd[text()='Rosy']/following::button[text()='Add Visit']")).click();
        driver.findElement(By.xpath("//input[@ng-reflect-name='date']")).sendKeys("2026-03-21");

        WebElement addButton = driver.findElement(By.xpath("//button[text()='Add Visit']"));
        assertFalse(addButton.isEnabled());
    }

    @Test
    public void testAddInvalidVisit2() {
        driver.findElement(By.xpath("//dd[text()='Rosy']/following::button[text()='Add Visit']")).click();
        driver.findElement(By.xpath("//input[@ng-reflect-name='description']")).sendKeys("Routine checkup");
        WebElement addButton = driver.findElement(By.xpath("//button[text()='Add Visit']"));
        assertFalse(addButton.isEnabled());
    }

    @Test
    public void testEditVisit() {
        driver.findElement(By.xpath("//td[contains(text(),'Routine checkup')]/following-sibling::td/button[text()='Edit Visit']")).click();
        WebElement descField = driver.findElement(By.xpath("//input[@ng-reflect-name='description']"));
        descField.clear();
        descField.sendKeys("Updated checkup");
        driver.findElement(By.xpath("//button[text()='Update Visit']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement updatedVisit = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//td[contains(text(),'Updated checkup')]")
                ));
        assertNotNull(updatedVisit);
    }

    @Test
    public void testDeleteVisit() {
        driver.findElement(By.xpath("//td[text()='Updated checkup']/following-sibling::td/button[text()='Delete Visit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean isDeleted = wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//td[text()='Updated checkup']")
        ));

        assertTrue(isDeleted);
    }
}
