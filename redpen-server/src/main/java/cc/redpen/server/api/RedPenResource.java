/*
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

package cc.redpen.server.api;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.validator.ValidationError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource to validate documents.
 */
@Path("/document")
public class RedPenResource {

    private static final Logger LOG = LoggerFactory.getLogger(RedPenResource.class);
    private final static String DEFAULT_INTERNAL_CONFIG_PATH = "/conf/redpen-conf.xml";
    @Context
    private ServletContext context;

    private Map<String, RedPen> langRedPenMap = new HashMap<>();

    private RedPen getRedPen(String lang) {
        if (langRedPenMap.size() == 0) {
            synchronized (this) {
                if (langRedPenMap.size() == 0) {
                    LOG.info("Starting Document Validator Server.");
                    try {
                        RedPen japaneseRedPen = new RedPen.Builder().setConfigPath("/conf/redpen-conf-ja.xml").build();
                        langRedPenMap.put("ja", japaneseRedPen);
                        RedPen englishRedPen = new RedPen.Builder().setConfigPath(DEFAULT_INTERNAL_CONFIG_PATH).build();
                        langRedPenMap.put("en", englishRedPen);
                        langRedPenMap.put("", englishRedPen);

                        String configPath;
                        if (context != null) {
                            configPath = context.getInitParameter("redpen.conf.path");
                            if (configPath != null) {
                                LOG.info("Config Path is set to \"{}\"", configPath);
                                RedPen defaultRedPen = new RedPen.Builder().setConfigPath(configPath).build();
                                langRedPenMap.put("", defaultRedPen);
                            } else {
                                // if config path is not set, fallback to default config path
                                LOG.info("Config Path is set to \"{}\"", DEFAULT_INTERNAL_CONFIG_PATH);
                            }
                        }
                        LOG.info("Document Validator Server is running.");
                    } catch (RedPenException e) {
                        LOG.error("Unable to initialize RedPen", e);
                        throw new ExceptionInInitializerError(e);
                    }
                }
            }
        }
        return langRedPenMap.getOrDefault(lang, langRedPenMap.get(""));
    }

    @Path("/validate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateDocument(@FormParam("textarea") @DefaultValue("") String document,
                                     @FormParam("lang") @DefaultValue("en") String lang)
            throws JSONException, RedPenException, UnsupportedEncodingException {

        LOG.info("Validating document");
        RedPen redPen = getRedPen(lang);
        JSONObject json = new JSONObject();

        json.put("document", document);

        Document parsedDocument = redPen.parse(DocumentParser.PLAIN, document);

        List<ValidationError> errors = redPen.validate(parsedDocument);

        JSONArray jsonErrors = new JSONArray();

        for (ValidationError error : errors) {
            JSONObject jsonError = new JSONObject();
            jsonError.put("sentence", error.getSentence().content);
            jsonError.put("message", error.getMessage());
            jsonErrors.put(jsonError);
        }

        json.put("errors", jsonErrors);

        return Response.ok().entity(json).build();
    }
}
