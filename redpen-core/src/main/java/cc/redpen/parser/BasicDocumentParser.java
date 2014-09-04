/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.SymbolTable;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.symbol.DefaultSymbols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Parser class containing common procedures to
 * implements the concrete Parser classes.
 */
public abstract class BasicDocumentParser implements Parser {
    private static final Logger LOG = LoggerFactory.getLogger(
            BasicDocumentParser.class);
    protected DocumentCollection.Builder builder;
    private SentenceExtractor sentenceExtractor;

    @Override
    public Document generateDocument(String fileName)
            throws RedPenException {
        Document document ;
        try {
            try (InputStream inputStream = this.loadStream(fileName)) {
                document = this.generateDocument(inputStream);
                if (document != null) {
                    document.setFileName(fileName);
                }
            }
        } catch (IOException e) {
            throw new RedPenException(e);
        }
        return document;
    }

    /**
     * Given configuration , return basic configuration settings.
     *
     * @param configuration   object containing configuration settings
     * @param documentBuilder Builder object of DocumentCollection
     */
    public final void initialize(Configuration configuration,
                                 DocumentCollection.Builder documentBuilder) throws
            RedPenException {
        if (configuration == null) {
            throw new RedPenException("Given configuration is null");
        }
        if (configuration.getSymbolTable() == null) {
            throw new RedPenException(
                    "Character table in the given configuration is null");
        }

        SymbolTable symbolTable = configuration.getSymbolTable();
        List<String> periods = extractPeriods(symbolTable);
        List<String> rightQuotations = extractRightQuotations(symbolTable);

        this.sentenceExtractor = new SentenceExtractor(periods, rightQuotations);
        this.builder = documentBuilder;
    }

    private List<String> extractRightQuotations(SymbolTable symbolTable) {
        List<String> rightQuotations = new ArrayList<>();
        if (symbolTable.containsSymbol("RIGHT_SINGLE_QUOTATION_MARK")) {
            rightQuotations.add(
                    symbolTable.getSymbol("RIGHT_SINGLE_QUOTATION_MARK").getValue());
        } else {
            rightQuotations.add(
                    DefaultSymbols.getInstance().get("RIGHT_SINGLE_QUOTATION_MARK").getValue());
        }
        if (symbolTable.containsSymbol("RIGHT_DOUBLE_QUOTATION_MARK")) {
            rightQuotations.add(
                    symbolTable.getSymbol("RIGHT_DOUBLE_QUOTATION_MARK").getValue());
        } else {
            rightQuotations.add(
                    DefaultSymbols.getInstance().get("RIGHT_DOUBLE_QUOTATION_MARK").getValue());
        }
        for (String rightQuotation : rightQuotations) {
            LOG.info("\"" + rightQuotation + "\" is added as a end of right quotation character.");
        }
        return rightQuotations;
    }

    private List<String> extractPeriods(SymbolTable symbolTable) {
        List<String> periods = new ArrayList<>();
        if (symbolTable.containsSymbol("FULL_STOP")) {
            periods.add(
                    symbolTable.getSymbol("FULL_STOP").getValue());
        } else {
            periods.add(
                    DefaultSymbols.getInstance().get("FULL_STOP").getValue());
        }

        if (symbolTable.containsSymbol("QUESTION_MARK")) {
            periods.add(
                    symbolTable.getSymbol("QUESTION_MARK").getValue());
        } else {
            periods.add(
                    DefaultSymbols.getInstance().get("QUESTION_MARK").getValue());
        }

        if (symbolTable.containsSymbol("EXCLAMATION_MARK")) {
            periods.add(
                    symbolTable.getSymbol("EXCLAMATION_MARK").getValue());
        } else {
            periods.add(
                    DefaultSymbols.getInstance().get("EXCLAMATION_MARK").getValue());
        }

        for (String period : periods) {
            LOG.info("\"" + period + "\" is added as a end of sentence character");
        }
        return periods;
    }

    /**
     * create BufferedReader from InputStream is.
     *
     * @param is InputStream using to parse
     * @return BufferedReader created from InputStream
     * @throws cc.redpen.RedPenException if InputStream is not
     *                                   supported UTF-8 encoding
     */
    protected BufferedReader createReader(InputStream is)
            throws RedPenException {
        if (is == null) {
            throw new RedPenException("input stream is null");
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RedPenException(
                    "does not support UTF-8 encoding", e);
        }
        return br;
    }

    protected final InputStream loadStream(String fileName)
            throws RedPenException {
        InputStream inputStream;
        if (fileName == null || fileName.equals("")) {
            throw new RedPenException("input file was not specified.");
        } else {
            try {
                inputStream = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                throw new RedPenException("Input file is not found", e);
            }
        }
        return inputStream;
    }

    /**
     * Get SentenceExtractor object.
     *
     * @return sentence extractor object
     */
    protected SentenceExtractor getSentenceExtractor() {
        return sentenceExtractor;
    }
}
