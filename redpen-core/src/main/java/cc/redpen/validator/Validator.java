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
import cc.redpen.config.Configuration;
import cc.redpen.config.SymbolTable;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.parser.LineOffset;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.DictionaryLoader;
import cc.redpen.util.RuleExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.ResourceBundle.Control.FORMAT_DEFAULT;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Validate input document.
 */
public abstract class Validator {
    private static final Logger LOG = LoggerFactory.getLogger(Validator.class);
    private final static ResourceBundle.Control fallbackControl = ResourceBundle.Control.getNoFallbackControl(FORMAT_DEFAULT);

    private Map<String, Object> attributes;
    private ResourceBundle errorMessages = null;
    private ValidatorConfiguration config;
    private Configuration globalConfig;

    public Validator() {
        this(new Object[0]);
    }

    /**
     * @param keyValues String key and Object value pairs for supported config attributes.
     */
    public Validator(Object...keyValues) {
        setLocale(Locale.getDefault());
        attributes = new HashMap<>();
        if (keyValues.length % 2 != 0) throw new IllegalArgumentException("Not enough values specified");
        for (int i = 0; i < keyValues.length; i+=2) {
            attributes.put(keyValues[i].toString(), keyValues[i+1]);
        }
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

    public final void preInit(ValidatorConfiguration config, Configuration globalConfig) throws RedPenException {
        this.config = config;
        this.globalConfig = globalConfig;
        initAttributes(config);
        init();
    }

    private void initAttributes(ValidatorConfiguration config) {
        attributes.forEach((name, defaultValue) -> {
            String value = config.getAttribute(name);
            if (value == null) return;
            if (defaultValue instanceof Integer)
                attributes.put(name, Integer.valueOf(value));
            else if (defaultValue instanceof Float)
                attributes.put(name, Float.valueOf(value));
            else if (defaultValue instanceof Boolean)
                attributes.put(name, Boolean.valueOf(value));
            else if (defaultValue instanceof Set)
                attributes.put(name, isEmpty(value) ? defaultValue : asList((value).split(",")).stream().map(String::toLowerCase).collect(toSet()));
            else
                attributes.put(name, value);
        });
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
     * @throws RedPenException when failed to initialize
     */
    protected void init() throws RedPenException {
    }

    protected int getIntAttribute(String name) {
        return (int)attributes.get(name);
    }

    protected float getFloatAttribute(String name) {
        return (float)attributes.get(name);
    }

    protected String getStringAttribute(String name) {
        return (String)attributes.get(name);
    }

    protected boolean getBooleanAttribute(String name) {
        return (boolean)attributes.get(name);
    }

    @SuppressWarnings("unchecked")
    protected Set<String> getSetAttribute(String name) {
        return (Set) attributes.get(name);
    }

    @Deprecated
    protected Optional<String> getConfigAttribute(String attributeName) {
        return Optional.ofNullable(config.getAttribute(attributeName));
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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
        return globalConfig.getSymbolTable();
    }

    protected File findFile(String relativePath) throws RedPenException {
        return globalConfig.findFile(relativePath);
    }

    /**
     * create a ValidationError for the specified position with specified message
     *
     * @param message        message
     * @param sentenceWithError sentence
     */
    protected void addError(String message, Sentence sentenceWithError) {
        errors.add(new ValidationError(this.getClass(), message, sentenceWithError));
    }

    /**
     * create a ValidationError for the specified position with specified message
     *
     * @param message        message
     * @param sentenceWithError sentence
     * @param start             start position
     * @param end               end position
     */
    protected void addErrorWithPosition(String message, Sentence sentenceWithError,
                                        int start, int end) {
        errors.add(new ValidationError(this.getClass(), message, sentenceWithError, start, end));
    }



    /**
     * create a ValidationError for the specified position with localized default error message
     *
     * @param sentenceWithError sentence
     * @param args              objects to format
     */
    protected void addLocalizedError(Sentence sentenceWithError, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(null, args), sentenceWithError));
    }

