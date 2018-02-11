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

package cc.redpen.server.api;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.*;
import cc.redpen.model.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.*;

/**
 * Helper class to access RedPen instances for use within the webapp
 */
public class RedPenService {
    private static final Logger LOG = LoggerFactory.getLogger(RedPenService.class);

    private final static String DEFAULT_LANGUAGE = "default";
    static final Map<String, RedPen> redPens = new LinkedHashMap<>();

    /**
     * Create redpens for the given context
     *
     * @param context the servlet context
     */
    public RedPenService(ServletContext context) throws RedPenException {
        if (!redPens.isEmpty()) {
            LOG.debug("Default RedPen objects are found...");
            return;
        }
        synchronized (redPens) {
            LOG.info("Creating RedPen instances");
            List<Document> emptyDocuments = new ArrayList<>();
            emptyDocuments.add(Document.builder().build());
            for (String key : Configuration.getDefaultConfigKeys()) {
                RedPen redpen = new RedPen(Configuration.builder(key).secure().addAvailableValidatorConfigs().build());
                redpen.validate(emptyDocuments);
                redPens.put(key, redpen);
            }


            String configPath = context != null ? context.getInitParameter("redpen.conf.path") : null;
            if (configPath != null) {
                LOG.info("Config Path is set to \"{}\"", configPath);
                Configuration configuration;
                try {
                    configuration = new ConfigurationLoader().secure().loadFromResource(configPath);
                } catch (RedPenException rpe) {
                    configuration = new ConfigurationLoader().secure().load(new File(configPath));
                }
                RedPen defaultRedPen = new RedPen(configuration);
                defaultRedPen.validate(emptyDocuments);
                redPens.put(DEFAULT_LANGUAGE, defaultRedPen);
                redPens.put(configuration.getLang(), defaultRedPen);
            } else {
                // if config path is not set, fallback to default config path
                LOG.info("No Config Path set, using default configurations");
                redPens.put(DEFAULT_LANGUAGE, redPens.get("en"));
            }
            LOG.info("Document Validator Server is running.");
        }
    }

    public RedPen getRedPen(String lang) {
        return redPens.getOrDefault(lang, redPens.get(DEFAULT_LANGUAGE));
    }

    /**
     * Create a new redpen for the JSON object.
     * @param requestJSON the JSON contains configurations
     * @return a configured redpen instance
     */
    public RedPen getRedPenFromJSON(JSONObject requestJSON) {
        String lang = "en";

        Map<String, Map<String, String>> properties = new HashMap<>();
        JSONObject config = null;
        if (requestJSON.has("config")) {
            try {
                config = requestJSON.getJSONObject("config");
                lang = getOrDefault(config, "lang", "en");
                if (config.has("validators")) {
                    JSONObject validators = config.getJSONObject("validators");
                    Iterator keyIter = validators.keys();
                    appendValidators(properties, validators, keyIter);
                } else {
                    LOG.warn("No validators are found in config...");
                }
            } catch (Exception e) {
                LOG.error("Exception when processing JSON properties", e);
            }
        }

        RedPen redPen = this.getRedPen(lang, properties);

        // override any symbols
        if ((config != null) && config.has("symbols")) {
            try {
                JSONObject symbols = config.getJSONObject("symbols");
                Iterator keyIter = symbols.keys();
                while (keyIter.hasNext()) {
                    registerSymbolSettings(redPen, symbols, keyIter);
                }
            } catch (Exception e) {
                LOG.error("Exception when processing JSON symbol overrides", e);
            }
        }
        return redPen;
    }

    private void registerSymbolSettings(RedPen redPen, JSONObject symbols, Iterator keyIter) throws JSONException {
        String symbolName = String.valueOf(keyIter.next());
        try {
            SymbolType symbolType = SymbolType.valueOf(symbolName);
            JSONObject symbolConfig = symbols.getJSONObject(symbolName);
            Symbol originalSymbol = redPen.getConfiguration().getSymbolTable().getSymbol(symbolType);
            if ((originalSymbol != null) && (symbolConfig != null) && symbolConfig.has("value")) {
                String value = symbolConfig.has("value") ? symbolConfig.getString("value") : String.valueOf(originalSymbol.getValue());
                boolean spaceBefore = symbolConfig.has("before_space") ? symbolConfig.getBoolean("before_space") : originalSymbol.isNeedBeforeSpace();
                boolean spaceAfter = symbolConfig.has("after_space") ? symbolConfig.getBoolean("after_space") : originalSymbol.isNeedAfterSpace();
                String invalidChars = symbolConfig.has("invalid_chars") ? symbolConfig.getString("invalid_chars") : String.valueOf(originalSymbol.getInvalidChars());
                if ((value != null) && !value.isEmpty()) {
                    redPen.getConfiguration().getSymbolTable().overrideSymbol(new Symbol(symbolType, value.charAt(0), invalidChars, spaceBefore, spaceAfter));
                }
            }
        } catch (IllegalArgumentException iae) {
            LOG.error("Ignoring unknown SymbolType " + symbolName);
        }
    }

    private void appendValidators(Map<String, Map<String, String>> properties, JSONObject validators, Iterator keyIter) throws JSONException {
        while (keyIter.hasNext()) {
            String validator = String.valueOf(keyIter.next());
            Map<String, String> props = new HashMap<>();
            properties.put(validator, props);
            JSONObject validatorConfig = validators.getJSONObject(validator);
            if ((validatorConfig != null) && validatorConfig.has("properties")) {
                JSONObject validatorProps = validatorConfig.getJSONObject("properties");
                Iterator propsIter = validatorProps.keys();
                while (propsIter.hasNext()) {
                    String propname = String.valueOf(propsIter.next());
                    props.put(propname, validatorProps.getString(propname));
                }
            }
        }
    }

    /**
     * Create a new redpen for the specified language. The validator properties map is a map of validator names to their (optional) properties.
     * Only validitors present in this map are added to the redpen configuration
     *
     * @param lang                the language to use
     * @param validatorProperties a map of redpen validator names to a map of their properties
     * @return a configured redpen instance
     */
    public RedPen getRedPen(String lang, Map<String, Map<String, String>> validatorProperties) {
        Configuration.ConfigurationBuilder configBuilder = Configuration.builder(lang).secure();

        // add the validators and their properties
        validatorProperties.forEach((validatorName, props) -> {
            ValidatorConfiguration validatorConfig = new ValidatorConfiguration(validatorName);
            props.forEach(validatorConfig::addProperty);
            configBuilder.addValidatorConfig(validatorConfig);
        });
        try {
            return new RedPen(configBuilder.build());
        } catch (RedPenException e) {
            LOG.error("Unable to initialize RedPen", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Get all preconfigured redpen instances
     *
     * @return map of available RedPens
     */
    public Map<String, RedPen> getRedPens() {
        return redPens;
    }

    public static String getOrDefault(JSONObject json, String property, String defaultValue) {
        try {
            String value = json.getString(property);
            if (value != null) {
                return value;
            }
        } catch (Exception e) {
            // intentionally empty
        }
        return defaultValue;
    }
}
