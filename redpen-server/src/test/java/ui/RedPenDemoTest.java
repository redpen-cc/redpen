package ui;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.*;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;

public class RedPenDemoTest {
    private final static String redpenServerUrl = "http://localhost:8080/";
    private static Server server;
    private final static int PORT = 8080;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // Run tests in PhantomJS by default if browser property is not set
        if (System.getProperty("browser") == null) {
            System.setProperty("browser", "phantomjs");
        }
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(PORT), 200);
            socket.close();
            // something is listening on port 8080
        } catch (IOException e) {
            // nothing is listening on port 8080
            WebAppContext context = new WebAppContext();
            File webapp = new File("redpen-server/src/main/webapp/");
            if (!webapp.exists()) {
                // working directory is redpen-server
                webapp = new File("src/main/webapp/");
            }
            context.setWar(webapp.getAbsolutePath());
            context.setContextPath("/");
            server = new Server(PORT);
            server.setHandler(context);
            server.start();
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (server != null) {
            server.stop();
        }
        try {
            // ensure phantomjs to quit
            WebDriverRunner.getWebDriver().quit();
        }catch(IllegalStateException ignored){
        }

    }

    @Before
    public void loadRedPen() throws IOException {
        try {
            new URL(redpenServerUrl).openConnection().connect();
            open(redpenServerUrl);
        } catch (IllegalStateException e) {
            assumeNoException("Please install " + System.getProperty("browser") + " for UI tests to run", e);
        }
    }

    @Test
    public void redpenEditorIsPrepopulated() throws Exception {
        String value = $("#redpen-editor").getAttribute("class");
        assertEquals("redpen-superimposed-editor-panel", value);
    }

    @Test
    public void userCanChooseSampleTexts() throws Exception {
        if ($(".navbar-toggle").isDisplayed())
            $(".navbar-toggle").click();

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
        $(".popover-content input[type=text]").shouldHave(value("max_len=120")).val("max_len=10");
        $(".popover-content button[type=submit]").click();

        validatorProperties.shouldHave(text("max_len=10"));

        $("#redpen-editor").val("This is a very long sentence of over ten words.");
        $("#redpen-errors").shouldHave(text("RedPen found 2 errors"));

        $$(".redpen-error-message").get(0).shouldHave(text("The length of the sentence (47) exceeds the maximum of 10."), text("SentenceLength"));
        $$(".redpen-error-message").get(1).shouldHave(text("\"very\" is considered a weak expression."), text("WeakExpression"));
    }
}
