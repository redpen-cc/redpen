package ui;

import com.codeborne.selenide.ElementsCollection;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.assertTrue;

public class RedPenDemoTest {
  @Before
  public void setUp() throws Exception {
    open("http://localhost:8080/");
  }

  @Test
  public void welcomeIsDisplayed() throws Exception {
    $("h1").shouldHave(text("Welcome to RedPen!"));
  }

  @Test
  public void redpenEditorIsPrepopulated() throws Exception {
    String value = $("#redpen-editor").shouldBe(visible).val();
    assertTrue(value.startsWith("Some software tools work"));
  }

  @Test
  public void userCanChooseSampleTexts() throws Exception {
    $("#themes").click();
    $(By.linkText("JAPANESE TEXT")).click();

    String value = $("#redpen-editor").shouldBe(visible).val();
    assertTrue(value.startsWith("最近利用"));
  }

  @Test
  public void userCanClearTheText() throws Exception {
    $("[title='Clear text']").click();
    $("#redpen-editor").shouldBe(empty);
  }

  @Test
  public void textIsValidatedAsItEntered() throws Exception {
    $("#redpen-editor").val("Hello Wodrl");
    $("#redpen-errors").shouldHave(text("RedPen found 1 error"));

    ElementsCollection errors = $$(".redpen-error-list .redpen-error-message").shouldHaveSize(1);
    errors.get(0).shouldHave(text("Found possibly misspelled word \"Wodrl\"."));
    errors.get(0).find(".redpen-error-validator").shouldHave(text("Spelling"));
  }
}
