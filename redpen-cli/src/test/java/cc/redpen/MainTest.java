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
package cc.redpen;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class MainTest {

    @Test
    void testMain() throws RedPenException {
        String[] args = new String[]{
                "-c", "sample/conf/redpen-conf-en.xml",
                "sample/sample-doc/en/sampledoc-en.txt"
        };
        Main.run(args);
    }

    @Test
    void testDefaultConfigFile() throws RedPenException, IOException {
        File file;

        file = Main.resolveConfigLocation("not-exist.conf");
        // not-exist.conf not found
        assertNull(file);

        String defaultConfPath = "." + File.separator + "redpen-conf.xml";
        File defaultConfFile = new File(defaultConfPath);
        // create empty configuration file in the current directory
        if (defaultConfFile.createNewFile()) {
            // ensure the file is deleted upon test failure
            defaultConfFile.deleteOnExit();
            file = Main.resolveConfigLocation(defaultConfPath);
            // .//redpen-conf.xml exists
            assertNotNull(file);
            // clean up config file in the current directory
            defaultConfFile.delete();
        }

        String localeSpecificConfPath = "." + File.separator
                + "redpen-conf-" + Locale.getDefault().getLanguage() + ".xml";
        File localeSpecificConfFile = new File(localeSpecificConfPath);
        // create empty configuration file in the current directory
        if (localeSpecificConfFile.createNewFile()) {
            // ensure the file is deleted upon test failure
            localeSpecificConfFile.deleteOnExit();
            // default config path resolves to locale specific config path
            file = Main.resolveConfigLocation(defaultConfPath);
            // .//redpen-conf-[lang].xml exists
            assertNotNull(file);
            // clean up config file in the current directory
            localeSpecificConfFile.delete();
        }

        // skipping test code for $REDPEN_HOME/redpen-conf.xml
        // environment variable cannot be set via Java program
    }

    @Test
    void testMainWithSentenceInput() throws RedPenException {
        String[] args = new String[]{
                "-c", "sample/conf/redpen-conf-en.xml",
                "-s", "this is a pen",
        };
        assertEquals(0, Main.run(args));
    }

    @Test
    void testPlugin() throws Exception {
        String[] args = new String[]{
                "-c", "sample/conf/redpen-conf-plugin.xml",
                "sample/sample-doc/en/sampledoc-en.txt",
                "-l", "1"
        };
        assertEquals(1, Main.run(args));
    }

    @Test
    void testMainWithoutParameters() throws RedPenException {
        String[] args = new String[]{};
        assertEquals(1, Main.run(args));
    }

    @Test
    void testMainWithoutConfig() throws RedPenException {
        String[] args = new String[]{
                "sample/sample-doc/en/sampledoc-en.txt"
        };
        assertEquals(1, Main.run(args));
    }

    @Test
    void testMainWithoutInput() throws RedPenException {
        String[] args = new String[]{
                "-c", "sample/conf/redpen-conf-en.xml",
        };
        assertEquals(1, Main.run(args));
    }

    @Test
    void testHelp() throws RedPenException {
        assertEquals(0, Main.run("-h"));
    }

    @Test
    void testVersion() throws RedPenException {
        assertEquals(0, Main.run("-v"));
    }

    @Test
    void testGuessFormat() throws Exception {
        String[] inputs = new String[]{
                "sample/conf/sampledoc1.adoc",
                "sample/conf/sampledoc2.adoc",
        };
        assertEquals("asciidoc", Main.guessInputFormat(inputs));
    }

    @Test
    void testGuessTwoFormats() throws Exception {
        String[] inputs = new String[]{
                "sample/conf/sampledoc1.md",
                "sample/conf/sampledoc2.adoc",
        };
        assertEquals("plain", Main.guessInputFormat(inputs));
    }

    @Test
    void testGuessFromAbbrebiatedFoamts() throws Exception {
        String[] inputs = new String[]{
                "sample/conf/sampledoc1.md",
                "sample/conf/sampledoc2.markdown",
        };
        assertEquals("markdown", Main.guessInputFormat(inputs));
    }

    @Test
    void testGuessFromNoExtensions() throws Exception {
        String[] inputs = new String[]{
                "sample/conf/sampledoc",
                "sample/conf/sampledoc",
        };
        assertEquals("plain", Main.guessInputFormat(inputs));
    }
}
