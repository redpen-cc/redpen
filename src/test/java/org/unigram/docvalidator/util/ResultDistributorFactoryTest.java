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

import static org.junit.Assert.*;

import org.junit.Test;

public class ResultDistributorFactoryTest {

  @Test
  public void testCreatePlainDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor("plain", System.out);
    assertNotNull(distributor);
  }

  @Test
  public void testCreateXMLDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor("xml", System.out);
    assertNotNull(distributor);
  }

  @Test
  public void testUnsupportedDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor("foobar", null);
    assertNull(distributor);
  }

  @Test
  public void testNullDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor(null, null);
    assertNull(distributor);
  }

  @Test
  public void testNullOutputDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor("plain", null);
    assertNull(distributor);
  }

}
