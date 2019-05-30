<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/styles.css" />">
    <title>${title}</title>
</head>
<body>
    <%@ include file="aside.jsp" %>
    <div class="content">
        <div id="heading">
            <h1>${title}</h1>
        </div>
        <div class="alphabetical_head">
            <c:forEach items="${items}" var="item">
                <a href="#${item.getKey()}"> ${item.getKey()} </a>
            </c:forEach>
        </div>
        <c:forEach items="${items}" var="item">
            <h2 class="alphabetical_group" id="${item.getKey()}">${item.getKey()}</h2>
            <ul class="alphabetical_item">
                <c:forEach items="${item.getValue()}" var="value">
                    <li class="expandable">
                        ${value.getUrl().html()}
                        <c:if test="${!value.getReferencedIn().isEmpty()}">
                                <div class="referenced-in" onclick="expandCollapse(this)">
                                     [упоминается в]
                                </div>
                        </c:if>
                        <ul>
                            <c:forEach items="${value.getReferencedIn()}" var="reference">
                                <li>${reference.html()}</li>
                            </c:forEach>
                        </ul>
                    </li>
                </c:forEach>
            </ul>
        </c:forEach>
    </div>
</body>
</html>
