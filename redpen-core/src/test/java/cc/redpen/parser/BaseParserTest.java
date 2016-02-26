package cc.redpen.parser;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

public class BaseParserTest {
  protected static List<LineOffset> initializeMappingTable(LineOffset... offsets) {
      List<LineOffset> offsetTable = new ArrayList<>();
      addAll(offsetTable, offsets);
      return offsetTable;
  }
}
