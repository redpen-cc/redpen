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

    private ResourceBundle errorMessages = null;
    private ValidatorConfiguration config;
    private SymbolTable symbolTable;

    public Validator() {
        setLocale(Locale.getDefault());
    }

    private List<ValidationError> errors;

    public void setErrorList(List<ValidationError> errors){
        this.errors = errors;
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
     * @param document  input
     */
    public void validate(Document document) {
    }

    /**
     * validate the input document and returns the invalid points.
     * {@link cc.redpen.validator.Validator} provides empty implementation. Validator implementation validates sentences can override this method.
     *
     * @param sentence input
     */
    public void validate(Sentence sentence) {
    }

    /**
     * validate the input document and returns the invalid points.
     * {@link cc.redpen.validator.Validator} provides empty implementation. Validator implementation validates sections can override this method.
     *
     * @param section input
     */
    public void validate(Section section) {
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
        // getPackage() would return null for default package
        String packageName = this.getClass().getPackage() != null ? this.getClass().getPackage().getName() : "";
        try {
            errorMessages = ResourceBundle.getBundle(packageName + "." + this.getClass().getSimpleName(), locale, fallbackControl);
        } catch (MissingResourceException ignore) {
            try {
                errorMessages = ResourceBundle.getBundle(packageName + ".error-messages", locale, fallbackControl);
            } catch (MissingResourceException ignoreAgain) {
            }
        }
    }

    /**
     * Return the configuration attributes
     *
     * @return a map of configuration attributes to their values
     */
    public Map<String, String> getConfigAttributes() {
        return config.getAttributes();
    }

    /**
     * Validation initialization, called after the configuration and symbol tables have been assigned
     *
     * @throws RedPenException
     */
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
     */
    protected void addValidationError(Sentence sentenceWithError, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(Optional.empty(), args), sentenceWithError));
    }

    /**
     * create a ValidationError for the specified position with specified message key
     *
     * @param messageKey        messageKey
     * @param sentenceWithError sentence
     * @param args              objects to format
     */
    protected void addValidationError(String messageKey, Sentence sentenceWithError, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(Optional.of(messageKey), args), sentenceWithError));
    }

    /**
     * create a ValidationError using the details within the given token
     *
     * @param sentenceWithError sentence
     * @param token             the TokenElement that has the error
     */
    protected void addValidationErrorFromToken(Sentence sentenceWithError, TokenElement token) {
        addValidationErrorWithPosition(
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
     */
    protected void addValidationErrorWithPosition(Sentence sentenceWithError,
                                                  Optional<LineOffset> start, Optional<LineOffset> end, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(Optional.empty(), args), sentenceWithError, start, end));
    }

    /**
     * create a ValidationError for the specified position with specified message key
     *
     * @param messageKey        messageKey
     * @param sentenceWithError sentence
     * @param start             start position
     * @param end               end position
     * @param args              objects to format
     */
    protected void addValidationErrorWithPosition(String messageKey, Sentence sentenceWithError,
                                                  Optional<LineOffset> start, Optional<LineOffset> end, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(Optional.of(messageKey), args), sentenceWithError, start, end));
    }

    /**
     * returns localized error message for the given key formatted with argument
     *
     * @param key  message key
     * @param args objects to format
     * @return localized error message
     */
    protected String getLocalizedErrorMessage(Optional<String> key, Object... args) {
        if (errorMessages != null) {
            String suffix = key.isPresent() ? "." + key.get() : "";
            return MessageFormat.format(errorMessages.getString(this.getClass().getSimpleName() + suffix), args);
        } else {
            throw new AssertionError("message resource not found.");
        }
    }

    /**
     * Resource Extractor loads key-value dictionary
     */
    protected final static DictionaryLoader<Map<String, String>> KEY_VALUE =
            new DictionaryLoader<>(HashMap::new, (map, line) -> {
                String[] result = line.split("\t");
                if (result.length == 2) {
                    map.put(result[0], result[1]);
                } else {
                    LOG.error("Skip to load line... Invalid line: " + line);
                }
            });
    /**
     * Resource Extractor loads word list
     */
    protected final static DictionaryLoader<Set<String>> WORD_LIST =
            new DictionaryLoader<>(HashSet::new, Set::add);
    /**
     * Resource Extractor loads word list while lowercasting lines
     */
    protected final static DictionaryLoader<Set<String>> WORD_LIST_LOWERCASED =
            new DictionaryLoader<>(HashSet::new, (set, line) -> set.add(line.toLowerCase()));
}
