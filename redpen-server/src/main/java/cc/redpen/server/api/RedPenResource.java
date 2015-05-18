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

import cc.redpen.ConfigurationLoader;
import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Symbol;
import cc.redpen.config.SymbolType;
import cc.redpen.formatter.Formatter;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.util.FormatterUtils;
import cc.redpen.validator.ValidationError;
import org.apache.wink.common.annotations.Workspace;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    @Context
    private ServletContext context;

    /**
     * Validate a source document posted from a form
     *
     * @param document       the source text of the document
     * @param documentParser specifies one of PLAIN, WIKI, or MARKDOWN
     * @param lang           the source document language (en, ja, etc)
     * @param config         the source of a RedPen XML configuration file
     * @return redpen validation errors
     * @throws RedPenException
     */
    @Path("/validate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @WinkAPIDescriber.Description("Validate a document and return any redpen errors")
    public Response validateDocument(@FormParam("document") @DefaultValue("") String document,
                                     @FormParam("documentParser") @DefaultValue(DEFAULT_DOCUMENT_PARSER) String documentParser,
                                     @FormParam("lang") @DefaultValue(DEFAULT_CONFIGURATION) String lang,
                                     @FormParam("format") @DefaultValue(DEFAULT_FORMAT) String format,
                                     @FormParam("config") String config) throws RedPenException {

        LOG.info("Validating document");
        RedPen redPen;
        if (config == null) {
            redPen = new RedPenService(context).getRedPen(lang);
        } else {
            redPen = new RedPen(new ConfigurationLoader().loadFromString(config));
        }
        Document parsedDocument = redPen.parse(DocumentParser.of(documentParser), document);
        List<ValidationError> errors = redPen.validate(parsedDocument);

        Formatter formatter = FormatterUtils.getFormatterByName(format);

        if (formatter == null) {
            throw new RedPenException("Unsupported format: " + format + " - please use xml, plain, plain2, json or json2");
        }

        String responseJSON = formatter.format(parsedDocument, errors);
        return Response.ok().entity(responseJSON).build();
    }


    /**
     * Validate a request encoded in JSON. Valid properties are:
     * <p/>
     * document : the source text of the document
     * documentParser : specifies one of PLAIN, WIKI, or MARKDOWN
     * lang : the source document language (en, ja, etc)
     * format : the format of the results, eg: json, json2, plain etc
     * config : the redpen validator configuration
     *
     * @param requestJSON the request, in JSON
     * @return redpen validation errors
     */
    @Path("/validate/json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WinkAPIDescriber.Description("Process a redpen JSON validation request and returns any redpen errors")
    public Response validateDocumentJSON(JSONObject requestJSON) throws RedPenException {

        LOG.info("Validating document using JSON request");
        String documentParser = getOrDefault(requestJSON, "documentParser", DEFAULT_DOCUMENT_PARSER);
        String documentText = getOrDefault(requestJSON, "document", "");
        String format = getOrDefault(requestJSON, "format", DEFAULT_FORMAT);

        String lang = DEFAULT_LANG;

        Map<String, Map<String, String>> properties = new HashMap<>();
        JSONObject config = null;
        if (requestJSON.has("config")) {
            try {
                config = requestJSON.getJSONObject("config");
                lang = getOrDefault(config, "lang", DEFAULT_LANG);
                if (config.has("validators")) {
                    JSONObject validators = config.getJSONObject("validators");
                    Iterator keyIter = validators.keys();
                    while (keyIter.hasNext()) {
                        String validator = String.valueOf(keyIter.next());
                        Map<String, String> props = new HashMap<>();
                        properties.put(validator, props);
                        JSONObject validatorConfig = validators.getJSONObject(validator);
                        if ((validatorConfig != null) && validatorConfig.has("properties")) {
                            JSONObject validatorProps = validatorConfig.getJSONObject("properties");
                            Iterator propsIter = validatorProps.keys();
                            while (propsIter.hasNext()) {
                                String propname = String.valueOf(propsIter.next());
                                props.put(propname, validatorProps.getString(propname));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Exception when processing JSON properties", e);
            }
        }

        RedPen redPen = new RedPenService(context).getRedPen(lang, properties);

        // override any symbols
        if ((config != null) && config.has("symbols")) {
            try {
                JSONObject symbols = config.getJSONObject("symbols");
                Iterator keyIter = symbols.keys();
                while (keyIter.hasNext()) {
                    String symbolName = String.valueOf(keyIter.next());
                    try {
                        SymbolType symbolType = SymbolType.valueOf(symbolName);
                        JSONObject symbolConfig = symbols.getJSONObject(symbolName);
                        Symbol originalSymbol = redPen.getConfiguration().getSymbolTable().getSymbol(symbolType);
                        if ((originalSymbol != null) && (symbolConfig != null) && symbolConfig.has("value")) {
                            String value = symbolConfig.has("value") ? symbolConfig.getString("value") : String.valueOf(originalSymbol.getValue());
                            boolean spaceBefore = symbolConfig.has("before_space") ? symbolConfig.getBoolean("before_space") : originalSymbol.isNeedBeforeSpace();
                            boolean spaceAfter = symbolConfig.has("after_space") ? symbolConfig.getBoolean("after_space") : originalSymbol.isNeedAfterSpace();
                            String invalidChars = symbolConfig.has("invalid_chars") ? symbolConfig.getString("invalid_chars") : String.valueOf(originalSymbol.getInvalidChars());
                            if ((value != null) && !value.isEmpty()) {
                                redPen.getConfiguration().getSymbolTable().overrideSymbol(new Symbol(symbolType, value.charAt(0), invalidChars, spaceBefore, spaceAfter));
                            }
                        }

                    } catch (IllegalArgumentException iae) {
                        LOG.error("Ignoring unknown SymbolType " + symbolName);
                    }
                }
            } catch (Exception e) {
                LOG.error("Exception when processing JSON symbol overrides", e);
            }
        }
        Document parsedDocument = redPen.parse(DocumentParser.of(documentParser), documentText);

        List<ValidationError> errors = redPen.validate(parsedDocument);

        Formatter formatter = FormatterUtils.getFormatterByName(format);

        if (formatter == null) {
            throw new RedPenException("Unsupported format: " + format + " - please use xml, plain, plain2, json or json2");
        }

        String responseJSON = formatter.format(parsedDocument, errors);
        return Response.ok().entity(responseJSON).build();
    }

    private String getOrDefault(JSONObject json, String property, String defaultValue) {
        try {
            String value = json.getString(property);
            if (value != null) {
                return value;
            }
        } catch (Exception e) {
            // intentionally empty
        }
        return defaultValue;
    }
}
