package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

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
    ValidationError error = new ValidationError("foobar");
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
