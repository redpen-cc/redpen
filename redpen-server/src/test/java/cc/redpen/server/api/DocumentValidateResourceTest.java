package cc.redpen.server.api;

import org.apache.wink.common.internal.application.ApplicationFileLoader;
import org.apache.wink.server.internal.servlet.MockServletInvocationTest;
import org.apache.wink.common.http.HttpStatus;
import cc.redpen.server.DocumentValidatorInitializer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import java.io.FileNotFoundException;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.MediaType;

public class DocumentValidateResourceTest extends MockServletInvocationTest {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Override
  protected Class<?>[] getClasses() {
    try {
      Set<Class<?>> classes = new ApplicationFileLoader("application").getClasses();
      Class<?>[] classesArray = new Class[classes.size()];
      return classes.toArray(classesArray);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void testRun() throws Exception {
    MockHttpServletRequest request =
        constructMockRequest("POST", "/document/validate", MediaType.WILDCARD);
    request.setContent(("textarea=foobar").getBytes());
    MockServletContext context = new MockServletContext();
    context.addInitParameter("redpen.conf.path", "conf/redpen-conf.xml");
    listner.contextInitialized(new ServletContextEvent(context));
    MockHttpServletResponse response = invoke(request);

    assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
    JSONArray errors = (JSONArray) new JSONObject(response.getContentAsString()).get("errors");
    assertEquals(0, errors.length());
  }

  public void testRunWithErrors() throws Exception {
    MockHttpServletRequest request =
        constructMockRequest("POST", "/document/validate", MediaType.WILDCARD);
    request.setContent(("textarea=foobar.foobar").getBytes()); //NOTE: need space between periods.
    MockServletContext context = new MockServletContext();
    context.addInitParameter("redpen.conf.path", "conf/redpen-conf.xml");
    listner.contextInitialized(new ServletContextEvent(context));
    MockHttpServletResponse response = invoke(request);

    assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
    JSONArray errors = (JSONArray) new JSONObject(response.getContentAsString()).get("errors");
    assertEquals(1, errors.length());
    assertTrue(errors.get(0).toString().contains("Need white space after symbol (FULL_STOP)"));
  }

  public void testRunWithoutContent() throws Exception {
    MockHttpServletRequest request =
        constructMockRequest("POST", "/document/validate", MediaType.WILDCARD);
    request.setContent(("").getBytes()); //NOTE: need space between periods.
    MockServletContext context = new MockServletContext();
    context.addInitParameter("redpen.conf.path", "conf/redpen-conf.xml");
    listner.contextInitialized(new ServletContextEvent(context));
    MockHttpServletResponse response = invoke(request);

    assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
  }

  public void testRunWithOnlyFormName() throws Exception {
    MockHttpServletRequest request =
        constructMockRequest("POST", "/document/validate", MediaType.WILDCARD);
    request.setContent(("textarea=").getBytes()); //NOTE: need space between periods.
    MockServletContext context = new MockServletContext();
    context.addInitParameter("redpen.conf.path", "conf/redpen-conf.xml");
    listner.contextInitialized(new ServletContextEvent(context));
    MockHttpServletResponse response = invoke(request);

    assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
  }

  // test helper
  private MockHttpServletRequest constructMockRequest(String method,
      String requestURI,
      String acceptHeader) {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest() {
      public String getPathTranslated() {
        return null; // prevent Spring to resolve the file on the filesystem which fails
      }
    };

    mockRequest.setMethod(method);
    mockRequest.setRequestURI(requestURI);
    mockRequest.setServerPort(8080);
    mockRequest.addHeader("Accept", acceptHeader);
    mockRequest.addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
    mockRequest.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return mockRequest;
  }

  private ServletContextListener listner = new DocumentValidatorInitializer();
}
