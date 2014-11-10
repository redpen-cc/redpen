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

            for (String symbolName : symbolSettings) {
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

        public Builder setSymbol(String name, String value) {
            this.symbolTable.override(new Symbol(name, value));
            return this;
        }

        public Builder addInvalidPattern(String name, String invalid) {
            Symbol symbol = this.symbolTable.getSymbol(name);
            symbol.addInvalid(invalid);
            return this;
        }

        public Builder addValidatorConfig(ValidatorConfiguration config) {
            validatorConfigs.add(config);
            return this;
        }

        public Configuration build() {
            return new Configuration(this.symbolTable, this.validatorConfigs, this.lang);
        }
    }
}
