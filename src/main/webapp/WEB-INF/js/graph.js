function load(data) {
    $("#warning").css("display", "none");
    if (data.length == 0) {
        return;
    }

    
    var nominal_stroke = .5;
    try {
        if(data.nodes != null){
         jsonarry = data;
        }else {
            jsonarry = JSON.parse(data);
        }
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
        .linkStrength(.2)
        .linkDistance(15)
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

    var highlight_node = null, focus_node = null;
    var default_link_color = "black";
    var highlight_color = "blue";
    var linkedByIndex = {};

    jsonarry.links.forEach(function(d){
        linkedByIndex[d.source + "," + d.target] = true;
    });

    function set_highlight(d)
    {
        // svg.style("cursor","pointer");
        if (focus_node!==null) d = focus_node;
        highlight_node = d;

        if (highlight_color!="white")
        {
            node.style("stroke", function(o){
                return isConnected(d, o) ? highlight_color : "white";});
            link.style("stroke", function(o) {
                return o.source.index == d.index || o.target.index == d.index ? highlight_color : ((isNumber(o.score) && o.score>=0)?color(o.score):default_link_color);
            }).style("stroke-width", function(o){
                return o.source.index == d.index || o.target.index == d.index ? 2 : ((isNumber(o.score) && o.score>=0)?color(o.score):nominal_stroke);
            });
        }
    }

    function exit_highlight()
    {
        highlight_node = null;
        if (focus_node===null)
        {
            // svg.style("cursor","move");
            if (highlight_color!="white")
            {
                node.style("stroke", "white");
                link.style("stroke", function(o) {return (isNumber(o.score) && o.score>=0)?color(o.score):default_link_color})
                    .style("stroke-width", nominal_stroke);
            }

        }
    }

    function isConnected(a, b) {
        return linkedByIndex[a.index + "," + b.index] || linkedByIndex[b.index + "," + a.index] || a.index == b.index;
    }

    function isNumber(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }

    var node = svg.selectAll('.node')
        .data(jsonarry.nodes)
        .enter().append('circle')
        .on("mouseover", function (d) {
            set_highlight(d);
        })
        .on("mouseout", function (d) {
            div.transition()
                .duration(500)
                .style("opacity", 0);
            exit_highlight();
        })
        .on("click", function(d){
            
            div.transition()
                .duration(100)
                .style("opacity", .9);
            div.html("<ul><li><strong>Name: </strong>" + d.name + "</li><li><strong>Community: </strong>" + d.group + "</li><li><strong>Permanence: </strong>" + d.permanence + "</li></ul>")
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY - 28) + "px");
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

