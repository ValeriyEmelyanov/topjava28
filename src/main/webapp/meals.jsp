<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <link href="<c:url value="/styles/meals.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<div id="commandpanel">
    <a href="meals?action=add" class="addButton">Add Meal</a>
</div>
<c:if test="${list.isEmpty()}">
    <p>No meals</p>
</c:if>

<c:if test="${!list.isEmpty()}">
    <div>
        <table>
            <tr id="title">
                <th>Date/Time</th>
                <th>Description</th>
                <th>Calories</th>
                <th colspan="2">Action</th>
            </tr>
            <c:forEach var="mealTo" items="${list}">
                <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.model.MealTo"/>
                <tr class="${mealTo.excess ? "excess" : "notexcess"}">
                    <td>${dateTimeFormatter.format(mealTo.dateTime)}</td>
                    <td>${mealTo.description}</td>
                    <td>${mealTo.calories}</td>
                    <td>
                        <a href="meals?action=edit&id=${mealTo.id}" class="rowButton">Edit</a>
                    </td>
                    <td>
                        <a href="meals?action=delete&id=${mealTo.id}" class="rowButton">Delete</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>
</body>
</html>