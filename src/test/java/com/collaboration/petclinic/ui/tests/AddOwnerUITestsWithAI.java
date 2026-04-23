package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import com.collaboration.petclinic.ui.tests.pages.AddOwnerPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive UI tests for the Add Owner functionality in PetClinic
 * 
 * This test suite covers:
 * - Happy path scenarios (successful owner creation)
 * - Form validation (all fields)
 * - Input format validation (patterns, length constraints)
 * - Error message display and removal
 * - Button state management (enabled/disabled)
 * - User interactions and data persistence
 */
@DisplayName("Add Owner UI Tests with Comprehensive Coverage")
public class AddOwnerUITestsWithAI extends BaseTest {

    private AddOwnerPage addOwnerPage;

    @BeforeEach
    void initializePageObject() {
        addOwnerPage = new AddOwnerPage(driver);
    }

    // =========================================================================
    // SECTION 1: HAPPY PATH - SUCCESSFUL OWNER CREATION
    // =========================================================================

    @Test
    @DisplayName("TC001: Successfully create owner with all valid data")
    void testCreateOwnerWithValidData() {
        addOwnerPage.navigateTo(BASE_URL);
        assertTrue(addOwnerPage.isPageLoaded(), "Add Owner page should be loaded");

        addOwnerPage.fillFormWithValidData("John", "Doe", "123 Main Street", "New York", "5551234567");
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Submit button should be enabled with valid data");

        addOwnerPage.submitForm();
        addOwnerPage.waitForOwnersListRedirect();

        String currentUrl = addOwnerPage.getCurrentUrl();
        assertTrue(currentUrl.contains("/owners"), "Should redirect to owners list after successful submission");
    }

