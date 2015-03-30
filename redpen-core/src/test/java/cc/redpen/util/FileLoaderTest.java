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
package cc.redpen.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FileLoaderTest {
    @Test
    public void testCreateWordList() throws IOException {
        String sampleWordSet = "Saitama\n";
        sampleWordSet += "Gumma\n";
        sampleWordSet += "Gifu\n";

        Set<String> result = ResourceExtractor.WORD.load(new ByteArrayInputStream(sampleWordSet.getBytes(StandardCharsets.UTF_8)));
        assertEquals(3, result.size());
    }

    @Test
    public void testCreateVacantWordList() throws IOException {
        String sampleWordSet = "";

        Set<String> result = ResourceExtractor.WORD.load(new ByteArrayInputStream(sampleWordSet.getBytes(StandardCharsets.UTF_8)));
        assertEquals(0, result.size());
    }

    @Test
    public void testCreateKeyValueList() throws IOException {
        String sampleWordSet = "Saitama\t100\n";
        sampleWordSet += "Gumma\t530000\n";
        sampleWordSet += "Gifu\t1200\n";

        Map<String, String> result = ResourceExtractor.KEY_VALUE.load(new ByteArrayInputStream(sampleWordSet.getBytes(StandardCharsets.UTF_8)));
        assertEquals(3, result.size());
        assertEquals("100", result.get("Saitama"));
        assertEquals("530000", result.get("Gumma"));
        assertEquals("1200", result.get("Gifu"));
    }

    @Test
    public void testCreateVacantKeyValueList() throws IOException {
        String sampleWordSet = "";
        Map<String, String> result = ResourceExtractor.KEY_VALUE.load(new ByteArrayInputStream(sampleWordSet.getBytes(StandardCharsets.UTF_8)));
        assertEquals(0, result.size());
    }
}
