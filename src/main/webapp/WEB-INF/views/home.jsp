<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
<script src="//d3js.org/d3.v3.min.js" type="text/javascript"></script>
<script type="text/javascript">
    var jsonarry;
    var force;
    var width = 1500, height = 500;
    var linkDistanceVar = 15;
    function loadGraph(){
        load(window["uploadTrg"].document.body.innerText);
    }

    function displayValues(){
        if(jsonarry != null) {
            document.getElementById("numOfNodes").innerHTML = "Number of nodes: "+jsonarry.nodes.length;
            document.getElementById("numOfEdges").innerHTML = "Number of edges: "+jsonarry.links.length;
            document.getElementById("numbers").style.display = "inline";
        }
    }

    function maxPermanence(){
        $.get("/maxpermanence", function(data){
           load(data);
            window["uploadTrg"].document.body.innerText = JSON.stringify(data);
        });
    }

    function updateLinkDistance(){
        force.stop();
        linkDistanceVar = 100;
        force.start();
    }
</script>
<script type="text/javascript">
    <%@ include file="../js/graph.js"%>
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
<body id="body">
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
        </ul>
        <br>
    </div>

    <button type="button" onclick="maxPermanence()">Max Permanence</button>
    <button type="button" onclick="updateLinkDistance()">Update Link Distance</button>
    <br>
    <br>
</div>
</body>
</html>
