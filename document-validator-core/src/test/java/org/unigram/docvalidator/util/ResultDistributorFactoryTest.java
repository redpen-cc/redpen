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
