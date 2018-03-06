package cgcp2.ds;

import java.util.HashSet;


public class DCEL {
    public HashSet<Vertex> vertices;
    public HashSet<Edge> edges;
    public HashSet<Face> faces;

    public DCEL() {
        vertices = new HashSet<Vertex>();
        edges = new HashSet<Edge>();
        faces = new HashSet<Face>();
    }

    public HashSet<Vertex> getVertices() {
        return vertices;
    }

    public HashSet<Edge> getEdges() {
        return edges;
    }

    public HashSet<Face> getFaces() {
        return faces;
    }
}

class Vertex implements Comparable<Vertex> {
    public Point coordinate;
    public Edge incidentEdge;

    public Vertex(Point coordinate, Edge incidentEdge) {
        this.coordinate = coordinate;
        this.incidentEdge = incidentEdge;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    public Edge getIncidentEdge() {
        return incidentEdge;
    }

    public void setIncidentEdge(Edge incidentEdge) {
        this.incidentEdge = incidentEdge;
    }

    @Override
    public int compareTo(Vertex arg0) {
        return this.coordinate.compareTo(arg0.getCoordinate());
    }

    public String toString() {
        return coordinate.toString();
    }
}

class Edge {
    public Vertex origin, dest;
    public Edge nEdge, pEdge;
    public Face lFace, rFace;

    public Vertex getOrigin() {
        return origin;
    }

    public void setOrigin(Vertex origin) {
        this.origin = origin;
    }

    public Vertex getDest() {
        return dest;
    }

    public void setDest(Vertex dest) {
        this.dest = dest;
    }

    public Edge getnEdge() {
        return nEdge;
    }

    public void setnEdge(Edge nEdge) {
        this.nEdge = nEdge;
    }

    public Edge getpEdge() {
        return pEdge;
    }

    public void setpEdge(Edge pEdge) {
        this.pEdge = pEdge;
    }

    public Face getlFace() {
        return lFace;
    }

    public void setlFace(Face lFace) {
        this.lFace = lFace;
    }

    public Face getrFace() {
        return rFace;
    }

    public void setrFace(Face rFace) {
        this.rFace = rFace;
    }

    public Edge(Vertex origin, Vertex dest, Edge nEdge, Edge pEdge, Face lFace, Face rFace) {

        this.origin = origin;
        this.dest = dest;
        this.nEdge = nEdge;
        this.pEdge = pEdge;
        this.lFace = lFace;
        this.rFace = rFace;
    }
}

class Face {
    public Edge edge;

    public Face(Edge edge) {
        this.edge = edge;
    }
}

