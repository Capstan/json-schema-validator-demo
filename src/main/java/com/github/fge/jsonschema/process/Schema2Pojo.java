/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.constants.ParseError;
import com.github.fge.jsonschema.processing.ProcessingResult;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.ValueHolder;
import com.github.fge.jsonschema2pojo.JsonSchema2SourceCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import static com.github.fge.jsonschema.constants.ResponseFields.*;

@Path("/schema2pojo")
@Produces("application/json;charset=utf-8")
public final class Schema2Pojo
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private static final Response OOPS = Response.status(500).build();

    private static final Logger log
        = LoggerFactory.getLogger(Schema2Pojo.class);

    private static final JsonSchema2SourceCode PROCESSOR
        = new JsonSchema2SourceCode();

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public static Response checkSyntax(@FormParam("input") final String schema)
    {
        try {
            final JsonNode ret = buildResult(schema);
            return Response.ok().entity(ret.toString()).build();
        } catch (IOException e) {
            log.error("I/O error while building response", e);
            return OOPS;
        }
    }

    /*
     * Build the response. When we arrive here, we are guaranteed that we have
     * the needed elements.
     */
    private static JsonNode buildResult(final String rawSchema)
        throws IOException
    {
        final ObjectNode ret = JsonNodeFactory.instance.objectNode();

        final boolean invalidSchema = fillWithData(ret, INPUT, INVALID_INPUT,
            rawSchema);

        final JsonNode schemaNode = ret.remove(INPUT);

        if (invalidSchema)
            return ret;

        final SchemaTree tree = new CanonicalSchemaTree(schemaNode);
        final SchemaHolder input = new SchemaHolder(tree);

        final ProcessingReport report = new ListProcessingReport();
        final ProcessingResult<ValueHolder<String>> result
            = ProcessingResult.uncheckedResult(PROCESSOR, report, input);

        final boolean  success = result.isSuccess();
        ret.put(VALID, success);

        if (success)
            ret.put(RESULTS, FACTORY.textNode(result.getResult().getValue()));
        else {
            final ArrayNode node = FACTORY.arrayNode();
            for (final ProcessingMessage message: result.getReport())
                node.add(message.asJson());
            ret.put(RESULTS, JacksonUtils.prettyPrint(node));
        }
        return ret;
    }

    /*
     * We have to use that since Java is not smart enough to detect that
     * sometimes, a variable is initialized in all paths.
     *
     * This returns true if the data is invalid.
     */
    private static boolean fillWithData(final ObjectNode node,
        final String onSuccess, final String onFailure, final String raw)
        throws IOException
    {
        try {
            node.put(onSuccess, JsonLoader.fromString(raw));
            return false;
        } catch (JsonProcessingException e) {
            node.put(onFailure, ParseError.build(e, raw.contains("\r\n")));
            return true;
        }
    }
}
