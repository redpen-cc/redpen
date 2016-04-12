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
package cc.redpen.parser.review;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.parser.SentenceExtractor;
import cc.redpen.parser.common.Line;
import cc.redpen.parser.common.LineParser;
import cc.redpen.parser.common.Model;
import cc.redpen.tokenizer.RedPenTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Optional;


public class ReVIEWParser extends LineParser {
    private static final Logger LOG = LoggerFactory.getLogger(ReVIEWParser.class);

    /**
     * current parser state
     */
    private class State {
        // are we in a block
        public boolean inBlock = false;
        // are we in a list?
        public boolean inList = false;
        // should we erase lines within the current block?
        public boolean eraseBlock = true;
        // the sort of block we are in
        public char blockMarker = 0;
        // length of the lead block marker
        public int blockMarkerLength = 0;
    }


    @Override
    public Document parse(InputStream inputStream, Optional<String> fileName,
                          SentenceExtractor sentenceExtractor,
                          RedPenTokenizer tokenizer) throws RedPenException {
        Document.DocumentBuilder documentBuilder = Document.builder(tokenizer);
        fileName.ifPresent(documentBuilder::setFileName);
        Model model = new Model(sentenceExtractor);
        populateModel(model, inputStream);
        return documentBuilder.build();
    }

    protected void populateModel(Model model, InputStream io) {
        State state = new State();
        BufferedReader reader = createReader(io);

        int lineno = 0;
        try {
            // add the lines to the model
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lineno++;
                model.add(new ReVIEWLine(line, lineno));
            }
            reader.close();

            for (model.rewind(); model.isMore(); model.getNextLine()) {
                processLine(model.getCurrentLine(), model, state);
            }

            //TODO: implementation
            //processHeader(model);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Exception when parsing Re:VIEW file", e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Re:VIEW parser model (X=erased line,[=block,section-listlevel-lineno,*=list item):\n" + model.toString());
        }
    }

    private void processLine(Line line, Model model, State state) {
        if (line.isErased()) { return; }

        TargetLine target = new TargetLine(line,
                model.getLine(line.getLineNo() - 1),
                model.getLine(line.getLineNo() + 1));

        // check for block
        // test for various block starts
        // handling comments
        // enclosed directives
        // headers
        // list
        // erase inline markups
    }
}
