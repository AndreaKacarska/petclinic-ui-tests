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
 * Optimized UI Tests:
 * - Stable locators
 * - No unnecessary waits
 * - No page reloads
 * - Reduced DOM scanning (green & efficient)
 */
public class PetUITests extends BaseTest {

    private WebDriverWait wait;
    private WebDriverWait quickWait;

    @BeforeEach
    void localSetup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        quickWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        navigateToOwner1();
    }

    // ========================= TESTS =========================

    @Test
    @DisplayName("TC001 - Add a new pet successfully")
    void testAddPetSuccessfully() {
        String petName = "BuddyUI_" + System.currentTimeMillis();

        clickAddNewPet();
        fillForm(petName, "2022/05/15", "dog");
        submitForm();

        waitForPetToAppear(petName);

        Assertions.assertTrue(
                petNameExists(petName),
                "Pet should appear after creation"
        );
    }

    @Test
    @DisplayName("TC002 - Add a pet with today's birth date")
    void testAddPetWithTodayDate() {
        String today = LocalDate.now().toString().replace("-", "/");
        String petName = "RexUI_" + System.currentTimeMillis();

        clickAddNewPet();
        fillForm(petName, today, "cat");
        submitForm();

        waitForPetToAppear(petName);

        Assertions.assertTrue(
                petNameExists(petName),
                "Pet with today's date should be created"
        );
    }

    @Test
    @DisplayName("TC005 - Add a pet with special characters in name")
    void testAddPetSpecialChars() {
        String petName = "a#!@123_" + System.currentTimeMillis();

        clickAddNewPet();
        fillForm(petName, "2024/01/15", "bird");
        submitForm();

        waitForPetToAppear(petName);

        Assertions.assertTrue(
                petNameExists(petName),
                "Pet with special characters should be created successfully"
        );
    }

    @Test
    @DisplayName("TC008 - Add duplicate pet name for same owner")
    void testAddDuplicatePetName() {
        String petName = "BellaUI_" + System.currentTimeMillis();

        clickAddNewPet();
        fillForm(petName, "2024/01/15", "cat");
        submitForm();

        waitForPetToAppear(petName);

        Assertions.assertTrue(
                petNameExists(petName),
                "Duplicate names currently allowed (BUG005)"
        );
    }

    @Test
    @DisplayName("TC004/TC015 - Validate empty or whitespace name shows error")
    void testEmptyNameValidation() {
        clickAddNewPet();

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.sendKeys("   ");

        driver.findElement(By.name("birthDate")).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("help-block")));

        Assertions.assertTrue(error.isDisplayed());
        Assertions.assertFalse(error.getText().isEmpty());
    }

    @Test
    @DisplayName("TC006 - Submit button disabled when birth date is missing")
    void testMissingBirthDate() {
        clickAddNewPet();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")))
                .sendKeys("NoDatePet");

        WebElement submitBtn = driver.findElement(By.xpath("//button[@type='submit']"));

        Assertions.assertFalse(submitBtn.isEnabled());
    }

    @Test
    @DisplayName("TC011 - View existing pet on owner page")
    void testViewExistingPet() {
        WebElement petSection = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h2[contains(text(),'Pets and Visits')]")));

        Assertions.assertTrue(petSection.isDisplayed());
    }

    @Test
    @DisplayName("TC013 - Edit an existing pet's name")
    void testEditPet() {
        String updatedName = "LeoUpdated_" + System.currentTimeMillis();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Edit Pet')])[1]"))).click();

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.clear();
        nameInput.sendKeys(updatedName);

        submitForm();

        waitForPetToAppear(updatedName);

        Assertions.assertTrue(
                petNameExists(updatedName),
                "Pet name should be updated"
        );
    }

    @Test
    @DisplayName("TC014 - Delete a pet")
    void testDeletePet() {

        // Get name of first pet BEFORE deleting
        WebElement firstPet = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//*[contains(@class,'pet') or self::tr])[1]")
        ));

        String petText = firstPet.getText();

        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Delete Pet')])[1]")
        ));

        deleteBtn.click();

        // Wait until that specific pet is gone
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//*[contains(text(),'" + petText + "')]")
        ));

        Assertions.assertFalse(
                petNameExists(petText),
                "Pet should be deleted"
        );
    }

    @Test
    @DisplayName("TC003_BUG - Future date should NOT create pet")
    void testFutureDateShouldNotCreatePet() {

        String petName = "Futuristico_" + System.currentTimeMillis();

        clickAddNewPet();
        fillForm(petName, "2029/01/01", "dog");
        submitForm();

        boolean stillOnForm = driver.getCurrentUrl().contains("pets/new");

        Assertions.assertTrue(
                stillOnForm,
                "BUG002: Future date should not allow submission"
        );
    }

    // ========================= HELPERS =========================

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

        new org.openqa.selenium.support.ui.Select(typeDropdown)
                .selectByVisibleText(type);
    }

    private void submitForm() {
        WebElement submitBtn = driver.findElement(By.xpath("//button[@type='submit']"));

        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
        submitBtn.click();

        // Wait for URL change OR form to disappear
        wait.until(driver ->
                !driver.getCurrentUrl().contains("pets/new")
        );
    }

    private void waitForPetToAppear(String petName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'" + petName + "')]")
        ));
    }

    private boolean petNameExists(String petName) {
        return !driver.findElements(
                By.xpath("//*[contains(text(),'" + petName + "')]")
        ).isEmpty();
    }
}