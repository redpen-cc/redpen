/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.parser.markdown;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.parser.LineOffset;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.util.Pair;
import org.parboiled.common.StringUtils;
import org.pegdown.Printer;
import org.pegdown.ast.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.parboiled.common.Preconditions.checkArgNotNull;

/**
 * Using Pegdown Parser.
 *
 * @see <a href="https://github.com/sirthias/pegdown">pegdown</a>
 */
public class ToFileContentSerializer implements Visitor {

    private static final Logger LOG =
            LoggerFactory.getLogger(ToFileContentSerializer.class);
    private final Map<String, ReferenceNode> references = new HashMap<>();
    private Document.DocumentBuilder builder = null;
    private SentenceExtractor sentenceExtractor;
    private int itemDepth = 0;
    private List<Integer> lineList = null;
    // TODO multi period character not supported
    private List<CandidateSentence> candidateSentences = new ArrayList<>();
    private Printer printer = new Printer();

    /**
     * Constructor.
     *
     * @param docBuilder       DocumentBuilder
     * @param listOfLineNumber the list of line number
     * @param extractor        utility object to extract a sentence list
     */
    public ToFileContentSerializer(Document.DocumentBuilder docBuilder,
                                   List<Integer> listOfLineNumber,
                                   SentenceExtractor extractor) {
        this.builder = docBuilder;
        this.lineList = listOfLineNumber;
        this.sentenceExtractor = extractor;
    }

    protected void visitChildren(SuperNode node) {
        for (Node child : node.getChildren()) {
            child.accept(this);
        }
    }

    /**
     * Traverse markdown tree that parsed Pegdown.
     *
     * @param astRoot Pegdown RootNode
     *                (markdown tree that is parsed pegdown parser)
     * @return file content that re-parse Pegdown RootNode.
     * @throws cc.redpen.RedPenException Fail to traverse markdown tree
     */
    public Document toFileContent(RootNode astRoot)
            throws RedPenException {
        try {
            checkArgNotNull(astRoot, "astRoot");
            astRoot.accept(this);
        } catch (NullPointerException e) {
            LOG.error("Fail to traverse RootNode.");
            throw new RedPenException("Fail to traverse RootNode.", e);
        }
        return builder.build();
    }

    private void fixSentence() {
        // 1. remain sentence append currentSection
        //TODO need line number
        List<Sentence> sentences = createSentenceList();
        for (Sentence sentence : sentences) {
            builder.addSentence(sentence);
        }
    }

    private void addCandidateSentence(int lineNum, String text, int positionOffset) {
        addCandidateSentence(lineNum, text, positionOffset, null);
    }

    private void addCandidateSentence(int lineNum, String text, int positionOffset, String link) {
        candidateSentences.add(new CandidateSentence(lineNum, text, link, positionOffset));
    }

    private int getLineNumberFromStartIndex(int startIndex) {
        int lineNum = 1;
        // TODO test
        for (int end : lineList) {
            if (startIndex < end) {
                break;
            }
            lineNum++;
        }
        return lineNum;
    }

    private int getLineStartIndex(int lineNumber) {
        if (lineNumber == 1) {
            return 0;
        } else {
            return lineList.get(lineNumber-2);
        }
    }

    private String printChildrenToString(SuperNode node) {
        // FIXME validate usecase
        Printer priorPrinter = printer;
        printer = new Printer();
        visitChildren(node);
        String result = printer.getString();
        printer = priorPrinter;
        return result;
    }

    private List<Sentence> createSentenceList() {
        List<Sentence> outputSentences = new ArrayList<>();
        Optional<MergedCandidateSentence> mergedCandidateSentence =
                MergedCandidateSentence.merge(candidateSentences);
        mergedCandidateSentence.ifPresent(m ->
            extractSentences(m, outputSentences)
        );
        candidateSentences.clear();
        return outputSentences;
    }

