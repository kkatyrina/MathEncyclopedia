<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/styles.css" />">
    <title>${article.getName()}</title>
</head>
<body>
    <aside>
        <img src="<c:url value="/resources/images/logo.png"/>" alt="МАТЕМАТИЧЕСКАЯ ЭНЦИКЛОПЕДИЯ">
        <%@ include file="topAside.jsp" %>
        <nav>
            <h3>На других языках</h3>
            <ul class="lang-menu">
                <c:forEach items="${article.getTranslations()}" var="translation">
                    <li><a href=${translation.value}>${translation.key}</a></li>
                </c:forEach>
            </ul>
        </nav>
    </aside>
    <script src='/resources/scripts/MathJax/MathJax.js?config=TeX-AMS-MML_HTMLorMML-full,/resources/scripts/mwMathJaxConfig'></script>
    <div class="content">
        <div id="heading">
            <h1>${article.getName()}</h1>
        </div>
        ${article.getBody()}
    </div>
</body>
</html>
