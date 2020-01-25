package com.example.lbt.lbt;

public class Edge {

    private final Vertex source;
    private final Vertex destination;
    private final double weight;

    public Edge(Vertex source, Vertex destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " -(" + weight + ")- " + destination;
    }

}
