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

package cc.redpen.server.api;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.ConfigurationLoader;
import cc.redpen.formatter.Formatter;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.tokenizer.NeologdJapaneseTokenizer;
import cc.redpen.tokenizer.RedPenTokenizer;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.util.FormatterUtils;
import cc.redpen.util.LanguageDetector;
import cc.redpen.validator.ValidationError;
import org.apache.wink.common.annotations.Workspace;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static cc.redpen.server.api.RedPenService.getOrDefault;

/**
 * Resource to validate documents.
 */
@Workspace(workspaceTitle = "RedPen", collectionTitle = "Document Validation")
@Path("/document")
public class RedPenResource {

    private static final Logger LOG = LoggerFactory.getLogger(RedPenResource.class);

    private static final String DEFAULT_DOCUMENT_PARSER = "PLAIN";
    private static final String DEFAULT_LANG = "en";
    private static final String DEFAULT_CONFIGURATION = "en";
    private static final String DEFAULT_FORMAT = "json";
    private static final String DEFAULT_ERROR_LEVEL = "error";

    /*package*/ static final String MIME_TYPE_XML = "application/xml; charset=utf-8";
    /*package*/ static final String MIME_TYPE_JSON = "application/json; charset=utf-8";
    /*package*/ static final String MIME_TYPE_PLAINTEXT = "text/plain; charset=utf-8";

    @Context
    private ServletContext context;

    /**
     * Detect language of document
     *
     * @param document       the source text of the document
     */
    @Path("/language")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @WinkAPIDescriber.Description("Detect language of document")
    public JSONObject detectLanguage(@FormParam("document") @DefaultValue("") String document) throws JSONException {
        String language = new LanguageDetector().detectLanguage(document);
        return new JSONObject().put("key", language);
    }

    /**
     * Validate a source document posted from a form
     *
     * @param document       the source text of the document
     * @param documentParser specifies one of PLAIN, WIKI, or MARKDOWN
     * @param lang           the source document language (en, ja, etc)
     * @param format         document format
     * @param config         the source of a RedPen XML configuration file
     * @return redpen validation errors
     * @throws RedPenException when failed to parse document
     */
    @Path("/validate")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
    @WinkAPIDescriber.Description("Validate a document and return any redpen errors")
    public Response validateDocument(@FormParam("document") @DefaultValue("") String document,
                                     @FormParam("documentParser") @DefaultValue(DEFAULT_DOCUMENT_PARSER) String documentParser,
                                     @FormParam("lang") @DefaultValue(DEFAULT_CONFIGURATION) String lang,
                                     @FormParam("format") @DefaultValue(DEFAULT_FORMAT) String format,
                                     @FormParam("errorLevel") @DefaultValue(DEFAULT_ERROR_LEVEL) String errorLevel,
                                     @FormParam("config") String config) throws RedPenException {

        LOG.info("Validating document");
        RedPen redPen;
        if (config == null) {
            redPen = new RedPenService(context).getRedPen(lang);
        } else {
            redPen = new RedPen(new ConfigurationLoader().secure().loadFromString(config));
        }
        redPen.setErrorLevel( errorLevel );
        Document parsedDocument = redPen.parse(DocumentParser.of(documentParser), document);
        List<ValidationError> errors = redPen.validate(parsedDocument);

        Formatter formatter = FormatterUtils.getFormatterByName(format);

        if (formatter == null) {
            throw new RedPenException("Unsupported format: " + format + " - please use xml, plain, plain2, json or json2");
        }

        return responseTyped(formatter.format(parsedDocument, errors), format);
    }

    /*package*/ static Response responseTyped(final String formatted, final String format) throws RedPenException {
        if (format.startsWith("xml")) {
            return Response.ok(formatted, RedPenResource.MIME_TYPE_XML).build();
        } else if (format.startsWith("json")) {
            return Response.ok(formatted, RedPenResource.MIME_TYPE_JSON).build();
        } else if (format.startsWith("plain")) {
            return Response.ok(formatted, RedPenResource.MIME_TYPE_PLAINTEXT).build();
        } else {
            throw new RedPenException("MIME type unknown with format: " + format);
        }
    }

    /**
     * Validate a request encoded in JSON. Valid properties are:
     * <p>
     * document : the source text of the document
     * documentParser : specifies one of PLAIN, WIKI, or MARKDOWN
     * lang : the source document language (en, ja, etc)
     * format : the format of the results, eg: json, json2, plain etc
     * config : the redpen validator configuration
     *
     * @param requestJSON the request, in JSON
     * @return redpen validation errors
     * @throws RedPenException when failed to validate the json
     */
    @Path("/validate/json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
    @WinkAPIDescriber.Description("Process a redpen JSON validation request and returns any redpen errors")
    public Response validateDocumentJSON(JSONObject requestJSON) throws RedPenException {

        LOG.info("Validating document using JSON request");
        String documentParser = getOrDefault(requestJSON, "documentParser", DEFAULT_DOCUMENT_PARSER);
        String documentText = getOrDefault(requestJSON, "document", "");
        String format = getOrDefault(requestJSON, "format", DEFAULT_FORMAT);

        RedPen redPen = new RedPenService(context).getRedPenFromJSON(requestJSON);

        Document parsedDocument = redPen.parse(DocumentParser.of(documentParser), documentText);

        List<ValidationError> errors = redPen.validate(parsedDocument);

        Formatter formatter = FormatterUtils.getFormatterByName(format);

        if (formatter == null) {
            throw new RedPenException("Unsupported format: " + format + " - please use xml, plain, plain2, json or json2");
        }

        return responseTyped(formatter.format(parsedDocument, errors), format);
    }

    /**
     * Tokenize some text and return the tokens
     *
     * @param document the source text of the document
     */
    @Path("/tokenize")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @WinkAPIDescriber.Description("Tokenize a document")
    public JSONObject tokenize(@FormParam("document") @DefaultValue("") String document,
                               @FormParam("lang") @DefaultValue(DEFAULT_CONFIGURATION) String lang) throws JSONException {
        RedPenTokenizer tokenizer;
        switch (lang == null ? "en" : lang) {
            case "ja":
                tokenizer = new NeologdJapaneseTokenizer();
                break;
            default:
                tokenizer = new WhiteSpaceTokenizer();
                break;
        }

        return new JSONObject().put("tokens", tokenizer.tokenize(document == null ? "" : document));
    }
}
