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

package org.bigram.docvalidator.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bigram.docvalidator.ConfigurationLoader;
import org.bigram.docvalidator.config.Configuration;
import org.bigram.docvalidator.DocumentValidatorException;
import org.bigram.docvalidator.DocumentValidator;

import java.io.InputStream;

/**
 * Document validator server.
 */
public class DocumentValidatorServer {

  static String DEFAULT_INTERNAL_CONFIG_PATH = "/conf/dv-conf.xml";

  private static final Logger LOG =
      LoggerFactory.getLogger(DocumentValidatorServer.class);

  private static DocumentValidatorServer documentValidatorServer = null;

  private DocumentValidator validator;

  private Configuration documentValidatorConfig;

  private DocumentValidatorServer() throws DocumentValidatorException {
    ConfigurationLoader configLoader = new ConfigurationLoader();
    String confPath = System.getProperty("redpen.conf.path", DEFAULT_INTERNAL_CONFIG_PATH);

    InputStream inputConfigStream = getClass()
        .getClassLoader()
        .getResourceAsStream(confPath);

    if (inputConfigStream == null) {
      LOG.info("Loading config from specified config file: " +
          "\"" + confPath + "\"");
      documentValidatorConfig = configLoader.loadConfiguration(confPath);
    } else {
      LOG.info("Loading config from default configuration");
      documentValidatorConfig = configLoader.loadConfiguration(inputConfigStream);
    }

    validator = new DocumentValidator.Builder()
        .setConfiguration(documentValidatorConfig)
        .build();
  }

  public DocumentValidator getValidator() {
    return validator;
  }

  public Configuration getDocumentValidatorConfig() {
    return documentValidatorConfig;
  }

  public static DocumentValidatorServer getInstance() throws
      DocumentValidatorException {
    if (documentValidatorServer == null) {
      initialize();
    }
    return documentValidatorServer;
  }

  public static synchronized void initialize() throws DocumentValidatorException {
    if(documentValidatorServer == null) {
      LOG.info("Initializing Document Validator");
      documentValidatorServer = new DocumentValidatorServer();
    }else{
      throw new IllegalStateException("DocumentValidatorServer already initialized.");
    }
  }
}
