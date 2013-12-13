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

/**
 * ResultDistributor flush the errors reported from Validators.
 */
public interface ResultDistributor {
  /**
   * flush given ValidationError.
   * @param err error reproted from a Validator
   * @return 0 succeeded, otherwise 1
   */
  int flushResult(ValidationError err);

  /**
   * set Formatter object
   * @param formatter
   */
  void setFormatter(Formatter formatter);
}
