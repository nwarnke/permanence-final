<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript">
    function load(){
        console.log(window["uploadTrg"].document.body.innerText);
    }
</script>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home</title>

    </head>
    <body>
        <h1>Hello world and all creatures thereof.</h1>
        <form action="<c:url value="/upload"/>" enctype="multipart/form-data" method="post" target="uploadTrg">
            <input type="file" name="file">
            <input type="submit">
        </form>
    <iframe id="uploadTrg" name="uploadTrg" onload="load()"></iframe>
    </body>
</html>
