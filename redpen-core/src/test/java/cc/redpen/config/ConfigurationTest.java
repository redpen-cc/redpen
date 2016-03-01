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
package cc.redpen.config;

import cc.redpen.RedPenException;
import org.junit.Test;

import java.io.*;
import java.util.Optional;

import static cc.redpen.config.SymbolType.AMPERSAND;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

/**
 * Test for Configuration Structure
 */
public class ConfigurationTest {

    @Test
    public void testSentenceValidatorConfiguration() throws Exception {
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("SentenceLength"))
                .addValidatorConfig(new ValidatorConfiguration("InvalidExpression"))
                .addValidatorConfig(new ValidatorConfiguration("SpaceBeginningOfSentence"))
                .addValidatorConfig(new ValidatorConfiguration("CommaNumber"))
                .addValidatorConfig(new ValidatorConfiguration("WordNumber"))
                .addValidatorConfig(new ValidatorConfiguration("SuggestExpression"))
                .addValidatorConfig(new ValidatorConfiguration("InvalidCharacter"))
                .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
                .addValidatorConfig(new ValidatorConfiguration("KatakanaEndHyphen"))
                .addValidatorConfig(new ValidatorConfiguration("KatakanaSpellCheck"))
                .build();
        assertEquals(10, configuration.getValidatorConfigs().size());
    }

    @Test
    public void testInvalidValidatorConfiguration() {
        // NOTE: not throw a exception even when adding a non exist validator.
        // The errors occurs when creating the added non existing validator instance.
        try {
            Configuration.builder()
                    .addValidatorConfig(new ValidatorConfiguration("ThereIsNoSuchValidator")).build();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSectionValidatorConfiguration() throws Exception {
        Configuration configuration = Configuration.builder().addValidatorConfig(new ValidatorConfiguration("SectionLength"))
                .addValidatorConfig(new ValidatorConfiguration("MaxParagraphNumber"))
                .addValidatorConfig(new ValidatorConfiguration("ParagraphStartWith")).build();
        assertEquals(3, configuration.getValidatorConfigs().size());
    }

    @Test
    public void testSymbolTableWithoutLanguageSetting() throws Exception {
        Configuration configuration = Configuration.builder().build(); // NOTE: load "en" setting when lang is not specified
        assertEquals("en", configuration.getLang());
        assertNotNull(configuration.getLang());
    }

    @Test
    public void keyIsLangAndType() throws Exception {
        SymbolTable symbolTable = new SymbolTable("ja", Optional.of("hankaku"), emptyList());
        assertEquals("ja.hankaku", new Configuration(new File(""), symbolTable, emptyList(), "ja", false).getKey());
    }

    @Test
    public void keyIsLangOnlyIfTypeIsMissing() throws Exception {
        SymbolTable symbolTable = new SymbolTable("en", Optional.empty(), emptyList());
        assertEquals("en", new Configuration(new File(""), symbolTable, emptyList(), "en", false).getKey());
    }

    @Test
    public void keyIsLangOnlyForZenkaku() throws Exception {
        SymbolTable symbolTable = new SymbolTable("ja", Optional.of("zenkaku"), emptyList());
        assertEquals("ja", new Configuration(new File(""), symbolTable, emptyList(), "ja", false).getKey());
    }

    @Test
    public void homeIsWorkingDirectoryByDefault() throws Exception {
        System.clearProperty("REDPEN_HOME");
        assertEquals(new File(""), Configuration.builder().build().getHome());
    }

    @Test
    public void homeIsResolvedFromSystemPropertyOrEnvironment() throws Exception {
        System.setProperty("REDPEN_HOME", "/foo");
        assertEquals(new File("/foo"), Configuration.builder().build().getHome());
    }

    @Test
    public void findFileLooksInWorkingDirectoryFirst() throws Exception {
        String localFile = new File(".").list()[0];
        assertEquals(new File(localFile), Configuration.builder().build().findFile(localFile));
    }

    @Test
    public void findFileLooksInConfigBaseDirectorySecond() throws Exception {
        assertEquals(new File("src/main"), Configuration.builder().setBaseDir(new File("src")).build().findFile("main"));
    }

    @Test
    public void findFileLooksInRedPenHomeDirectoryThird() throws Exception {
        System.setProperty("REDPEN_HOME", "src");
        assertEquals(new File("src/main"), Configuration.builder().build().findFile("main"));
    }

    @Test
    public void findFileFailsIfFileNotFound() throws Exception {
        try {
            System.setProperty("REDPEN_HOME", "src");
            Configuration.builder().build().findFile("hello.xml");
            fail("Expecting RedPenException");
        }
        catch (RedPenException e) {
            assertEquals("hello.xml is not under working directory (" + new File("").getAbsoluteFile() + "), $REDPEN_HOME (" + new File("src").getAbsoluteFile() + ").", e.getMessage());
        }
    }

    @Test
    public void findFileFailsIfFileNotFound_basePathPresent() throws Exception {
        try {
            System.setProperty("REDPEN_HOME", "src");
            Configuration.builder().setBaseDir(new File("some/base/dir")).build().findFile("hello.xml");
            fail("Expecting RedPenException");
        }
        catch (RedPenException e) {
            assertEquals("hello.xml is not under working directory (" + new File("").getAbsoluteFile() + "), base (some/base/dir), $REDPEN_HOME (" + new File("src").getAbsoluteFile() + ").", e.getMessage());
        }
    }

    @Test
    public void findFile_workingDirectorySecureMode() throws Exception {
        String localFile = new File(".").list()[0];
        try {
            System.setProperty("REDPEN_HOME", "");
            Configuration.builder().secure().build().findFile(localFile);
            fail("Secure mode should not allow files from working directory");
        }
        catch (RedPenException e) {
            assertEquals(localFile + " is not under $REDPEN_HOME (" + new File("").getAbsoluteFile() + ").", e.getMessage());
        }
    }

    @Test
    public void findFile_secureMode() throws Exception {
        try {
            System.setProperty("REDPEN_HOME", "");
            Configuration.builder().secure().build().findFile("/etc/passwd");
            fail("Secure mode should not allow file locations outside config paths");
        }
        catch (RedPenException e) {
            assertEquals("/etc/passwd is not under $REDPEN_HOME (" + new File("").getAbsoluteFile() + ").", e.getMessage());
        }
    }

    @Test
    public void canBeCloned() throws Exception {
        Configuration conf = Configuration.builder("ja.hankaku")
          .addValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();

        Configuration clone = conf.clone();
        assertNotSame(conf, clone);
        assertEquals(conf.getLang(), clone.getLang());
        assertEquals(conf.getVariant(), clone.getVariant());

        assertNotSame(conf.getValidatorConfigs(), clone.getValidatorConfigs());
        assertNotSame(conf.getValidatorConfigs().get(0), clone.getValidatorConfigs().get(0));
        assertEquals(conf.getValidatorConfigs(), clone.getValidatorConfigs());

        assertNotSame(conf.getSymbolTable(), clone.getSymbolTable());
        assertEquals(conf.getSymbolTable(), clone.getSymbolTable());
    }

    @Test
    public void equals() throws Exception {
        Configuration conf = Configuration.builder("ja.hankaku")
          .addValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();

        Configuration clone = conf.clone();
        assertEquals(conf, clone);
        assertEquals(conf.hashCode(), clone.hashCode());

        clone.getValidatorConfigs().remove(0);
        assertFalse(conf.equals(clone));

        clone = conf.clone();
        clone.getSymbolTable().overrideSymbol(new Symbol(AMPERSAND, '^'));
        assertFalse(conf.equals(clone));
    }

    @Test
    public void serializable() throws Exception {
        Configuration conf = Configuration.builder("ja.hankaku")
          .addValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(conf);

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        Configuration conf2 = (Configuration)in.readObject();

        assertEquals(conf, conf2);
        assertEquals(conf.getTokenizer().getClass(), conf2.getTokenizer().getClass());
    }

    @Test
    public void addAvailableValidatorsForLanguage() throws Exception {
        Configuration ja = Configuration.builder("ja").addAvailableValidatorConfigs().build();
        assertTrue(ja.getValidatorConfigs().stream().anyMatch(v -> v.getConfigurationName().equals("SentenceLength")));
        assertTrue(ja.getValidatorConfigs().stream().anyMatch(v -> v.getConfigurationName().equals("HankakuKana")));

        Configuration en = Configuration.builder("en").addAvailableValidatorConfigs().build();
        assertTrue(en.getValidatorConfigs().stream().anyMatch(v -> v.getConfigurationName().equals("SentenceLength")));
        assertFalse(en.getValidatorConfigs().stream().anyMatch(v -> v.getConfigurationName().equals("HankakuKana")));

        ValidatorConfiguration sentenceLength = en.getValidatorConfigs().stream().filter(v -> v.getConfigurationName().equals("SentenceLength")).findAny().get();
        assertEquals("120", sentenceLength.getAttribute("max_len"));
    }
}
