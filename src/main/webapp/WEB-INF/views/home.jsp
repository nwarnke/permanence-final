<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript">
    <%@ include file="../js/jquery.min.js"%>
    <%@ include file="../js/d3.v3.min.js"%>
    <%@ include file="../js/graph.js"%>
    <%@ include file="../js/default.js"%>
</script>
<style>
    <%@ include file="../css/graph.css"%>
    <%@ include file="../css/default.css"%>
</style>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Home</title>

</head>
<body id="body" onload="loadValues()">
<div id="container">
    <div class="header">
        <h1>Interactive Permanence Example</h1>
        <p>Upload a graph to begin.</p>
    </div>
        <form action="<c:url value="/upload"/>" enctype="multipart/form-data" method="post" target="uploadTrg">
            <input type="file" name="file"><br><br>
            <input type="submit">
            <br><br>
        </form>
    Raw output:<br>
        <iframe id="uploadTrg" name="uploadTrg" width="500" onload="loadGraph(); displayValues();"></iframe>
    <br>
        <div id="warning" style="display:none">
            <p class="warning">Unable to parse input file</p>
        </div>
    <div id="numbers" style="display:none">
        <ul>
            <li>
                <div id="numOfNodes"></div>
            </li>
            <li>
                <div id="numOfEdges"></div>
            </li>
            <li>
                <div id="permanenceOfGraph"></div>
            </li>
        </ul>
        <br>
    </div>

    <input type="text" id="linkDist" size="5"><br>
    <button type="button" onclick="updateLinkDistance()">Update Link Distance</button><br><br>
    <button type="button" onclick="maxPermanence()">Max Permanence</button>
    <br>
    <br>
</div>
<div id="drawing" class="drawing"></div>
<br>
</body>

</html>