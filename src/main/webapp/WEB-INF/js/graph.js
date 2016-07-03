function load() {
    var data = window["uploadTrg"].document.body.innerText;
    $("#warning").css("display", "none");
    if (data.length == 0) {
        return;
    }

    
    var nominal_stroke = .5;
    try {
        jsonarry = JSON.parse(data);
    } catch (e) {
        d3.select('svg').remove();
        $("#warning").css("display", "inline");
    }
    var width = 1500, height = 700, radius = 6;

    var color = d3.scale.category20();

    var div = d3.select("body").append("div")
        .attr("class", "tooltip")
        .style("opacity", 0);

    var force = d3.layout.force()
        .charge(-120)

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
        .on("mouseover", function (d) {
            div.transition()
                .duration(100)
                .style("opacity", .9);
            div.html("<ul><li><strong>Name: </strong>" + d.name + "</li><li><strong>Community: </strong>" + d.group + "</li><li><strong>Permanence: </strong>" + d.permanence + "</li></ul>")
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY - 28) + "px");
        })
        .on("mouseout", function (d) {
            div.transition()
                .duration(500)
                .style("opacity", 0);
        })
        .attr('class', 'node')
        .attr("r", 5)
        .style("fill", function (d) {
            return color(d.group);
        })
        .call(force.drag);


    var texts = svg.selectAll("text.label")
        .data(jsonarry.nodes)
        .enter().append("text")
        .attr("class", "label")
        .attr("fill", "black")
        .text(function (d) {
            return d.name;
        });

    // node.on("mouseover", function (d) {
    //     console.log("mouseover event");
    //     div.html("<p>Hello world</p>")
    //
    // });
    // node.on("mouseout", function (d) {
    //     console.log("mouseout event");
    // });


    force.on("tick", function () {
        link.attr("x1", function (d) {
            return d.source.x;
        }).attr("y1", function (d) {
            return d.source.y;
        }).attr("x2", function (d) {
            return d.target.x;
        }).attr("y2", function (d) {
            return d.target.y;
        });

        node.attr("cx", function (d) {
            return d.x = Math.max(radius, Math.min(width - radius, d.x));
        }).attr("cy", function (d) {
            return d.y = Math.max(radius, Math.min(height - radius, d.y));
        });

        texts.attr("transform", function (d) {
            return "translate(" + (d.x + 5) + "," + d.y + ")";
        });

    });

    force.start();
}

