package com.service;

import com.dto.*;

import java.util.*;

public class Service {
    private static List<Vertex> vertices = new ArrayList<>();

    public static void calculatePermanence(Vertex vertex) {
        findNumberOfInternalConnections(vertex);
        findMaximumNumberOfExternalConnections(vertex);
        vertex.setTotalNumOfConnections(vertex.getNeighbors().size());
        findClusteringCoefficient(vertex);
        final float permanence = (vertex.getNumOfInternalConnections() / vertex.getMaxNumOfExternalConnections()) * (1 / vertex.getTotalNumOfConnections()) - (1 - vertex.getClusteringCoefficient());
        vertex.setPermanence(permanence);
    }

    public static void findNumberOfInternalConnections(Vertex vertex) {
        float internalConnections = 0;
        for (Vertex neighbor : vertex.getNeighbors()) {
            try {
                if (neighbor.getCommunity().equals(vertex.getCommunity())) {
                    internalConnections++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        vertex.setNumOfInternalConnections(internalConnections);
    }

    public static void findMaximumNumberOfExternalConnections(Vertex vertex) {

        Map<String, Integer> communities = new HashMap<>();

        for (Vertex neighbor : vertex.getNeighbors()) {
            if (!neighbor.getCommunity().equals(vertex.getCommunity())) {
                if (communities.containsKey(neighbor.getCommunity())) {
                    Integer integer = communities.get(neighbor.getCommunity());
                    integer++;
                    communities.put(neighbor.getCommunity(), integer);
                } else {
                    communities.put(neighbor.getCommunity(), 1);
                }
            }
        }
        if (communities.size() > 0) {
            vertex.setMaxNumOfExternalConnections(Collections.max(communities.values()));
        } else {
            vertex.setMaxNumOfExternalConnections(1);
        }
    }

    public static void findClusteringCoefficient(Vertex vertex) {
        float connectionsAmongNeighborsOfVertex = 0;
        float numberOfNeighborsInCommunity = vertex.getNumOfInternalConnections();
        float totalPossibleNumberOfConnectionsAmongNeighborsOfVertex = numberOfNeighborsInCommunity * ((numberOfNeighborsInCommunity - 1));

        for (Vertex neighbor : vertex.getNeighbors()) {
            if (neighbor.getCommunity().equals(vertex.getCommunity())) {
                for (Vertex secondNeighbor : neighbor.getNeighbors()) {
                    if (secondNeighbor.getCommunity().equals(neighbor.getCommunity()) && vertex.getNeighbors().contains(new Vertex(secondNeighbor.getName()))) {
                        connectionsAmongNeighborsOfVertex++;
                    }
                }
            }

        }
//        for (Vertex neighbor : vertex.getNeighbors()) {
//            if (neighbor.getCommunity().equals(vertex.getCommunity())) {
//                numberOfNeighborsInCommunity++;
//            }
//        }
//        for (float i = numberOfNeighborsInCommunity - 1; i > 0; i--) {
//            totalPossibleNumberOfConnectionsAmongNeighborsOfVertex = i + totalPossibleNumberOfConnectionsAmongNeighborsOfVertex;
//        }
        if (totalPossibleNumberOfConnectionsAmongNeighborsOfVertex > 1) {
            vertex.setClusteringCoefficient(connectionsAmongNeighborsOfVertex / totalPossibleNumberOfConnectionsAmongNeighborsOfVertex);
        } else {
            vertex.setClusteringCoefficient(0);
        }
    }

    public static List<Vertex> getVertices() {
        return vertices;
    }

    public static void setVertices(List<Vertex> vertices) {
        Service.vertices = vertices;
    }
}
