<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/styles.css" />">
    <title>MSC ${msc.getId()} — ${msc.getTitle()}</title>
</head>
<body>
    <%@ include file="aside.jsp" %>
    <div class="content">
        <div id="heading">
            <h1>MSC ${msc.getId()} — ${msc.getTitle()}</h1>
        </div>
        <c:if test="${!msc.getArticles().isEmpty()}">
            <ul class="msc-articles">
                <c:forEach items="${msc.getArticles()}" var="article">
                    <li>${article.html()}</li>
                </c:forEach>
            </ul>
        </c:if>
        <c:if test="${!msc.getRelated().isEmpty()}">
            <h2>Смежные разделы</h2>
            <ul class="msc-related-categories">
                <c:forEach items="${msc.getRelated()}" var="relatedCategory">
                    <c:set var="category" value="${relatedCategory}" scope="request"/>
                    <jsp:include page="mscCategory.jsp"/>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</body>
</html>
