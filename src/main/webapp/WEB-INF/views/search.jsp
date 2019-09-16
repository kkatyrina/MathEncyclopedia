<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/styles.css" />">
    <title>Поиск</title>
</head>
<body>
    <%@ include file="aside.jsp" %>
    <script src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.5/MathJax.js?config=TeX-AMS-MML_HTMLorMML-full,/resources/scripts/mwMathJaxConfig'></script>
    <script defer src='/resources/scripts/externalLinkDetection.js'></script>
    <div class="content">
        <div id="heading">
            <h1>Поиск</h1>
        </div>
        <form action="/search">
            <input type="text" name="query" value="${query}">
            <input type="submit" value="Искать">
        </form>
        <c:forEach items="${searchResults}" var="searchResult">
            <div class="search-result-link">
                ${searchResult.getUrl().html()}
            </div>
            <div class="search-result-body">
                ${searchResult.getBody()}
            </div>
        </c:forEach>
    </div>
</body>
</html>
