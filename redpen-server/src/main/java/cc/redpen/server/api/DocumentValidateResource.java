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
import cc.redpen.server.RedPenServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cc.redpen.parser.DocumentParserFactory;
import cc.redpen.parser.Parser;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.ValidationError;

import javax.ws.rs.*;
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

  @Path("/validate")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response validateDocument(@FormParam("textarea") @DefaultValue("") String document) throws
    JSONException, RedPenException, UnsupportedEncodingException {

    LOG.info("Validating document");
    RedPenServer server = RedPenServer.getInstance();
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
