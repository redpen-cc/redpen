package cc.redpen.server.api;

import cc.redpen.RedPen;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RedPenServiceTest {
  @Test
  public void twoConfigurationsByDefault() throws Exception {
    RedPenService service = new RedPenService(null);
    Map<String, RedPen> redPens = service.getRedPens();
    assertEquals(3, redPens.size());
    assertEquals("en", redPens.get("default").getConfiguration().getLang());
    assertEquals("en", redPens.get("en").getConfiguration().getLang());
    assertEquals("ja", redPens.get("ja").getConfiguration().getLang());
  }
}