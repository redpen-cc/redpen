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
import cc.redpen.config.ValidatorConfiguration;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * Factory class of validators.
 */
public class ValidatorFactory {
    private static final String validatorPackage = Validator.class.getPackage().getName();
    private static final List<String> VALIDATOR_PACKAGES = asList(validatorPackage, validatorPackage + ".sentence", validatorPackage + ".section");
    static final Map<String, Validator> validators = new LinkedHashMap<>();
    private static final Map<String, String> jsValidators = new LinkedHashMap<>();

    static void registerValidator(Class<? extends Validator> clazz) {
        validators.put(clazz.getSimpleName().replace("Validator", ""), createValidator(clazz));
    }

    static {
        Reflections reflections = new Reflections("cc.redpen.validator");
        // register Validator implementations under cc.redpen.validator package
        reflections.getSubTypesOf(Validator.class).stream()
                .filter(validator -> !Modifier.isAbstract(validator.getModifiers()))
                .forEach(validator -> {
                    try {
                        registerValidator(validator);
                    } catch (RuntimeException ignored) {
                        // the validator doesn't implement default constructor
                    }
                });
        Reflections jsReflections = new Reflections(
                new ConfigurationBuilder()
                        .setScanners(new ResourcesScanner())
                        .setUrls(ClasspathHelper.forPackage("cc.redpen.validator")));
        jsReflections.getResources(Pattern.compile(".*js"))
                .forEach(e -> {
                    InputStream inputStream = ValidatorFactory.class.getResourceAsStream("/" + e);
                    try (InputStreamReader isr = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                         BufferedReader br = new BufferedReader(isr)) {
                        StringBuilder sb = new StringBuilder(1024);
                        String str;
                        while ((str = br.readLine()) != null) {
                            sb.append(str);
                        }
                        String validatorName = e.replaceFirst(".*/", "").replaceFirst("\\.js$", "");
                        jsValidators.put(validatorName, sb.toString());
                    } catch (IOException ignored) {
                    }

                });
    }

    public static List<ValidatorConfiguration> getConfigurations(String lang) {
        List<ValidatorConfiguration> configurations = validators.entrySet().stream().filter(e -> {
            List<String> supportedLanguages = e.getValue().getSupportedLanguages();
            boolean deprecated = e.getValue().getClass().getAnnotation(Deprecated.class) == null ? false : true;
            return (supportedLanguages.isEmpty() || supportedLanguages.contains(lang)) && !deprecated;
        }).map(e -> new ValidatorConfiguration(e.getKey(), toStrings(e.getValue().getProperties()))).collect(toList());
        Map<String, String> emptyMap = new LinkedHashMap<>();
        for (String jsValidator : jsValidators.keySet()) {
            try {
                Validator jsValidatorInstance = getInstance(jsValidator);
                List<String> supportedLanguages = jsValidatorInstance.getSupportedLanguages();
                if (supportedLanguages.isEmpty() || supportedLanguages.contains(lang)) {
                    configurations.add(new ValidatorConfiguration(jsValidator, emptyMap));
                }
            } catch (RedPenException ignored) {
            }
        }
        return configurations;
    }

    @SuppressWarnings("unchecked")
    static Map<String, String> toStrings(Map<String, Object> properties) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : properties.entrySet()) {
            if (e.getValue() instanceof Iterable)
                result.put(e.getKey(), join((Iterable)e.getValue(), ','));
            else
                result.put(e.getKey(), e.getValue().toString());
        }
        return result;
    }

    public static Validator getInstance(String validatorName) throws RedPenException {
        Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
        return getInstance(conf.getValidatorConfigs().get(0), conf);
    }

    public static Validator getInstance(ValidatorConfiguration config, Configuration globalConfig) throws RedPenException {
        String validatorName = config.getConfigurationName();
        // lookup JavaScript validators
        String script = jsValidators.get(validatorName);
        if(script != null){
            JavaScriptLoader javaScriptValidator = new JavaScriptLoader(validatorName, script);
            javaScriptValidator.preInit(config, globalConfig);
            return javaScriptValidator;
        }

        // fallback to Java validators
        Validator prototype = validators.get(config.getConfigurationName());
        Class<? extends Validator> validatorClass = prototype != null ? prototype.getClass() : loadPlugin(validatorName);
        Validator validator = createValidator(validatorClass);
        validator.preInit(config, globalConfig);
        return validator;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Validator> loadPlugin(String name) throws RedPenException {
        for (String p : VALIDATOR_PACKAGES) {
            try {
                Class<? extends Validator> validatorClass = (Class)Class.forName(p + "." + name + "Validator");
                registerValidator(validatorClass);
                return validatorClass;
            }
            catch (ClassNotFoundException ignore) {
            }
        }
        throw new RedPenException("There is no such validator: " + name);
    }

    private static Validator createValidator(Class<? extends Validator> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create instance of " + clazz + " using default constructor");
        }
    }
}
