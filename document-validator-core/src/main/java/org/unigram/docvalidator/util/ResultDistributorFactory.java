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
package org.unigram.docvalidator.util;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class of ResultDistributor.
 */
public class ResultDistributorFactory {
  /**
   * Create ResultDistributor object.
   *
   * @param outputFormat syntax of output
   * @param output       output stream
   * @return ResultDistributor object when succeeded to create, null otherwise
   */
  static public ResultDistributor createDistributor(String outputFormat,
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
      if (outputFormat.equals("plain")) {
        distributor.setFormatter(new PlainFormatter());
      } else if (outputFormat.equals("xml")) {
        distributor.setFormatter(new XMLFormatter());
      } else {
        LOG.error("No specified distributor...");
        return null;
      }
    } catch (DocumentValidatorException e) {
      LOG.error(e.getMessage());
      return null;
    }
    return distributor;
  }

  private static final Logger LOG = LoggerFactory.getLogger(ResultDistributor.class);
}
