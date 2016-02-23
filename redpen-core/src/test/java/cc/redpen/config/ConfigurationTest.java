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

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        assertEquals("ja.hankaku", new Configuration(symbolTable, emptyList(), "ja").getKey());
    }

    @Test
    public void keyIsLangOnlyIfTypeIsMissing() throws Exception {
        SymbolTable symbolTable = new SymbolTable("en", Optional.empty(), emptyList());
        assertEquals("en", new Configuration(symbolTable, emptyList(), "en").getKey());
    }

    @Test
    public void keyIsLangOnlyForZenkaku() throws Exception {
        SymbolTable symbolTable = new SymbolTable("ja", Optional.of("zenkaku"), emptyList());
        assertEquals("ja", new Configuration(symbolTable, emptyList(), "ja").getKey());
    }

    @Test
    public void canBeCloned() throws Exception {
        Configuration conf = Configuration.builder("ja")
          .setVariant("hankaku")
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
        Configuration conf = Configuration.builder("ja")
          .setVariant("hankaku")
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
        Configuration conf = Configuration.builder("ja")
          .setVariant("hankaku")
          .addValidatorConfig(new ValidatorConfiguration("SentenceLength")).build();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(conf);

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        Configuration conf2 = (Configuration)in.readObject();

        assertEquals(conf, conf2);
        assertEquals(conf.getTokenizer().getClass(), conf2.getTokenizer().getClass());
    }
}
