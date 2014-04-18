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
package org.unigram.docvalidator.distributor;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.DocumentValidatorException;
import org.unigram.docvalidator.formatter.Formatter;
import org.unigram.docvalidator.formatter.PlainFormatter;
import org.unigram.docvalidator.formatter.XMLFormatter;

/**
 * Factory class of ResultDistributor.
 */
public final class ResultDistributorFactory {
  /**
   * Create ResultDistributor object.
   *
   * @param outputFormat syntax of output
   * @param output       output stream
   * @return ResultDistributor object when succeeded to create, null otherwise
   */
  public static ResultDistributor createDistributor(Formatter.Type outputFormat,
                                                    OutputStream output) {
    if (outputFormat == null) {
      LOG.error("Specified output format is null...");
      return null;
    }

    if (output == null) {
      LOG.error("Output stream is null...");
      return null;
    }
    ResultDistributor distributor = new DefaultResultDistributor(output);

    LOG.info("Creating Distributor...");
    try {
      switch (outputFormat) {
        case PLAIN:
          distributor.setFormatter(new PlainFormatter());
          break;
        case XML:
          distributor.setFormatter(new XMLFormatter());
          break;
        default :
          LOG.error("No specified distributor...");
          return null;
      }
    } catch (DocumentValidatorException e) {
      LOG.error(e.getMessage());
      return null;
    }
    return distributor;
  }

  private ResultDistributorFactory() {
    // for safe
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(ResultDistributor.class);
}
