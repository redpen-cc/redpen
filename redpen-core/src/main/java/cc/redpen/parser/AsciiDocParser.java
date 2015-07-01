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

package cc.redpen.parser;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.tokenizer.RedPenTokenizer;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.RedPenTreeProcessor;
import org.pegdown.ParsingTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Parser for AsciiDoc format, utilizing AsciiDoctorJ.<br/>
 * <p/>
 * AsciiDoc's syntax and grammar is documented at @see http://asciidoc.org/
 */
public class AsciiDocParser extends BaseDocumentParser {
    private static final Logger LOG = LoggerFactory.getLogger(AsciiDocParser.class);

    @Override
    public Document parse(InputStream io, Optional<String> fileName, SentenceExtractor sentenceExtractor, RedPenTokenizer tokenizer) throws RedPenException {
        Document.DocumentBuilder documentBuilder = new Document.DocumentBuilder(tokenizer);
        fileName.ifPresent(documentBuilder::setFileName);
        BufferedReader reader = createReader(io);
        try {
            // create an asciidoctor instance
            Asciidoctor asciidoctor = Asciidoctor.Factory.create();

            // register our 'redpen' backend
            InputStream rubySource = new ByteArrayInputStream(AsciiDoctorRedPenRubySource.SOURCE_TEXT.getBytes("UTF-8"));
            asciidoctor.rubyExtensionRegistry().loadClass(rubySource);

            // set our 'redpen' backend as the active AsciiDoctor backend
            Map<String, Object> options = new HashMap<>();
            options.put("backend", "redpen");

            // tell AsciiDoctor to record the source line numbers
            options.put("sourcemap", true);
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sourcemap", true);
            options.put("attributes", attributes);

            // register our documentbuilding TreeProcessor
            asciidoctor.javaExtensionRegistry().treeprocessor(new RedPenTreeProcessor(documentBuilder, sentenceExtractor, options));

            try {
                // trigger the tree processor, which will consequently fill the documentBuilder
                asciidoctor.readDocumentStructure(reader, options);
            } catch (Exception e) {
                LOG.error("Asciidoctor parser error: " + e.getMessage());
            }

        } catch (ParsingTimeoutException e) {
            throw new RedPenException("Failed to parse timeout: ", e);
        } catch (Exception e) {
            throw new RedPenException("Exception when configuring AsciiDoctor", e);
        }

        return documentBuilder.build();
    }
}

/**
 * The Ruby source for the AsciiDoctor RedPen backend
 */
class AsciiDoctorRedPenRubySource {
    public static String SOURCE_TEXT = "# encoding: UTF-8\n" +
            "module Asciidoctor\n" +
            "class Converter::RedPen < Converter::BuiltIn\n" +

            "def initialize backend, opts = {}\n" +
            "end\n" +

            "def redpen_output node, opts = {}\n" +
            "  content = defined?(node.content) ? node.content : (defined?(node.text) ? node.text : '')\n" +
            "  location=''\n" +

            // add the line number to the text, if known, bracketed by ^A and ^B
            "  if defined?(node.source_location) && (node.source_location != nil)\n" +
            "    location = \"\\001#{node.source_location.lineno}\\002\"\n" +
            "  end\n" +
            // return the location, and the converted content bracketed by ^C and ^D
            "  \"#{location}\\003#{content}\\004\"\n" +
            "end\n" +

            "alias paragraph redpen_output\n" +
            "alias document redpen_output\n" +
            "alias embedded redpen_output\n" +
            "alias outline redpen_output\n" +
            "alias section redpen_output\n" +
            "alias admonition redpen_output\n" +
            "alias audio redpen_output\n" +
            "alias colist redpen_output\n" +
            "alias dlist redpen_output\n" +
            "alias example redpen_output\n" +
            "alias floating_title redpen_output\n" +
            "alias image redpen_output\n" +
            "alias listing redpen_output\n" +
            "alias literal redpen_output\n" +
            "alias stem redpen_output\n" +
            "alias olist redpen_output\n" +
            "alias open redpen_output\n" +
            "alias page_break redpen_output\n" +
            "alias preamble redpen_output\n" +
            "alias quote redpen_output\n" +
            "alias thematic_break redpen_output\n" +
            "alias sidebar redpen_output\n" +
            "alias table redpen_output\n" +
            "alias toc redpen_output\n" +
            "alias ulist redpen_output\n" +
            "alias verse redpen_output\n" +
            "alias video redpen_output\n" +
            "alias inline_anchor redpen_output\n" +
            "alias inline_break redpen_output\n" +
            "alias inline_button redpen_output\n" +
            "alias inline_callout redpen_output\n" +
            "alias inline_footnote redpen_output\n" +
            "alias inline_image redpen_output\n" +
            "alias inline_indexterm redpen_output\n" +
            "alias inline_kbd redpen_output\n" +
            "alias inline_menu redpen_output\n" +
            "alias inline_quoted redpen_output\n" +
            "end\n" +

