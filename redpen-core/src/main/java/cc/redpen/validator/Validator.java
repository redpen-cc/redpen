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

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
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

    private Map<String, Object> properties;
    private ResourceBundle errorMessages = null;
    private ValidatorConfiguration config;
    private Configuration globalConfig;

    public Validator() {
        this(new Object[0]);
    }

    /**
     * @param keyValues String key and Object value pairs for supported config properties.
     */
    public Validator(Object...keyValues) {
        setLocale(Locale.getDefault());
        setDefaultProperties(keyValues);
    }

    protected void setDefaultProperties(Object...keyValues) {
        properties = new LinkedHashMap<>();
        addDefaultProperties(keyValues);
    }

    protected void addDefaultProperties(Object...keyValues) {
        if (keyValues.length % 2 != 0) throw new IllegalArgumentException("Not enough values specified");
        for (int i = 0; i < keyValues.length; i+=2) {
            properties.put(keyValues[i].toString(), keyValues[i+1]);
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
     * Return the configuration properties
     *
     * @return a map of configuration properties to their values
     */
    @Deprecated
    public Map<String, String> getConfigAttributes() {
        return config.getProperties();
    }

    /**
     * Validation initialization, called after the configuration and symbol tables have been assigned
     *
     * @throws RedPenException when failed to initialize
     */
    protected void init() throws RedPenException {
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    protected int getInt(String name) {
        Object value = config.getProperty(name);
        if(value == null) {
            value = properties.get(name);
        }
        if(value instanceof Integer) {
            return (int) value;
        }else{
            return Integer.valueOf((String)value);
        }
    }

    protected float getFloat(String name) {
        Object value = config.getProperty(name);
        if(value == null) {
            value = properties.get(name);
        }
        if(value instanceof Float) {
            return (float) value;
        }else{
            return Float.valueOf((String)value);
        }
    }

    protected String getString(String name) {
        return config.getProperties().getOrDefault(name, (String)properties.get(name));
    }

    protected boolean getBoolean(String name) {
        Object value = config.getProperty(name);
        if(value == null) {
            value = properties.get(name);
        }
        if(value instanceof Boolean) {
            return (boolean) value;
        }else{
            return Boolean.valueOf((String)value);
        }
    }

    @SuppressWarnings("unchecked")
    protected Set<String> getSet(String name) {
        Object value = config.getProperty(name);
        if (isEmpty(((String)value))) {
            value = properties.get(name);
        }
        if(value == null){
            return null;
        }
        if(value instanceof Set){
            return (Set<String>) value;
        }
        Set<String> newValue = Arrays.stream(((String)value).split(",")).map(String::toLowerCase).collect(toSet());
        properties.put(name,newValue);
        return newValue;
    }

    protected Optional<String> getConfigAttribute(String name) {
        return Optional.ofNullable(config.getProperty(name));
    }

    /** @deprecated Please use constructor with default properties instead, and then getXXX() methods */
    @Deprecated
    protected String getConfigAttribute(String name, String defaultValue) {
        return getConfigAttribute(name).orElse(defaultValue);
    }

    @Deprecated
    protected int getConfigAttributeAsInt(String name, int defaultValue) {
        return parseInt(getConfigAttribute(name, Integer.toString(defaultValue)));
    }

    @Deprecated
    protected boolean getConfigAttributeAsBoolean(String name, boolean defaultValue) {
        return parseBoolean(getConfigAttribute(name, Boolean.toString(defaultValue)));
    }

    @Deprecated
    protected double getConfigAttributeAsDouble(String name, double defaultValue) {
        return parseDouble(getConfigAttribute(name, Double.toString(defaultValue)));
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
        return getClass().getSimpleName() + properties;
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
