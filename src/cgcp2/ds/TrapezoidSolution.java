package cgcp2.ds;

import java.awt.Point;
import java.util.ArrayList;

public class TrapezoidSolution {

    public DCEL dcel;
    public ArrayList<java.awt.Point> pointArrayList;
    public ArrayList<Edge> edgeList;

    public void generateTrap() {
        for(Edge edge : dcel.edges) {
            Vertex v1 = edge.origin;
            Vertex v2 = edge.dest;
            Vertex v3 = edge.nEdge.dest;
            v2.type = getVType(v1,v2,v3);
        }

        for(Vertex v : dcel.vertices) {
            System.out.println(v);
        }
    }

    public vType getVType(Vertex v1, Vertex v2, Vertex v3) {
        if(v2.coord.y < v1.coord.y && v3.coord.y < v2.coord.x) {
            return vType.regL;
        }
        else if(v2.coord.y > v1.coord.y && v3.coord.y > v2.coord.x) {
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
