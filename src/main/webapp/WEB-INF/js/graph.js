function load() {
    var data = window["uploadTrg"].document.body.innerText;
    $("#warning").css("display", "none");
    if (data.length == 0) {
        return;
    }

    var jsonarry;

    try {
        jsonarry = JSON.parse(data);
    } catch (e) {
        d3.select('svg').remove();
        $("#warning").css("display", "inline");
    }
    var width = 640, height = 480;

    var color = d3.scale.category20();

    var force = d3.layout.force()
        .charge(-120)
        .linkDistance(30)
        .size([width, height])
        .nodes(jsonarry.nodes)
        .links(jsonarry.links);

    d3.select('svg').remove();

    var svg = d3.select('body').append('svg')
        .attr('width', width)
        .attr('height', height);

    var link = svg.selectAll('.link')
        .data(jsonarry.links)
        .enter().append('line')
        .attr('class', 'link')
        .style("stroke-width", function (d) {
            return Math.sqrt(d.value);
        });

    var node = svg.selectAll('.node')
        .data(jsonarry.nodes)
        .enter().append('circle')
        .attr('class', 'node')
        .attr("r", 5)
        .style("fill", function (d) {
            return color(d.group);
        })
        .call(force.drag);

    force.on("tick", function () {
        link.attr("x1", function (d) {
            return d.source.x;
        })
            .attr("y1", function (d) {
                return d.source.y;
            })
            .attr("x2", function (d) {
                return d.target.x;
            })
            .attr("y2", function (d) {
                return d.target.y;
            });

        node.attr("cx", function (d) {
            return d.x;
        })
            .attr("cy", function (d) {
                return d.y;
            });
    });

    force.start();
}