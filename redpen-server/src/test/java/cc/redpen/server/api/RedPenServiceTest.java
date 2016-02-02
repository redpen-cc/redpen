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
    assertEquals(5, redPens.size());
    assertEquals("en", redPens.get("default").getConfiguration().getLang());
    assertEquals("en", redPens.get("en").getConfiguration().getLang());

    assertEquals("ja", redPens.get("ja").getConfiguration().getLang());
    assertEquals("zenkaku", redPens.get("ja").getConfiguration().getVariant());

    assertEquals("ja", redPens.get("ja.zenkaku2").getConfiguration().getLang());
    assertEquals("zenkaku2", redPens.get("ja.zenkaku2").getConfiguration().getVariant());

    assertEquals("ja", redPens.get("ja.hankaku").getConfiguration().getLang());
    assertEquals("hankaku", redPens.get("ja.hankaku").getConfiguration().getVariant());
  }
}