    @Test
    @DisplayName("TC002: Verify created owner data is displayed in the list")
    void testCreatedOwnerDataDisplayed() {
        addOwnerPage.navigateTo(BASE_URL);
        String firstName = "Alice";
        String lastName = "Smith";
        String city = "Boston";

        addOwnerPage.fillFormWithValidData(firstName, lastName, "456 Oak Avenue", city, "6171234567");
        addOwnerPage.submitForm();
        addOwnerPage.waitForOwnersListRedirect();

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(firstName), "First name should appear in owners list");
        assertTrue(pageSource.contains(lastName), "Last name should appear in owners list");
        assertTrue(pageSource.contains(city), "City should appear in owners list");
    }

    @Test
    @DisplayName("TC003: Create multiple owners successfully in sequence")
    void testCreateMultipleOwnersSequence() {
        String[][] owners = {
                {"John", "Doe", "123 Main St", "New York", "2125551234"},
                {"Jane", "Smith", "456 Oak Ave", "Boston", "6175551234"},
                {"Bob", "Johnson", "789 Pine Rd", "Chicago", "3125551234"}
        };

        for (String[] owner : owners) {
            addOwnerPage.navigateTo(BASE_URL);
            addOwnerPage.fillFormWithValidData(owner[0], owner[1], owner[2], owner[3], owner[4]);
            addOwnerPage.submitForm();
            addOwnerPage.waitForOwnersListRedirect();
            
            String pageSource = driver.getPageSource();
            assertTrue(pageSource.contains(owner[1]), "Created owner " + owner[0] + " " + owner[1] + " should be in list");
        }
    }

    @Test
    @DisplayName("TC004: Back button navigates without saving")
    void testBackButtonWithoutSaving() {
        addOwnerPage.navigateTo(BASE_URL);
        addOwnerPage.fillFormWithValidData("TestOwner", "NoSave", "123 Test St", "TestCity", "1234567890");
        
        addOwnerPage.clickBack();
        addOwnerPage.waitForOwnersListRedirect();

        String pageSource = driver.getPageSource();
        assertFalse(pageSource.contains("TestOwner"), "Owner filled in form should not be created when Back button clicked");
    }

    // =========================================================================
    // SECTION 2: SUBMIT BUTTON STATE - ENABLED/DISABLED CONDITIONS
    // =========================================================================

    @Test
    @DisplayName("TC005: Submit button disabled when form is empty")
    void testSubmitButtonDisabledWhenEmpty() {
        addOwnerPage.navigateTo(BASE_URL);
        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Submit button should be disabled when form is empty");
    }

    @Test
    @DisplayName("TC006: Submit button becomes enabled after filling all required fields")
    void testSubmitButtonEnabledWhenFormValid() {
        addOwnerPage.navigateTo(BASE_URL);
        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Initially submit button should be disabled");

        addOwnerPage.fillFormWithValidData("John", "Doe", "123 Main St", "New York", "2125551234");
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Submit button should be enabled when all fields are valid");
    }

    @Test
    @DisplayName("TC007: Submit button disabled when any field has validation error")
    void testSubmitButtonDisabledWithValidationErrors() {
        addOwnerPage.navigateTo(BASE_URL);

        // Fill with invalid first name (numbers)
        addOwnerPage.fillFirstName("123");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");

        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Submit button should be disabled when firstName has validation error");
    }

    @Test
    @DisplayName("TC008: Submit button disabled when required field is empty")
    void testSubmitButtonDisabledWithEmptyField() {
        addOwnerPage.navigateTo(BASE_URL);

        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName(""); // Empty
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");

        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Submit button should be disabled when required field is empty");
    }

    // =========================================================================
    // SECTION 3: FIRST NAME VALIDATION
    // =========================================================================

    @Test
    @DisplayName("TC009: First name required validation - empty field shows error")
    void testFirstNameRequiredValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillFirstName(""); // Clear it
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name is required"), 
                "Error message for required field should be displayed");
    }

    @Test
    @DisplayName("TC010: First name pattern validation - numbers show error")
    void testFirstNamePatternValidationNumbers() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("123");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name must consist of letters only"),
                "Error message for pattern violation should be displayed");
    }

    @Test
    @DisplayName("TC011: First name pattern validation - special characters show error")
    void testFirstNamePatternValidationSpecialChars() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("John@#$");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name must consist of letters only"),
                "Error message for special characters should be displayed");
    }

    @Test
    @DisplayName("TC012: First name maxlength validation - exceeds 30 characters")
    void testFirstNameMaxlengthValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        String longName = "a".repeat(35); // Exceeds maxlength of 30
        addOwnerPage.fillFirstName(longName);
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name may be at most 30 characters long"),
                "Error message for maxlength violation should be displayed");
    }

    @Test
    @DisplayName("TC013: First name minlength validation - exactly 1 character is valid")
    void testFirstNameMinlengthValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("A");
        addOwnerPage.fillLastName("Smith");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Single character first name should be valid");
    }

    @Test
    @DisplayName("TC014: First name accepts valid alphabetic characters")
    void testFirstNameAcceptsValidCharacters() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("Alexander");
        addOwnerPage.fillLastName("Smith");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Valid alphabetic first name should be accepted");
        assertFalse(addOwnerPage.isErrorMessageDisplayed("First name must consist of letters only"),
                "No error should be shown for valid first name");
    }

    @Test
    @DisplayName("TC015: First name error cleared after fixing invalid input")
    void testFirstNameErrorClearedAfterFix() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("123");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name must consist of letters only"),
                "Error should be displayed for invalid input");
        
        addOwnerPage.fillFirstName("John");
        assertTrue(addOwnerPage.isErrorMessageNotDisplayed("First name must consist of letters only"),
                "Error should be cleared after fixing the input");
    }

    // =========================================================================
    // SECTION 4: LAST NAME VALIDATION
    // =========================================================================

    @Test
    @DisplayName("TC016: Last name required validation - empty field shows error")
    void testLastNameRequiredValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillLastName("Smith");
        addOwnerPage.fillLastName(""); // Clear it
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Last name is required"),
                "Error message for required field should be displayed");
    }

    @Test
    @DisplayName("TC017: Last name pattern validation - numbers show error")
    void testLastNamePatternValidationNumbers() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillLastName("Smith123");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Last name must consist of letters only"),
                "Error message for pattern violation should be displayed");
    }

    @Test
    @DisplayName("TC018: Last name pattern validation - special characters show error")
    void testLastNamePatternValidationSpecialChars() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillLastName("O'Brien");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Last name must consist of letters only"),
                "Error message for special characters should be displayed");
    }

    @Test
    @DisplayName("TC019: Last name maxlength validation - exceeds 30 characters")
    void testLastNameMaxlengthValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        String longName = "a".repeat(35); // Exceeds maxlength of 30
        addOwnerPage.fillLastName(longName);
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Last name may be at most 30 characters long"),
                "Error message for maxlength violation should be displayed");
    }

    @Test
    @DisplayName("TC020: Last name minlength validation - exactly 1 character is valid")
    void testLastNameMinlengthValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("D");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Single character last name should be valid");
    }

    @Test
    @DisplayName("TC021: Last name error cleared after fixing invalid input")
    void testLastNameErrorClearedAfterFix() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillLastName("Smith123");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Last name must consist of letters only"),
                "Error should be displayed for invalid input");
        
        addOwnerPage.fillLastName("Smith");
        assertTrue(addOwnerPage.isErrorMessageNotDisplayed("Last name must consist of letters only"),
                "Error should be cleared after fixing the input");
    }

    // =========================================================================
    // SECTION 5: ADDRESS VALIDATION
    // =========================================================================

    @Test
    @DisplayName("TC022: Address required validation - empty field shows error")
    void testAddressRequiredValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillAddress(""); // Clear it
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Address is required"),
                "Error message for required field should be displayed");
    }

    @Test
    @DisplayName("TC023: Address maxlength validation - exceeds 255 characters")
    void testAddressMaxlengthValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        String longAddress = "a".repeat(260); // Exceeds maxlength of 255
        addOwnerPage.fillAddress(longAddress);
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Address may be at most 255 characters long"),
                "Error message for maxlength violation should be displayed");
    }

    @Test
    @DisplayName("TC024: Address accepts special characters and numbers")
    void testAddressAcceptsSpecialCharactersAndNumbers() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillAddress("123 Main St #456 Apt B");
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Address with numbers and special characters should be valid");
    }

    @Test
    @DisplayName("TC025: Address at maxlength boundary - exactly 255 characters")
    void testAddressAtMaxlengthBoundary() {
        addOwnerPage.navigateTo(BASE_URL);
        
        String addressAt255 = "a".repeat(255); // Exactly maxlength
        addOwnerPage.fillAddress(addressAt255);
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Address with exactly 255 characters should be valid");
    }

    @Test
    @DisplayName("TC026: Address error cleared after fixing invalid input")
    void testAddressErrorClearedAfterFix() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillAddress("a".repeat(260));
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Address may be at most 255 characters long"),
                "Error should be displayed for exceeding maxlength");
        
        addOwnerPage.fillAddress("123 Main Street");
        assertTrue(addOwnerPage.isErrorMessageNotDisplayed("Address may be at most 255 characters long"),
                "Error should be cleared after fixing the input");
    }

    // =========================================================================
    // SECTION 6: CITY VALIDATION
    // =========================================================================

    @Test
    @DisplayName("TC027: City required validation - empty field shows error")
    void testCityRequiredValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillCity(""); // Clear it
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("City is required"),
                "Error message for required field should be displayed");
    }

    @Test
    @DisplayName("TC028: City maxlength validation - exceeds 80 characters")
    void testCityMaxlengthValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        String longCity = "a".repeat(85); // Exceeds maxlength of 80
        addOwnerPage.fillCity(longCity);
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("City may be at most 80 characters long"),
                "Error message for maxlength violation should be displayed");
    }

    @Test
    @DisplayName("TC029: City accepts letters, numbers and special characters")
    void testCityAcceptsVariousCharacters() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillCity("New York City 123");
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "City with numbers and characters should be valid");
    }

    @Test
    @DisplayName("TC030: City at maxlength boundary - exactly 80 characters")
    void testCityAtMaxlengthBoundary() {
        addOwnerPage.navigateTo(BASE_URL);
        
        String cityAt80 = "a".repeat(80); // Exactly maxlength
        addOwnerPage.fillCity(cityAt80);
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "City with exactly 80 characters should be valid");
    }

    @Test
    @DisplayName("TC031: City error cleared after fixing invalid input")
    void testCityErrorClearedAfterFix() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillCity("a".repeat(85));
        assertTrue(addOwnerPage.isErrorMessageDisplayed("City may be at most 80 characters long"),
                "Error should be displayed for exceeding maxlength");
        
        addOwnerPage.fillCity("New York");
        assertTrue(addOwnerPage.isErrorMessageNotDisplayed("City may be at most 80 characters long"),
                "Error should be cleared after fixing the input");
    }

    // =========================================================================
    // SECTION 7: TELEPHONE VALIDATION
    // =========================================================================

    @Test
    @DisplayName("TC032: Telephone required validation - empty field shows error")
    void testTelephoneRequiredValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillTelephone("1234567890");
        addOwnerPage.fillTelephone(""); // Clear it
        
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Phone number is required"),
                "Error message for required field should be displayed");
    }

    @Test
    @DisplayName("TC033: Telephone pattern validation - letters show error")
    void testTelephonePatternValidationLetters() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillTelephone("abc");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Phone number only accept digits"),
                "Error message for non-numeric characters should be displayed");
    }

    @Test
    @DisplayName("TC034: Telephone pattern validation - special characters show error")
    void testTelephonePatternValidationSpecialChars() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillTelephone("212-555-1234");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Phone number only accept digits"),
                "Error message for special characters should be displayed");
    }

    @Test
    @DisplayName("TC035: Telephone maxlength validation - exceeds 20 digits")
    void testTelephoneMaxlengthValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillTelephone("12345678901234567890123"); // More than 20 digits
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Phone number cannot be more than 20 digits long"),
                "Error message for maxlength violation should be displayed");
    }

    @Test
    @DisplayName("TC036: Telephone at maxlength boundary - exactly 20 digits")
    void testTelephoneAtMaxlengthBoundary() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("12345678901234567890"); // Exactly 20 digits
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Telephone with exactly 20 digits should be valid");
    }

    @Test
    @DisplayName("TC037: Telephone minlength validation - exactly 1 digit is valid")
    void testTelephoneMinlengthValidation() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("1");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Single digit telephone should be valid");
    }

    @Test
    @DisplayName("TC038: Telephone accepts valid numeric input")
    void testTelephoneAcceptsValidNumbers() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Valid numeric telephone should be accepted");
        assertFalse(addOwnerPage.isErrorMessageDisplayed("Phone number only accept digits"),
                "No error should be shown for valid telephone");
    }

    @Test
    @DisplayName("TC039: Telephone error cleared after fixing invalid input")
    void testTelephoneErrorClearedAfterFix() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillTelephone("abc123");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Phone number only accept digits"),
                "Error should be displayed for non-numeric input");
        
        addOwnerPage.fillTelephone("2125551234");
        assertTrue(addOwnerPage.isErrorMessageNotDisplayed("Phone number only accept digits"),
                "Error should be cleared after fixing the input");
    }

    // =========================================================================
    // SECTION 8: COMPLEX SCENARIOS & EDGE CASES
    // =========================================================================

    @Test
    @DisplayName("TC040: Form field interaction - filling in different order")
    void testFormFieldInteractionDifferentOrder() {
        addOwnerPage.navigateTo(BASE_URL);
        
        // Fill in reverse order
        addOwnerPage.fillTelephone("2125551234");
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillFirstName("John");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Form should be valid regardless of fill order");
        addOwnerPage.submitForm();
        addOwnerPage.waitForOwnersListRedirect();
        assertTrue(addOwnerPage.getCurrentUrl().contains("/owners"), "Should successfully submit when filled in different order");
    }

    @Test
    @DisplayName("TC041: Form with leading and trailing spaces in fields")
    void testFormWithLeadingTrailingSpaces() {
        addOwnerPage.navigateTo(BASE_URL);
        
        // HTML pattern validation might reject spaces or app should handle them
        addOwnerPage.fillFirstName("  John  ");
        addOwnerPage.fillLastName("  Doe  ");
        addOwnerPage.fillAddress("  123 Main St  ");
        addOwnerPage.fillCity("  New York  ");
        addOwnerPage.fillTelephone("2125551234");
        
        // Should either be valid or show specific error
        assertTrue(addOwnerPage.isSubmitButtonEnabled() || addOwnerPage.isErrorMessageDisplayed("letters only"),
                "Form should handle spaces appropriately");
    }

    @Test
    @DisplayName("TC042: Clearing partially filled form")
    void testClearingPartiallFilledForm() {
        addOwnerPage.navigateTo(BASE_URL);
        
        addOwnerPage.fillFirstName("John");
        addOwnerPage.fillLastName("Doe");
        addOwnerPage.fillAddress("123 Main St");
        
        assertFalse(addOwnerPage.isSubmitButtonEnabled(), "Form should be invalid when not all required fields are filled");
        
        addOwnerPage.fillCity("New York");
        addOwnerPage.fillTelephone("2125551234");
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Form should become valid after all fields are filled");
    }

    @Test
    @DisplayName("TC043: Dynamic validation while typing")
    void testDynamicValidationWhileTyping() {
        addOwnerPage.navigateTo(BASE_URL);
        
        // Type invalid character
        addOwnerPage.fillFirstName("Jo1");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name must consist of letters only"),
                "Error should appear immediately after typing invalid character");
        
        // Correct it
        addOwnerPage.fillFirstName("John");
        assertTrue(addOwnerPage.isErrorMessageNotDisplayed("First name must consist of letters only"),
                "Error should disappear when input is corrected");
    }

    @Test
    @DisplayName("TC044: Multiple validation errors on one field")
    void testMultipleValidationErrorsOnOneField() {
        addOwnerPage.navigateTo(BASE_URL);
        
        // This test verifies that maxlength error is shown when exceeded
        String longInvalidName = "a".repeat(35) + "123";
        addOwnerPage.fillFirstName(longInvalidName);
        
        // Should show maxlength error (usually shown first)
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name may be at most 30 characters long"),
                "Maxlength error should be shown");
    }

    @Test
    @DisplayName("TC045: Form submission with exact boundary values")
    void testFormSubmissionWithBoundaryValues() {
        addOwnerPage.navigateTo(BASE_URL);
        
        // Use exact boundary values
        addOwnerPage.fillFirstName("J"); // Min: 1 char
        addOwnerPage.fillLastName("D"); // Min: 1 char
        addOwnerPage.fillAddress("a".repeat(255)); // Max: 255 chars
        addOwnerPage.fillCity("a".repeat(80)); // Max: 80 chars
        addOwnerPage.fillTelephone("1".repeat(20)); // Max: 20 chars
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Form should accept boundary values");
        addOwnerPage.submitForm();
        addOwnerPage.waitForOwnersListRedirect();
        assertTrue(addOwnerPage.getCurrentUrl().contains("/owners"), "Should successfully submit with boundary values");
    }

    @Test
    @DisplayName("TC046: Verify all error messages are visible simultaneously")
    void testAllErrorMessagesVisibleSimultaneously() {
        addOwnerPage.navigateTo(BASE_URL);
        
        // Create a scenario with all fields having issues
        addOwnerPage.fillFirstName(""); // Will show required error
        addOwnerPage.fillLastName(""); // Will show required error
        addOwnerPage.fillAddress(""); // Will show required error
        addOwnerPage.fillCity(""); // Will show required error
        addOwnerPage.fillTelephone(""); // Will show required error
        
        // All fields should show required errors
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name is required"), "First name required error should show");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Last name is required"), "Last name required error should show");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Address is required"), "Address required error should show");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("City is required"), "City required error should show");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("Phone number is required"), "Telephone required error should show");
    }

    @Test
    @DisplayName("TC047: Case sensitivity in name fields")
    void testCaseSensitivityInNames() {
        addOwnerPage.navigateTo(BASE_URL);
        
        // All cases should be valid (only letters matter for pattern)
        addOwnerPage.fillFirstName("JOHN");
        addOwnerPage.fillLastName("doe");
        addOwnerPage.fillAddress("123 Main St");
        addOwnerPage.fillCity("new york");
        addOwnerPage.fillTelephone("2125551234");
        
        assertTrue(addOwnerPage.isSubmitButtonEnabled(), "Mixed case names should be valid");
    }

    @Test
    @DisplayName("TC048: Continuous typing and error message responsiveness")
    void testContinuousTypingAndErrorMessageResponsiveness() {
        addOwnerPage.navigateTo(BASE_URL);
        
        // Type one invalid character at a time
        addOwnerPage.fillFirstName("1");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name must consist of letters only"),
                "Error should appear for single invalid character");
        
        // Continue typing valid characters
        addOwnerPage.fillFirstName("1John");
        assertTrue(addOwnerPage.isErrorMessageDisplayed("First name must consist of letters only"),
                "Error should still be shown with mixed characters");
        
        // Final correction
        addOwnerPage.fillFirstName("John");
        assertTrue(addOwnerPage.isErrorMessageNotDisplayed("First name must consist of letters only"),
                "Error should be cleared with all valid characters");
    }

    // =========================================================================
    // SECTION 9: VISUAL & UI BEHAVIOR VALIDATION
    // =========================================================================

    @Test
    @DisplayName("TC049: Verify form page title and header")
    void testFormPageTitle() {
        addOwnerPage.navigateTo(BASE_URL);
        assertTrue(addOwnerPage.isPageLoaded(), "Page should load with correct title");
        assertTrue(driver.getPageSource().contains("New Owner"), "Page should display 'New Owner' header");
    }

    @Test
    @DisplayName("TC050: Verify all form fields are present and visible")
    void testAllFormFieldsPresent() {
        addOwnerPage.navigateTo(BASE_URL);
        
        assertTrue(driver.findElement(addOwnerPage.getFirstNameFieldLocator()).isDisplayed(), "First name field should be visible");
        assertTrue(driver.findElement(addOwnerPage.getLastNameFieldLocator()).isDisplayed(), "Last name field should be visible");
        assertTrue(driver.findElement(addOwnerPage.getAddressFieldLocator()).isDisplayed(), "Address field should be visible");
        assertTrue(driver.findElement(addOwnerPage.getCityFieldLocator()).isDisplayed(), "City field should be visible");
        assertTrue(driver.findElement(addOwnerPage.getTelephoneFieldLocator()).isDisplayed(), "Telephone field should be visible");
    }
}

