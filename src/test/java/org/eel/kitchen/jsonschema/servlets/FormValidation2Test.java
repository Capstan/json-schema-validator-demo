package org.eel.kitchen.jsonschema.servlets;

import org.eel.kitchen.jsonschema.constants.ServletInputs;
import org.mockito.ArgumentMatcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

public final class FormValidation2Test
{
    private static final ArgumentMatcher<String> IS_ERROR
        = new ArgumentMatcher<String>()
    {
        @Override
        public boolean matches(final Object argument)
        {
            return ((String) argument).startsWith("ERROR: ");
        }
    };

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FormValidation2 servlet;
    private PrintWriter writer;

    @BeforeMethod
    public void init()
        throws IOException
    {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        servlet = new FormValidation2();
    }

    @Test
    public void missingBothParametersReturns401()
        throws ServletException, IOException
    {
        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Missing parameters");
        verify(request, never()).getRequestDispatcher(any(String.class));
    }

    @Test
    public void missingSchemaParameterReturns401()
        throws ServletException, IOException
    {
        when(request.getParameter(ServletInputs.SCHEMA)).thenReturn("{}");
        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Missing parameters");
        verify(request, never()).getRequestDispatcher(any(String.class));
    }

    @Test
    public void missingDataParameterReturns401()
        throws ServletException, IOException
    {
        when(request.getParameter(ServletInputs.DATA)).thenReturn("{}");
        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Missing parameters");
        verify(request, never()).getRequestDispatcher(any(String.class));
    }

    @Test
    public void necessaryDataIsReturned()
        throws ServletException, IOException
    {
        final String schema = "{}";
        // FIXME: see below
        final String data = "{ }";

        when(request.getParameter(ServletInputs.SCHEMA)).thenReturn(schema);
        when(request.getParameter(ServletInputs.DATA)).thenReturn(data);

        servlet.doPost(request, response);

        verify(writer).write(eq(data));
    }

    @Test(dependsOnMethods = "necessaryDataIsReturned")
    public void invalidSchemaRaisesAnError()
        throws ServletException, IOException
    {
        final String schema = "foo";
        final String data = "{}";

        when(request.getParameter(ServletInputs.SCHEMA)).thenReturn(schema);
        when(request.getParameter(ServletInputs.DATA)).thenReturn(data);

        servlet.doPost(request, response);

        verify(writer).write(argThat(IS_ERROR));
    }

    @Test(dependsOnMethods = "necessaryDataIsReturned")
    public void invalidDataRaisesAnError()
        throws ServletException, IOException
    {
        final String schema = "{}";
        final String data = "foo";

        when(request.getParameter(ServletInputs.SCHEMA)).thenReturn(schema);
        when(request.getParameter(ServletInputs.DATA)).thenReturn(data);

        servlet.doPost(request, response);

        verify(writer).write(argThat(IS_ERROR));
    }
}
