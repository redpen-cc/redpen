package ui;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.net.ConnectException;
import java.net.URL;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;

public class RedPenDemoTest {
  private String redpenServerUrl = "http://localhost:8080/";

  @Before
  public void setUp() throws Exception {
    try {
      new URL(redpenServerUrl).openConnection().connect();
      System.setProperty("browser", "phantomjs");
      open(redpenServerUrl);
    }
    catch (ConnectException e) {
      assumeNoException("RedPen server is not running, skipping UI tests", e);
    }
    catch (IllegalStateException e) {
      assumeNoException("Please install PhantomJS for UI tests to run", e);
    }
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

  @Test
  public void validatorsCanBeDisabled() throws Exception {
    $("input[type=checkbox][value=Spelling]").click();
    $("#redpen-editor").val("Hello Wodrl");
    $("#redpen-errors").shouldHave(text("RedPen found 0 errors"));
  }

  @Test
  public void validatorPropertiesCanBeChanged() throws Exception {
    SelenideElement validatorProperties = $(".redpen-validator-properties[name=SentenceLength]");
    validatorProperties.click();

    $(".popover-title").should(appear).shouldHave(text("SentenceLength properties"));
    $(".popover-content input[type=text]").shouldHave(value("max_len=200")).val("max_len=10");
    $(".popover-content button[type=submit]").click();

    validatorProperties.shouldHave(text("max_len=10"));

    $("#redpen-editor").val("This is a very long sentence of over ten words.");
    $("#redpen-errors").shouldHave(text("RedPen found 2 errors"));

    $$(".redpen-error-message").get(0).shouldHave(text("The length of the sentence (47) exceeds the maximum of 10."), text("SentenceLength"));
    $$(".redpen-error-message").get(1).shouldHave(text("\"very\" is considered a weak expression."), text("WeakExpression"));
  }
}
