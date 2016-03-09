package cc.redpen.parser;

import cc.redpen.model.Section;

public class ParserUtils {
  public static boolean addChild(Section candidate, Section child) {
    if (candidate.getLevel() < child.getLevel()) {
      candidate.appendSubSection(child);
      child.setParentSection(candidate);
    } else { // search parent
      Section parent = candidate.getParentSection();
      while (parent != null) {
        if (parent.getLevel() < child.getLevel()) {
          parent.appendSubSection(child);
          child.setParentSection(parent);
          break;
        }
        parent = parent.getParentSection();
      }
      if (parent == null) {
        return false;
      }
    }
    return true;
  }
}
