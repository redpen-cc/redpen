package org.bigram.docvalidator.server.api;

import org.apache.wink.common.internal.application.ApplicationFileLoader;
import org.apache.wink.server.internal.servlet.MockServletInvocationTest;
import org.apache.wink.common.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.FileNotFoundException;
import java.util.Set;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

public class DocumentValidateResourceTest extends MockServletInvocationTest {
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
    request.setContent(("{\"textarea\" : \"foobar.\"}").getBytes("UTF-8"));
    request.addParameter("textarea", "foobar");
    request.setServerPort(8080);
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
}
