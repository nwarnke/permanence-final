package com.dto;

import java.util.ArrayList;
import java.util.List;

public class JsonVertexLists {
    List<Node> nodes = new ArrayList<>();
    List<Link> links = new ArrayList<>();

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
