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
package cc.redpen.validator;


import cc.redpen.RedPenException;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.parser.LineOffset;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.DictionaryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * Validate input document.
 */
public abstract class Validator {

    private static final Logger LOG = LoggerFactory.getLogger(Validator.class);
    private final static ResourceBundle.Control fallbackControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);

    private Optional<ResourceBundle> errorMessages = Optional.empty();
    private ValidatorConfiguration config;
    private SymbolTable symbolTable;

    public Validator() {
        setLocale(Locale.getDefault());
    }

    /**
     * Process input blocks before run validation. This method is used to store
     * the information needed to run Validator before the validation process.
     *
     * @param sentence input sentence
     */
    public void preValidate(Sentence sentence) {
    }

    /**
     * Process input blocks before run validation. This method is used to store
     * the information needed to run Validator before the validation process.
     *
     * @param section input section
     */
    public void preValidate(Section section) {
    }

    /**
     * validate the input document and returns the invalid points.
     * {@link cc.redpen.validator.Validator} provides empty implementation. Validator implementation validates documents can override this method.
     *
     * @param errorList list of validation errors. Validator implementations will add Validation errors to this list.
     * @param document  input
     */
    public void validate(List<ValidationError> errorList, Document document) {
    }

    /**
     * validate the input document and returns the invalid points.
     * {@link cc.redpen.validator.Validator} provides empty implementation. Validator implementation validates sentences can override this method.
     *
     * @param errors   list of validation errors. Validator implementations will add Validation errors to this list.
     * @param sentence input
     */
    public void validate(List<ValidationError> errors, Sentence sentence) {
    }

    /**
     * validate the input document and returns the invalid points.
     * {@link cc.redpen.validator.Validator} provides empty implementation. Validator implementation validates sections can override this method.
     *
     * @param errors  list of validation errors. Validator implementations will add Validation errors to this list.
     * @param section input
     */
    public void validate(List<ValidationError> errors, Section section) {
    }

    /**
     * Return an array of languages supported by this validator
     * {@link cc.redpen.validator.Validator} provides empty implementation. Validator implementation validates sections can override this method.
     *
     * @return an array of the languages supported by this validator. An empty list implies there are no restrictions on the languages supported by this validator.
     */
    public List<String> getSupportedLanguages() {
        return Collections.emptyList();
    }

    final void preInit(ValidatorConfiguration config, SymbolTable symbolTable) throws RedPenException {
        this.config = config;
        this.symbolTable = symbolTable;
        init();
    }

    void setLocale(Locale locale) {
        try {
            // getPackage() would return null for default package
            String packageName = this.getClass().getPackage() != null ? this.getClass().getPackage().getName() : "";
            errorMessages = Optional.ofNullable(ResourceBundle.getBundle(packageName + ".error-messages", locale, fallbackControl));
        } catch (MissingResourceException ignore) {
        }
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

    protected double getConfigAttributeAsDouble(String attributeName, double defaultValue) {
        String value = config.getAttribute(attributeName);
        if (value != null) {
            LOG.info("{} is set to {}", attributeName, value);
            return Double.valueOf(value);
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
     * @param args              objects to format
     * @return ValidationError with localized message
     */
    protected ValidationError createValidationError(Sentence sentenceWithError, Object... args) {
        return new ValidationError(this.getClass(), getLocalizedErrorMessage(Optional.empty(), args), sentenceWithError);
    }

    /**
     * create a ValidationError for the specified position with specified message key
     *
     * @param messageKey        messageKey
     * @param sentenceWithError sentence
     * @param args              objects to format
     * @return ValidationError with localized message
     */
    protected ValidationError createValidationError(String messageKey, Sentence sentenceWithError, Object... args) {
        return new ValidationError(this.getClass(), getLocalizedErrorMessage(Optional.of(messageKey), args), sentenceWithError);
    }

    /**
     * create a ValidationError using the details within the given token
     *
     * @param sentenceWithError sentence
     * @param token             the TokenElement that has the error
     * @return ValidationError with localized message
     */
    protected ValidationError createValidationErrorFromToken(Sentence sentenceWithError, TokenElement token) {
        return createValidationErrorWithPosition(
                sentenceWithError,
                sentenceWithError.getOffset(token.getOffset()),
                sentenceWithError.getOffset(token.getOffset() + token.getSurface().length()),
                token.getSurface()
        );
    }

    /**
     * create a ValidationError for the specified position with default error message
     *
     * @param sentenceWithError sentence
     * @param start             start position
     * @param end               end position
     * @param args              objects to format
     * @return ValidationError with localized message
     */
    protected ValidationError createValidationErrorWithPosition(Sentence sentenceWithError,
                                                                Optional<LineOffset> start, Optional<LineOffset> end, Object... args) {
        return new ValidationError(this.getClass(), getLocalizedErrorMessage(Optional.empty(), args), sentenceWithError, start, end);
    }

    /**
     * create a ValidationError for the specified position with specified message key
     *
     * @param messageKey        messageKey
     * @param sentenceWithError sentence
     * @param start             start position
     * @param end               end position
     * @param args              objects to format
     * @return ValidationError with localized message
     */
    protected ValidationError createValidationErrorWithPosition(String messageKey, Sentence sentenceWithError,
                                                                Optional<LineOffset> start, Optional<LineOffset> end, Object... args) {
        return new ValidationError(this.getClass(), getLocalizedErrorMessage(Optional.of(messageKey), args), sentenceWithError, start, end);
    }

    /**
     * returns localized error message for the given key formatted with argument
     *
     * @param key  message key
     * @param args objects to format
     * @return localized error message
     */
    private String getLocalizedErrorMessage(Optional<String> key, Object... args) {
        if (errorMessages.isPresent()) {
            String suffix = key.isPresent() ? "." + key.get() : "";
            return MessageFormat.format(errorMessages.get().getString(this.getClass().getSimpleName() + suffix), args);
        } else {
            throw new AssertionError("message resource not found.");
        }
    }

    protected Set<String> loadWordListFromResource(String path, String dictionaryName) throws RedPenException {
        return DictionaryLoader.WORD.loadCachedFromResource(path, dictionaryName);
    }
    protected Set<String> loadLowercaseWordListFromResource(String path, String dictionaryName) throws RedPenException {
        return DictionaryLoader.WORD_LOWERCASE.loadCachedFromResource(path, dictionaryName);
    }
    protected Set<String> loadWordListFromFile(String filePath, String dictionaryName) throws RedPenException {
        return DictionaryLoader.WORD.loadCachedFromFile(filePath, dictionaryName);
    }
    protected Set<String> loadLowercaseWordListFromFlie(String path, String dictionaryName) throws RedPenException {
        return DictionaryLoader.WORD_LOWERCASE.loadCachedFromFile(path, dictionaryName);
    }
}
