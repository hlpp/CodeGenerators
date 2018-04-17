<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/WEB-INF/jsp/common.jsp" />
</head>
<body>
    <form id="form" method="post">
        <input type="hidden" name="id" value="${volunteerHelp.id}" />
        <table class="form">
        </table>
    </form>
</body>
</html>