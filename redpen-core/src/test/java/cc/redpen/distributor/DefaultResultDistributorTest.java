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
package cc.redpen.distributor;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import cc.redpen.ValidationError;
import cc.redpen.formatter.PlainFormatter;

public class DefaultResultDistributorTest {
  @Test
  public void testFlushHeaderWithPlainFormatter() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    DefaultResultDistributor distributor = new DefaultResultDistributor(os);
    distributor.setFormatter(new PlainFormatter());
    distributor.flushFooter();
    String result = null;
    try {
      result = new String(os.toByteArray(),"UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      fail();
    }
    assertEquals("", result);
  }

  @Test
  public void testFlushFooterWithPlainFormatter() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    DefaultResultDistributor distributor = new DefaultResultDistributor(os);
    distributor.setFormatter(new PlainFormatter());
    distributor.flushFooter();
    String result = null;
    try {
      result = new String(os.toByteArray(),"UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      fail();
    }
    assertEquals("", result);
  }

  @Test
  public void testFlushErrorWithPlainFormatter() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    DefaultResultDistributor distributor = new DefaultResultDistributor(os);
    distributor.setFormatter(new PlainFormatter());
    ValidationError error = new ValidationError(this.getClass(), "foobar", -1);
    distributor.flushResult(error);
    String result = null;
    try {
      result = new String(os.toByteArray(),"UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      fail();
    }
    Pattern p = Pattern.compile("foobar");
    Matcher m = p.matcher(result);
    assertTrue(m.find());
  }

  @Test(expected=IllegalArgumentException.class)
  public void testFlushErrorWithPlainFormatterForNull() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    DefaultResultDistributor distributor = new DefaultResultDistributor(os);
    distributor.setFormatter(new PlainFormatter());
    distributor.flushResult(null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testCreatePlainFormatterNullStream() {
    DefaultResultDistributor distributor = new DefaultResultDistributor(null);
    distributor.setFormatter(new PlainFormatter());
  }

}
