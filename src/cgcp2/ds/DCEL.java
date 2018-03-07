package cgcp2.ds;

import java.util.TreeSet;

enum vType {
    split, merge, start, end, regL, regR;
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

    public java.awt.Point toPoint() {
        return new java.awt.Point((int) this.coord.x, (int) this.coord.y);
    }

    public static double slope(Vertex orig, Vertex dest) {
        return Math.atan2(dest.coord.y - orig.coord.y, dest.coord.x - orig.coord.x);
    }
}

class Edge implements Comparable<Edge> {
    public Vertex origin, dest;
    public Edge nEdge, pEdge;
    public Face lFace, rFace;
    public double slope;

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

    public Edge(Vertex origin, Vertex dest) {
        this.origin = origin;
        this.dest = dest;
    }

    public Edge(Vertex origin, Vertex dest, Edge nEdge, Edge pEdge, Face lFace, Face rFace) {

        this.origin = origin;
        this.dest = dest;
        this.nEdge = nEdge;
        this.pEdge = pEdge;
        this.lFace = lFace;
        this.rFace = rFace;
    }

    public double getSlope() {
        this.slope = Math.atan2(dest.coord.y - origin.coord.y, dest.coord.x - origin.coord.x);
        return this.slope;
    }

    public double getReverseSlope() {
        return Math.atan2(origin.coord.y - dest.coord.y, origin.coord.x - dest.coord.x);
    }

    @Override
    public int compareTo(Edge o) {
        if (this.dest == o.dest && this.origin == o.origin) return 0;
        if (o.origin == this.dest) {
            int mul = 1;
            if (o.dest.coord.y > o.origin.coord.y && this.origin.coord.y > o.origin.coord.y)
                mul = 1;
            else mul = -1;
            if (o.getSlope() > this.getReverseSlope()) return mul;
            else return -mul;
        } else if (o.dest == this.origin) {
            int mul = 1;
            if (o.origin.coord.y > o.dest.coord.y && this.dest.coord.y > o.dest.coord.y)
                mul = -1;
            else mul = 1;
            if (o.getSlope() > this.getReverseSlope()) return -mul;
            else return mul;
        }
        Edge up;
        Edge down;
        int mul = 1;
        if (Math.max(o.dest.coord.y, o.origin.coord.y) > Math.max(this.dest.coord.y, this.origin.coord.y)) {
            up = o;
            down = this;
        } else {
            up = this;
            down = o;
            mul = -1;
        }

        double dmaxx, dmaxy;
        if (down.dest.coord.y < down.origin.coord.y) {
            dmaxx = down.origin.coord.x;
            dmaxy = down.origin.coord.y;
        } else {
            dmaxx = down.dest.coord.x;
            dmaxy = down.dest.coord.y;
        }

        double uminx, uminy;
        if (up.dest.coord.y < up.origin.coord.y) {
            uminx = up.dest.coord.x;
            uminy = up.dest.coord.y;
        } else {
            uminx = up.origin.coord.x;
            uminy = up.origin.coord.y;
        }

        if (dmaxy < uminy) return 1;

        java.awt.Point inter = TrapezoidSolution.getYIntersection(up, dmaxy);

        if (inter.x < dmaxx) {
            return mul;
        } else return -mul;

    }
}

class Face implements Comparable<Face> {
    public Edge edge;

    public Face(Edge edge) {
        this.edge = edge;
    }

    @Override
    public int compareTo(Face o) {
        return 1;
    }
}