            // monkey patch the parser to remember the header source lines
            // this change adds each header source line, and its line number,
            // to document-level attributes we can retreive later
            "class Parser\n" +
            "  def self.parse_section_title(reader, document)\n" +
            "    line1 = reader.read_line\n" +
            "    line1_lineno = reader.cursor.lineno\n" +
            "    sect_id = nil\n" +
            "    sect_title = nil\n" +
            "    sect_level = -1\n" +
            "    sect_reftext = nil\n" +
            "    single_line = true\n" +
            "\n" +
            "    first_char = line1.chr\n" +
            "    if (first_char == '=' || (Compliance.markdown_syntax && first_char == '#')) &&\n" +
            "        (match = AtxSectionRx.match(line1))\n" +
            "      sect_level = single_line_section_level match[1]\n" +
            "      sect_title = match[2]\n" +
            "      if sect_title.end_with?(']]') && (anchor_match = InlineSectionAnchorRx.match(sect_title))\n" +
            "        if anchor_match[2].nil?\n" +
            "          sect_title = anchor_match[1]\n" +
            "          sect_id = anchor_match[3]\n" +
            "          sect_reftext = anchor_match[4]\n" +
            "        end\n" +
            "      end\n" +
            "    elsif Compliance.underline_style_section_titles\n" +
            "      if (line2 = reader.peek_line(true)) && SECTION_LEVELS.has_key?(line2.chr) && line2 =~ SetextSectionLineRx &&\n" +
            "        (name_match = SetextSectionTitleRx.match(line1)) &&\n" +
            "        # chomp so that a (non-visible) endline does not impact calculation\n" +
            "        (line_length(line1) - line_length(line2)).abs <= 1\n" +
            "        sect_title = name_match[1]\n" +
            "        if sect_title.end_with?(']]') && (anchor_match = InlineSectionAnchorRx.match(sect_title))\n" +
            "          if anchor_match[2].nil?\n" +
            "            sect_title = anchor_match[1]\n" +
            "            sect_id = anchor_match[3]\n" +
            "            sect_reftext = anchor_match[4]\n" +
            "          end\n" +
            "        end\n" +
            "        sect_level = section_level line2\n" +
            "        single_line = false\n" +
            "        reader.advance\n" +
            "      end\n" +
            "    end\n" +
            "    if sect_level >= 0\n" +
            "      sect_level += document.attr('leveloffset', 0).to_i\n" +
            "    end\n" +
            "    document.attributes['header_lines_source'] ||= []\n" +
            "    document.attributes['header_lines_source'].push(line1)\n" +
            "    document.attributes['header_lines_linenos'] ||= []\n" +
            "    document.attributes['header_lines_linenos'].push(line1_lineno)\n" +
            "    [sect_id, sect_reftext, sect_title, sect_level, single_line]\n" +
            "  end\n\n" +
            "end\n" +
            "\n" +

            "Asciidoctor::Converter::Factory.register Asciidoctor::Converter::RedPen, [\"redpen\"]\n" +

            // need to prevent AsciiDoctor's substitutors from replacing quotes with fancy-quotes, and (c) with the &copy; etc
            "Substitutors::SUBS[:basic]=[:specialcharacters]\n" +
            "Substitutors::SUBS[:normal]=[:specialcharacters, :quotes, :attributes,  :macros, :post_replacements]\n" +
            "Substitutors::SUBS[:verbatim]=[:specialcharacters, :callouts]\n" +
            "Substitutors::SUBS[:title]=[:specialcharacters, :quotes, :macros, :attributes, :post_replacements]\n" +
            "Substitutors::SUBS[:header]=[:specialcharacters, :attributes]\n" +
            "end\n";
}