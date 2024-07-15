<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>

<html>
	<head>
		<meta charset="ISO-8859-1">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		
		<link rel="icon" type="image/x-icon" href="${ruta_estaticos_baibot}/img/favicon.ico">
		
		<spring:url var="ruta_recursos" value="/recursos" />
		
		<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
		<link rel="stylesheet" href="${ruta_recursos}/css/estilos.css">
		
		<title>Sartu</title>

		<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
		<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
		<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
		
	</head>
	<body class="d-flex flex-column h-100">
		
		
		
<!-- 		<div class="container"> -->
		
<%-- 			*${pageContext.servletContext.contextPath}* --%>
<%-- 			*${proba}* --%>
<%-- 			<img src="${ruta_recursos}/imagenes/login-background.jpg" width="50" alt="cargando" /> --%>
		
			<div class="container-fluid d-flex flex-column align-items-center justify-content-center flex-grow-1 login-background" style="max-height: 100vh; overflow: auto;">
				
				<section class="w-100 p-4 d-flex justify-content-center pb-4">
	
					<form:form action="${pageContext.request.contextPath}/oauth/login" id="loginForm" method="POST" modelAttribute="loginForm">
						<div class="text-center mb-5">
							<img src="${ruta_recursos}/imagenes/sartu.svg" class="w-75" />
						</div>
						
						<c:choose>
							<c:when test="${failureMessage != null || param.failureMessage != null}">
								<div class="alert alert-danger align-middle alert-dismissible fade show" role="alert">
									<button type="button" class="close" data-dismiss="alert">&times;</button>
									<strong><spring:message code="${failureMessage}${param.failureMessage}"/></strong>
								</div>
							</c:when>
							<c:when test="${param.auth eq 'failure'}">
								<div class="alert alert-danger align-middle alert-dismissible fade show" role="alert">
									<button type="button" class="close" data-dismiss="alert">&times;</button>
									<c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />.
								</div>
							</c:when>
							<c:when test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
								<div class="alert alert-danger align-middle alert-dismissible fade show" role="alert">
									<button type="button" class="close" data-dismiss="alert">&times;</button>
									<strong><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.</strong>
								</div>
							</c:when>
						</c:choose>
						
						<!-- Id del usuario -->
						<div class="form-group mb-4">
							<label for="idUsuario">Usuario/a</label>
							<form:input path="idUsuario" class="form-control" id="idUsuario" autofocus="true" />
						</div>
						
						<!-- Contraseña del usuario -->
						<div class="form-group mb-4">
							<label for="contrasena">Contrase&ntilde;a</label>
							<form:password path="contrasena" class="form-control" id="contrasena"/>
						</div>
						
						<form:button class="btn btn-primary btn-block mb-1"><i class="fas fa-sign-in-alt"></i><i class="fa fa-sign-in mr-2 align-middle"></i>Entrar</form:button>
						
						<!--
						<div class="row mb-4">
							<div class="col d-flex justify-content-center">
								<div class="form-check">
									<input class="form-check-input" type="checkbox" value="" id="recordarCredenciales" checked />
									<label class="form-check-label" for="recordarCredenciales"> Recordar </label>
								</div>
							</div>
							
							<div class="col">
								<a href="#!">¿Has olvidado la contraseña?</a>
							</div>
						</div>
						
						<div class="text-center">
							<p>¿No eres miembro? <a href="#!">Regístrate</a></p>
						</div>
						-->
						
					</form:form>
			</section>
		</div>
	</body>
	
	<footer class="bg-body-tertiary text-center text-lg-start">
		<div class="text-center p-3 bg-light">
			<a class="text-body" href="https://www.unir.net/" target="_blank"><img src="${ruta_recursos}/imagenes/unir.svg" width="50" alt="UNIR" /></a>
		</div>
	</footer>
</html>