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

public class ResultDistributorFactory {

  static public ResultDistributor createDistributor(String type,
      OutputStream output) {
    if(type == null) {
      LOG.error("Specified distributor type is null...");
      return null;
    }

    LOG.info("Creating Distributor...");
    if (type.equals("default")) {
      if (output == null) {
        LOG.error("Output stream is null...");
        return null;
      }
      return new DefaultResultDistributor(output);
    } else if (type.equals("fake")) {
      return new FakeResultDistributor();
    }
    LOG.error("No specified distributor...");
    return null;
  }

  private static Logger LOG = LoggerFactory.getLogger(ResultDistributor.class);
}
