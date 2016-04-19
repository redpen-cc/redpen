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
import java.util.ArrayList;
import java.util.List;
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
        public String type;
    }

    class ReVIEWBlock {
        public String type = "";
        public List<String> properties = new ArrayList<>();
        public boolean isOpen = false;
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

        // check for block end
        if (state.inBlock) {
            if (target.line.startsWith("//}") && target.line.length() == 3) {
                state.inBlock = false;
            }
            line.erase();
        }

        // test for various block starts
        if ((!state.inBlock) && target.line.startsWith("//")) {
            ReVIEWBlock block = parseBlock(line);
            line.erase();
            state.inBlock = block.isOpen;
        }

        // handling comments
        if (target.line.startsWith("#@#")) {
            line.erase();
        }

        // enclosed inline markups
        line.eraseEnclosure("@<list>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<code>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<img>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<table>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<fn>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<chap>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<title>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<chapref>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<bou>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<kw>{", "}", Line.EraseStyle.Markers); // TODO: extract keyword only
        line.eraseEnclosure("@<chapter>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<ruby>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<ami>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<b>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<i>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<strong>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<em>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<tt>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<tti>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<ttb>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<u>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<br>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<m>{", "}", Line.EraseStyle.All);
        line.eraseEnclosure("@<icon>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<uchar>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<href>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<column>{", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<raw>{html|", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<raw>{latex|", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<raw>{idgxml|", "}", Line.EraseStyle.Markers);
        line.eraseEnclosure("@<raw>{top|", "}", Line.EraseStyle.Markers);

        //opening annotation for preprocessor
        line.eraseEnclosure("#@warn(", ")", Line.EraseStyle.All);
        line.eraseEnclosure("@comment(", ")", Line.EraseStyle.All);
        line.eraseEnclosure("#@mapfile(", ")", Line.EraseStyle.All);
        line.eraseEnclosure("#@maprange(", ")", Line.EraseStyle.All);
        line.eraseEnclosure("#@mapoutput(", ")", Line.EraseStyle.All);

        //closing annotation for preprocessor
        if (target.line.startsWith("#@end")) {
            line.erase();
        }

        // list
        // headers
    }

    ReVIEWBlock parseBlock(Line line) {
        ReVIEWBlock block = new ReVIEWBlock();
        String text = line.getText();
        // detect type
        int openIdx = text.indexOf("[");
        if (openIdx > 0) {
            block.type = text.substring(2, openIdx);
            // detect properties
            int closeIdx = text.indexOf("]");
            while(closeIdx > 0) {
                block.properties.add(text.substring(openIdx+1, closeIdx));
                openIdx = text.indexOf("[", openIdx+1);
                closeIdx = text.indexOf("]", closeIdx+1);
            }
        } else {
            block.type = text.substring(2, text.indexOf("{"));
        }

        // detect open block
        if (text.indexOf("{") > 0) {
            block.isOpen = true;
        }
        return block;
    }
}
