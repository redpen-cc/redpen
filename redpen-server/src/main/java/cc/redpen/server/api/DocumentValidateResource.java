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

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.parser.DocumentParserFactory;
import cc.redpen.parser.Parser;
import cc.redpen.server.RedPenServer;
import cc.redpen.validator.ValidationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Resource to validate documents.
 */
@Path("/document")
public class DocumentValidateResource {

    private static final Logger LOG = LogManager.getLogger(
            DocumentValidateResource.class
    );
    private final static String DEFAULT_INTERNAL_CONFIG_PATH = "/conf/redpen-conf.xml";
    @Context
    private ServletContext context;
    private RedPenServer server = null;

    private RedPenServer getServer() {
        if (server == null) {
            LOG.info("Starting Document Validator Server.");
            String configPath = null;
            if (context != null) {
                configPath = context.getInitParameter("redpen.conf.path");
            }
            // if config path is not set, fallback to default config path
            if (configPath == null) {
                configPath = DEFAULT_INTERNAL_CONFIG_PATH;
            }

            LOG.info("Config Path is set to " + "\"" + configPath + "\"");
            try {
                server = new RedPenServer(configPath);
                LOG.info("Document Validator Server is running.");
            } catch (RedPenException e) {
                LOG.error("Could not initialize Document Validator Server: ", e);
            }
        }
        return server;
    }
    @Path("/validate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateDocument(@FormParam("textarea") @DefaultValue("") String document) throws
            JSONException, RedPenException, UnsupportedEncodingException {

        LOG.info("Validating document");
        RedPenServer server = getServer();
        JSONObject json = new JSONObject();

        json.put("document", document);

        Parser parser = DocumentParserFactory.generate(
                Parser.Type.PLAIN, server.getConfig(), new DocumentCollection.Builder());
        Document fileContent = parser.generateDocument(new
                ByteArrayInputStream(document.getBytes("UTF-8")));

        DocumentCollection d = new DocumentCollection();
        d.addDocument(fileContent);

        List<ValidationError> errors = server.getRedPen().check(d);

        JSONArray jsonErrors = new JSONArray();

        for (ValidationError error : errors) {
            JSONObject jsonError = new JSONObject();
            if (error.getSentence().isPresent()) {
                jsonError.put("sentence", error.getSentence().get().content);
            }
            jsonError.put("message", error.getMessage());
            jsonErrors.put(jsonError);
        }

        json.put("errors", jsonErrors);

        return Response.ok().entity(json).build();
    }
}
