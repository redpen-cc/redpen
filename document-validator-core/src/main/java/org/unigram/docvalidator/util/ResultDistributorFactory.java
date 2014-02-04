/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
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
