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
import cc.redpen.formatter.JSONBySentenceFormatter;
import cc.redpen.formatter.JSONFormatter;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.validator.ValidationError;
import org.apache.wink.common.annotations.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Resource to validate documents.
 */
@Workspace(workspaceTitle = "RedPen", collectionTitle = "Document Validation")
@Path("/document")
public class RedPenResource {

    private static final Logger LOG = LoggerFactory.getLogger(RedPenResource.class);

    private static final String DEFAULT_DOCUMENT_PARSER = "PLAIN";
    private static final String DEFAULT_CONFIGURATION = "en";

    @Context
    private ServletContext context;

    @Path("/validate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @WinkAPIDescriber.Description("Validate a document and return any redpen errors")
    public Response validateDocument(@FormParam("document") @DefaultValue("") String document,
                                     @FormParam("documentParser") @DefaultValue(DEFAULT_DOCUMENT_PARSER) String documentParser,
                                     @FormParam("lang") @DefaultValue(DEFAULT_CONFIGURATION) String lang) throws RedPenException {

        LOG.info("Validating document");
        RedPen redPen = new RedPenService(context).getRedPen(lang);
        Document parsedDocument = redPen.parse(DocumentParser.of(documentParser), document);
        List<ValidationError> errors = redPen.validate(parsedDocument);

        String responseJSON = new JSONFormatter().format(parsedDocument, errors);
        return Response.ok().entity(responseJSON).build();
    }

    @Path("/validate_by_sentence")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @WinkAPIDescriber.Description("Validate a document and return any redpen errors correlated by sentence, in order of appearance in the document")
    public Response validateDocumentBySentence(@FormParam("document") @DefaultValue("") String document,
                                               @FormParam("documentParser") @DefaultValue(DEFAULT_DOCUMENT_PARSER) String documentParser,
                                               @FormParam("lang") @DefaultValue(DEFAULT_CONFIGURATION) String lang) throws RedPenException {

        LOG.info("Validating document by sentence");
        RedPen redPen = new RedPenService(context).getRedPen(lang);
        Document parsedDocument = redPen.parse(DocumentParser.of(documentParser), document);
        List<ValidationError> errors = redPen.validate(parsedDocument);

        String responseJSON = new JSONBySentenceFormatter().format(parsedDocument, errors);
        return Response.ok().entity(responseJSON).build();
    }
}
