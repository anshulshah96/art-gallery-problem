package cgcp2.ds;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class DualitySolution {
    public DCEL dcel;
    public ArrayList<Segment> dualEdges;
    public HashMap<Point, ArrayList<Point>> adjList;
    public ArrayList<Point> dualVertices;
    public Face of;
    public void generate() {
        dualEdges = new ArrayList<>();
        adjList = new HashMap<>();
        dualVertices = new ArrayList<>();

        for(Face face : dcel.faces) {
            if(face.getNumVertices() == dcel.vertices.size()) {
                of = face;
                continue;
            }
            dualVertices.add(face.getMidPoint());
            adjList.put(face.getMidPoint(), new ArrayList<>());
        }

        for(Edge edge : dcel.edges) {
            if (edge.rFace != of) {
                Point v1 = edge.rFace.getMidPoint();
                Point v2 = edge.lFace.getMidPoint();
                dualEdges.add(new Segment(v1, v2));

                adjList.get(v1).add(v2);
                adjList.get(v2).add(v1);
            }
        }

    }
}
