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
package cc.redpen.validator;


import cc.redpen.RedPenException;
import cc.redpen.ValidationError;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Sentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Validate input document.
 */
public abstract class Validator<E> {
    private static final Logger LOG =
            LoggerFactory.getLogger(Validator.class);
    ResourceBundle errorMessages;
    private ValidatorConfiguration config;
    private SymbolTable symbolTable;

    /**
     * validate the input document and returns the invalid points.
     *
     * @param block input
     * @return List of ValidationError
     */
    abstract public List<ValidationError> validate(E block);

    final void preInit(ValidatorConfiguration config, SymbolTable symbolTable) throws RedPenException {
        this.config = config;
        this.symbolTable = symbolTable;
        setLocale(Locale.getDefault());
        init();
    }

    void setLocale(Locale locale) {
        errorMessages = ResourceBundle.getBundle(this.getClass().getPackage().getName() + ".error-messages", locale);
    }

    protected void init() throws RedPenException {
    }

    protected Optional<String> getConfigAttribute(String attributeName) {
        return Optional.ofNullable(config.getAttribute(attributeName));
    }

    protected String getConfigAttribute(String attributeName, String defaultValue) {
        String value = config.getAttribute(attributeName);
        if (value != null) {
            LOG.info("{} is set to {}", attributeName, value);
            return value;
        } else {
            LOG.info("{} is not set. Use default value of {}", attributeName, defaultValue);
            return defaultValue;
        }
    }


    protected int getConfigAttributeAsInt(String attributeName, int defaultValue) {
        String value = config.getAttribute(attributeName);
        if (value != null) {
            LOG.info("{} is set to {}", attributeName, value);
            return Integer.valueOf(value);
        } else {
            LOG.info("{} is not set. Use default value of {}", attributeName, defaultValue);
            return defaultValue;
        }
    }

    protected boolean getConfigAttributeAsBoolean(String attributeName, boolean defaultValue) {
        String value = config.getAttribute(attributeName);
        if (value != null) {
            LOG.info("{} is set to {}", attributeName, value);
            return Boolean.valueOf(value);
        } else {
            LOG.info("{} is not set. Use default value of {}", attributeName, defaultValue);
            return defaultValue;
        }
    }

    protected SymbolTable getSymbolTable() {
        return symbolTable;
    }

    /**
     * create a ValidationError for the specified position with default error message
     *
     * @param sentenceWithError sentence
     * @param args
     * @return
     */
    protected ValidationError createValidationError(Sentence sentenceWithError, Object... args) {
        return new ValidationError(this.getClass(), getLocalizedErrorMessage(args), sentenceWithError);

    }

    private String getLocalizedErrorMessage(Object... args) {
        return MessageFormat.format(errorMessages.getString(this.getClass().getSimpleName()), args);
    }

    private String getLocalizedErrorMessage(String key, Object... args) {
        return MessageFormat.format(errorMessages.getString(this.getClass().getSimpleName() + "." + key), args);
    }

}
