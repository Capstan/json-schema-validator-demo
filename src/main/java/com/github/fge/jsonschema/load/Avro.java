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

package com.github.fge.jsonschema.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.constants.ResponseFields;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.ImmutableList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Path("/avro")
@Produces("application/json;charset=utf-8")
public final class Avro
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final Random RND = new Random();
    private static final List<JsonNode> SAMPLE_DATA;
    private static final int SAMPLE_DATA_SIZE;

    static {
        try {
            final JsonNode node = JsonLoader.fromResource("/avro.json");
            SAMPLE_DATA = ImmutableList.copyOf(node);
            SAMPLE_DATA_SIZE = SAMPLE_DATA.size();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @GET
    public static Response getSamples()
    {
        final int index = RND.nextInt(SAMPLE_DATA_SIZE);
        final JsonNode ret = SAMPLE_DATA.get(index);
        final ObjectNode node = FACTORY.objectNode();
        node.put(ResponseFields.INPUT, JacksonUtils.prettyPrint(ret));

        return Response.ok().entity(node.toString()).build();
    }
}
