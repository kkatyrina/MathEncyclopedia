<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
    <%@ include file="/resources/scripts/expandCollapse.js" %>
</script>
<aside>
    <a href="/" title="МАТЕМАТИЧЕСКАЯ ЭНЦИКЛОПЕДИЯ">
        <img src="<c:url value="/resources/images/logo.png"/>" alt="МАТЕМАТИЧЕСКАЯ ЭНЦИКЛОПЕДИЯ">
    </a>
    <nav>
        <ul class="aside-menu">
            <li><a href="/articles/ru">Алфавитный указатель</a></li>
            <li><a href="/msc/ru">Индекс MSC</a></li>
        </ul>
    </nav>
    <c:if test="${!empty translations && !translations.isEmpty()}">
        <nav>
            <h3>На других языках</h3>
            <ul class="lang-menu">
                <c:forEach items="${translations}" var="translation">
                    <li>${translation.html()}</li>
                </c:forEach>
            </ul>
        </nav>
    </c:if>
</aside>