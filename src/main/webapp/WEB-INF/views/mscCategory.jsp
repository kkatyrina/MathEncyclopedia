<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<li ${category.getChildren().isEmpty() ? "" : "class=\"expandable\""}>
    <div class="category-title" ${category.getChildren().isEmpty() ? "" : "onclick=\"expandCollapse(this)\""}>
        ${category.getTitle()}
    </div>
    <c:choose>
        <c:when test="${!category.getArticles().isEmpty() || !category.getRelated().isEmpty()}">
            ${category.getUrl().html("[".concat(category.getId()).concat("]"))}
        </c:when>
        <c:otherwise>
            [${category.getId()}]
        </c:otherwise>
    </c:choose>
    <c:if test="${!category.getChildren().isEmpty()}">
        <ul class="msc-sub-categories">
            <c:forEach items="${category.getChildren()}" var="subCategory">
                <c:set var="category" value="${subCategory}" scope="request"/>
                <jsp:include page="mscCategory.jsp"/>
            </c:forEach>
        </ul>
    </c:if>
</li>