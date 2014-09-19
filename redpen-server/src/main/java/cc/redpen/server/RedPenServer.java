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

    private static final Logger LOG = LoggerFactory.getLogger(RedPenServer.class);

    private RedPen redPen;

    private Configuration config;

    public RedPenServer(String confPath) throws RedPenException {
        ConfigurationLoader configLoader = new ConfigurationLoader();
        InputStream inputConfigStream = RedPenServer.class.getResourceAsStream(confPath);

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

    public RedPen getRedPen() {
        return redPen;
    }

    public Configuration getConfig() {
        return config;
    }
}
