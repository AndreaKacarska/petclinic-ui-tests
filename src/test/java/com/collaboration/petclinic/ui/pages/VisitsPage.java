package com.collaboration.petclinic.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for Visits page
 * Encapsulates all locators and interactions related to visits management
 */
public class VisitsPage {
    private WebDriver driver;
    private static final int TIMEOUT_SECONDS = 10;

    private static final String ADD_VISIT_BUTTON_XPATH = "//dd[text()='%s']/following::button[text()='Add Visit']";
    private static final String DATE_INPUT_XPATH = "//input[@ng-reflect-name='date']";
    private static final String DESCRIPTION_INPUT_XPATH = "//input[@ng-reflect-name='description']";
    private static final String ADD_VISIT_SUBMIT_BUTTON_XPATH = "//button[text()='Add Visit']";
    private static final String UPDATE_VISIT_BUTTON_XPATH = "//button[text()='Update Visit']";
    private static final String VISIT_DESCRIPTION_XPATH = "//td[contains(text(),'%s')]";
    private static final String EDIT_VISIT_BUTTON_XPATH = "//td[contains(text(),'%s')]/following-sibling::td//button[text()='Edit Visit']";
    private static final String DELETE_VISIT_BUTTON_XPATH = "//td[contains(text(),'%s')]/following-sibling::td//button[text()='Delete Visit']";

    public VisitsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickAddVisitButton(String petName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//dd[text()='" + petName + "']")));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(String.format(ADD_VISIT_BUTTON_XPATH, petName))));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
    }

    public void enterDate(String date) {
        WebElement dateInput = driver.findElement(By.xpath(DATE_INPUT_XPATH));
        dateInput.clear();
        dateInput.sendKeys(date);
    }

    public void enterDescription(String description) {
        WebElement descInput = driver.findElement(By.xpath(DESCRIPTION_INPUT_XPATH));
        descInput.clear();
        descInput.sendKeys(description);
    }

    public void submitAddVisit() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(ADD_VISIT_SUBMIT_BUTTON_XPATH)));
        button.click();
    }

    public void submitUpdateVisit() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(UPDATE_VISIT_BUTTON_XPATH)));
        button.click();
    }

    public boolean visitExists(String description) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(VISIT_DESCRIPTION_XPATH, description))));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickEditVisitButton(String visitDescription) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(String.format(EDIT_VISIT_BUTTON_XPATH, visitDescription))));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
    }

    public void clickDeleteVisitButton(String visitDescription) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(String.format(DELETE_VISIT_BUTTON_XPATH, visitDescription))));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
    }

    public boolean waitForVisitDelete(String visitDescription) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(String.format(VISIT_DESCRIPTION_XPATH, visitDescription))));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAddVisitButtonEnabled() {
        try {
            return driver.findElement(By.xpath(ADD_VISIT_SUBMIT_BUTTON_XPATH)).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clearDateInput() {
        WebElement dateInput = driver.findElement(By.xpath(DATE_INPUT_XPATH));
        dateInput.clear();
    }

    public void clearDescriptionInput() {
        WebElement descInput = driver.findElement(By.xpath(DESCRIPTION_INPUT_XPATH));
        descInput.clear();
    }
}
