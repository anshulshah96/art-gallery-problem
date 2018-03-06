package cgcp2.ds;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.BinaryOperator;

public class TrapezoidSolution {

    public DCEL dcel;
    public ArrayList<java.awt.Point> pointArrayList;
    public ArrayList<Segment> closestLines;

    public void generateTrap() {
        for(Edge edge : dcel.edges) {
            Vertex v1 = edge.origin;
            Vertex v2 = edge.dest;
            Vertex v3 = edge.nEdge.dest;
            v2.type = getVType(v1,v2,v3);
        }

        closestLines.clear();
        TreeSet SList = new TreeSet<Edge>();
        for(Vertex v : dcel.vertices) {
            if(v.type == vType.start) {
                SList.add(v.incidentEdge);
                SList.add(v.incidentEdge.pEdge);
            }
            else if(v.type == vType.end) {
                SList.remove(v.incidentEdge);
                SList.remove(v.incidentEdge.pEdge);
            }
            else if(v.type == vType.split) {
                Edge be1 = v.incidentEdge;
                Edge be2 = v.incidentEdge.pEdge;
                SList.add(be1);
                SList.add(be2);
                Iterator it = SList.iterator();
                Iterator it2 = SList.iterator();
                Edge prev = (Edge) it2.next();
                Edge next = null;
                while (it2.hasNext()) {
                    Edge e1 = (Edge) it.next();
                    Edge e2 = (Edge) it2.next();
                    if(e2 == (be1.compareTo(be2)==1?be1:be2)) {
                        prev = e1;
                        it2.next();
                        next = (Edge) it2.next();
                        break;
                    }
                }

                Point lIntersection = getYIntersection(prev, v.coord.y);
                Point rIntersection = getYIntersection(next, v.coord.y);
                closestLines.add(new Segment(lIntersection,rIntersection));
            }
            else if(v.type == vType.merge) {
                Edge be1 = v.incidentEdge;
                Edge be2 = v.incidentEdge.pEdge;
                Iterator it = SList.iterator();
                Iterator it2 = SList.iterator();
                Edge prev = (Edge) it2.next();
                Edge next = null;
                while (it2.hasNext()) {
                    Edge e1 = (Edge) it.next();
                    Edge e2 = (Edge) it2.next();
                    if(e2 == (be1.compareTo(be2)==1?be1:be2)) {
                        prev = e1;
                        it2.next();
                        next = (Edge) it2.next();
                        break;
                    }
                }
                SList.remove(be1);
                SList.remove(be2);

                Point lIntersection = getYIntersection(prev, v.coord.y);
                Point rIntersection = getYIntersection(next, v.coord.y);
                closestLines.add(new Segment(lIntersection,rIntersection));
            }
            else if(v.type == vType.regL) {
                Edge be = v.incidentEdge.pEdge;
                Iterator it = SList.iterator();
                while(it.next() != be) {

                }
                Edge next = (Edge) it.next();
                SList.remove(be);
                SList.add(v.incidentEdge);
                Point rIntersection = getYIntersection(next, v.coord.y);
                closestLines.add(new Segment(v.toPoint(), rIntersection));
            }
            else {
                Edge be = v.incidentEdge;
                Iterator it = SList.descendingIterator();
                while(it.next() != be) {

                }
                Edge next = (Edge) it.next();
                SList.remove(be);
                SList.add(v.incidentEdge.pEdge);
                Point rIntersection = getYIntersection(next, v.coord.y);
                closestLines.add(new Segment(v.toPoint(), rIntersection));
            }
        }
    }

    public Point getYIntersection(Edge edge, double y) {
        double sx = edge.origin.coord.x;
        double sy = edge.origin.coord.y;
        double ex = edge.dest.coord.x;
        double ey = edge.dest.coord.y;
        double ans = sx + (y-sy) * ((ex - sx) / (ey - sy));
        return new Point((int)ans, (int)y);
    }

    public vType getVType(Vertex v1, Vertex v2, Vertex v3) {
        if(v2.coord.y < v1.coord.y && v3.coord.y < v2.coord.y) {
            return vType.regL;
        }
        else if(v2.coord.y > v1.coord.y && v3.coord.y > v2.coord.y) {
            return vType.regR;
        }
        else if(v2.coord.y < v1.coord.y && v2.coord.y < v3.coord.y) {
            if(v1.coord.x < v3.coord.x)
                return vType.end;
            else return vType.merge;
        }
        else if(v2.coord.y > v1.coord.y && v2.coord.y > v3.coord.y) {
            if(v1.coord.x < v3.coord.x)
                return vType.split;
            else return vType.start;
        }
        else return null;
    }
}
