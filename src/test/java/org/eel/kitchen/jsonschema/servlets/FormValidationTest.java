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

package org.eel.kitchen.jsonschema.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class FormValidationTest
{
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FormValidation servlet;

    @BeforeMethod
    public void init()
        throws IOException
    {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servlet = new FormValidation();
    }

    @Test
    public void missingBothParametersReturns401()
        throws ServletException, IOException
    {
        when(request.getParameterNames()).thenReturn(emptyEnumeration());
        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Missing parameters");
    }

    @Test
    public void missingSchemaParameterReturns401()
        throws ServletException, IOException
    {
        when(request.getParameterNames()).thenReturn(enumerationOf("data"));
        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Missing parameters");
    }

    @Test
    public void missingDataParameterReturns401()
        throws ServletException, IOException
    {
        when(request.getParameterNames()).thenReturn(enumerationOf("schema"));
        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Missing parameters");
    }

    @DataProvider
    public Iterator<Object[]> inputData()
    {
        return ImmutableSet.of(
            new Object[] { "", "", true, true },
            new Object[] { "{}", "", false, true },
            new Object[] { "", "{}", true, false },
            new Object[] { "{}", "{}", false, false }
        ).iterator();
    }

    @Test(dataProvider = "inputData")
    public void inputValidityIsCorrectlyDetected(final String rawSchema,
        final String rawData, final boolean invalidSchema,
        final boolean invalidData)
        throws IOException
    {
        final JsonNode node = FormValidation.buildResult(rawSchema,
            rawData, false, false);

        assertTrue(node.get("invalidSchema").isBoolean());
        assertTrue(node.get("invalidData").isBoolean());
        assertEquals(node.get("invalidSchema").booleanValue(), invalidSchema);
        assertEquals(node.get("invalidData").booleanValue(), invalidData);

        if (invalidSchema || invalidData)
            assertEquals(Sets.newHashSet(node.fieldNames()),
                ImmutableSet.of("invalidSchema", "invalidData"));
    }

    @DataProvider
    public Iterator<Object[]> sampleInputs()
    {
        return ImmutableSet.of(
            new Object[] { "\"hello\"", true },
            new Object[] { "0", false }
        ).iterator();
    }

    @Test(
        dataProvider = "sampleInputs",
        dependsOnMethods = "inputValidityIsCorrectlyDetected"
    )
    public void validationResultsAreCorrectlyReported(final String rawData,
        final boolean valid)
        throws IOException
    {
        final String rawSchema = "{\"type\":\"string\"}";

        final JsonNode result = FormValidation.buildResult(rawSchema, rawData,
            false, false);

        assertTrue(result.get("results").isObject());
        assertTrue(result.get("valid").isBoolean());
        assertEquals(result.get("valid").booleanValue(), valid);
        assertEquals(Sets.newHashSet(result.fieldNames()), ImmutableSet.of(
            "invalidSchema", "invalidData", "valid", "results"));
    }

    private static Enumeration<String> emptyEnumeration()
    {
        return new Enumeration<String>()
        {
            @Override
            public boolean hasMoreElements()
            {
                return false;
            }

            @Override
            public String nextElement()
            {
                throw new NoSuchElementException();
            }
        };
    }

    private static Enumeration<String> enumerationOf(final String... elements)
    {
        final List<String> list = Lists.newArrayList(elements);

        return new Enumeration<String>()
        {
            @Override
            public boolean hasMoreElements()
            {
                return !list.isEmpty();
            }

            @Override
            public String nextElement()
            {
                if (!hasMoreElements())
                    throw new NoSuchElementException();
                return list.remove(0);
            }
        };
    }
}
