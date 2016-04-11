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
import cc.redpen.parser.BaseDocumentParser;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.tokenizer.RedPenTokenizer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class LineParser extends BaseDocumentParser {

    @Override
    public Document parse(InputStream io, Optional<String> fileName,
                          SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer) throws RedPenException {
        Document.DocumentBuilder documentBuilder = Document.builder(tokenizer);
        fileName.ifPresent(documentBuilder::setFileName);

        Model model = new Model(sentenceExtractor);

        // add the lines from the input to the abstract model
        this.populateModel(model, io);

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

            if (model.isMore()) {
                // check for new sections
                if (model.getCurrentLine().getSectionLevel() > 0) {
                    builder.addSection(
                            model.getCurrentLine().getSectionLevel(),
                            model.convertToSentences(model.getCurrentLine())
                    );
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
    }

    protected abstract void populateModel(Model model, InputStream io);
}
