/*
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.unigram.docvalidator.parser.markdown;

import org.parboiled.common.StringUtils;
import org.pegdown.Printer;
import org.pegdown.ast.AbbreviationNode;
import org.pegdown.ast.AutoLinkNode;
import org.pegdown.ast.BlockQuoteNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.DefinitionListNode;
import org.pegdown.ast.DefinitionNode;
import org.pegdown.ast.DefinitionTermNode;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.HtmlBlockNode;
import org.pegdown.ast.InlineHtmlNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.MailLinkNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.OrderedListNode;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.QuotedNode;
import org.pegdown.ast.RefImageNode;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SimpleNode;
import org.pegdown.ast.SpecialTextNode;
import org.pegdown.ast.StrikeNode;
import org.pegdown.ast.StrongEmphSuperNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TableBodyNode;
import org.pegdown.ast.TableCaptionNode;
import org.pegdown.ast.TableCellNode;
import org.pegdown.ast.TableColumnNode;
import org.pegdown.ast.TableHeaderNode;
import org.pegdown.ast.TableNode;
import org.pegdown.ast.TableRowNode;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.VerbatimNode;
import org.pegdown.ast.Visitor;
import org.pegdown.ast.WikiLinkNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.parser.ParseUtils;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.DocumentValidatorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.parboiled.common.Preconditions.checkArgNotNull;

/**
 * Using Pegdown Parser. <br/>
 *
 * @see https://github.com/sirthias/pegdown
 */
public class ToFileContentSerializer implements Visitor {

  private static final Logger LOG =
      LoggerFactory.getLogger(ToFileContentSerializer.class);

  private FileContent fileContent = null;

  private final Map<String, ReferenceNode> references =
      new HashMap<String, ReferenceNode>();

  private final Map<String, String> abbreviations =
      new HashMap<String, String>();

  private int itemDepth = 0;

  private Section currentSection = null;

  protected void visitChildren(SuperNode node) {
    for (Node child : node.getChildren()) {
      child.accept(this);
    }
  }

  private List<Integer> lineList = null;

  // TODO multi period character not supported
  private String period;

  private List<CandidateSentence> candidateSentences =
      new ArrayList<CandidateSentence>();

  /**
   * Constructor.
   *
   * @param content          FileContent
   * @param listOfLineNumber the list of line number
   * @param parserPeriod     end character of sentence
   */
  public ToFileContentSerializer(FileContent content,
                                 List<Integer> listOfLineNumber,
                                 String parserPeriod) {
    this.fileContent = content;
    this.lineList = listOfLineNumber;
    this.period = parserPeriod;
    currentSection = fileContent.getLastSection();
  }

  /**
   * traverse markdown tree that parsed Pegdown.
   *
   * @param astRoot Pegdown RootNode
   *                (markdown tree that is parsed pegdown parser)
   * @return file content that re-parse Pegdown RootNode.
   * @throws org.unigram.docvalidator.util.DocumentValidatorException
   * Fail to traverse markdown tree
   */
  public FileContent toFileContent(RootNode astRoot)
      throws DocumentValidatorException {
    try {
      checkArgNotNull(astRoot, "astRoot");
      astRoot.accept(this);
    } catch (Throwable e) {
      LOG.error("Fail to traverse RootNode.");
      throw new DocumentValidatorException("Fail to traverse RootNode.", e);
    }
    return fileContent;
  }

  private void fixSentence() {
    // 1. remain sentence append currentSection
    //TODO need line number
    List<Sentence> sentences = createSentenceList();
    for (Sentence sentence : sentences) {
      currentSection.appendSentence(sentence);
    }
  }

  private void addCandidateSentence(int lineNum, String text) {
    addCandidateSentence(lineNum, text, null);
  }

  private void addCandidateSentence(int lineNum, String text, String link) {
    candidateSentences.add(new CandidateSentence(lineNum, text, link));
  }

  private int lineNumberFromStartIndex(int startIndex) {
    int lineNum = 0;
    // TODO test
    for (int end : lineList) {
      if (end < startIndex) {
        break;
      }
      lineNum++;
    }
    return lineNum;
  }

  private Printer printer = new Printer();

  private String printChildrenToString(SuperNode node) {
    // FIXME check usecase
    Printer priorPrinter = printer;
    printer = new Printer();
    visitChildren(node);
    String result = printer.getString();
    printer = priorPrinter;
    return result;
  }

