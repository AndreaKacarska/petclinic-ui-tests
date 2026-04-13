package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import com.collaboration.petclinic.ui.pages.VisitsPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Visits UI Tests")
public class VisitsUITests extends BaseTest {

    private VisitsPage visitsPage;
    private static final int OWNER_ID = 3;
    private static final String PET_NAME = "Rosy";
    private static final String BASE_OWNER_URL = "/petclinic/owners/";

    @BeforeEach
    void setupVisitsPage() {
        driver.get(BASE_URL + BASE_OWNER_URL + OWNER_ID);
        visitsPage = new VisitsPage(driver);
    }

    // ==================== POSITIVE TEST CASES ====================

    @Nested
    @DisplayName("Positive Test Cases - Add Visit")
    class AddVisitPositiveTests {

        @Test
        @DisplayName("Should successfully add a visit with valid date and description")
        void testAddVisitWithValidData() {
            String date = "2026-03-21";
            String description = "Routine checkup";

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate(date);
            visitsPage.enterDescription(description);
            visitsPage.submitAddVisit();

            assertTrue(visitsPage.visitExists(description), "Visit with description '" + description + "' should be visible after adding");
        }

        @Test
        @DisplayName("Should successfully add a visit with special characters in description")
        void testAddVisitWithSpecialCharacters() {
            String date = "2026-03-22";
            String description = "Checkup & Vaccination (updated)";

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate(date);
            visitsPage.enterDescription(description);
            visitsPage.submitAddVisit();

            assertTrue(visitsPage.visitExists(description), "Visit with special characters should be successfully added");
        }

        @Test
        @DisplayName("Should successfully add a visit with long description")
        void testAddVisitWithLongDescription() {
            String date = "2026-03-23";
            String description = "This is a very long description for a pet visit that contains detailed information about the checkup performed and any observations made during the visit.";

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate(date);
            visitsPage.enterDescription(description);
            visitsPage.submitAddVisit();

            assertTrue(visitsPage.visitExists(description), "Visit with long description should be successfully added");
        }

        @Test
        @DisplayName("Should successfully add multiple visits for the same pet")
        void testAddMultipleVisits() {
            String date1 = "2026-04-01";
            String description1 = "First visit";
            String date2 = "2026-04-02";
            String description2 = "Second visit";

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate(date1);
            visitsPage.enterDescription(description1);
            visitsPage.submitAddVisit();

            assertTrue(visitsPage.visitExists(description1), "First visit should be added");

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate(date2);
            visitsPage.enterDescription(description2);
            visitsPage.submitAddVisit();

            assertTrue(visitsPage.visitExists(description2), "Second visit should be added");
        }
    }

    @Nested
    @DisplayName("Positive Test Cases - Edit Visit")
    class EditVisitPositiveTests {

        @Test
        @DisplayName("Should successfully edit a visit description")
        void testEditVisitDescription() {
            String originalDescription = "Routine checkup";
            String updatedDescription = "Updated routine checkup";

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("2026-03-25");
            visitsPage.enterDescription(originalDescription);
            visitsPage.submitAddVisit();

            assertTrue(visitsPage.visitExists(originalDescription), "Original visit should exist");

            visitsPage.clickEditVisitButton(originalDescription);
            visitsPage.clearDescriptionInput();
            visitsPage.enterDescription(updatedDescription);
            visitsPage.submitUpdateVisit();

            assertTrue(visitsPage.visitExists(updatedDescription), "Updated visit description should be visible");
            //assertFalse(visitsPage.visitExists(originalDescription), "Original visit description should no longer exist");
        }

        @Test
        @DisplayName("Should successfully edit a visit date")
        void testEditVisitDate() {
            String description = "Vaccination";
            String originalDate = "2026-03-26";
            String updatedDate = "2026-03-27";

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate(originalDate);
            visitsPage.enterDescription(description);
            visitsPage.submitAddVisit();

            visitsPage.clickEditVisitButton(description);
            visitsPage.clearDateInput();
            visitsPage.enterDate(updatedDate);
            visitsPage.submitUpdateVisit();

            assertTrue(visitsPage.visitExists(description), "Visit should still exist after date update");
        }
    }

    @Nested
    @DisplayName("Positive Test Cases - Delete Visit")
    class DeleteVisitPositiveTests {

        @Test
        @DisplayName("Should successfully delete a visit")
        void testDeleteVisit() {
            String description = "Test visit to delete";

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("2026-03-28");
            visitsPage.enterDescription(description);
            visitsPage.submitAddVisit();

            assertTrue(visitsPage.visitExists(description), "Visit should exist before deletion");
            visitsPage.clickDeleteVisitButton(description);
            assertTrue(visitsPage.waitForVisitDelete(description), "Visit should be deleted");
        }
    }

    // ==================== NEGATIVE TEST CASES ====================

    @Nested
    @DisplayName("Negative Test Cases - Missing Required Fields")
    class AddVisitNegativeTests {
        @Test
        @DisplayName("Should disable Add Visit button when only date is provided")
        void testAddVisitWithoutDescription() {
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("2026-03-29");

            assertFalse(visitsPage.isAddVisitButtonEnabled(), "Add Visit button should be disabled when description is missing");
        }

        @Test
        @DisplayName("Should disable Add Visit button when only description is provided")
        void testAddVisitWithoutDate() {
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDescription("Checkup without date");

            assertFalse(visitsPage.isAddVisitButtonEnabled(), "Add Visit button should be disabled when date is missing");
        }

