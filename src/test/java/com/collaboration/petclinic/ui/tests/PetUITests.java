package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDate;

/**
 * UI TESTS — Add Pet for Specific Owner
 * Student 3
 * Prerequisites:
 * - Backend running on localhost:9966
 * - Frontend running on localhost:4200
 * - Owner with ID 1 (George Franklin) exists
 *
 * KNOWN BUGS IDENTIFIED DURING TESTING:
 *
 * BUG002: No error message shown when future birth date is entered
 *   - Steps: Navigate to Add Pet form, enter future date, click Save
 *   - Expected: Error message "Birth date cannot be in the future"
 *   - Actual: Form silently refuses to submit, no feedback to user
 *   - Test: TC003_BUG (intentionally failing to document this bug)
 *
 * BUG003: Required fields show no validation messages on empty submit
 *   - Steps: Navigate to Add Pet form, leave fields empty, click Save
 *   - Expected: Each field shows "This field is required"
 *   - Actual: Nothing happens, no messages shown
 *
 * BUG004: Pet name accepts special characters and numbers
 *   - Needs clarification from team whether this is intended behavior
 *
 * BUG005: Duplicate pet names allowed for same owner
 *   - Needs clarification from team whether this is intended behavior
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetUITests extends BaseTest {

    private WebDriverWait wait;

    @BeforeEach
    void localSetup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @Order(1)
    @DisplayName("TC001 - Add a new pet successfully")
    void testAddPetSuccessfully() {
        navigateToOwner1();
        clickAddNewPet();
        fillForm("BuddyUI", "2022/05/15", "dog");
        submitForm();

        sleep(2000);
        navigateToOwner1();
        Assertions.assertTrue(
                driver.getPageSource().contains("BuddyUI"),
                "Newly created pet should appear on owner's page"
        );
    }

    @Test
    @Order(2)
    @DisplayName("TC002 - Add a pet with today's birth date")
    void testAddPetWithTodayDate() {
        String today = LocalDate.now().toString().replace("-", "/");
        navigateToOwner1();
        clickAddNewPet();
        fillForm("RexUI", today, "cat");
        submitForm();

        sleep(2000);
        navigateToOwner1();
        Assertions.assertTrue(
                driver.getPageSource().contains("RexUI"),
                "Pet with today's birth date should be created successfully"
        );
    }

    // This test documents that the system currently ALLOWS spec. char
    // Linked to BUG004 - needs clarification from team
    @Test
    @Order(3)
    @DisplayName("TC005 - Add a pet with special characters in name")
    void testAddPetSpecialChars() {
        navigateToOwner1();
        clickAddNewPet();
        fillForm("#!@123", "2024/01/15", "bird");
        submitForm();

        sleep(3000);
        navigateToOwner1();
        sleep(2000);
        Assertions.assertTrue(
                driver.getPageSource().contains("123"),
                "System currently allows special characters - needs clarification BUG004"
        );
    }
    // This test documents that duplicates ARE currently allowed
    // Linked to BUG005 - needs clarification from team
    @Test
    @Order(4)
    @DisplayName("TC008 - Add duplicate pet name for same owner")
    void testAddDuplicatePetName() {
        navigateToOwner1();
        clickAddNewPet();
        fillForm("BellaUI", "2024/01/15", "cat");
        submitForm();

        sleep(2000);
        navigateToOwner1();
        Assertions.assertTrue(
                driver.getPageSource().contains("BellaUI"),
                "System currently allows duplicate pet names for same owner - needs clarification"
        );
    }

    @Test
    @Order(5)
    @DisplayName("TC004/TC015 - Validate empty or whitespace name shows error")
    void testEmptyNameValidation() {
        navigateToOwner1();
        clickAddNewPet();

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.sendKeys("   ");

        driver.findElement(By.name("birthDate")).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("help-block")));
        Assertions.assertTrue(
                error.isDisplayed(),
                "Validation error should appear for whitespace name"
        );
        Assertions.assertFalse(
                error.getText().isEmpty(),
                "Validation message should not be empty"
        );
    }

    @Test
    @Order(6)
    @DisplayName("TC006 - Submit button disabled when birth date is missing")
    void testMissingBirthDate() {
        navigateToOwner1();
        clickAddNewPet();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")))
                .sendKeys("NoDatePet");

        WebElement submitBtn = driver.findElement(
                By.xpath("//button[@type='submit']"));
        Assertions.assertFalse(
                submitBtn.isEnabled(),
                "Submit button should be disabled when birth date is missing"
        );
    }

    @Test
    @Order(7)
    @DisplayName("TC011 - View existing pet on owner page")
    void testViewExistingPet() {
        navigateToOwner1();

        sleep(3000);
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(
                pageSource.contains("Franklin") ||
                        pageSource.contains("Pets and Visits") ||
                        pageSource.contains("2010"),
                "Owner page should show pet information"
        );
    }

    @Test
    @Order(8)
    @DisplayName("TC013 - Edit an existing pet's name")
        // NOTE: This test renames Leo to LeoUpdated
        // TC011 must account for this change
    void testEditPet() {
        navigateToOwner1();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Edit Pet')]"))).click();

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.clear();
        nameInput.sendKeys("LeoUpdated");
        submitForm();

        wait.until(ExpectedConditions.urlContains("/owners/1"));
        Assertions.assertTrue(
                driver.getPageSource().contains("LeoUpdated"),
                "Pet name should be updated to LeoUpdated"
        );
    }

    @Test
    @Order(9)
    @DisplayName("TC014 - Delete a pet")
    void testDeletePet() {
        navigateToOwner1();
        sleep(2000);

        WebElement deleteBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("(//button[contains(text(),'Delete Pet')])[1]")));

        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", deleteBtn);
        sleep(500);

        int petCountBefore = driver.findElements(
                By.xpath("//button[contains(text(),'Delete Pet')]")).size();

        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", deleteBtn);

        sleep(2000);
        navigateToOwner1();
        sleep(1000);

        int petCountAfter = driver.findElements(
                By.xpath("//button[contains(text(),'Delete Pet')]")).size();

        Assertions.assertTrue(
                petCountAfter < petCountBefore,
                "Number of pets should decrease after deletion"
        );
    }


    @Test
    @Order(10)
    @DisplayName("TC003_BUG - Future date should show error message - KNOWN BUG002")
        // BUG002: When a future birth date is entered, the form silently prevents
        // saving but shows NO error message to the user.
    void testFutureDateShouldShowErrorMessage() {
        navigateToOwner1();
        clickAddNewPet();
        fillForm("Futuristico", "2029/01/01", "dog");
        submitForm();
        sleep(1000);

        WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'cannot be in the future') " +
                                "or contains(text(),'invalid date') " +
                                "or contains(@class,'alert-danger')]")
                )
        );
        Assertions.assertTrue(
                errorMessage.isDisplayed(),
                "BUG002: No error message shown when future birth date is entered"
        );
    }


    private void navigateToOwner1() {
        driver.get(BASE_URL + "/owners/1");
    }

    private void clickAddNewPet() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Add New Pet')]"))).click();
    }

    private void fillForm(String name, String date, String type) {
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.clear();
        nameInput.sendKeys(name);

        WebElement dateInput = driver.findElement(By.name("birthDate"));
        dateInput.clear();
        dateInput.sendKeys(date);

        WebElement typeDropdown = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("type")));
        org.openqa.selenium.support.ui.Select select =
                new org.openqa.selenium.support.ui.Select(typeDropdown);
        select.selectByVisibleText(type);
    }

    private void submitForm() {
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}