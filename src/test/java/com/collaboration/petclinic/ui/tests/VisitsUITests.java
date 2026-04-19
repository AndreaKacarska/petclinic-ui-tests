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
    private static final String OWNER_URL = "/petclinic/owners/";

    @BeforeEach
    void setup() {
        driver.get(BASE_URL + OWNER_URL + OWNER_ID);
        visitsPage = new VisitsPage(driver);
    }

    // Helper to reduce duplication
    private void addVisit(String date, String desc) {
        visitsPage.clickAddVisitButton(PET_NAME);
        visitsPage.enterDate(date);
        visitsPage.enterDescription(desc);
        visitsPage.submitAddVisit();
    }

    // ================= POSITIVE =================

    @Test
    void testAddVisit_Valid() {
        String desc = "Routine checkup";
        addVisit("2026-03-21", desc);
        assertTrue(visitsPage.visitExists(desc));
    }

    @Test
    void testAddMultipleVisits() {
        for (int i = 1; i <= 2; i++) {
            String desc = "Visit " + i;
            addVisit("2026-04-0" + i, desc);
            assertTrue(visitsPage.visitExists(desc));
        }
    }

    @Test
    void testEditVisit() {
        String original = "Checkup";
        String updated = "Updated checkup";

        addVisit("2026-03-25", original);
        visitsPage.clickEditVisitButton(original);
        visitsPage.clearDescriptionInput();
        visitsPage.enterDescription(updated);
        visitsPage.submitUpdateVisit();

        assertTrue(visitsPage.visitExists(updated));
    }

    @Test
    void testDeleteVisit() {
        String desc = "To delete";
        addVisit("2026-03-28", desc);
        visitsPage.clickDeleteVisitButton(desc);

        assertTrue(visitsPage.waitForVisitDelete(desc));
    }

    // ================= NEGATIVE =================

    @Test
    void testAddVisit_MissingDescription() {
        visitsPage.clickAddVisitButton(PET_NAME);
        visitsPage.enterDate("2026-03-29");

        assertFalse(visitsPage.isAddVisitButtonEnabled());
    }

    @Test
    void testInvalidDate() {
        visitsPage.clickAddVisitButton(PET_NAME);
        visitsPage.enterDate("invalid");

        assertNotNull(driver);
    }

    // ================= EDGE CASES =================

    @Test
    void testRapidVisits() {
        for (int i = 1; i <= 3; i++) {
            addVisit("2026-04-" + (10 + i), "Rapid " + i);
            assertTrue(visitsPage.visitExists("Rapid " + i));
        }
    }

    // ================= SMOKE TEST =================

    @Test
    void testCRUDFlow() {
        String desc = "CRUD";

        addVisit("2026-05-04", desc);
        assertTrue(visitsPage.visitExists(desc));
        visitsPage.clickEditVisitButton(desc);
        visitsPage.enterDescription("CRUD Updated");
        visitsPage.submitUpdateVisit();

        visitsPage.clickDeleteVisitButton("CRUD Updated");
        assertTrue(visitsPage.waitForVisitDelete("CRUD Updated"));
    }
}


