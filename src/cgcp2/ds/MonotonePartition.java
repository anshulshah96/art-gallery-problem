package cgcp2.ds;

import java.awt.Point;
import java.util.*;

public class MonotonePartition {
    public DCEL dcel;
    public ArrayList<Segment> partitionLines;
    private ArrayList<Edge> diagonals;

    public MonotonePartition() {
        partitionLines = new ArrayList<>();
        diagonals = new ArrayList<>();
    }

    public void generate() {
        partitionLines.clear();
        diagonals.clear();

        TreeSet<Edge> SList = new TreeSet<Edge>();
        TreeMap<Edge, Vertex> helper = new TreeMap<Edge, Vertex>();
        for (Vertex v : dcel.vertices) {
            if (v.type == vType.start) {
                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else if (v.type == vType.end) {
                Edge eprev = v.incidentEdge.pEdge;
                if (helper.get(eprev).type == vType.merge) {
                    addEdge(v, helper.get(eprev));
                }
                helper.remove(eprev);
                SList.remove(eprev);
            } else if (v.type == vType.split) {
                Edge dummyEdge = new Edge(v, v);
                Edge prev = SList.floor(dummyEdge);
                addEdge(v, helper.get(prev));
                helper.put(prev, v);
                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else if (v.type == vType.merge) {
                Edge eprev = v.incidentEdge.pEdge;
                if (helper.get(eprev).type == vType.merge) {
                    addEdge(v, helper.get(eprev));
                }
                helper.remove(eprev);
                SList.remove(eprev);

                Edge dummyEdge = new Edge(v, v);
                Edge prev = SList.floor(dummyEdge);
                if (helper.get(prev).type == vType.merge) {
                    addEdge(v, helper.get(prev));
                }
                helper.put(prev, v);
            } else if (v.type == vType.regL) {
                Edge eprev = v.incidentEdge.pEdge;
                if (helper.get(eprev).type == vType.merge) {
                    addEdge(v, helper.get(eprev));
                }
                helper.remove(eprev);
                SList.remove(eprev);

                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else {
                Edge dummyEdge = new Edge(v, v);
                Edge prev = SList.floor(dummyEdge);
                if (helper.get(prev).type == vType.merge) {
                    addEdge(v, helper.get(prev));
                }
                helper.put(prev, v);
            }

        }
        HashSet<Vertex> isDiag = new HashSet<>();
        for (Edge diag : diagonals) {
            Face nFace = new Face(diag);
            nFace.edge = diag;
            dcel.faces.add(nFace);
            Vertex origin = diag.origin;

            if (!isDiag.contains(diag.origin)) {
            } else if (!isDiag.contains(diag.dest)) {
                Vertex temp = diag.origin;
                diag.origin = diag.dest;
                diag.dest = temp;
            } else {
                System.out.println("NOT POSSIBLE");
            }

            isDiag.add(diag.origin);
            isDiag.add(diag.dest);
            Edge init = diag.origin.incidentEdge.pEdge;
            Face oFace = init.lFace;
            while (init.dest != diag.dest && init.origin != diag.dest) {
                if (oFace == init.lFace) {
                    init.lFace = nFace;
                    init = init.pEdge;
                } else {
                    init.rFace = nFace;
                    init = init.nEdge;
                }
            }
            if (oFace == init.lFace) {
                Edge temp = init.pEdge;
                init.lFace = nFace;
                init.pEdge = diag;
                init = temp;
            } else {
                Edge temp = init.nEdge;
                init.rFace = nFace;
                init.nEdge = diag;
                init = temp;
            }
            diag.lFace = nFace;
            diag.rFace = oFace;
            diag.pEdge = diag.origin.incidentEdge.pEdge;
            diag.nEdge = init;
            diag.origin.incidentEdge.pEdge = diag;
            oFace.edge = init;
        }
    }

    public void printAllFaces() {
        System.out.println("Total Faces: " + dcel.faces.size());
        for(Face face : dcel.faces) {
            Edge edge = face.edge;
            System.out.println("\n Face from " + edge + ":\n");
            if(face == edge.lFace) {
                System.out.println(edge.origin + ", ");
                edge = edge.pEdge;
            }
            else {
                System.out.println(edge.dest + ", ");
                edge = edge.nEdge;
            }
            while(edge != face.edge) {
                if(face == edge.lFace) {
                    System.out.println(edge.origin + ", ");
                    edge = edge.pEdge;
                }
                else {
                    System.out.println(edge.dest + ", ");
                    edge = edge.nEdge;
                }
            }
        }
    }

    public ArrayList<DCEL> getSeparateDCEL() {
        ArrayList<DCEL> dcelArrayList = new ArrayList<>();
        for(Face face : dcel.faces) {
            ArrayList<Point> pointArrayList = new ArrayList<>();
            Edge edge = face.edge;
            if(face == edge.lFace) {
                pointArrayList.add(edge.origin.toPoint());
                edge = edge.pEdge;
            }
            else {
                pointArrayList.add(edge.dest.toPoint());
                edge = edge.nEdge;
            }
            while(edge != face.edge) {
                if(face == edge.lFace) {
                    pointArrayList.add(edge.origin.toPoint());
                    edge = edge.pEdge;
                }
                else {
                    pointArrayList.add(edge.dest.toPoint());
                    edge = edge.nEdge;
                }
            }
            if (pointArrayList.size() == dcel.vertices.size()) {
                continue;
            }
            DCEL dcel = new DCEL();
            PolygonSolution polygonSolution = new PolygonSolution();
            polygonSolution.dcel = dcel;
            polygonSolution.pointArrayList = pointArrayList;
            polygonSolution.generatePolygon();
            dcelArrayList.add(dcel);
        }
        return dcelArrayList;
    }

    private void addEdge(Vertex v1, Vertex v2) {
        partitionLines.add(new Segment(v1.toPoint(), v2.toPoint()));
        diagonals.add(new Edge(v1, v2));
    }
}
