function load() {
    var width = 640, height = 480;

    var data = window["uploadTrg"].document.body.innerText;

    if(data.length == 0) {
        return;
    }

    var jsonarry = JSON.parse(data);

    var svg = d3.select('body').append('svg')
        .attr('width', width)
        .attr('height', height);

    var force = d3.layout.force()
        .size([width, height])
        .nodes(jsonarry.nodes)
        .links(jsonarry.links);

    var link = svg.selectAll('.link')
        .data(jsonarry.links)
        .enter().append('line')
        .attr('class', 'link');

    var node = svg.selectAll('.node')
        .data(jsonarry.nodes)
        .enter().append('circle')
        .attr('class', 'node');

    force.on('end', function () {
        node.attr('r', width / 25)
            .attr('cx', function (d) {
                return d.x;
            })
            .attr('cy', function (d) {
                return d.y;
            });

        link.attr('x1', function (d) {
            return d.source.x;
        })
            .attr('y1', function (d) {
                return d.source.y;
            })
            .attr('x2', function (d) {
                return d.target.x;
            })
            .attr('y2', function (d) {
                return d.target.y;
            });
    });

    force.start();
}