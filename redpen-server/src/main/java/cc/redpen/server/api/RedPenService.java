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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to access RedPen instances for use within the webapp
 */
public class RedPenService {

    private static final Logger LOG = LoggerFactory.getLogger(RedPenService.class);

    private final static String DEFAULT_INTERNAL_CONFIG_PATH = "/conf/redpen-conf.xml";
    private final static String DEFAULT_LANGUAGE = "default";

    private static Map<String, RedPen> langRedPenMap = new HashMap<>();

    public RedPenService(ServletContext context) {
        synchronized (this) {
            if (langRedPenMap.size() == 0) {
                LOG.info("Creating RedPen instances");
                try {
                    RedPen englishRedPen = new RedPen(DEFAULT_INTERNAL_CONFIG_PATH);
                    langRedPenMap.put("en", englishRedPen);
                    RedPen japaneseRedPen = new RedPen("/conf/redpen-conf-ja.xml");
                    langRedPenMap.put("ja", japaneseRedPen);

                    String configPath;
                    if (context != null) {
                        configPath = context.getInitParameter("redpen.conf.path");
                        if (configPath != null) {
                            LOG.info("Config Path is set to \"{}\"", configPath);
                            RedPen defaultRedPen = new RedPen(configPath);
                            langRedPenMap.put(DEFAULT_LANGUAGE, defaultRedPen);
                        } else {
                            // if config path is not set, fallback to default config path
                            LOG.info("Config Path is set to \"{}\"", DEFAULT_INTERNAL_CONFIG_PATH);
                        }
                    }
                    LOG.info("Document Validator Server is running.");
                } catch (RedPenException e) {
                    LOG.error("Unable to initialize RedPen", e);
                    throw new ExceptionInInitializerError(e);
                }
            }
        }
    }

    public RedPen getRedPen(String lang) {

        return langRedPenMap.getOrDefault(lang, langRedPenMap.get(DEFAULT_LANGUAGE));
    }

    public Map<String, RedPen> getRedPens() {
        return langRedPenMap;
    }
}
