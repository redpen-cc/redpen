/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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

import cc.redpen.symbol.AbstractSymbols;
import cc.redpen.symbol.DefaultSymbols;
import cc.redpen.symbol.JapaneseSymbols;
import cc.redpen.symbol.SymbolType;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.tokenizer.RedPenTokenizer;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains Settings used throughout {@link cc.redpen.RedPen}.
 */
public final class Configuration {
    private final SymbolTable symbolTable;
    private final List<ValidatorConfiguration> validatorConfigs =
            new ArrayList<>();
    private String lang;
    private RedPenTokenizer tokenizer;

    /**
     * Constructor.
     *
     */
    Configuration(SymbolTable symbolTable, List<ValidatorConfiguration> validatorConfigs, String lang) {
        if (symbolTable == null) {
            this.symbolTable = new SymbolTable();
        } else {
            this.symbolTable = symbolTable;
        }
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
     * @return language
     */
    public String getLang() {
        return lang;
    }

    /**
     * returns Tokenizer aasociated with this configuration
     * @return tokenizer
     */
    public RedPenTokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Builder class of Configuration.
     */
    public static class Builder {
        private final List<ValidatorConfiguration> validatorConfigs =
                new ArrayList<>();
        private SymbolTable symbolTable;

        private String lang = "en";
        private static SymbolTable loadLanguageDefaultSymbolTable(
                String lang) {
            SymbolTable symbolTable = new SymbolTable();

            AbstractSymbols symbolSettings;
            if (lang.equals("ja")) {
                symbolSettings = JapaneseSymbols.getInstance();
                symbolTable.setLang("ja");
            } else {
                symbolSettings = DefaultSymbols.getInstance();
                symbolTable.setLang("en");
            }

            for (SymbolType symbolName : symbolSettings) {
                Symbol symbol = symbolSettings.get(symbolName);
                symbolTable.override(symbol);
            }
            return symbolTable;
        }

        public Builder setLanguage(String lang) {
            this.symbolTable = loadLanguageDefaultSymbolTable(lang);
            this.lang = lang;
            return this;
        }

        public Builder setSymbol(Symbol symbol) {
            this.symbolTable.override(symbol);
            return this;
        }

        public Builder setSymbol(SymbolType symbolType, String value) {
            this.symbolTable.override(new Symbol(symbolType, value));
            return this;
        }

        public Builder addInvalidPattern(SymbolType symbolType, String invalid) {
            Symbol symbol = this.symbolTable.getSymbol(symbolType);
            symbol.addInvalid(invalid);
            return this;
        }

        public Builder addValidatorConfig(ValidatorConfiguration config) {
            validatorConfigs.add(config);
            return this;
        }

        public Configuration build() {
            if (symbolTable == null) {
                setLanguage("en");
            }
            return new Configuration(this.symbolTable, this.validatorConfigs, this.lang);
        }
    }
}
