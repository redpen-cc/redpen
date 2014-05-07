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
package org.bigram.docvalidator.distributor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.bigram.docvalidator.distributor.ResultDistributor;
import org.bigram.docvalidator.distributor.ResultDistributorFactory;
import org.bigram.docvalidator.formatter.Formatter;

public class ResultDistributorFactoryTest {

  @Test
  public void testCreatePlainDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor(
        Formatter.Type.PLAIN, System.out);
    assertNotNull(distributor);
  }

  @Test
  public void testCreateXMLDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor(
        Formatter.Type.XML, System.out);
    assertNotNull(distributor);
  }

  @Test
  public void testNullDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor(null, null);
    assertNull(distributor);
  }

  @Test
  public void testNullOutputDistributor() {
    ResultDistributor distributor = ResultDistributorFactory.createDistributor(Formatter.Type.PLAIN, null);
    assertNull(distributor);
  }

}