    private List<Sentence> extractSentences(MergedCandidateSentence mergedCandidateSentence,
            List<Sentence> outputSentences) {
        List<Pair<Integer, Integer>> sentencePositions = new ArrayList<>();
        final String line = mergedCandidateSentence.getContents();
        int lastPosition = sentenceExtractor.extract(line , sentencePositions);

        for (Pair<Integer, Integer> sentencePosition : sentencePositions) {
            List<LineOffset> offsetMap =
                    mergedCandidateSentence.getOffsetMap().subList(sentencePosition.first,
                    sentencePosition.second);
            outputSentences.add(new Sentence(line.substring(
                    sentencePosition.first, sentencePosition.second), offsetMap,
                    mergedCandidateSentence.getRangedLinks(sentencePosition.first, sentencePosition.second - 1)));
        }
        if (lastPosition < mergedCandidateSentence.getContents().length()) {
            List<LineOffset> offsetMap = mergedCandidateSentence.getOffsetMap().subList(lastPosition,
                    mergedCandidateSentence.getContents().length());
            outputSentences.add(new Sentence(line.substring(
                    lastPosition, mergedCandidateSentence.getContents().length()),
                    offsetMap,
                    mergedCandidateSentence.getRangedLinks(lastPosition,
                            mergedCandidateSentence.getContents().length())));
        }
        return outputSentences;
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

        // To deal with a header content as a paragraph
        if (headerContents.size() > 0) {
            headerContents.get(0).setIsFirstSentence(true);
        }

        // 3. create new Section
        Section currentSection = builder.getLastSection();
        builder.appendSection(new Section(headerNode.getLevel(), headerContents));
        //FIXME move this validate process to addChild
        if (!addChild(currentSection, builder.getLastSection())) {
            LOG.warn("Failed to add parent for a Section: "
                    + builder.getLastSection().getHeaderContents().get(0));
        }
    }

    public void visit(AbbreviationNode abbreviationNode) {
        // current not implement

    }

    public void visit(AutoLinkNode autoLinkNode) {
        // TODO GitHub Markdown Extension
        int lineNumber = getLineNumberFromStartIndex(autoLinkNode.getStartIndex());
        addCandidateSentence(
                lineNumber,
                autoLinkNode.getText(),
                autoLinkNode.getStartIndex() - getLineStartIndex(lineNumber),
                autoLinkNode.getText());
    }

    public void visit(BlockQuoteNode blockQuoteNode) {
        visitChildren(blockQuoteNode);
    }

    public void visit(CodeNode codeNode) {
        int lineNumber = getLineNumberFromStartIndex(codeNode.getStartIndex());
        addCandidateSentence(getLineNumberFromStartIndex(
                        codeNode.getStartIndex()),
                codeNode.getText(),
                codeNode.getStartIndex() - getLineStartIndex(lineNumber));
    }

    public void visit(ExpImageNode expImageNode) {
        // TODO exp image not implement
    }

    public void visit(ExpLinkNode expLinkNode) {
        // title attribute don't use
        String linkName = printChildrenToString(expLinkNode);
        // FIXME how to handle url, if linkName includes period character?
        // TODO temporary implementation
        CandidateSentence lastCandidateSentence =
                candidateSentences.get(candidateSentences.size() - 1);
        lastCandidateSentence.setLink(expLinkNode.url);
    }

    public void visit(HeaderNode headerNode) {
        appendSection(headerNode);
    }

    // list part
    public void visit(BulletListNode bulletListNode) {
        //FIXME test and validate
        // TODO handle bulletListNode and orderdListNode
        if (itemDepth == 0) {
            fixSentence();
            builder.addListBlock();
        } else {
            List<Sentence> sentences = createSentenceList();
            builder.addListElement(itemDepth, sentences);
        }
        itemDepth++;
        visitChildren(bulletListNode);
        itemDepth--;
    }

    public void visit(OrderedListNode orderedListNode) {
        // TODO handle bulletListNode and orderdListNode
        if (itemDepth == 0) {
            fixSentence();
            builder.addListBlock();
        } else {
            List<Sentence> sentences = createSentenceList();
            builder.addListElement(itemDepth, sentences);
        }
        itemDepth++;
        visitChildren(orderedListNode);
        itemDepth--;
    }

    public void visit(ListItemNode listItemNode) {
        visitChildren(listItemNode);
        List<Sentence> sentences = createSentenceList();
        // TODO for nested ListNode process
        if (sentences != null && sentences.size() > 0) {
            builder.addListElement(itemDepth, sentences);
        }
    }


    public void visit(ParaNode paraNode) {
        builder.addParagraph();
        visitChildren(paraNode);
        fixSentence();
    }

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

    public void visit(SimpleNode simpleNode) {
        //TODO validate detail
        int lineNumber = getLineNumberFromStartIndex(simpleNode.getStartIndex());

        switch (simpleNode.getType()) {
            case Linebreak:
                addCandidateSentence(
                        getLineNumberFromStartIndex(simpleNode.getStartIndex() + 1),
                        sentenceExtractor.getBrokenLineSeparator(), 0); //NOTE: column Offset of Linebreak should be always 0.
                break;
            case Nbsp:
                break;
            case HRule:
                break;
            case Apostrophe:
                addCandidateSentence(
                        getLineNumberFromStartIndex(simpleNode.getStartIndex()),
                        "'", simpleNode.getStartIndex() - getLineStartIndex(lineNumber));
                break;
            case Ellipsis:
                addCandidateSentence(
                        getLineNumberFromStartIndex(simpleNode.getStartIndex()),
                        "...", simpleNode.getStartIndex() - getLineStartIndex(lineNumber));
                break;
            case Emdash:
                addCandidateSentence(
                        getLineNumberFromStartIndex(simpleNode.getStartIndex()),
                        "–", simpleNode.getStartIndex() - getLineStartIndex(lineNumber));
                break;
            case Endash:
                addCandidateSentence(
                        getLineNumberFromStartIndex(simpleNode.getStartIndex()),
                        "—", simpleNode.getStartIndex() - getLineStartIndex(lineNumber));
                break;
            default:
                LOG.warn("Illegal SimpleNode:[" + simpleNode.toString() + "]");
        }
    }

