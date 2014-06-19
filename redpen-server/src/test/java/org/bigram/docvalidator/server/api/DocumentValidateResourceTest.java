package org.bigram.docvalidator.server.api;

import org.apache.wink.common.internal.application.ApplicationFileLoader;
import org.apache.wink.server.internal.servlet.MockServletInvocationTest;
import org.apache.wink.common.http.HttpStatus;
import org.bigram.docvalidator.server.DocumentValidatorInitializer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.FileNotFoundException;
import java.util.Set;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

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

  @Test
  public void testRun() throws Exception {
    MockHttpServletRequest request =
        constructMockRequest("POST", "/document/validate", MediaType.WILDCARD);
    request.setContent(("textarea=foobar").getBytes());
    request.addParameter("textarea", "foobar");
    request.setServerPort(8080);
    request.addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
    request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MockServletContext context = new MockServletContext();
    context.setAttribute("redpen.conf.path", "conf/dv-conf.xml");
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
        return null; // prevent Spring to resolve the file on the
        // filesystem which fails
      }

    };
    mockRequest.setMethod(method);
    mockRequest.setRequestURI(requestURI);
    mockRequest.addHeader("Accept", acceptHeader);
    return mockRequest;
  }

  private ServletContextListener listner = new DocumentValidatorInitializer();
}
