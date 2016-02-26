package cc.redpen.config;

import org.junit.Test;

import static org.junit.Assert.*;

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

  @Test
  public void equals() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addAttribute("foo", "bar");
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test").addAttribute("foo", "bar");
    assertEquals(conf, conf2);
  }

  @Test
  public void equals_attributes() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addAttribute("foo", "bar");
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test").addAttribute("foo", "bar2");
    assertFalse(conf.equals(conf2));
  }

  @Test
  public void equals_names() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test");
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test2");
    assertFalse(conf.equals(conf2));
  }
}