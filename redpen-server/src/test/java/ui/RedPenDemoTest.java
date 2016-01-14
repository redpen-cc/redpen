package ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class RedPenDemoTest {
  @Before
  public void setUp() throws Exception {
    open("http://localhost:8080/");
  }

  @Test
  public void welcomeIsDisplayed() throws Exception {
    $("h1").shouldHave(text("Welcome to RedPen!"));
  }
}