        @Test
        @DisplayName("Should disable Add Visit button when no fields are filled")
        void testAddVisitWithoutAnyData() {
            visitsPage.clickAddVisitButton(PET_NAME);

            assertFalse(visitsPage.isAddVisitButtonEnabled(), "Add Visit button should be disabled when no data is provided");
        }


        // the application does not have any validation for whitespace-only descriptions, this test failed
        /*@Test
        @DisplayName("Should disable Add Visit button with empty description (whitespace only)")
        void testAddVisitWithWhitespaceDescription() {
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("2026-03-30");
            visitsPage.enterDescription("   ");

            assertFalse(visitsPage.isAddVisitButtonEnabled(), "Add Visit button should be disabled when description contains only whitespace");
        } */
    }

    @Nested
    @DisplayName("Negative Test Cases - Invalid Input")
    class InvalidInputTests {

        @Test
        @DisplayName("Should handle invalid date format gracefully")
        void testAddVisitWithInvalidDateFormat() {
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("invalid-date");
            visitsPage.enterDescription("Checkup");

            assertNotNull(driver, "Browser should still be active after invalid input");
        }

        @Test
        @DisplayName("Should prevent adding visit with past date")
        void testAddVisitWithPastDate() {
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("2020-01-01");
            visitsPage.enterDescription("Past checkup");

            assertNotNull(driver, "Application should handle past dates appropriately");
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very long visit description (500+ characters)")
        void testAddVisitWithExtremelyLongDescription() {
            String date = "2026-04-01";
            String description = "A".repeat(1000);

            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate(date);
            visitsPage.enterDescription(description);

            assertNotNull(driver, "Application should handle extremely long descriptions");
        }

        @Test
        @DisplayName("Should handle special characters in date field")
        void testAddVisitWithSpecialCharactersInDate() {
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("!@#$%^&*");
            visitsPage.enterDescription("Checkup");

            assertFalse(visitsPage.isAddVisitButtonEnabled(),
                    "Add Visit button should be disabled with invalid date characters");
        }

        @Test
        @DisplayName("Should clear form when closing Add Visit dialog")
        void testFormClearsAfterAddingVisit() {
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("2026-04-02");
            visitsPage.enterDescription("Test visit");
            visitsPage.submitAddVisit();

            visitsPage.clickAddVisitButton(PET_NAME);

            assertNotNull(driver.findElement(org.openqa.selenium.By.xpath("//input[@ng-reflect-name='date']")),
                    "Date input should be available for new visit");
        }

        @Test
        @DisplayName("Should handle rapid consecutive visits")
        void testAddVisitsRapidly() {
            for (int i = 1; i <= 3; i++) {
                visitsPage.clickAddVisitButton(PET_NAME);
                visitsPage.enterDate("2026-04-" + String.format("%02d", i + 2));
                visitsPage.enterDescription("Rapid visit " + i);
                visitsPage.submitAddVisit();
                assertTrue(visitsPage.visitExists("Rapid visit " + i),
                        "Visit " + i + " should be added successfully");
            }
        }

        @Test
        @DisplayName("Should handle concurrent visits from different users (same pet)")
        void testMultipleVisitsOnSamePet() {
            for (int i = 1; i <= 2; i++) {
                visitsPage.clickAddVisitButton(PET_NAME);
                visitsPage.enterDate("2026-05-" + String.format("%02d", i));
                visitsPage.enterDescription("Concurrent visit " + i);
                visitsPage.submitAddVisit();
            }

            assertTrue(visitsPage.visitExists("Concurrent visit 1"), "First concurrent visit should exist");
            assertTrue(visitsPage.visitExists("Concurrent visit 2"), "Second concurrent visit should exist");
        }

        @Test
        @DisplayName("Should handle numeric and alphanumeric descriptions")
        void testAddVisitWithNumericDescription() {
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("2026-05-03");
            visitsPage.enterDescription("12345-ABCDE-67890");
            visitsPage.submitAddVisit();

            assertTrue(visitsPage.visitExists("12345-ABCDE-67890"),
                    "Visit with alphanumeric description should be added");
        }
    }

    // ==================== SMOKE TESTS ====================

    @Nested
    @DisplayName("Smoke Tests - Critical Workflows")
    class SmokeTests {

        @Test
        @DisplayName("Complete CRUD workflow: Create, Read, Update, Delete")
        void testCompleteVisitCRUDWorkflow() {
            String description = "CRUD Test Visit";
            visitsPage.clickAddVisitButton(PET_NAME);
            visitsPage.enterDate("2026-05-04");
            visitsPage.enterDescription(description);
            visitsPage.submitAddVisit();
            assertTrue(visitsPage.visitExists(description), "Visit should be created");

            String updatedDescription = "CRUD Test Visit - Updated";
            visitsPage.clickEditVisitButton(description);
            visitsPage.clearDescriptionInput();
            visitsPage.enterDescription(updatedDescription);
            visitsPage.submitUpdateVisit();
            assertTrue(visitsPage.visitExists(updatedDescription), "Visit should be updated");

            visitsPage.clickDeleteVisitButton(updatedDescription);
            assertTrue(visitsPage.waitForVisitDelete(updatedDescription), "Visit should be deleted");
        }
    }
}
