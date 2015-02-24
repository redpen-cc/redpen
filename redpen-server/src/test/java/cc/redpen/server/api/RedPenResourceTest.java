/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.server.api;

import org.apache.wink.common.http.HttpStatus;
import org.apache.wink.common.internal.application.ApplicationFileLoader;
import org.apache.wink.server.internal.servlet.MockServletInvocationTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.ws.rs.core.MediaType;
import java.io.FileNotFoundException;
import java.util.Set;

public class RedPenResourceTest extends MockServletInvocationTest {

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
        request.setContent(("document=Foobar").getBytes());
        MockServletContext context = new MockServletContext();
        context.addInitParameter("redpen.conf.path", "conf/redpen-conf.xml");
        MockHttpServletResponse response = invoke(request);

        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
        JSONArray errors = (JSONArray) new JSONObject(response.getContentAsString()).get("errors");
        assertEquals(0, errors.length());
    }

    public void testRunWithErrors() throws Exception {
        MockHttpServletRequest request =
                constructMockRequest("POST", "/document/validate", MediaType.WILDCARD);
        request.setContent(("document=foobar.foobar").getBytes()); //NOTE: need space between periods.
        MockServletContext context = new MockServletContext();
        context.addInitParameter("redpen.conf.path", "conf/redpen-conf.xml");
        MockHttpServletResponse response = invoke(request);

        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
        System.out.println(response.getContentAsString());
        JSONArray errors = (JSONArray) new JSONObject(response.getContentAsString()).get("errors");
        assertEquals(3, errors.length());
        assertTrue(errors.get(0).toString().length() > 0);
    }

    public void testRunWithoutContent() throws Exception {
        MockHttpServletRequest request =
                constructMockRequest("POST", "/document/validate", MediaType.WILDCARD);
        request.setContent(("").getBytes()); //NOTE: need space between periods.
        MockServletContext context = new MockServletContext();
        context.addInitParameter("redpen.conf.path", "conf/redpen-conf.xml");
        MockHttpServletResponse response = invoke(request);
        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
    }

    public void testRunWithOnlyFormName() throws Exception {
        MockHttpServletRequest request =
                constructMockRequest("POST", "/document/validate", MediaType.WILDCARD);
        request.setContent(("document=").getBytes()); //NOTE: need space between periods.
        MockServletContext context = new MockServletContext();
        context.addInitParameter("redpen.conf.path", "conf/redpen-conf.xml");
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
}
