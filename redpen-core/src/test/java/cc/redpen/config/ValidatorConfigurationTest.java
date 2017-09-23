package cc.redpen.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ValidatorConfigurationTest {

  @Test
  void canBeCloned() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addProperty("foo", "bar");
    ValidatorConfiguration clone = conf.clone();

    assertNotSame(conf, clone);
    assertEquals(conf, clone);

    assertNotSame(conf.getProperties(), clone.getProperties());
    assertEquals(conf.getProperties(), clone.getProperties());

    assertNotSame(conf.getProperties(), clone.getProperties());
    assertEquals(conf.getLevel(), clone.getLevel());
  }

  @Test
  void equals() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addProperty("foo", "bar").setLevel(ValidatorConfiguration.LEVEL.ERROR);
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test").addProperty("foo", "bar").setLevel(ValidatorConfiguration.LEVEL.ERROR);
    assertEquals(conf, conf2);
  }

  @Test
  void equals_properties() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addProperty("foo", "bar");
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test").addProperty("foo", "bar2");
    assertFalse(conf.equals(conf2));
  }

  @Test
  void equals_names() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test");
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test2");
    assertFalse(conf.equals(conf2));
  }

  @Test
  void equals_levels() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").setLevel(ValidatorConfiguration.LEVEL.INFO);
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test2").setLevel(ValidatorConfiguration.LEVEL.INFO);;
    assertFalse(conf.equals(conf2));
  }
}
