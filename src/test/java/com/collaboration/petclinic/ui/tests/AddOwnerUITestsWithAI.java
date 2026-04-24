package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import com.collaboration.petclinic.ui.tests.pages.AddOwnerPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sustainable Add Owner UI tests:
 * - keeps high-value coverage while reducing duplicate browser work
 * - uses parameterized tests to reduce repeated page loads and interactions
 */
@DisplayName("Add Owner UI Tests with Sustainable Coverage")
public class AddOwnerUITestsWithAI extends BaseTest {

    private AddOwnerPage addOwnerPage;
    private WebDriverWait wait;

    @BeforeEach
    void initializePageObject() {
        addOwnerPage = new AddOwnerPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(6));
    }

    @Test
    @DisplayName("TC001: Create owner with valid data")
    void testCreateOwnerWithValidData() {
        openAddOwnerForm();
        fillValidOwner("John", "Doe", "123 Main Street", "New York", "5551234567");

        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Submit button should be enabled with valid data");
        submitAndVerifyRedirect();
    }

    @Test
    @DisplayName("TC002: Created owner is visible in owners list")
    void testCreatedOwnerDataDisplayed() {
        // Sustainability: Verify by unique tokens to avoid brittle row-structure coupling and reruns.
        String uid = uniqueSuffix();
        String firstName = "Eco" + uid;
        String lastName = "Owner" + uid;
        String city = "Boston";

        openAddOwnerForm();
        fillValidOwner(firstName, lastName, "456 Oak Avenue", city, "6171234567");
        submitAndVerifyRedirect();

        assertOwnerTextPresent(firstName);
        assertOwnerTextPresent(lastName);
    }

    @Test
    @DisplayName("TC003: Back button returns to list without creating owner")
    void testBackButtonWithoutSaving() {
        // Sustainability: Replaces broad page text assertion with unique-text absence check.
        String uid = uniqueSuffix();
        String firstName = "Back" + uid;
        String lastName = "NoSave" + uid;

        openAddOwnerForm();
        fillValidOwner(firstName, lastName, "123 Test St", "TestCity", "1234567890");
        addOwnerPage.clickBack();
        addOwnerPage.waitForOwnersListRedirect();

        assertOwnerTextAbsent(firstName);
        assertOwnerTextAbsent(lastName);
    }

    @Test
    @DisplayName("TC004: Submit is disabled when form is empty")
    void testSubmitButtonDisabledWhenEmpty() {
        openAddOwnerForm();
        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Submit button should be disabled when form is empty");
    }

    @Test
    @DisplayName("TC005: Submit becomes enabled after all required fields are valid")
    void testSubmitButtonStateTransition() {
        // Sustainability: Merges two overlapping state tests into one transition test to cut one full navigation.
        openAddOwnerForm();
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillAddress("123 Main St");
        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Submit should stay disabled when form is partially filled");

        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Submit should be enabled once all required fields are valid");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requiredFieldCases")
    @DisplayName("TC006: Required-field validation errors are shown")
    void testRequiredFieldValidation(String caseName, Consumer<AddOwnerPage> clearFieldAction, String errorMessage) {
        // Sustainability: Consolidates five near-identical required-field tests into one parameterized flow.
        openAddOwnerForm();
        fillValidOwner("John", "Doe", "123 Main St", "New York", "2125551234");

        clearFieldAction.accept(addOwnerPage);

        assertTrue(addOwnerPage.isErrorMessageDisplayed(errorMessage),
                "Expected required-field error: " + errorMessage);
        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Submit should be disabled when a required field is invalid");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("namePatternCases")
    @DisplayName("TC007: Name fields reject non-letter characters")
    void testNamePatternValidation(String caseName, Consumer<AddOwnerPage> invalidAction, String errorMessage) {
        // Sustainability: Covers first/last-name invalid formats in one parameterized test instead of duplicated methods.
        openAddOwnerForm();
        invalidAction.accept(addOwnerPage);

        assertTrue(addOwnerPage.isErrorMessageDisplayed(errorMessage),
                "Expected pattern validation error: " + errorMessage);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("maxLengthCases")
    @DisplayName("TC008: Max-length rules are enforced")
    void testMaxLengthValidation(
            String caseName,
            Consumer<AddOwnerPage> invalidAction,
            Function<AddOwnerPage, By> locatorProvider,
            int maxLength) {
        // Sustainability: Asserting final field value length is deterministic and avoids fragile message checks.
        openAddOwnerForm();
        invalidAction.accept(addOwnerPage);

        String finalValue = driver.findElement(locatorProvider.apply(addOwnerPage)).getDomProperty("value");
        assertNotNull(finalValue, "Field value should be readable");
        assertTrue(finalValue.length() <= maxLength,
                "Field value should not exceed max length of " + maxLength);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTelephoneCases")
    @DisplayName("TC009: Telephone rejects non-digit formats")
    void testTelephonePatternValidation(String caseName, String phoneInput) {
        // Sustainability: Reduces duplicate telephone negative tests by sharing setup and assertions.
        openAddOwnerForm();
        addOwnerPage.fillTelephone(phoneInput);

        assertTrue(addOwnerPage.isErrorMessageDisplayed("Phone number only accept digits"),
                "Telephone should reject non-digit input");
        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Submit should stay disabled for invalid phone input");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("errorRecoveryCases")
    @DisplayName("TC010: Validation errors clear after correcting input")
    void testValidationErrorClearsAfterFix(
            String caseName,
            Consumer<AddOwnerPage> invalidAction,
            Consumer<AddOwnerPage> fixAction,
            String errorMessage) {
        // Sustainability: Reuses one error-recovery path across fields instead of five repetitive tests.
        openAddOwnerForm();

        invalidAction.accept(addOwnerPage);
        assertTrue(addOwnerPage.isErrorMessageDisplayed(errorMessage),
                "Error should appear for invalid input");

        fixAction.accept(addOwnerPage);
        assertTrue(addOwnerPage.isErrorMessageNotDisplayed(errorMessage),
                "Error should clear after valid correction");
    }

    @Test
    @DisplayName("TC011: Form remains valid regardless of field fill order")
    void testFormFieldInteractionDifferentOrder() {
        openAddOwnerForm();

        // Sustainability: Keep one representative order-independence test, remove additional overlapping interaction variants.
        addOwnerPage.fillTelephone("2125551234");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillFirstName("John");

        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Form should be valid regardless of fill order");
        submitAndVerifyRedirect();
    }

    @Test
    @DisplayName("TC012: Form submits with exact boundary values")
    void testFormSubmissionWithBoundaryValues() {
        // Sustainability: One boundary submit test replaces multiple single-field boundary tests.
        openAddOwnerForm();

        fillValidOwner("J", "D", "a".repeat(255), "a".repeat(80), "1".repeat(20));

        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Form should accept valid boundary values");
        submitAndVerifyRedirect();
    }

    @Test
    @DisplayName("TC013: Key form fields are visible")
    void testAllFormFieldsPresent() {
        // Sustainability: Keep a single lightweight UI smoke test and remove redundant page-source header checks.
        openAddOwnerForm();

        assertTrue(driver.findElement(addOwnerPage.getFirstNameFieldLocator()).isDisplayed(), "First name field should be visible");
        assertTrue(driver.findElement(addOwnerPage.getLastNameFieldLocator()).isDisplayed(), "Last name field should be visible");
        assertTrue(driver.findElement(addOwnerPage.getAddressFieldLocator()).isDisplayed(), "Address field should be visible");
        assertTrue(driver.findElement(addOwnerPage.getCityFieldLocator()).isDisplayed(), "City field should be visible");
        assertTrue(driver.findElement(addOwnerPage.getTelephoneFieldLocator()).isDisplayed(), "Telephone field should be visible");
    }

    private static Stream<Arguments> requiredFieldCases() {
        return Stream.of(
                Arguments.of("first name required", (Consumer<AddOwnerPage>) page -> page.fillFirstName(""), "First name is required"),
                Arguments.of("last name required", (Consumer<AddOwnerPage>) page -> page.fillLastName(""), "Last name is required"),
                Arguments.of("address required", (Consumer<AddOwnerPage>) page -> page.fillAddress(""), "Address is required"),
                Arguments.of("city required", (Consumer<AddOwnerPage>) page -> page.fillCity(""), "City is required"),
                Arguments.of("telephone required", (Consumer<AddOwnerPage>) page -> page.fillTelephone(""), "Phone number is required")
        );
    }

    private static Stream<Arguments> namePatternCases() {
        return Stream.of(
                Arguments.of("first name with digits", (Consumer<AddOwnerPage>) page -> page.fillFirstName("123"), "First name must consist of letters only"),
                Arguments.of("first name with symbols", (Consumer<AddOwnerPage>) page -> page.fillFirstName("John@#$"), "First name must consist of letters only"),
                Arguments.of("last name with digits", (Consumer<AddOwnerPage>) page -> page.fillLastName("Smith123"), "Last name must consist of letters only"),
                Arguments.of("last name with symbols", (Consumer<AddOwnerPage>) page -> page.fillLastName("O'Brien"), "Last name must consist of letters only")
        );
    }

    private static Stream<Arguments> maxLengthCases() {
        return Stream.of(
                Arguments.of("first name > 30", (Consumer<AddOwnerPage>) page -> page.fillFirstName("a".repeat(35)),
                        (Function<AddOwnerPage, By>) AddOwnerPage::getFirstNameFieldLocator, 30),
                Arguments.of("last name > 30", (Consumer<AddOwnerPage>) page -> page.fillLastName("a".repeat(35)),
                        (Function<AddOwnerPage, By>) AddOwnerPage::getLastNameFieldLocator, 30),
                Arguments.of("address > 255", (Consumer<AddOwnerPage>) page -> page.fillAddress("a".repeat(260)),
                        (Function<AddOwnerPage, By>) AddOwnerPage::getAddressFieldLocator, 255),
                Arguments.of("city > 80", (Consumer<AddOwnerPage>) page -> page.fillCity("a".repeat(85)),
                        (Function<AddOwnerPage, By>) AddOwnerPage::getCityFieldLocator, 80),
                Arguments.of("telephone > 20", (Consumer<AddOwnerPage>) page -> page.fillTelephone("12345678901234567890123"),
                        (Function<AddOwnerPage, By>) AddOwnerPage::getTelephoneFieldLocator, 20)
        );
    }

    private static Stream<Arguments> invalidTelephoneCases() {
        return Stream.of(
                Arguments.of("letters only", "abc"),
                Arguments.of("punctuation characters", "212-555-1234")
        );
    }

    private static Stream<Arguments> errorRecoveryCases() {
        return Stream.of(
                Arguments.of(
                        "first name error clears",
                        (Consumer<AddOwnerPage>) page -> page.fillFirstName("123"),
                        (Consumer<AddOwnerPage>) page -> page.fillFirstName("John"),
                        "First name must consist of letters only"
                ),
                Arguments.of(
                        "last name error clears",
                        (Consumer<AddOwnerPage>) page -> page.fillLastName("Smith123"),
                        (Consumer<AddOwnerPage>) page -> page.fillLastName("Smith"),
                        "Last name must consist of letters only"
                ),
                Arguments.of(
                        "telephone error clears",
                        (Consumer<AddOwnerPage>) page -> page.fillTelephone("abc123"),
                        (Consumer<AddOwnerPage>) page -> page.fillTelephone("2125551234"),
                        "Phone number only accept digits"
                )
        );
    }

    private void openAddOwnerForm() {
        addOwnerPage.navigateTo(BASE_URL);
        assertTrue(addOwnerPage.isPageLoaded(), "Add Owner page should be loaded");
    }

    private void fillValidOwner(String firstName, String lastName, String address, String city, String telephone) {
        addOwnerPage.fillFormWithValidData(firstName, lastName, address, city, telephone);
    }

    private void submitAndVerifyRedirect() {
        addOwnerPage.submitForm();
        addOwnerPage.waitForOwnersListRedirect();
        assertTrue(addOwnerPage.getCurrentUrl().contains("/owners"), "Should redirect to owners list");
    }

    private void assertOwnerTextPresent(String token) {
        By tokenLocator = By.xpath("//*[contains(normalize-space(.), '" + token + "')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(tokenLocator));
        assertTrue(driver.findElement(tokenLocator).isDisplayed(), "Expected owner token not found: " + token);
    }

    private void assertOwnerTextAbsent(String token) {
        // Sustainability: Fast DOM lookup by unique token avoids scanning full page source strings.
        By tokenLocator = By.xpath("//*[contains(normalize-space(.), '" + token + "')]");
        List<?> elements = driver.findElements(tokenLocator);
        assertTrue(elements.isEmpty(), "Unexpected owner token found: " + token);
    }

    private String uniqueSuffix() {
        return String.valueOf(System.currentTimeMillis());
    }
}