    /**
     * create a ValidationError for the specified position with localized message with specified message key
     *
     * @param messageKey        messageKey
     * @param sentenceWithError sentence
     * @param args              objects to format
     */
    protected void addLocalizedError(String messageKey, Sentence sentenceWithError, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(messageKey, args), sentenceWithError));
    }

    /**
     * create a ValidationError using the details within the given token &amp; localized message
     *
     * @param sentenceWithError sentence
     * @param token             the TokenElement that has the error
     */
    protected void addLocalizedErrorFromToken(Sentence sentenceWithError, TokenElement token) {
        addLocalizedErrorWithPosition(
                sentenceWithError,
                token.getOffset(),
                token.getOffset() + token.getSurface().length(),
                token.getSurface()
        );
    }

    /**
     * create a ValidationError for the specified position with default localized error message
     *
     * @param sentenceWithError sentence
     * @param start             start position in parsed sentence
     * @param end               end position in parsed sentence
     * @param args              objects to format
     */
    protected void addLocalizedErrorWithPosition(Sentence sentenceWithError,
                                                 int start, int end, Object... args) {
        addLocalizedErrorWithPosition(null, sentenceWithError, start, end, args);
    }

    /**
     * create a ValidationError for the specified position with specified message key
     *
     * @param messageKey        messageKey
     * @param sentenceWithError sentence
     * @param start             start position in parsed sentence
     * @param end               end position in parsed sentence
     * @param args              objects to format
     */
    protected void addLocalizedErrorWithPosition(String messageKey, Sentence sentenceWithError,
                                                 int start, int end, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(messageKey, args), sentenceWithError, start, end));
    }

    /**
     * returns localized error message for the given key formatted with argument
     *
     * @param key  message key
     * @param args objects to format
     * @return localized error message
     */
    protected String getLocalizedErrorMessage(String key, Object... args) {
        if (errorMessages != null) {
            String suffix = key != null ? "." + key : "";
            return MessageFormat.format(errorMessages.getString(this.getClass().getSimpleName() + suffix), args);
        } else {
            throw new AssertionError("message resource not found.");
        }
    }



    /**
     * create a ValidationError for the specified position with default error message
     *
     * @param sentenceWithError sentence
     * @param args              objects to format
     * @deprecated use {@link #addLocalizedError(Sentence, Object...)} instead
     */
    protected void addValidationError(Sentence sentenceWithError, Object... args) {
        addLocalizedError(sentenceWithError, args);
    }

    /**
     * create a ValidationError for the specified position with specified message key
     *
     * @param messageKey        messageKey
     * @param sentenceWithError sentence
     * @param args              objects to format
     * @deprecated use {@link #addLocalizedError(String, Sentence, Object...)} instead
     */
    protected void addValidationError(String messageKey, Sentence sentenceWithError, Object... args) {
        addLocalizedError(messageKey, sentenceWithError, args);
    }

    /**
     * create a ValidationError using the details within the given token
     *
     * @param sentenceWithError sentence
     * @param token             the TokenElement that has the error
     * @deprecated use {@link #addLocalizedErrorFromToken(Sentence, TokenElement)} instead
     */
    protected void addValidationErrorFromToken(Sentence sentenceWithError, TokenElement token) {
        addLocalizedError(sentenceWithError, token);
    }

    /**
     * create a ValidationError for the specified position with default error message
     *
     * @param sentenceWithError sentence
     * @param start             start position
     * @param end               end position
     * @param args              objects to format
     * @deprecated use {@link #addLocalizedErrorWithPosition(Sentence, int, int, Object...)} instead
     */
    protected void addValidationErrorWithPosition(Sentence sentenceWithError,
                                                  Optional<LineOffset> start, Optional<LineOffset> end, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(null, args), sentenceWithError, start.get(), end.get()));
    }

    /**
     * create a ValidationError for the specified position with specified message key
     *
     * @param messageKey        messageKey
     * @param sentenceWithError sentence
     * @param start             start position
     * @param end               end position
     * @param args              objects to format
     * @deprecated use {@link #addLocalizedErrorWithPosition(String, Sentence, int, int, Object...)} instead
     */
    protected void addValidationErrorWithPosition(String messageKey, Sentence sentenceWithError,
                                                  Optional<LineOffset> start, Optional<LineOffset> end, Object... args) {
        errors.add(new ValidationError(this.getClass(), getLocalizedErrorMessage(messageKey, args), sentenceWithError, start.get(), end.get()));
    }

    @Override public String toString() {
        return getClass().getSimpleName() + attributes;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Validator)) return false;
        Validator validator = (Validator)o;
        return Objects.equals(getClass(), validator.getClass()) && Objects.equals(config, validator.config);
    }

    @Override public int hashCode() {
        return Objects.hash(getClass(), config);
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
     * Resource Extractor loads rule dictionary
     */
    protected final static DictionaryLoader<Set<ExpressionRule>> RULE =
            new DictionaryLoader<>(HashSet::new, (set, line) -> set.add(RuleExtractor.run(line)));

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
