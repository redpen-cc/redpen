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

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testMain() throws RedPenException {
        String[] args = new String[]{
                "-c", "sample/conf/redpen-conf-en.xml",
                "sample/sample-doc/en/sampledoc-en.txt"
        };
        Main.run(args);
    }

    @Test
    public void testDefaultConfigFile() throws RedPenException, IOException {
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
    public void testPlugin() throws Exception {
        String[] args = new String[]{
                "-c", "sample/conf/redpen-conf-plugin.xml",
                "sample/sample-doc/en/sampledoc-en.txt",
                "-l", "1"
        };
        assertEquals(1, Main.run(args));
    }

    @Test
    public void testMainWithoutParameters() throws RedPenException {
        String[] args = new String[]{};
        assertEquals(1, Main.run(args));
    }

    @Test
    public void testMainWithoutConfig() throws RedPenException {
        String[] args = new String[]{
                "sample/sample-doc/en/sampledoc-en.txt"
        };
        assertEquals(1, Main.run(args));
    }

    @Test
    public void testMainWithoutInput() throws RedPenException {
        String[] args = new String[]{
                "-c", "sample/conf/redpen-conf-en.xml",
        };
        assertEquals(1, Main.run(args));
    }

    @Test
    public void testHelp() throws RedPenException {
        assertEquals(0, Main.run("-h"));
    }

    @Test
    public void testVersion() throws RedPenException {
        assertEquals(0, Main.run("-v"));
    }
}
