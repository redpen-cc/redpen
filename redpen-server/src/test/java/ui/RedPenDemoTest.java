package ui;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class RedPenDemoTest {
    private static String redpenServerUrl;
    private static Server server;

    @BeforeAll
    static void beforeClass() throws Exception {
        // Run tests in PhantomJS by default if browser property is not set
        if (System.getProperty("browser") == null) {
            System.setProperty("browser", "phantomjs");
        }
        Socket socket = new Socket();
        for (int port = 8080; port < 65535; port++) {
            redpenServerUrl = String.format("http://localhost:%s/", port);
            try {
                socket.connect(new InetSocketAddress(port), 200);
                socket.close();
                // something is listening on the port
                // ensure that is RedPen
                open(redpenServerUrl);
                try {
                    $("#redpen-editor");
                }catch(NoSuchElementException ignored){
                }
            } catch (IOException e) {
                // nothing is listening on the port
                WebAppContext context = new WebAppContext();
                File webapp = new File("redpen-server/src/main/webapp/");
                if (!webapp.exists()) {
                    // working directory is redpen-server
                    webapp = new File("src/main/webapp/");
                }
                context.setWar(webapp.getAbsolutePath());
                context.setContextPath("/");
                server = new Server(port);
                server.setHandler(context);
                server.start();
                break;
            }
        }
    }

    @AfterAll
    static void afterClass() throws Exception {
        if (server != null) {
            server.stop();
        }
        try {
            // ensure phantomjs to quit
            WebDriverRunner.getWebDriver().quit();
        } catch (IllegalStateException ignored) {
        }

    }

    @BeforeEach
    void loadRedPen() throws IOException {
        boolean redPenLoaded;
        System.out.println(redpenServerUrl);
        try {
            new URL(redpenServerUrl).openConnection().connect();
            open(redpenServerUrl);
            redPenLoaded = true;
        } catch (IllegalStateException e) {
            redPenLoaded = false;
        }
        assumeTrue(redPenLoaded, "Please install " + System.getProperty("browser") + " for UI tests to run");
    }

    @Test
    void redpenEditorIsPrepopulated() throws Exception {
        String value = $("#redpen-editor").getAttribute("class");
        assertEquals("redpen-superimposed-editor-panel", value);
    }

    @Test
    void userCanChooseSampleTexts() throws Exception {
        if ($(".navbar-toggle").isDisplayed())
            $(".navbar-toggle").click();

        $("#themes").click();
        $(By.linkText("JAPANESE TEXT")).click();

        String value = $("#redpen-editor").shouldBe(visible).val();
        assertTrue(value.startsWith("最近利用"));
    }

    @Test
    void userCanClearTheText() throws Exception {
        $("[title='Clear text']").click();
        $("#redpen-editor").shouldBe(empty);
    }

    @Test
    void textIsValidatedAsItEntered() throws Exception {
        $("#redpen-editor").val("Hello Wodrl");
        $("#redpen-errors").shouldHave(text("RedPen found 1 error"));

        ElementsCollection errors = $$(".redpen-error-list .redpen-error-message").shouldHaveSize(1);
        errors.get(0).shouldHave(text("Found possibly misspelled word \"Wodrl\"."));
        errors.get(0).find(".redpen-error-validator").shouldHave(text("Spelling"));
    }

    @Test
    void validatorsCanBeDisabled() throws Exception {
        $("input[type=checkbox][value=Spelling]").click();
        $("#redpen-editor").val("Hello Wodrl");
        $("#redpen-errors").shouldHave(text("RedPen found 0 errors"));
    }

    @Test
    void validatorPropertiesCanBeChanged() throws Exception {
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
