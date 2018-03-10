package cgcp2.ds;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class TrapezoidSolution {

    public DCEL dcel;
    public ArrayList<Segment> trapezoidalLines;

    public void generateTrap() {
        for (Edge edge : dcel.edges) {
            Vertex v1 = edge.origin;
            Vertex v2 = edge.dest;
            Vertex v3 = edge.nEdge.dest;
            v2.type = getVType(v1, v2, v3);
        }

        trapezoidalLines.clear();
        TreeSet SList = new TreeSet<Edge>();
        for (Vertex v : dcel.vertices) {
            if (v.type == vType.start) {
                SList.add(v.incidentEdge);
                SList.add(v.incidentEdge.pEdge);
            } else if (v.type == vType.end) {
                SList.remove(v.incidentEdge);
                SList.remove(v.incidentEdge.pEdge);
            } else if (v.type == vType.split) {
                Edge be1 = v.incidentEdge;
                Edge be2 = v.incidentEdge.pEdge;
                Edge dummyPointEdge = new Edge(v, v);
                Edge prev = (Edge) SList.floor(dummyPointEdge);
                Edge next = (Edge) SList.ceiling(dummyPointEdge);
                SList.add(be1);
                SList.add(be2);
                Point lIntersection = getYIntersection(prev, v.coord.y);
                Point rIntersection = getYIntersection(next, v.coord.y);
                trapezoidalLines.add(new Segment(lIntersection, rIntersection));
            } else if (v.type == vType.merge) {
                Edge be1 = v.incidentEdge;
                Edge be2 = v.incidentEdge.pEdge;
                SList.remove(be1);
                SList.remove(be2);
                Edge dummyPointEdge = new Edge(v, v);
                Edge prev = (Edge) SList.floor(dummyPointEdge);
                Edge next = (Edge) SList.ceiling(dummyPointEdge);

                Point lIntersection = getYIntersection(prev, v.coord.y);
                Point rIntersection = getYIntersection(next, v.coord.y);
                trapezoidalLines.add(new Segment(lIntersection, rIntersection));
            } else if (v.type == vType.regL) {
                Edge be = v.incidentEdge.pEdge;
                Iterator<Edge> it = SList.iterator();
                Edge now = null;
                while (it.hasNext() && now != be) {
                    now = it.next();
                }
                Edge next = it.next();
                boolean cont = SList.remove(be);
                SList.add(v.incidentEdge);
                Point rIntersection = getYIntersection(next, v.coord.y);
                trapezoidalLines.add(new Segment(v.toPoint(), rIntersection));
            } else if (v.type == vType.regR) {
                Edge be = v.incidentEdge;
                Iterator it = SList.descendingIterator();
                while (it.next() != be) {

                }
                Edge next = (Edge) it.next();
                SList.remove(be);
                SList.add(v.incidentEdge.pEdge);
                Point rIntersection = getYIntersection(next, v.coord.y);
                trapezoidalLines.add(new Segment(v.toPoint(), rIntersection));
            } else {
                Edge b1 = v.incidentEdge;
                Edge b2 = v.incidentEdge.pEdge;

                if (b1.dest.coord.y == b1.origin.coord.y) {
                    if (SList.contains(b2))
                        SList.remove(b2);
                    else
                        SList.add(b2);
                } else {
                    if (SList.contains(b1))
                        SList.remove(b1);
                    else
                        SList.add(b1);
                }
            }
        }
    }

    public static Point getYIntersection(Edge edge, double y) {
        double sx = edge.origin.coord.x;
        double sy = edge.origin.coord.y;
        double ex = edge.dest.coord.x;
        double ey = edge.dest.coord.y;
        if (sy == y)
            return edge.origin.toPoint();
        if (ey == y)
            return edge.dest.toPoint();
        double ans = sx + (y - sy) * ((ex - sx) / (ey - sy));
        return new Point((int) ans, (int) y);
    }

    public vType getVType(Vertex v1, Vertex v2, Vertex v3) {
        if (v2.coord.y < v1.coord.y && v3.coord.y < v2.coord.y) {
            return vType.regL;
        } else if (v2.coord.y > v1.coord.y && v3.coord.y > v2.coord.y) {
            return vType.regR;
        } else if (v2.coord.y < v1.coord.y && v2.coord.y < v3.coord.y) {
            if (Vertex.slope(v2, v1) > Vertex.slope(v2, v3))
                return vType.end;
            else return vType.merge;
        } else if (v2.coord.y > v1.coord.y && v2.coord.y > v3.coord.y) {
            if (Vertex.slope(v2, v3) < Vertex.slope(v2, v1))
                return vType.start;
            else return vType.split;
        } else return null;
    }
}
