package com.collaboration.petclinic.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class VisitsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final int TIMEOUT_SECONDS = 5;

    public VisitsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
    }

    private static final String ADD_VISIT_BUTTON = "//dd[text()='%s']/following::button[text()='Add Visit']";
    private static final By DATE_INPUT = By.xpath("//input[@ng-reflect-name='date']");
    private static final By DESCRIPTION_INPUT = By.xpath("//input[@ng-reflect-name='description']");
    private static final By ADD_VISIT_SUBMIT = By.xpath("//button[text()='Add Visit']");
    private static final By UPDATE_VISIT_SUBMIT = By.xpath("//button[text()='Update Visit']");

    private static final String VISIT_DESC = "//td[contains(text(),'%s')]";
    private static final String EDIT_BTN = "//td[contains(text(),'%s')]/following-sibling::td//button[text()='Edit Visit']";
    private static final String DELETE_BTN = "//td[contains(text(),'%s')]/following-sibling::td//button[text()='Delete Visit']";

    private WebElement find(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void click(By locator) {
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    private void type(By locator, String text) {
        WebElement el = find(locator);
        el.clear();
        el.sendKeys(text);
    }


    public void clickAddVisitButton(String petName) {
        By locator = By.xpath(String.format(ADD_VISIT_BUTTON, petName));
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        click(locator);
    }

    public void enterDate(String date) {
        type(DATE_INPUT, date);
    }

    public void enterDescription(String description) {
        type(DESCRIPTION_INPUT, description);
    }

    public void submitAddVisit() {
        click(ADD_VISIT_SUBMIT);
    }

    public void submitUpdateVisit() {
        click(UPDATE_VISIT_SUBMIT);
    }

    public boolean visitExists(String description) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(String.format(VISIT_DESC, description))));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickEditVisitButton(String description) {
        click(By.xpath(String.format(EDIT_BTN, description)));
    }

    public void clickDeleteVisitButton(String description) {
        click(By.xpath(String.format(DELETE_BTN, description)));
    }

    public boolean waitForVisitDelete(String description) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath(String.format(VISIT_DESC, description))));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isAddVisitButtonEnabled() {
        try {
            return find(ADD_VISIT_SUBMIT).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clearDescriptionInput() {
        find(DESCRIPTION_INPUT).clear();
    }

    public void waitForAddVisitButton(String petName) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(String.format(ADD_VISIT_BUTTON, petName))));
    }
}
