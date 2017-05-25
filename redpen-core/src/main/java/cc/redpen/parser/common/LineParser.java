/*
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.parser.common;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.parser.BaseDocumentParser;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.tokenizer.RedPenTokenizer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cc.redpen.parser.ParserUtils.addChild;

public abstract class LineParser extends BaseDocumentParser {

    /**
     * Target line of parser
     */
    static public class TargetLine {
        // target line
        public Line line;
        // previous line of target line
        public Line previousLine;
        // next line of target line
        public Line nextLine;
        // fist character of target line
        public char firstChar;
        // second character of target line
        public char secondChar;

        public TargetLine(Line line, Line previousLine,
                          Line nextLine) {
            this.line = line;
            this.previousLine = previousLine;
            this.nextLine = nextLine;
            this.firstChar = line.charAt(0);
            this.secondChar = line.charAt(1);
        }
    }

    @Override
    public Document parse(InputStream io, Optional<String> fileName,
                          SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer) throws RedPenException {
        Document.DocumentBuilder documentBuilder = Document.builder(tokenizer);
        fileName.ifPresent(documentBuilder::setFileName);

        Model model = new Model(sentenceExtractor);

        // add the lines from the input to the abstract model
        this.populateModel(model, io);

        // register the preprocessor rules
        documentBuilder.setPreprocessorRules(model.getPreprocessorRules());

        // convert the model to a redpen document
        this.convertModel(model, documentBuilder);

        return documentBuilder.build();
    }

    /**
     * Conver the parser's model to the RedPen document model
     *
     * @param model model to convert
     * @param builder doc builder
     */
    protected void convertModel(Model model, Document.DocumentBuilder builder) {
        model.rewind();

        // add a header if there isn't one in the model
        if ((model.getCurrentLine() != null) && (model.getCurrentLine().getSectionLevel() == 0)) {
            builder.addSection(0);
        }

        while (model.isMore()) {
            // skip blank lines
            while (model.isMore() && model.getCurrentLine().isEmpty()) {
                model.getNextLine();
            }
            if (!model.isMore()) { return; } // no contents

            // check for new sections
            if (model.getCurrentLine().getSectionLevel() > 0) {
                Section currentSection = builder.getLastSection();
                builder.addSection(
                        model.getCurrentLine().getSectionLevel(),
                        model.convertToSentences(model.getCurrentLine())
                );
                addChild(currentSection, builder.getLastSection());
                model.getNextLine();
            }
            // check for a list item
            else if (model.getCurrentLine().isListStart()) {
                List<Line> listElementLines = new ArrayList<>();
                int listLevel = model.getCurrentLine().getListLevel();

                // add the list start line
                listElementLines.add(model.getCurrentLine());

                // test the following lines to see if they continue this list item
                model.getNextLine();
                while (model.isMore() &&
                        !model.getCurrentLine().isListStart() &&
                        (model.getCurrentLine().getListLevel() == listLevel)) {
                    listElementLines.add(model.getCurrentLine());
                    model.getNextLine();
                }
                builder.addListElement(listLevel, model.convertToSentences(listElementLines));
            }
            // process a paragraph
            else {
                List<Line> paragraphLines = new ArrayList<>();
                // current line can't be empty, so this loop will enter at least once
                while (model.isMore() && !model.getCurrentLine().isEmpty()) {
                    paragraphLines.add(model.getCurrentLine());
                    model.getNextLine();
                }
                builder.addParagraph();
                model.convertToSentences(paragraphLines).forEach(builder::addSentence);
            }
        }
    }

    protected abstract void populateModel(Model model, InputStream io);
}
