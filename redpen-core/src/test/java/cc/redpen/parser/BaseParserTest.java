package cc.redpen.parser;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.of;

public class BaseParserTest {
  protected List<LineOffset> offsets(int lineNum, IntStream...streams) {
    return concat(streams).mapToObj(p -> new LineOffset(lineNum, p)).collect(toList());
  }

  private IntStream concat(IntStream...streams) {
    IntStream result = of();
    for (IntStream stream : streams) {
      result = IntStream.concat(result, stream);
    }
    return result;
  }
}
