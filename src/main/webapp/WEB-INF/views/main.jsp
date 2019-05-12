<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/styles.css" />">
    <title>Example</title>
</head>
<body>
    <aside>
        <img src="<c:url value="/resources/images/logo.png"/>" alt="МАТЕМАТИЧЕСКАЯ ЭНЦИКЛОПЕДИЯ">
        <%@ include file="topAside.jsp" %>
        <nav>
            <h3>На других языках</h3>
            <ul class="lang-menu">
                <li><a href="/">Русский</a></li>
                <li><a href="/">English</a></li>
            </ul>
        </nav>
    </aside>
    <div class="content">
        <div id="heading">
            <h1>Example</h1>
        </div>
        Start with 👉🏻 <a href="/index.php/Differential">Differential</a>
    </div>
</body>
</html>
