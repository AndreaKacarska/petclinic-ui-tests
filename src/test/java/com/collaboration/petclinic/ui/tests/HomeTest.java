package com.collaboration.petclinic.ui.tests;

import com.collaboration.petclinic.ui.base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertTrue;

public class HomeTest extends BaseTest {

  @Test
  void homePageLoads() throws InterruptedException {
    driver.get(BASE_URL);
    Thread.sleep(5000);
    String title = driver.getTitle();
    System.out.println("Page title: " + title);

    Assertions.assertNotNull(title);
    assertTrue(title.contains("Pet"));
  }
}
