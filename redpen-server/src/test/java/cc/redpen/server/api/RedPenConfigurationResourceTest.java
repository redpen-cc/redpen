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

import cc.redpen.RedPen;
import cc.redpen.parser.DocumentParser;
import cc.redpen.tokenizer.JapaneseTokenizer;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RedPenConfigurationResourceTest {
    RedPenConfigurationResource resource = new RedPenConfigurationResource();

    @Test
    public void versionIsReturned() throws Exception {
        JSONObject response = (JSONObject)resource.getRedPens("").getEntity();
        assertEquals(RedPen.VERSION, response.getString("version"));
    }

    @Test
    public void availableDocumentParsersAreReturned() throws Exception {
        JSONObject response = (JSONObject)resource.getRedPens("").getEntity();
        assertEquals(new JSONArray(DocumentParser.PARSER_MAP.keySet()).toString(), response.get("documentParsers").toString());
    }

    @Test
    public void allConfigurationsIfLangNotSpecified() throws Exception {
        RedPenService service = mock(RedPenService.class);
        RedPen redPen = mock(RedPen.class, RETURNS_DEEP_STUBS);
        doReturn(ImmutableMap.of("en", redPen, "ja", redPen, "et", redPen)).when(service).getRedPens();

        resource = spy(resource);
        doReturn(service).when(resource).getRedPenService();

        JSONObject response = (JSONObject)resource.getRedPens(null).getEntity();
        JSONObject redpens = response.getJSONObject("redpens");

        assertEquals(3, redpens.length());

        assertNotNull(redpens.getJSONObject("en"));
        assertNotNull(redpens.getJSONObject("ja"));
        assertNotNull(redpens.getJSONObject("et"));
    }

    @Test
    public void redPenFields() throws Exception {
        JSONObject response = (JSONObject)resource.getRedPens(null).getEntity();
        JSONObject redpens = response.getJSONObject("redpens");

        JSONObject ja = redpens.getJSONObject("ja");
        assertEquals("ja", ja.getString("lang"));
        assertEquals("zenkaku", ja.getString("variant"));
        assertEquals(JapaneseTokenizer.class.getName(), ja.getString("tokenizer"));
        assertTrue(!ja.getString("validators").isEmpty());
        assertTrue(!ja.getString("symbols").isEmpty());
    }

    @Test
    public void exportConfiguration() throws Exception {
        String json = "{\"config\": {\"lang\": \"ru\"}}";
        String response = (String)resource.exportConfiguration(new JSONObject(json)).getEntity();

        assertTrue(response.contains("redpen-conf lang=\"ru\""));
    }
}
