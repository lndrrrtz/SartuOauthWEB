<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Insert title here</title>
	</head>
	<body>
		<form:form action="${pageContext.request.contextPath}/oauth/login" id="loginForm" method="POST" modelAttribute="loginForm" autocomplete="off">
			
			<label for="usuario">Usuario/a</label>
			<form:input path="usuario" class="form-control" cssErrorClass="form-control is-invalid" id="usuario" autofocus="true" />
			
			<label for="contrasena">Contrase&ntilde;a</label>
			<form:password path="contrasena" class="form-control" cssErrorClass="form-control is-invalid" id="contrasena"/>
			
			<form:button class="btn btn-primary"><i class="far fa-sign-in mr-2 align-middle"></i>Sartu - Entrar</form:button>
			
		</form:form>
	</body>
</html>