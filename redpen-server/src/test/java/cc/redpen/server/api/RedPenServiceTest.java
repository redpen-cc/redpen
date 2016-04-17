package cc.redpen.server.api;

import cc.redpen.RedPen;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedPenServiceTest {
    @Before
    public void setUp() {
        RedPenService.redPens.clear();
    }

    @Test
    public void defaultConfigurations() throws Exception {
        RedPenService service = new RedPenService(null);
        Map<String, RedPen> redPens = service.getRedPens();
        assertEquals("en", redPens.get("default").getConfiguration().getLang());
        assertEquals("en", redPens.get("en").getConfiguration().getLang());

        assertEquals("ja", redPens.get("ja").getConfiguration().getLang());
        assertEquals("zenkaku", redPens.get("ja").getConfiguration().getVariant());

        assertEquals("ja", redPens.get("ja.zenkaku2").getConfiguration().getLang());
        assertEquals("zenkaku2", redPens.get("ja.zenkaku2").getConfiguration().getVariant());

        assertEquals("ja", redPens.get("ja.hankaku").getConfiguration().getLang());
        assertEquals("hankaku", redPens.get("ja.hankaku").getConfiguration().getVariant());

        assertTrue(redPens.values().stream().allMatch(r -> r.getConfiguration().isSecure()));
    }

    @Test
    public void canSpecifyDifferentDefaultConfiguration() throws Exception {
        MockServletContext context = new MockServletContext();
        context.addInitParameter("redpen.conf.path", "/conf/redpen-conf-ru.xml");
        RedPen defaultRedPen = new RedPenService(context).getRedPen("default");
        assertEquals("ru", defaultRedPen.getConfiguration().getKey());
        assertTrue(defaultRedPen.getConfiguration().isSecure());
    }

    @Test
    public void redPensWithCustomPropertiesAreAlsoSecure() throws Exception {
        RedPen en = new RedPenService(null).getRedPen("en", emptyMap());
        assertTrue(en.getConfiguration().isSecure());
    }
}