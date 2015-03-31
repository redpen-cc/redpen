/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.util;

import cc.redpen.RedPenException;
import cc.redpen.validator.Validator;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DictionaryLoaderTest extends Validator {
    @Test
    public void testCreateWordList() throws IOException {
        String sampleWordSet = "Saitama\n";
        sampleWordSet += "Gumma\n";
        sampleWordSet += "Gifu\n";

        Set<String> result = WORD_LIST.load(new ByteArrayInputStream(sampleWordSet.getBytes(StandardCharsets.UTF_8)));
        assertEquals(3, result.size());
    }

    @Test
    public void testCreateVacantWordList() throws IOException {
        String sampleWordSet = "";

        Set<String> result = WORD_LIST.load(new ByteArrayInputStream(sampleWordSet.getBytes(StandardCharsets.UTF_8)));
        assertEquals(0, result.size());
    }

    @Test
    public void testCreateKeyValueList() throws IOException {
        String sampleWordSet = "Saitama\t100\n";
        sampleWordSet += "Gumma\t530000\n";
        sampleWordSet += "Gifu\t1200\n";

        Map<String, String> result = KEY_VALUE.load(new ByteArrayInputStream(sampleWordSet.getBytes(StandardCharsets.UTF_8)));
        assertEquals(3, result.size());
        assertEquals("100", result.get("Saitama"));
        assertEquals("530000", result.get("Gumma"));
        assertEquals("1200", result.get("Gifu"));
    }

    @Test
    public void testCreateVacantKeyValueList() throws IOException {
        String sampleWordSet = "";
        Map<String, String> result = KEY_VALUE.load(new ByteArrayInputStream(sampleWordSet.getBytes(StandardCharsets.UTF_8)));
        assertEquals(0, result.size());
    }

    @Test
    public void testLoadCachedFile() throws IOException, RedPenException {
        Path path = Files.createTempFile("test", "txt");
        File file = path.toFile();
        Files.copy(new ByteArrayInputStream("foo".getBytes()), path, StandardCopyOption.REPLACE_EXISTING);
        Set<String> strings;
        strings = WORD_LIST.loadCachedFromFile(path.toFile(), "temp file");
        assertEquals(1, strings.size());
        assertTrue(strings.contains("foo"));

        // hopefully loaded from cache
        strings = WORD_LIST.loadCachedFromFile(path.toFile(), "temp file");
        assertEquals(1, strings.size());
        assertTrue(strings.contains("foo"));

        long lastModified = file.lastModified();


        Files.copy(new ByteArrayInputStream("foo\nbar".getBytes()), path, StandardCopyOption.REPLACE_EXISTING);
        file.setLastModified(lastModified);
        // won't be reloaded because the last modified date is not changed
        strings = WORD_LIST.loadCachedFromFile(path.toFile(), "temp file");
        assertEquals(1, strings.size());
        assertTrue(strings.contains("foo"));

        file.setLastModified(lastModified + 1000);
        // will be reloaded because the last modified date is changed
        strings = WORD_LIST.loadCachedFromFile(path.toFile(), "temp file");
        assertEquals(2, strings.size());
        assertTrue(strings.contains("foo"));
        assertTrue(strings.contains("bar"));
    }
}
