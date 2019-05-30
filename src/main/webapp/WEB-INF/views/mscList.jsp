<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/styles.css" />">
    <title>Индекс MSC</title>
</head>
<body>
    <%@ include file="aside.jsp" %>
    <div class="content">
        <div id="heading">
            <h1>Индекс MSC</h1>
        </div>
        <ul class="msc-sub-categories">
            <c:forEach items="${root.getChildren()}" var="subCategory">
                <c:set var="category" value="${subCategory}" scope="request"/>
                <jsp:include page="mscCategory.jsp"/>
            </c:forEach>
        </ul>
    </div>
</body>
</html>
