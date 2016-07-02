function load() {
    var data = window["uploadTrg"].document.body.innerText;
    $("#warning").css("display", "none");
    if (data.length == 0) {
        return;
    }

    var jsonarry;
    var nominal_stroke = 1.5;
    try {
        jsonarry = JSON.parse(data);
    } catch (e) {
        d3.select('svg').remove();
        $("#warning").css("display", "inline");
    }
    var width = 1500, height = 750, radius = 6;

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
        .style("stroke-width", nominal_stroke);

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
            })
        ;

        node.attr("cx", function(d) { return d.x = Math.max(radius, Math.min(width - radius, d.x)); })
            .attr("cy", function(d) { return d.y = Math.max(radius, Math.min(height - radius, d.y)); });
    });

    force.start();
}