<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/styles.css" />">
    <title>${article.getTitle()}</title>
</head>
<body>
    <%@ include file="aside.jsp" %>
    <script src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.5/MathJax.js?config=TeX-AMS-MML_HTMLorMML-full,/resources/scripts/mwMathJaxConfig'></script>
    <script defer src='/resources/scripts/externalLinkDetection.js'></script>
    <div class="content">
        <div id="heading">
            <h1>${article.getTitle()}</h1>
        </div>
        ${article.getBody()}
        <c:if test="${!article.getSeeAlso().isEmpty()}">
            <h2>См. также</h2>
            <ul class="see_also">
                <c:forEach items="${article.getSeeAlso()}" var="ref">
                    <li>${ref.html()}</li>
                </c:forEach>
            </ul>
        </c:if>
        <c:if test="${!article.getMsc().isEmpty()}">
            <h2>MSC</h2>
            <ul>
                <c:forEach items="${article.getMsc()}" var="msc">
                    <c:set var="category" value="${msc}" scope="request"/>
                    <jsp:include page="mscCategory.jsp"/>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</body>
</html>
