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

package org.unigram.docvalidator.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.unigram.docvalidator.DocumentValidatorException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Initiation of the document validator server.
 */
public class DocumentValidatorInitServlet extends HttpServlet {

  private static Logger log = LogManager.getLogger(
    DocumentValidatorInitServlet.class
  );

  @Override
  public void init() throws ServletException {
    log.info("Starting Document Validator Server.");
    try {
      DocumentValidatorServer.initialize();
      log.info("Document Validator Server is running.");
    } catch (DocumentValidatorException e) {
      log.error("Could not initialize Document Validator Server: ", e);
    }
  }

  @Override
  public void destroy() {
    log.info("Stopping Document Validator Server.");
  }
}
