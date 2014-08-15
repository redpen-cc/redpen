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

package cc.redpen.server;

import cc.redpen.ConfigurationLoader;
import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Document redPen server.
 */
public class RedPenServer {

    private static final Logger LOG =
            LoggerFactory.getLogger(RedPenServer.class);
    static String DEFAULT_INTERNAL_CONFIG_PATH = "/conf/redpen-conf.xml";
    private static RedPenServer redPenServer = null;

    private RedPen redPen;

    private Configuration config;

    private RedPenServer() throws RedPenException {
        ConfigurationLoader configLoader = new ConfigurationLoader();
        String confPath = System.getProperty("redpen.conf.path", DEFAULT_INTERNAL_CONFIG_PATH);

        InputStream inputConfigStream = getClass()
                .getClassLoader()
                .getResourceAsStream(confPath);

        if (inputConfigStream == null) {
            LOG.info("Loading config from specified config file: " +
                    "\"" + confPath + "\"");
            config = configLoader.loadConfiguration(confPath);
        } else {
            LOG.info("Loading config from default configuration");
            config = configLoader.loadConfiguration(inputConfigStream);
        }

        redPen = new RedPen.Builder()
                .setConfiguration(config)
                .build();
    }

    public static RedPenServer getInstance() throws
            RedPenException {
        if (redPenServer == null) {
            initialize();
        }
        return redPenServer;
    }

    public static synchronized void initialize() throws RedPenException {
        LOG.info("Initializing Document Validator");
        redPenServer = new RedPenServer();
    }

    public RedPen getRedPen() {
        return redPen;
    }

    public Configuration getConfig() {
        return config;
    }
}
