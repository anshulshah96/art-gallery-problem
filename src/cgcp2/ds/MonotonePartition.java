package cgcp2.ds;

import java.awt.Point;
import java.util.*;

public class MonotonePartition {
    public DCEL dcel;
    public ArrayList<Segment> partitionLines;
    public ArrayList<Edge> partitionDiagonals;

    public MonotonePartition() {
        partitionLines = new ArrayList<>();
        partitionDiagonals = new ArrayList<>();
    }

    public void generate() {
        partitionLines.clear();
        partitionDiagonals.clear();

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
        dcel.addEdges(partitionDiagonals);
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
        int excount = 0;
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
            if (pointArrayList.size() == dcel.vertices.size() && excount == 0) {
                excount++;
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
        partitionDiagonals.add(new Edge(v1, v2));
    }

    public DCEL mergePartitions(ArrayList<Edge> partitionDiagonals, ArrayList<DCEL> dcelArrayList) {
        DCEL merged = new DCEL();
        for(Edge diag : partitionDiagonals) {
            DCEL dcel1 = null, dcel2 = null;
            for(DCEL dcel : dcelArrayList) {
                if(dcel.edges.contains(diag) && dcel1 == null) {
                    dcel1 = dcel;
                }
                else if(dcel.edges.contains(diag) && dcel2 == null) {
                    dcel2 = dcel;
                }
                else if(dcel.edges.contains(diag)) {
                    System.out.println("Not Possible");
                }
            }

            merge(diag, dcel1, dcel2);
            merged = dcel1;
        }
        return merged;
    }

    public void merge(Edge diag, DCEL dcel1, DCEL dcel2) {

    }
}
