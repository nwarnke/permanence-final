package com.dto;

import java.util.ArrayList;
import java.util.List;

public class JsonVertexList {
    private List<Node> nodes = new ArrayList<>();
    private List<Link> links = new ArrayList<>();
    private String permanenceOfGraph;

    public String getPermanenceOfGraph() {
        return permanenceOfGraph;
    }

    public void setPermanenceOfGraph(String permanenceOfGraph) {
        this.permanenceOfGraph = permanenceOfGraph;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
