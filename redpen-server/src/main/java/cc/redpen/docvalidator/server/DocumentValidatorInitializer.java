/*
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

package cc.redpen.docvalidator.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cc.redpen.docvalidator.DocumentValidatorException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Initiation of the document validator server.
 */
public class DocumentValidatorInitializer implements ServletContextListener {


  private static Logger log = LogManager.getLogger(
    DocumentValidatorInitializer.class
  );

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    log.info("Starting Document Validator Server.");
    String configPath = System.getProperty("redpen.conf.path");
    // if redpen.conf.path is not set via system property, fallback to web.xml's context-param.
    if (configPath == null) {
      configPath = servletContextEvent.getServletContext().getInitParameter("redpen.conf.path");
      if (configPath == null) {
        throw new ExceptionInInitializerError("redpen.conf.path not specified in web.xml");
      }
      System.setProperty("redpen.conf.path", configPath);
    }

    log.info("Config Path is set to " + "\"" + configPath + "\"");
    try {
      DocumentValidatorServer.initialize();
      log.info("Document Validator Server is running.");
    } catch (DocumentValidatorException e) {
      log.error("Could not initialize Document Validator Server: ", e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    log.info("Stopping Document Validator Server.");
  }
}
