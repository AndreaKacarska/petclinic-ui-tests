package com.collaboration.petclinic.ui.base;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BaseTest {
  protected WebDriver driver;
  protected final String BASE_URL = "http://localhost:4200";

  @BeforeEach
  void setUp() {
    ChromeOptions options = new ChromeOptions();
//    options.addArguments("--headless=new");
    driver = new ChromeDriver(options);
    driver.manage().window().maximize();
  }

  @AfterEach
  void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

}
