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
import cc.redpen.tokenizer.NeologdJapaneseTokenizer;
import cc.redpen.tokenizer.RedPenTokenizer;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidatorFactory;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Contains Settings used throughout {@link cc.redpen.RedPen}.
 */
public class Configuration implements Serializable, Cloneable {
    private SymbolTable symbolTable;
    private List<ValidatorConfiguration> validatorConfigs = new ArrayList<>();
    private final String lang;
    private transient RedPenTokenizer tokenizer;
    private final File home = new File(Optional.ofNullable(System.getProperty("REDPEN_HOME", System.getenv("REDPEN_HOME"))).orElse(""));
    private final File base;
    private final boolean secure;

    /**
     * @return default supported languages and variants that can be used with {@link #builder(String)}
     */
    public static List<String> getDefaultConfigKeys() {
        return asList("en", "ja", "ja.hankaku", "ja.zenkaku2", "ru");
    }

    Configuration(File base, SymbolTable symbolTable, List<ValidatorConfiguration> validatorConfigs, String lang, boolean secure) {
        this.base = base;
        this.symbolTable = symbolTable;

        this.validatorConfigs.addAll(validatorConfigs);
        this.lang = lang;
        this.secure = secure;
        initTokenizer();
    }

    private void initTokenizer() {
        this.tokenizer = lang.equals("ja") ? new NeologdJapaneseTokenizer() : new WhiteSpaceTokenizer();
    }

    /**
     * Get SymbolTable.
     *
     * @return SymbolTable
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    /**
     * Get validator configurations.
     *
     * @return list of configurations
     */
    public List<ValidatorConfiguration> getValidatorConfigs() {
        return validatorConfigs;
    }

    /**
     * returns language targeted by this configuration
     *
     * @return language
     */
    public String getLang() {
        return lang;
    }

    /**
     * returns symbol table variant targeted by this configuration
     *
     * @return variant, or empty String if none
     */
    public String getVariant() {
        return getSymbolTable().getVariant();
    }

    /**
     * returns Tokenizer associated with this configuration
     *
     * @return tokenizer
     */
    public RedPenTokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * @return unique key for this lang and type combination
     */
    public String getKey() {
        if (getLang().equals("ja") && getVariant().equals("zenkaku")) return "ja";
        return getLang() + (isEmpty(getVariant()) ? "" : "." + getVariant());
    }

    /**
     * @return RedPen home directory, relative to which custom resources are evaluated
     */
    public File getHome() {
        return home;
    }

    /**
     * @return RedPen configuration base directory, relative to which custom resources are evaluated
     */
    public File getBase() {
        return base;
    }

    /**
     * Finds file relative to either working directory, base directory or $REDPEN_HOME
     * @param relativePath of file to find
     * @return resolved file if it exists
     * @throws RedPenException if file doesn't exist in either place
     */
    public File findFile(String relativePath) throws RedPenException {
        File file;
        if (!secure) {
            file = new File(relativePath);
            if (file.exists()) return file;
        }

        if (base != null) {
            file = new File(base, relativePath);
            if (secureExists(file, base)) return file;
        }

        file = new File(home, relativePath);
        if (secureExists(file, home)) return file;

        throw new RedPenException(relativePath + " is not under " +
          (!secure ? "working directory (" + new File("").getAbsoluteFile() + "), " : "") +
          (base != null ? "base (" + base + "), " : "") +
          "$REDPEN_HOME (" + home.getAbsolutePath() + ").");
    }

    private boolean secureExists(File file, File base) {
        try {
            return file.exists() && (!secure || file.getCanonicalPath().startsWith(base.getCanonicalPath()));
        }
        catch (IOException e) {
            return false;
        }
    }

    public boolean isSecure() {
        return secure;
    }

    /**
     * @return a deep copy of this configuration
     */
    @Override public Configuration clone() {
        Configuration clone;
        try {
            clone = (Configuration)super.clone();
            clone.validatorConfigs = validatorConfigs.stream().map(ValidatorConfiguration::clone).collect(toList());
            clone.symbolTable = symbolTable.clone();
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initTokenizer();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration)) return false;
        Configuration that = (Configuration)o;
        return Objects.equals(lang, that.lang) &&
          Objects.equals(symbolTable, that.symbolTable) &&
          Objects.equals(validatorConfigs, that.validatorConfigs);
    }

    @Override public int hashCode() {
        return getKey().hashCode();
    }

    @Override public String toString() {
        return "Configuration{" +
          "lang='" + lang + '\'' +
          ", tokenizer=" + tokenizer +
          ", validatorConfigs=" + validatorConfigs +
          ", symbolTable=" + symbolTable +
          '}';
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public static ConfigurationBuilder builder(String key) {
        int dotPos = key.indexOf('.');
        ConfigurationBuilder builder = new ConfigurationBuilder().setLanguage(dotPos > 0 ? key.substring(0, dotPos) : key);
        if (dotPos > 0) builder.setVariant(key.substring(dotPos+1));
        return builder;
    }

    /**
     * Builder class of Configuration.
     */
    public static class ConfigurationBuilder {
        private final List<ValidatorConfiguration> validatorConfigs = new ArrayList<>();
        private final List<Symbol> customSymbols = new ArrayList<>();
        private boolean built = false;

        private String lang = "en";
        private Optional<String> variant = Optional.empty();
        private File base;
        private boolean secure;

        private void checkBuilt() {
            if (built) throw new IllegalStateException("Configuration already built.");
        }

        public ConfigurationBuilder setLanguage(String lang) {
            checkBuilt();
            this.lang = lang;
            return this;
        }

        public ConfigurationBuilder setBaseDir(File base) {
            checkBuilt();
            this.base = base;
            return this;
        }

        public ConfigurationBuilder addSymbol(Symbol symbol) {
            checkBuilt();
            customSymbols.add(symbol);
            return this;
        }

        public ConfigurationBuilder addValidatorConfig(ValidatorConfiguration config) {
            checkBuilt();
            validatorConfigs.add(config);
            return this;
        }

        public ConfigurationBuilder addAvailableValidatorConfigs() {
            checkBuilt();
            validatorConfigs.addAll(ValidatorFactory.getConfigurations(lang));
            return this;
        }

        public ConfigurationBuilder setVariant(String variant) {
            checkBuilt();
            this.variant = Optional.of(variant);
            return this;
        }

      /**
       * Enables secure mode suitable for servers, where validator properties can come from end-users.
       */
        public ConfigurationBuilder secure() {
            checkBuilt();
            secure = true;
            return this;
        }

        public Configuration build() {
            checkBuilt();
            built = true;
            return new Configuration(base, new SymbolTable(lang, variant, customSymbols), this.validatorConfigs, this.lang, this.secure);
        }
    }
}
