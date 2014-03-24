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

package org.unigram.docvalidator.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.unigram.docvalidator.ConfigurationLoader;
import org.unigram.docvalidator.server.util.ServerConfigurationLoader;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ResultDistributorFactory;
import org.unigram.docvalidator.validator.DocumentValidator;

/**
 * Document validator server.
 */
public class DocumentValidatorServer {

  private static Logger log = LogManager.getLogger(
      DocumentValidatorServer.class
  );

  private static DocumentValidatorServer documentValidatorServer;

  private DocumentValidator validator;

  private DVResource documentValidatorResource;

  private DocumentValidatorServer() throws DocumentValidatorException {
    ConfigurationLoader configLoader = new ServerConfigurationLoader();
    documentValidatorResource = configLoader.loadConfiguration(
        getClass()
            .getClassLoader()
            .getResourceAsStream("/conf/dv-conf.xml")
    );

//    ResultDistributor distributor = ResultDistributorFactory
//        .createDistributor("plain", System.out);
    validator = new DocumentValidator.Builder()
        .setResource(documentValidatorResource)
//        .setResultDistributor(distributor)
        .build();
  }

  public DocumentValidator getValidator() {
    return validator;
  }

  public DVResource getDocumentValidatorResource() {
    return documentValidatorResource;
  }

  public static DocumentValidatorServer getInstance() throws
      DocumentValidatorException {
    if (documentValidatorServer == null) {
      initialize();
    }
    return documentValidatorServer;
  }

  public static void initialize() throws DocumentValidatorException {
    log.info("Initializing Document Validator");
    documentValidatorServer = new DocumentValidatorServer();
  }
}
