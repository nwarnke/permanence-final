var jsonarry;
var force;
var width = 1000, height = 700;
var linkDistanceVar = 15;
function loadGraph() {
    if (window["uploadTrg"].document.body.innerText.length > 0) {
        var graphdatum = JSON.parse(window["uploadTrg"].document.body.innerText);
        if (graphdatum.length > 1) {
            graphdatum.forEach(function (graph) {
                setTimeout(function () {
                }, 3000);
                load(graph);
            });
        } else {
            load(window["uploadTrg"].document.body.innerText);
        }

    }
}

function displayValues() {
    if (jsonarry != null) {
        document.getElementById("numOfNodes").innerHTML = "Number of nodes: " + jsonarry.nodes.length;
        document.getElementById("numOfEdges").innerHTML = "Number of edges: " + jsonarry.links.length;
        document.getElementById("permanenceOfGraph").innerHTML = "Permanence of graph:" + jsonarry.permanenceOfGraph;
        document.getElementById("numbers").style.display = "inline";
    }
}

function maxPermanence() {
    var graphs;
    $.ajax({
        url: "/maxpermanence",
        type: "GET",
        success: function (data) {
            graphs = data;
        },
        async: false
    });


    for (var i = 0; i < graphs.length; i++) {
        setTimeout(function (x) {
            return function () {
                for(var y = 0; y < jsonarry.nodes.length; y++){
                    jsonarry.nodes[y].group = graphs[x].nodes[y].group;
                    jsonarry.nodes[y].permanence = graphs[x].nodes[y].permanence;
                }
                document.getElementById("permanenceOfGraph").innerHTML = "Permanence of graph:"+graphs[x].permanenceOfGraph;
                start();
            };
        }(i), 3000 * i);
    }

}


function updateLinkDistance() {
    force.stop();
    linkDistanceVar = document.getElementById("linkDist").value;
    force.start();
}

function loadValues() {
    document.getElementById("linkDist").value = 15;
}

function start() {
    force.start();
}