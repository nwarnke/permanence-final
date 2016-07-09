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
        document.getElementById("permanenceOfGraph").innerHTML = "Permanence of graph:"+jsonarry.permanenceOfGraph;
        document.getElementById("numbers").style.display = "inline";
    }
}

function maxPermanence(){
    $.get("/maxpermanence", function(data){
        load(data);
        window["uploadTrg"].document.body.innerText = JSON.stringify(data);
        displayValues();
    });
}

function updateLinkDistance(){
    force.stop();
    linkDistanceVar = document.getElementById("linkDist").value;
    force.start();
}

function loadValues(){
    document.getElementById("linkDist").value = 15;
}