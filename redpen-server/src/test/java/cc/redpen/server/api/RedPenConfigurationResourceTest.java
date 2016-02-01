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
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
