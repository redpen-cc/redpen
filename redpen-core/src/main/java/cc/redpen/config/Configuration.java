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

import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.tokenizer.RedPenTokenizer;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Contains Settings used throughout {@link cc.redpen.RedPen}.
 */
public final class Configuration {
    private final SymbolTable symbolTable;
    private final List<ValidatorConfiguration> validatorConfigs = new ArrayList<>();
    private String lang;
    private RedPenTokenizer tokenizer;

    /**
     * Constructor.
     */
    Configuration(SymbolTable symbolTable, List<ValidatorConfiguration> validatorConfigs, String lang) {
        this.symbolTable = symbolTable;

        this.validatorConfigs.addAll(validatorConfigs);
        this.lang = lang;
        switch (lang) {
            case "ja":
                this.tokenizer = new JapaneseTokenizer();
                break;
            default:
                this.tokenizer = new WhiteSpaceTokenizer();
        }
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
     * returns Tokenizer aasociated with this configuration
     *
     * @return tokenizer
     */
    public RedPenTokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Builder class of Configuration.
     */
    public static class ConfigurationBuilder {
        private final List<ValidatorConfiguration> validatorConfigs = new ArrayList<>();
        private final List<Symbol> customSymbols = new ArrayList<>();

        private String lang = "en";
        private Optional<String> type = Optional.empty();

        public ConfigurationBuilder setLanguage(String lang) {
            this.lang = lang;
            return this;
        }

        public ConfigurationBuilder setSymbol(Symbol symbol) {
            customSymbols.add(symbol);
            return this;
        }

        public ConfigurationBuilder addValidatorConfig(ValidatorConfiguration config) {
            validatorConfigs.add(config);
            return this;
        }

        public ConfigurationBuilder setType(String type) {
            this.type = Optional.of(type);
            return this;
        }

        public Configuration build() {
            return new Configuration(new SymbolTable(lang, type, customSymbols), this.validatorConfigs, this.lang);
        }
    }
}
