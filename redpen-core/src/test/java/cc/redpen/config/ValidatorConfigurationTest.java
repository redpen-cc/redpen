package cc.redpen.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ValidatorConfigurationTest {

  @Test
  public void canBeCloned() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addAttribute("foo", "bar");
    ValidatorConfiguration clone = conf.clone();

    assertNotSame(conf, clone);
    assertEquals(conf, clone);

    assertNotSame(conf.getAttributes(), clone.getAttributes());
    assertEquals(conf.getAttributes(), clone.getAttributes());
  }
}