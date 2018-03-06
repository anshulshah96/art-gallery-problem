package cgcp2.ds;

import java.util.TreeSet;

enum vType {
    split,  merge, start, end, regL, regR;
}

public class DCEL {
    public TreeSet<Vertex> vertices;
    public TreeSet<Edge> edges;
    public TreeSet<Face> faces;

    public DCEL() {
        vertices = new TreeSet<Vertex>();
        edges = new TreeSet<Edge>();
        faces = new TreeSet<Face>();
    }

    public TreeSet<Vertex> getVertices() {
        return vertices;
    }

    public TreeSet<Edge> getEdges() {
        return edges;
    }

    public TreeSet<Face> getFaces() {
        return faces;
    }
}

class Vertex implements Comparable<Vertex> {
    public Point coord;
    public Edge incidentEdge;
    public vType type;

    public Vertex(Point coord, Edge incidentEdge) {
        this.coord = coord;
        this.incidentEdge = incidentEdge;
    }

    public Point getCoord() {
        return coord;
    }

    public void setCoord(Point coord) {
        this.coord = coord;
    }

    public Edge getIncidentEdge() {
        return incidentEdge;
    }

    public void setIncidentEdge(Edge incidentEdge) {
        this.incidentEdge = incidentEdge;
    }

    @Override
    public int compareTo(Vertex arg0) {
        return this.coord.compareTo(arg0.getCoord());
    }

    public String toString() {
        return coord.toString();
    }
}

class Edge implements Comparable<Edge> {
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

    @Override
    public int compareTo(Edge o) {
        return 0;
    }
}

class Face implements Comparable<Face> {
    public Edge edge;

    public Face(Edge edge) {
        this.edge = edge;
    }

    @Override
    public int compareTo(Face o) {
        return 0;
    }
}

