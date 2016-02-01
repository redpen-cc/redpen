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
    assertEquals(2, redPens.size());
    assertEquals("en", redPens.get("en").getConfiguration().getLang());
    assertEquals("ja", redPens.get("ja").getConfiguration().getLang());
  }
}