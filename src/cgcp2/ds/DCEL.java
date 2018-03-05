package cgcp2.ds;

import java.util.HashMap;
import java.util.HashSet;


public class DCEL {
    public HashSet<Vertex> vertices;
    public HashSet<HalfEdge> edges;
    public HashMap<Point, Face> faces;

    public DCEL() {
        vertices = new HashSet<Vertex>();
        edges = new HashSet<HalfEdge>();
        faces = new HashMap<Point, Face>();
    }

    public HashSet<Vertex> getVertices() {
        return vertices;
    }

    public HashSet<HalfEdge> getEdges() {
        return edges;
    }

    public HashMap<Point, Face> getFaces() {
        return faces;
    }


}

class Vertex implements Comparable<Vertex> {
    private Point coordinate;
    private HalfEdge incidentEdge;

    public Vertex(Point coordinate, HalfEdge incidentEdge) {
        this.coordinate = coordinate;
        this.incidentEdge = incidentEdge;
    }

    public Vertex() {
        this(null, null);
    }

    public Vertex(Point point) {
        this(point, null);
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public HalfEdge getIncident() {
        return incidentEdge;
    }

    public void setCoordinate(Point p) {
        coordinate = p;
    }

    public void setIncident(HalfEdge e) {
        incidentEdge = e;
    }

    @Override
    public int hashCode() {
        return coordinate.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int compareTo(Vertex arg0) {
        return this.coordinate.compareTo(arg0.getCoordinate());
    }

    public String toString() {
        return coordinate.toString();
    }
}

class HalfEdge {
    public Vertex origin;
    private HalfEdge twin, next, previous;
    private Face incidentFace;

    public HalfEdge(Vertex origin, HalfEdge twin, HalfEdge next, HalfEdge previous, Face incidentFace) {
        this.origin = origin;
        this.twin = twin;
        this.next = next;
        this.previous = previous;
        this.incidentFace = incidentFace;
    }

    public HalfEdge() {
        this(null, null, null, null, null);
    }

    public String toString() {
        return "Origin : " + origin;

    }

    public Vertex getOrigin() {
        return origin;
    }

    public HalfEdge getTwin() {
        return twin;
    }

    public HalfEdge getNext() {
        return next;
    }

    public HalfEdge getPrev() {
        return previous;
    }

    public Face getIncident() {
        return incidentFace;
    }

    public void setOrigin(Vertex o) {
        origin = o;
    }

    public void setTwin(HalfEdge e) {
        twin = e;
    }

    public void setNext(HalfEdge e) {
        next = e;
    }

    public void setPrev(HalfEdge e) {
        previous = e;
    }

    public void setIncident(Face f) {
        incidentFace = f;
    }


}

class Face {
    private HalfEdge outerComponent, innerComponent;
    Point site;

    public Face(Point site, HalfEdge outerComponent, HalfEdge innerComponent) {
        this.site = site;
        this.innerComponent = innerComponent;
        this.outerComponent = outerComponent;
    }

    public Face(Point p) {
        this(p, null, null);
    }

    public Face() {
        this(null, null, null);
    }

    public String toString() {
        return "Center :" + site;
    }

    public HalfEdge getOuter() {
        return outerComponent;
    }

    public HalfEdge getInner() {
        return innerComponent;
    }

    public Point getSite() {
        return site;
    }

    public void setOuter(HalfEdge e) {
        outerComponent = e;
    }

    public void setInner(HalfEdge e) {
        innerComponent = e;
    }

    public void setSite(Point p) {
        site = p;
    }

    @Override
    public int hashCode() {
        return site.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}

