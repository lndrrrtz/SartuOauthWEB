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
		<div class="container-fluid d-flex flex-column align-items-center justify-content-center flex-grow-1 login-background" style="max-height: 100vh; overflow: auto;">
			
			<div class="row justify-content-center mb-3">
				<h1><spring:message code="acceso.denegado" /></h1>
			</div>
<!-- 			<div class="row justify-content-center"> -->
<%-- 				<spring:url var="urlHome" value="/oauth/login"/> --%>
<%-- 				<a href="${urlHome}"><spring:message code="volver.login.sartu" /></a> --%>
<!-- 			</div> -->
			<div class="row justify-content-center">
				<a href="${param.redirect_uri}"><spring:message code="volver.aplicacion" /></a>
			</div>
			
		</div>
	</body>
	
	<footer class="bg-body-tertiary text-center text-lg-start">
		<div class="text-center p-3 bg-light">
			<a class="text-body" href="https://www.unir.net/" target="_blank"><img src="${ruta_recursos}/imagenes/unir.svg" width="50" alt="UNIR" /></a>
		</div>
	</footer>
</html>