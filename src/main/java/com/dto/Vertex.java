package com.dto;

import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex>{

  private String name;
  private List<Vertex> neighbors = new ArrayList<Vertex>();
  private float permanence;
  private String community;
  private boolean visited = false;
  private float numOfInternalConnections;
  private float totalNumOfConnections;
  private float clusteringCoefficient;
  private float maxNumOfExternalConnections;


  public float getMaxNumOfExternalConnections() {
    return maxNumOfExternalConnections;
  }

  public void setMaxNumOfExternalConnections(float maxNumOfExternalConnections) {
    this.maxNumOfExternalConnections = maxNumOfExternalConnections;
  }

  public float getClusteringCoefficient() {
    return clusteringCoefficient;
  }

  public void setClusteringCoefficient(float clusteringCoefficient) {
    this.clusteringCoefficient = clusteringCoefficient;
  }

  public float getTotalNumOfConnections() {
    return totalNumOfConnections;
  }

  public void setTotalNumOfConnections(float totalNumOfConnections) {
    this.totalNumOfConnections = totalNumOfConnections;
  }

  public float getNumOfInternalConnections() {
    return numOfInternalConnections;
  }

  public void setNumOfInternalConnections(float numOfInternalConnections) {
    this.numOfInternalConnections = numOfInternalConnections;
  }

  public boolean isVisited() {
    return visited;
  }

  public void setVisited(boolean visited) {
    this.visited = visited;
  }

  public String getCommunity() {
    return community;
  }

  public void setCommunity(String community) {
    this.community = community;
  }

  public Vertex(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public List<Vertex> getNeighbors() {
    return neighbors;
  }

  public float getPermanence() {
    return permanence;
  }

  public void setPermanence(float permanence) {
    this.permanence = permanence;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Vertex vertex = (Vertex) o;

    return name.equals(vertex.name);

  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public int compareTo(Vertex o) {
    Integer first = this.getNeighbors().size();
    Integer second = o.getNeighbors().size();
    return second.compareTo(first);
  }
}
