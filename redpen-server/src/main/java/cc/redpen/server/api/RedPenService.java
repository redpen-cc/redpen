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
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

/**
 * Helper class to access RedPen instances for use within the webapp
 */
public class RedPenService {

    private static final Logger LOG = LoggerFactory.getLogger(RedPenService.class);

    private final static String DEFAULT_INTERNAL_CONFIG_PATH = "/conf/redpen-conf.xml";
    private final static String DEFAULT_LANGUAGE = "default";

    private static Map<String, RedPen> langRedPenMap = new HashMap<>();

    /**
     * Create redpens for the given context
     *
     * @param context the servlet context
     */
    public RedPenService(ServletContext context) {
        synchronized (this) {
            if (langRedPenMap.size() == 0) {
                LOG.info("Creating RedPen instances");
                try {
                    for (String fileName : findConfFiles()) {
                        RedPen redPen = new RedPen(fileName);
                        Configuration config = redPen.getConfiguration();
                        langRedPenMap.put(config.getKey(), redPen);
                    }

                    String configPath = context != null ? context.getInitParameter("redpen.conf.path") : null;
                    if (configPath != null) {
                        LOG.info("Config Path is set to \"{}\"", configPath);
                        RedPen defaultRedPen = new RedPen(configPath);
                        langRedPenMap.put(DEFAULT_LANGUAGE, defaultRedPen);
                    } else {
                        // if config path is not set, fallback to default config path
                        LOG.info("Config Path is set to \"{}\"", DEFAULT_INTERNAL_CONFIG_PATH);
                        langRedPenMap.put(DEFAULT_LANGUAGE, langRedPenMap.get("en"));
                    }
                    LOG.info("Document Validator Server is running.");
                } catch (RedPenException e) {
                    LOG.error("Unable to initialize RedPen", e);
                    throw new ExceptionInInitializerError(e);
                }
            }
        }
    }

    private Set<String> findConfFiles() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver(getClass().getClassLoader()).getResources("classpath*:conf/*.xml");
            return Stream.of(resources).map(r -> "/conf/" + r.getFilename()).collect(toSet());
        }
        catch (IOException e) {
            LOG.error("Failed to load default config files", e);
            return emptySet();
        }
    }

    public RedPen getRedPen(String lang) {
        return langRedPenMap.getOrDefault(lang, langRedPenMap.get(DEFAULT_LANGUAGE));
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
        Configuration.ConfigurationBuilder configBuilder = Configuration.builder();
        configBuilder.setLanguage(lang);

        // add the validators and their properties
        validatorProperties.forEach((validatorName, props) -> {
            ValidatorConfiguration validatorConfig = new ValidatorConfiguration(validatorName);
            props.forEach((name, value) -> {
                validatorConfig.addAttribute(name, value);
            });
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
        return langRedPenMap;
    }
}