  private List<Sentence> createSentenceList() {
    List<Sentence> newSentences = new ArrayList<Sentence>();
    Sentence currentSentence = null;
    StringBuffer sentenceContent =
        new StringBuffer();
    for (CandidateSentence candidateSentence : candidateSentences) {
      String remain =
          ParseUtils.extractSentencesWithoutLastSentence(
              candidateSentence.getSentence(),
              this.period, newSentences);

      //TODO refactor StringUtils...
      if (StringUtils.isNotEmpty(remain)) {
        if (currentSentence != null) {
          currentSentence.content += candidateSentence.getSentence();
        } else {
          currentSentence = new Sentence(remain,
              candidateSentence.getLineNum());
          newSentences.add(currentSentence);
        }
        // FIXME check: pegdown extract 1 candidate sentence to 1 link?
        if (candidateSentence.getLink() != null) {
          currentSentence.links.add(candidateSentence.getLink());
        }
      } else {
        currentSentence = null;
      }

    }
    candidateSentences.clear();
    return newSentences;
  }

  //FIXME wikiparser have same method. pull up or expand to utils
  private boolean addChild(Section candidate, Section child) {
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

  private void appendSection(HeaderNode headerNode) {
    // 1. remain sentence flush to current section
    fixSentence();

    // 2. retrieve children for header content create;
    visitChildren(headerNode);
    List<Sentence> headerContents = createSentenceList();

    // 3. create new Section
    Section newSection = new Section(headerNode.getLevel(), headerContents);
    fileContent.appendSection(newSection);
    //FIXME move this check process to addChild
    if (!addChild(currentSection, newSection)) {
      LOG.warn("Failed to add parent for a Section: "
          + newSection.getHeaderContents().next());
    }
    currentSection = newSection;
  }

  @Override
  public void visit(AbbreviationNode abbreviationNode) {
    // current not implement

  }

  @Override
  public void visit(AutoLinkNode autoLinkNode) {
    // TODO GitHub Markdown Extension
    addCandidateSentence(
        lineNumberFromStartIndex(autoLinkNode.getStartIndex()),
        autoLinkNode.getText(), autoLinkNode.getText());
  }

  @Override
  public void visit(BlockQuoteNode blockQuoteNode) {
    visitChildren(blockQuoteNode);
  }

  @Override
  public void visit(CodeNode codeNode) {
    addCandidateSentence(lineNumberFromStartIndex(
        codeNode.getStartIndex()), codeNode.getText());
  }

  @Override
  public void visit(ExpImageNode expImageNode) {
    // TODO exp image not implement
  }

  @Override
  public void visit(ExpLinkNode expLinkNode) {
    // title attribute don't use
    String linkName = printChildrenToString(expLinkNode);
    // FIXME how to handle url, if linkName include period character?
    // TODO temporary implementation
    CandidateSentence lastCandidateSentence =
        candidateSentences.get(candidateSentences.size() - 1);
    lastCandidateSentence.setLink(expLinkNode.url);
  }

  @Override
  public void visit(HeaderNode headerNode) {
    appendSection(headerNode);
  }

  // list part
  @Override
  public void visit(BulletListNode bulletListNode) {
    //FIXME test and check
    // TODO handle bulletListNode and orderdListNode
    if (itemDepth == 0) {
      fixSentence();
      currentSection.appendListBlock();
    } else {
      List<Sentence> sentences = createSentenceList();
      currentSection.appendListElement(itemDepth, sentences);
    }
    itemDepth++;
    visitChildren(bulletListNode);
    itemDepth--;
  }

  @Override
  public void visit(OrderedListNode orderedListNode) {
    // TODO handle bulletListNode and orderdListNode
    if (itemDepth == 0) {
      fixSentence();
      currentSection.appendListBlock();
    } else {
      List<Sentence> sentences = createSentenceList();
      currentSection.appendListElement(itemDepth, sentences);
    }
    itemDepth++;
    visitChildren(orderedListNode);
    itemDepth--;
  }


  @Override
  public void visit(ListItemNode listItemNode) {
    visitChildren(listItemNode);
    List<Sentence> sentences = createSentenceList();
    // TODO for nested ListNode process
    if (sentences != null && sentences.size() > 0) {
      currentSection.appendListElement(itemDepth, sentences);
    }
  }


  @Override
  public void visit(ParaNode paraNode) {
    currentSection.appendParagraph(new Paragraph());
    visitChildren(paraNode);
    fixSentence();
  }

  @Override
  public void visit(RootNode rootNode) {
    // create refNode reference map
    for (ReferenceNode refNode : rootNode.getReferences()) {
      //visitChildren(refNode);
      //TODO need to decide reference node handling
    }
    // create abbrNode reference map
    for (AbbreviationNode abbrNode : rootNode.getAbbreviations()) {
      //visitChildren(abbrNode);
      //TODO need to decide abbreviation node handling
    }
    visitChildren(rootNode);
  }

  @Override
  public void visit(SimpleNode simpleNode) {
    //TODO check detail
    switch (simpleNode.getType()) {
      case Linebreak:
        break;
      case Nbsp:
        break;
      case HRule:
        break;
      case Apostrophe:
        addCandidateSentence(
            lineNumberFromStartIndex(simpleNode.getStartIndex()), "'");
        break;
      case Ellipsis:
        addCandidateSentence(
            lineNumberFromStartIndex(simpleNode.getStartIndex()), "...");
        break;
      case Emdash:
        addCandidateSentence(
            lineNumberFromStartIndex(simpleNode.getStartIndex()), "–");
        break;
      case Endash:
        addCandidateSentence(
            lineNumberFromStartIndex(simpleNode.getStartIndex()), "—");
        break;
      default:
        LOG.warn("Illegal SimpleNode:[" + simpleNode.toString() + "]");
    }
  }

  @Override
  public void visit(SpecialTextNode specialTextNode) {
    // TODO to sentence
    addCandidateSentence(
        lineNumberFromStartIndex(
            specialTextNode.getStartIndex()), specialTextNode.getText());
  }

  @Override
  public void visit(StrikeNode strikeNode) {
    visitChildren(strikeNode);
  }

  @Override
  public void visit(StrongEmphSuperNode strongEmphSuperNode) {
    visitChildren(strongEmphSuperNode);
  }


  @Override
  public void visit(TextNode textNode) {
    // to sentence, if sentence breaker appear
    // append remain sentence, if sentence breaker not appear
    addCandidateSentence(
        lineNumberFromStartIndex(
            textNode.getStartIndex()), textNode.getText());
    // for printChildrenToString
    printer.print(textNode.getText());
  }

  // code block
  @Override
  public void visit(VerbatimNode verbatimNode) {
    // paragraph?
    // FIXME implement
    // TODO remove tag
  }


  @Override
  public void visit(QuotedNode quotedNode) {
    //TODO quoted not implement
  }

  @Override
  public void visit(ReferenceNode referenceNode) {
    //TODO reference node not implement
  }

  @Override
  public void visit(RefImageNode refImageNode) {
    // TODO reference image require implement
    // to expand sentence
  }

  @Override
  public void visit(RefLinkNode refLinkNode) {
    // TODO reference link require implement
    // to expand sentence
    String linkName = printChildrenToString(refLinkNode);
    String url = getRefLinkUrl(refLinkNode.referenceKey, linkName);
    // FIXME how to handle url, if linkName include period character?
    // TODO temporary implementation
    CandidateSentence lastCandidateSentence =
        candidateSentences.get(candidateSentences.size() - 1);
    lastCandidateSentence.setLink(url);
  }

  private String getRefLinkUrl(SuperNode referenceKey, String linkName) {
    //FIXME need to implement
    return "";
  }

  // html part
  @Override
  public void visit(HtmlBlockNode htmlBlockNode) {
    // TODO html block not implement
  }

  @Override
  public void visit(InlineHtmlNode inlineHtmlNode) {
    // TODO inline html not implement
  }

  @Override
  public void visit(MailLinkNode mailLinkNode) {
    // TODO mail link not implement.
  }

  @Override
  public void visit(WikiLinkNode wikiLinkNode) {
    // TODO not supported
    // no handle
  }

  @Override
  public void visit(SuperNode superNode) {
    visitChildren(superNode);
  }

  @Override
  public void visit(Node node) {
    // not necessary implement, for pegdown parser plugin
  }

  // handle definition list
  @Override
  public void visit(DefinitionListNode definitionListNode) {
    // TODO dl tag not implement
  }

  @Override
  public void visit(DefinitionNode definitionNode) {
    // TODO dt tag not implement
  }

  @Override
  public void visit(DefinitionTermNode definitionTermNode) {
    // TODO dd tag not implement
  }

  // handle Table contents
  // current not implemented
  @Override
  public void visit(TableBodyNode tableBodyNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableCaptionNode tableCaptionNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableCellNode tableCellNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableColumnNode tableColumnNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableHeaderNode tableHeaderNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableNode tableNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableRowNode tableRowNode) {
    // TODO not implement
  }

}
