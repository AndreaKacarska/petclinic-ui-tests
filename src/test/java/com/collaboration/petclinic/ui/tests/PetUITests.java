package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetUITests extends BaseTest {

    private WebDriverWait wait;
    private static final By ADD_NEW_PET_BUTTON = By.xpath("//button[contains(text(),'Add New Pet')]");
    private static final By SUBMIT_BUTTON = By.xpath("//button[@type='submit']");
    private static final By EDIT_PET_BUTTON = By.xpath("//button[contains(text(),'Edit Pet')]");
    private static final By DELETE_PET_BUTTONS = By.xpath("//button[contains(text(),'Delete Pet')]");
    private static final By FIRST_DELETE_PET_BUTTON = By.xpath("(//button[contains(text(),'Delete Pet')])[1]");
    private static final By NAME_INPUT = By.id("name");
    private static final By BIRTH_DATE_INPUT = By.name("birthDate");
    private static final By PET_TYPE_DROPDOWN = By.id("type");
    private static final By VALIDATION_HELP_BLOCK = By.className("help-block");
    private static final By VALIDATION_ALERT_DANGER = By.className("alert-danger");

    @BeforeEach
    void localSetup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @Order(1)
    @DisplayName("TC001 - Add a new pet successfully")
    void testAddPetSuccessfully() {
        addPetForOwner1("BuddyUI", "2022/05/15", "dog");
        wait.until(ExpectedConditions.urlContains("/owners/1"));
        navigateToOwner1();
        waitForOwnerPageLoaded();
        assertPageContains("BuddyUI", "Expected newly created pet 'BuddyUI' on owner details page");
    }
    @Test
    @Order(2)
    @DisplayName("TC002 - Add a pet with today's birth date")
    void testAddPetWithTodayDate() {
        String today = LocalDate.now().toString().replace("-", "/");
        addPetForOwner1("RexUI", today, "cat");
        wait.until(ExpectedConditions.urlContains("/owners/1"));
        navigateToOwner1();
        waitForOwnerPageLoaded();
        assertPageContains("RexUI", "Expected pet 'RexUI' with today's birth date on owner details page");
    }

    // This test documents that the system currently ALLOWS spec. char
    // Linked to BUG004 - needs clarification from team
    @Test
    @Order(3)
    @DisplayName("TC005 - Add a pet with special characters in name")
    void testAddPetSpecialChars() {
        addPetForOwner1("#!@123", "2024/01/15", "bird");
        wait.until(ExpectedConditions.urlContains("/owners/1"));
        navigateToOwner1();
        waitForOwnerPageLoaded();
        assertPageContains("123", "Expected persisted pet name containing '123' for BUG004 tracking");
    }

    @Test
    @Order(4)
    @DisplayName("TC008 - Add duplicate pet name for same owner")
    void testAddDuplicatePetName() {
        addPetForOwner1("BellaUI", "2024/01/15", "cat");
        wait.until(ExpectedConditions.urlContains("/owners/1"));
        navigateToOwner1();
        waitForOwnerPageLoaded();
        assertPageContains("BellaUI", "Expected duplicate pet name 'BellaUI' to be visible (current behavior)");
    }

    @Test
    @Order(5)
    @DisplayName("TC004/TC015 - Validate empty or whitespace name shows error")
    void testEmptyNameValidation() {
        navigateToOwner1();
        clickAddNewPet();

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(NAME_INPUT));
        nameInput.sendKeys("   ");

        driver.findElement(BIRTH_DATE_INPUT).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(VALIDATION_HELP_BLOCK));
        Assertions.assertTrue(
                error.isDisplayed(),
                "Expected validation error to be visible for whitespace-only pet name"
        );
        Assertions.assertFalse(
                error.getText().isEmpty(),
                "Expected non-empty validation message for whitespace-only pet name"
        );
    }

    @Test
    @Order(6)
    @DisplayName("TC006 - Submit button disabled when birth date is missing")
    void testMissingBirthDate() {
        navigateToOwner1();
        clickAddNewPet();
        wait.until(ExpectedConditions.visibilityOfElementLocated(NAME_INPUT))
                .sendKeys("NoDatePet");

        WebElement submitBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(SUBMIT_BUTTON));
        Assertions.assertFalse(
                submitBtn.isEnabled(),
                "Expected submit button to stay disabled when birth date is missing"
        );
    }

    @Test
    @Order(7)
    @DisplayName("TC011 - View existing pet on owner page")
    void testViewExistingPet() {
        navigateToOwner1();

        waitForOwnerPageLoaded();
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(
                pageSource.contains("Franklin") ||
                        pageSource.contains("Pets and Visits") ||
                        pageSource.contains("2010"),
                "Expected owner page to show existing pet information"
        );
    }

    @Test
    @Order(8)
    @DisplayName("TC013 - Edit an existing pet's name")
    void testEditPet() {
        navigateToOwner1();
        wait.until(ExpectedConditions.elementToBeClickable(
                EDIT_PET_BUTTON)).click();
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(NAME_INPUT));
        nameInput.clear();
        nameInput.sendKeys("LeoUpdated");
        submitForm();
        wait.until(ExpectedConditions.urlContains("/owners/1"));
        assertPageContains("LeoUpdated", "Expected edited pet name 'LeoUpdated' on owner details page");
    }

    @Test
    @Order(9)
    @DisplayName("TC014 - Delete a pet")
    void testDeletePet() {
        navigateToOwner1();
        waitForOwnerPageLoaded();

        WebElement deleteBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                FIRST_DELETE_PET_BUTTON));

        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", deleteBtn);
        wait.until(ExpectedConditions.elementToBeClickable(deleteBtn));

        int petCountBefore = driver.findElements(
                DELETE_PET_BUTTONS).size();

        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", deleteBtn);

        navigateToOwner1();
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(DELETE_PET_BUTTONS, petCountBefore));

        int petCountAfter = driver.findElements(
                DELETE_PET_BUTTONS).size();

        Assertions.assertTrue(
                petCountAfter < petCountBefore,
                "Expected pet count to decrease after deletion (before=" + petCountBefore + ", after=" + petCountAfter + ")"
        );
    }


    @Test
    @Order(10)
    @DisplayName("TC003_BUG - Future date should show error message - KNOWN BUG002")
    void testFutureDateShouldShowErrorMessage() {
        navigateToOwner1();
        clickAddNewPet();
        fillForm("Futuristico", "2029/01/01", "dog");
        submitForm();

        WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'cannot be in the future') " +
                                "or contains(text(),'invalid date') " +
                                "or contains(@class,'alert-danger')]")
                )
        );
        Assertions.assertTrue(
                errorMessage.isDisplayed(),
                "BUG002: Expected an error message when future birth date is submitted"
        );
    }

    @Test
    @Order(11)
    @DisplayName("TC016 - Add pet with valid leap-day birth date")
    void testAddPetWithValidLeapDayDate() {
        String leapPetName = "LeapDayUI";

        navigateToOwner1();
        clickAddNewPet();
        fillForm(leapPetName, "2024/02/29", "dog");
        submitForm();

        wait.until(ExpectedConditions.urlContains("/owners/1"));
        assertPageContains(leapPetName, "Expected leap-day pet '" + leapPetName + "' to be created");
    }

    @Test
    @Order(12)
    @DisplayName("TC017 - Reject non-leap-year February 29 date")
    void testAddPetWithInvalidNonLeapDate() {
        String invalidDatePetName = "InvalidLeapUI";

        navigateToOwner1();
        clickAddNewPet();
        fillForm(invalidDatePetName, "2023/02/29", "cat");
        submitForm();

        waitForValidationFeedbackOnForm();
        Assertions.assertTrue(
                isOnAddPetForm() && hasValidationFeedback(),
                "Expected Add Pet form validation for invalid non-leap date, but URL was: " + driver.getCurrentUrl()
        );

        navigateToOwner1();
        waitForOwnerPageLoaded();
        assertPageNotContains(invalidDatePetName,
                "Expected no pet created for invalid non-leap date: " + invalidDatePetName);
    }

    @Test
    @Order(13)
    @DisplayName("TC018 - Reject unsupported birth date format")
    void testAddPetWithUnsupportedDateFormat() {
        String invalidFormatPetName = "FormatEdgeUI";

        navigateToOwner1();
        clickAddNewPet();

        wait.until(ExpectedConditions.visibilityOfElementLocated(NAME_INPUT))
                .sendKeys(invalidFormatPetName);
        WebElement dateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                BIRTH_DATE_INPUT));
        dateInput.clear();
        dateInput.sendKeys("15-05-2022");

        WebElement typeDropdown = wait.until(
                ExpectedConditions.presenceOfElementLocated(PET_TYPE_DROPDOWN));
        org.openqa.selenium.support.ui.Select select =
                new org.openqa.selenium.support.ui.Select(typeDropdown);
        select.selectByVisibleText("bird");

        submitForm();

        waitForValidationFeedbackOnForm();
        Assertions.assertTrue(
                isOnAddPetForm() && hasValidationFeedback(),
                "Expected Add Pet form validation for unsupported date format, but URL was: " + driver.getCurrentUrl()
        );

        navigateToOwner1();
        waitForOwnerPageLoaded();
        assertPageNotContains(invalidFormatPetName,
                "Expected no pet created for unsupported date format: " + invalidFormatPetName);
    }


    private void navigateToOwner1() {
        driver.get(BASE_URL + "/owners/1");
    }

    private void addPetForOwner1(String name, String date, String type) {
        navigateToOwner1();
        clickAddNewPet();
        fillForm(name, date, type);
        submitForm();
    }

    private void waitForOwnerPageLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_NEW_PET_BUTTON));
    }

    private void clickAddNewPet() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_NEW_PET_BUTTON)).click();
    }

    private void fillForm(String name, String date, String type) {
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(NAME_INPUT));
        nameInput.clear();
        nameInput.sendKeys(name);

        WebElement dateInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(BIRTH_DATE_INPUT));
        dateInput.clear();
        dateInput.sendKeys(date);

        WebElement typeDropdown = wait.until(
                ExpectedConditions.presenceOfElementLocated(PET_TYPE_DROPDOWN));
        org.openqa.selenium.support.ui.Select select =
                new org.openqa.selenium.support.ui.Select(typeDropdown);
        select.selectByVisibleText(type);
    }

    private void submitForm() {
        wait.until(ExpectedConditions.presenceOfElementLocated(SUBMIT_BUTTON)).click();
    }

    private void waitForValidationFeedbackOnForm() {
        wait.until(d -> hasValidationFeedback() || isOnOwnerPage());
    }

    private boolean isOnAddPetForm() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl != null
                && (currentUrl.contains("/pets/new") || currentUrl.contains("/pets/add"));
    }

    private boolean isOnOwnerPage() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl != null && currentUrl.contains("/owners/1");
    }

    private boolean hasValidationFeedback() {
        return !driver.findElements(VALIDATION_HELP_BLOCK).isEmpty()
                || !driver.findElements(VALIDATION_ALERT_DANGER).isEmpty();
    }

    private void assertPageContains(String expectedText, String message) {
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource != null && pageSource.contains(expectedText), message);
    }

    private void assertPageNotContains(String text, String message) {
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource == null || !pageSource.contains(text), message);
    }
}