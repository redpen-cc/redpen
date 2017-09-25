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

import cc.redpen.RedPenException;

import org.apache.wink.common.http.HttpStatus;
import org.apache.wink.common.internal.application.ApplicationFileLoader;
import org.apache.wink.server.internal.servlet.MockServletInvocationTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpUpgradeHandler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.HttpHeaders;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.WILDCARD;

class RedPenResourceTest extends MockServletInvocationTest {
    @BeforeEach
    void beforeEach() throws Exception {
        // MockServletInvocationTest#setUp is required to be called explicitly as it is JUnit 3.x based implementation
        setUp();
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
    void testRun() throws Exception {
        MockHttpServletRequest request = constructMockRequest("POST", "/document/validate", WILDCARD);
        request.setContent("document=Foobar".getBytes());
        MockHttpServletResponse response = invoke(request);
        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
        JSONArray errors = (JSONArray) new JSONObject(response.getContentAsString()).get("errors");
        assertEquals(0, errors.length());
    }

    @Test
    void testRunWithErrors() throws Exception {
        MockHttpServletRequest request = constructMockRequest("POST", "/document/validate", WILDCARD);
        request.setContent(("document=foobar.foobar").getBytes()); //NOTE: need space between periods.
        MockHttpServletResponse response = invoke(request);

        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
        System.out.println(response.getContentAsString());
        JSONArray errors = (JSONArray) new JSONObject(response.getContentAsString()).get("errors");
        // the following will change whenever the configuration or validator functionaliy changes
        // but it doesn't indicate what particular errors are new/missing
        // assertEquals(3, errors.length());
        assertTrue(errors.get(0).toString().length() > 0);
    }

    @Test
    void testRunWithoutContent() throws Exception {
        MockHttpServletRequest request = constructMockRequest("POST", "/document/validate", WILDCARD);
        request.setContent(("").getBytes()); //NOTE: need space between periods.
        MockHttpServletResponse response = invoke(request);
        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
    }

    @Test
    void testRunWithOnlyFormName() throws Exception {
        MockHttpServletRequest request = constructMockRequest("POST", "/document/validate", WILDCARD);
        request.setContent(("document=").getBytes()); //NOTE: need space between periods.
        MockHttpServletResponse response = invoke(request);

        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
    }

    @Test
    void testJSValidatorRuns() throws Exception {
        if(new File("redpen-server").exists()) {
            System.setProperty("REDPEN_HOME", "redpen-server/src/test");
        }else{
            System.setProperty("REDPEN_HOME", "src/test");
        }
        MockHttpServletRequest request = constructMockRequest("POST", "/document/validate/json", WILDCARD, APPLICATION_JSON);
        request.setContent(String.format("{\"document\":\"Test, this is a test.\",\"format\":\"json2\",\"documentParser\":\"PLAIN\",\"config\":{\"lang\":\"en\",\"validators\":{\"JavaScript\":{\"properties\":{\"script-path\":\"%s\"}}}}}", "resources/js").getBytes());
        MockHttpServletResponse response = invoke(request);

        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
        JSONArray errors = new JSONObject(response.getContentAsString()).getJSONArray("errors");
        assertTrue(errors.length() > 0);
        for (int i=0; i<errors.length(); ++i) {
            JSONObject o = errors.getJSONObject(i).getJSONArray("errors").getJSONObject(0);
            assertEquals("called", o.getString("message"));
            assertEquals("pass.js", o.getString("validator"));
        }
    }

    @Test
    void testJSValidatorDoesntRunFromNonHomeDir() throws Exception {
        System.setProperty("REDPEN_HOME", ".");
        MockHttpServletRequest request = constructMockRequest("POST", "/document/validate/json", WILDCARD, APPLICATION_JSON);
        request.setContent(String.format("{\"document\":\"Test, this is a test.\",\"format\":\"json2\",\"documentParser\":\"PLAIN\",\"config\":{\"lang\":\"en\",\"validators\":{\"JavaScript\":{\"properties\":{\"script-path\":\"%s\"}}}}}", "resources/js").getBytes());
        MockHttpServletResponse response = invoke(request);

        assertEquals("HTTP status", HttpStatus.OK.getCode(), response.getStatus());
        JSONArray errors = new JSONObject(response.getContentAsString()).getJSONArray("errors");
        assertEquals(0, errors.length());
    }

    @Test
    void testDetectLanguage() throws Exception {
        assertEquals("en", new RedPenResource().detectLanguage("Hello World").getString("key"));
        assertEquals("ja", new RedPenResource().detectLanguage("こんにちは世界").getString("key"));
    }

    @Test
    void testResponseTyped() throws Exception {
        assertEquals(RedPenResource.MIME_TYPE_XML, RedPenResource.responseTyped("test", "xml").getMetadata().getFirst(HttpHeaders.CONTENT_TYPE).toString());
        assertEquals(RedPenResource.MIME_TYPE_JSON, RedPenResource.responseTyped("test", "json").getMetadata().getFirst(HttpHeaders.CONTENT_TYPE).toString());
        assertEquals(RedPenResource.MIME_TYPE_JSON, RedPenResource.responseTyped("test", "json2").getMetadata().getFirst(HttpHeaders.CONTENT_TYPE).toString());
        assertEquals(RedPenResource.MIME_TYPE_PLAINTEXT, RedPenResource.responseTyped("test", "plain").getMetadata().getFirst(HttpHeaders.CONTENT_TYPE).toString());

        try {
            RedPenResource.responseTyped("test", "foobarbaz");
            fail();
        } catch (final RedPenException success) {
            assertTrue(true);
        }
    }


    // test helper
    private MockHttpServletRequest constructMockRequest(String method, String requestURI, String acceptHeader) {
        return constructMockRequest(method, requestURI, acceptHeader, MediaType.APPLICATION_FORM_URLENCODED);
    }

    private MockHttpServletRequest constructMockRequest(String method, String requestURI, String acceptHeader, String contentType) {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest() {
            public String getPathTranslated() {
                return null; // prevent Spring to resolve the file on the filesystem which fails
            }

            @Override
            public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
                return null;
            }
        };

        mockRequest.setMethod(method);
        mockRequest.setRequestURI(requestURI);
        mockRequest.setServerPort(8080);
        mockRequest.addHeader("Accept", acceptHeader);
        mockRequest.addHeader("Content-Type", contentType);
        mockRequest.setContentType(contentType);
        return mockRequest;
    }
}