    public void visit(SpecialTextNode specialTextNode) {
        // TODO to sentence
        int lineNumber = getLineNumberFromStartIndex(specialTextNode.getStartIndex());
        addCandidateSentence(
                getLineNumberFromStartIndex(
                        specialTextNode.getStartIndex()),
                specialTextNode.getText(),
                specialTextNode.getStartIndex() - getLineStartIndex(lineNumber));
    }

    public void visit(StrikeNode strikeNode) {
        visitChildren(strikeNode);
    }

    public void visit(StrongEmphSuperNode strongEmphSuperNode) {
        visitChildren(strongEmphSuperNode);
    }

    public void visit(TextNode textNode) {
        int lineNumber = getLineNumberFromStartIndex(textNode.getStartIndex());
        // to sentence, if sentence breaker appear
        // append remain sentence, if sentence breaker not appear
        addCandidateSentence(
                getLineNumberFromStartIndex(textNode.getStartIndex()),
                textNode.getText(),
                textNode.getStartIndex() - getLineStartIndex(lineNumber));
        // for printChildrenToString
        printer.print(textNode.getText());
    }

    // code block
    public void visit(VerbatimNode verbatimNode) {
        // paragraph?
        // FIXME implement
        // TODO remove tag
    }

    public void visit(QuotedNode quotedNode) {
        //TODO quoted not implement
    }

    public void visit(ReferenceNode referenceNode) {
        //TODO reference node not implement
    }

    public void visit(RefImageNode refImageNode) {
        // TODO reference image require implement
        // to expand sentence
    }

    public void visit(RefLinkNode refLinkNode) {
        // TODO reference link require implement
        // to expand sentence
        String linkName = printChildrenToString(refLinkNode);
        String url = getRefLinkUrl(refLinkNode.referenceKey, linkName);
        // FIXME how to handle url, if linkName include period character?
        // TODO temporary implementation
        CandidateSentence lastCandidateSentence =
                candidateSentences.get(candidateSentences.size() - 1);
        if (StringUtils.isNotEmpty(url)) {
            lastCandidateSentence.setLink(url);
        } else {
            lastCandidateSentence.setContent(
                    lastCandidateSentence.getContent());
        }
    }

    private String getRefLinkUrl(SuperNode referenceKey, String linkName) {
        //FIXME need to implement
        ReferenceNode refNode = references.get(linkName);
        StringBuilder sb = new StringBuilder();
        if (refNode != null) {
            sb.append(refNode.getUrl());
        }
        return sb.toString();
    }

    // html part

    public void visit(HtmlBlockNode htmlBlockNode) {
        // TODO html block not implement
    }


    public void visit(InlineHtmlNode inlineHtmlNode) {
        // TODO inline html not implement
    }

    public void visit(MailLinkNode mailLinkNode) {
        // TODO mail link not implement.
    }

    public void visit(WikiLinkNode wikiLinkNode) {
        // TODO not supported
        // no handle
    }

    public void visit(SuperNode superNode) {
        visitChildren(superNode);
    }

    public void visit(Node node) {
        // not necessary implement, for pegdown parser plugin
    }

    // handle definition list
    public void visit(DefinitionListNode definitionListNode) {
        // TODO dl tag not implement
    }

    public void visit(DefinitionNode definitionNode) {
        // TODO dt tag not implement
    }

    public void visit(DefinitionTermNode definitionTermNode) {
        // TODO dd tag not implement
    }

    // handle Table contents
    // current not implemented
    public void visit(TableBodyNode tableBodyNode) {
        // TODO not implement
    }
    public void visit(TableCaptionNode tableCaptionNode) {
        // TODO not implement
    }

    public void visit(TableCellNode tableCellNode) {
        // TODO not implement
    }

    public void visit(TableColumnNode tableColumnNode) {
        // TODO not implement
    }

    public void visit(TableHeaderNode tableHeaderNode) {
        // TODO not implement
    }

    public void visit(TableNode tableNode) {
        // TODO not implement
        visitChildren(tableNode);
    }

    public void visit(TableRowNode tableRowNode) {
        // TODO not implement
    }
}
