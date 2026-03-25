package veterinarian.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

//UI does not support CRUD operations for veterinarians,
//therefore Selenium tests are focused on data visualization and validation.

public class VeterinarianUiTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


    // VETERINARIANS TESTS

    @Test
    void veterinariansPageLoads() {

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("VETERINARIANS")
        )).click();

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.tagName("h2")
        ));

        assertTrue(title.getText().contains("Veterinarians"));
    }

    @Test
    void veterinariansListDisplayed() {

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("VETERINARIANS")
        )).click();

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.tagName("table")
        ));

        assertTrue(table.isDisplayed());
    }

    @Test
    void specificVeterinarianExists() {

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("VETERINARIANS")
        )).click();

        String page = driver.getPageSource();

        assertTrue(
            page.contains("James Carter") ||
                page.contains("Helen Leary") ||
                page.contains("Linda Douglas"),
            "Expected veterinarian not found"
        );
    }

    @Test
    void verifySpecialtiesDisplayed() {

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("VETERINARIANS")
        )).click();

        String page = driver.getPageSource();

        assertTrue(
            page.contains("radiology") ||
                page.contains("surgery") ||
                page.contains("dentistry"),
            "Specialties not displayed"
        );
    }


    // OWNER TESTS


    @Test
    void addOwnerValid() {

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("FIND OWNERS")
        )).click();

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("Add Owner")
        )).click();

        driver.findElement(By.id("firstName")).sendKeys("Jasmin");
        driver.findElement(By.id("lastName")).sendKeys("Abazi");
        driver.findElement(By.id("address")).sendKeys("Street 1");
        driver.findElement(By.id("city")).sendKeys("Skopje");
        driver.findElement(By.id("telephone")).sendKeys("123456789");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String page = driver.getPageSource();
        assertTrue(page.contains("Jasmin"));
    }

    @Test
    void addOwnerEmptyFields() {

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("FIND OWNERS")
        )).click();

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("Add Owner")
        )).click();

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getPageSource().contains("Owner"));
        assertTrue(driver.findElement(By.id("firstName")).isDisplayed());
        assertTrue(driver.findElement(By.id("lastName")).isDisplayed());
    }

    @Test
    void addOwnerInvalidPhone() {

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("FIND OWNERS")
        )).click();

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("Add Owner")
        )).click();

        driver.findElement(By.id("firstName")).sendKeys("Test");
        driver.findElement(By.id("lastName")).sendKeys("User");
        driver.findElement(By.id("address")).sendKeys("Street");
        driver.findElement(By.id("city")).sendKeys("City");

        driver.findElement(By.id("telephone")).sendKeys("abc");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String page = driver.getPageSource();

        assertTrue(
            page.toLowerCase().contains("numeric") ||
                page.toLowerCase().contains("error"),
            "Invalid phone not detected"
        );
    }

    @Test
    void searchOwner() {

        wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("FIND OWNERS")
        )).click();

        driver.findElement(By.id("lastName")).sendKeys("Franklin");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String page = driver.getPageSource();

        assertTrue(
            page.contains("Franklin"),
            "Owner search failed"
        );
    }
}
