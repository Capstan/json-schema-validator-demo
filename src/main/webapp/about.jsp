<!DOCTYPE html>
<%--
  ~ Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the Lesser GNU General Public License as
  ~ published by the Free Software Foundation, either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ Lesser GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<c:set var="pageName" value="about" scope="request"/>
<c:set var="pageTitle" value="About this web site" scope="request"/>
<c:set var="pageDescription"
    value="JSON Schema software in Java used on this site" scope="request"/>
<c:import url="include/software.jspf" var="devnull"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <jsp:include page="include/head-common.jspf"/>
</head>
<body>

<jsp:include page="include/menu.jspf"/>
<div class="about">

    <h2>Software used</h2>

    <p>This web site uses the following software, all written in Java:</p>

    <ul>
        <li><a href="${software['json-schema-core']}">json-schema-core</a>;</li>
        <li><a href="${software['json-schema-validator']}">
        json-schema-validator</a>;</li>
        <li><a href="${software['JJSchema']}">JJSchema</a>;</li>
        <li><a href="${software['jsonschema2pojo']}">jsonschema2pojo</a>;</li>
        <li><a href="${sowftware['avro']}">Avro Java library</a>;</li>
        <li><a href="${software['json-schema-processor-examples']}">
        json-schema-processor-examples</a>.</li>
    </ul>

    <p>The source code for this web site is also available <a
    href="https://github.com/fge/json-schema-validator-demo">here</a>. As it
    uses an embedded <a href="http://jetty.codehaus.org">Jetty</a> servlet
    container, you can run it yourself.</p>

    <p>Other software used includes <a href="http://jersey.java.net">Jersey</a>
    and <a href="http://jquery.com">jQuery</a> as a JavaScript library.</p>

    <h2>Reporting problems</h2>

    <p>
        If you spot a problem on this site, either open an issue on this
        application's <a
        href="https://github.com/fge/json-schema-validator-demo">GitHub
        repository</a>, or if you do not have an account on GitHub, you can <a
        href="mailto:fgaliegue@gmail.com">email me directly</a>.
    </p>

    <p>
        <a href="http://jigsaw.w3.org/css-validator/check/referer">
            <img style="border:0;width:88px;height:31px"
                src="http://jigsaw.w3.org/css-validator/images/vcss-blue"
                alt="Valid CSS!"/>
        </a>
    </p>
</div>

</body>
</html>
