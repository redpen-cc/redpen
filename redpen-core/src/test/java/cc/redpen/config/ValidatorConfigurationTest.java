package cc.redpen.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidatorConfigurationTest {

  @Test
  public void canBeCloned() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addProperty("foo", "bar");
    ValidatorConfiguration clone = conf.clone();

    assertNotSame(conf, clone);
    assertEquals(conf, clone);

    assertNotSame(conf.getProperties(), clone.getProperties());
    assertEquals(conf.getProperties(), clone.getProperties());

    assertNotSame(conf.getProperties(), clone.getProperties());
    assertEquals(conf.getSeverity(), clone.getSeverity());
  }

  @Test
  public void equals() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addProperty("foo", "bar").setSeverity(ValidatorConfiguration.SEVERITY.ERROR);
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test").addProperty("foo", "bar").setSeverity(ValidatorConfiguration.SEVERITY.ERROR);
    assertEquals(conf, conf2);
  }

  @Test
  public void equals_properties() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").addProperty("foo", "bar");
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test").addProperty("foo", "bar2");
    assertFalse(conf.equals(conf2));
  }

  @Test
  public void equals_names() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test");
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test2");
    assertFalse(conf.equals(conf2));
  }

  @Test
  public void equals_levels() throws Exception {
    ValidatorConfiguration conf = new ValidatorConfiguration("test").setSeverity(ValidatorConfiguration.SEVERITY.INFO);
    ValidatorConfiguration conf2 = new ValidatorConfiguration("test2").setSeverity(ValidatorConfiguration.SEVERITY.INFO);;
    assertFalse(conf.equals(conf2));
  }
}
