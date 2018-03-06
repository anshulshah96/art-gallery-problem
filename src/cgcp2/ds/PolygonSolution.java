package cgcp2.ds;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PolygonSolution {

    public DCEL dcel;
    public ArrayList<Point> pointArrayList;
    public ArrayList<Point> linePointList;

    public PolygonSolution() {
    }

    public void generatePolygon() {
        pointArrayList.sort((o1, o2) -> {
            if (o1.y < o2.y) return 1;
            else if (o2.y < o1.y) return -1;
            else {
                if (o1.x < o2.x) return 1;
                else return -1;
            }
        });

        Point minPoint = pointArrayList.get(0);
        ArrayList<Point> nxtList = (ArrayList<Point>) pointArrayList.clone();
        nxtList.remove(0);
        nxtList.sort((o1, o2) -> {
            double slope1 = Math.atan2(o1.y - minPoint.y, o1.x - minPoint.x);
            double slope2 = Math.atan2(o2.y - minPoint.y, o2.x - minPoint.x);
            if (slope1 < slope2) return 1;
            else return -1;
        });
        nxtList.add(minPoint);
        linePointList = (ArrayList<Point>) nxtList.clone();

        Face iFace = new Face(null);
        Face oFace = new Face(null);

        List<Vertex> vList = new ArrayList<Vertex>();
        List<Edge> eList = new ArrayList<Edge>();
        for (Point p : pointArrayList) {
            Vertex tVertex = new Vertex(cgcp2.ds.Point.toPoint(p), null);
            vList.add(tVertex);
        }

        for (int i = 0; i < vList.size(); i++) {
            if (i != vList.size() - 1) {
                eList.add(new Edge(vList.get(i), vList.get(i + 1), null, null, iFace, oFace));
            } else {
                eList.add(new Edge(vList.get(i), vList.get(0), null, null, iFace, oFace));
            }
        }

        for (int i = 0; i < vList.size(); i++) {
            vList.get(i).incidentEdge = eList.get(i);
        }

        for(int i = 0; i < eList.size(); i++) {
            if(i==0) {
                eList.get(i).nEdge = eList.get(i+1);
                eList.get(i).pEdge = eList.get(eList.size()-1);
            }
            else if(i==eList.size()-1) {
                eList.get(i).nEdge = eList.get(0);
                eList.get(i).pEdge = eList.get(i-1);
            }
            else {
                eList.get(i).nEdge = eList.get(i+1);
                eList.get(i).pEdge = eList.get(i-1);
            }
        }

        iFace.edge = eList.get(0);
        oFace.edge = eList.get(0);

        dcel = new DCEL();
        dcel.edges = new HashSet<>(eList);
        dcel.vertices = new HashSet<>(vList);
        dcel.faces = new HashSet<>();
        dcel.faces.add(iFace);
        dcel.faces.add(oFace);
    }
}
