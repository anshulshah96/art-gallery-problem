// GroupID-11 (14114013_14114065) - Anshul Shah & Suraj Gupta
// Date: March 7, 2018
// DualitySolution.java - Algorithm for Generating a Dual Graph from a Polygon

package cgcp2.ds;

import java.awt.Point;
import java.util.*;


public class DualitySolution {
    public ArrayList<DCEL> dcelArrayList;
    public ArrayList<Edge> partitionDiagonals;
    public ArrayList<Segment> dualEdges;
    public HashMap<Face, ArrayList<Face>> adjList;
    public ArrayList<Vertex> dualVertices;
    public TreeMap<Face, Integer> adjCount;
    public Face of;
    public Face start;
    public HashMap<Point, Integer> colour;
    public int[] ct = new int[4];
    public int minc;

    public void generate() {
        dualEdges = new ArrayList<>();
        adjList = new HashMap<>();
        dualVertices = new ArrayList<>();
        adjCount = new TreeMap<>();

        for (DCEL dcel : dcelArrayList) {
            for (Face face : dcel.faces) {
                if (face.getNumVertices() == dcel.vertices.size() && face.edge.rFace == face) {
                    of = face;
                    continue;
                }
                dualVertices.add(face.getMidPoint());
                adjList.put(face, new ArrayList<>());
                start = face;
                adjCount.put(face, 0);
            }

            for (Edge edge : dcel.edges) {
                if (edge.rFace != of) {
                    Vertex v1 = edge.rFace.getMidPoint();
                    Vertex v2 = edge.lFace.getMidPoint();
                    dualEdges.add(new Segment(v1.toPoint(), v2.toPoint()));

                    adjList.get(edge.rFace).add(edge.lFace);
                    adjList.get(edge.lFace).add(edge.rFace);
                }
            }
        }

        Face face1 = null, face2 = null;
        for (Edge diag : partitionDiagonals) {
            face1 = null;
            face2 = null;
            for (DCEL dcel : dcelArrayList) {
                if (dcel.isEdge(diag) && face1 == null) {
                    face1 = dcel.isEdgeReturn(diag).lFace;
                } else if (dcel.isEdge(diag) && face2 == null) {
                    face2 = dcel.isEdgeReturn(diag).lFace;
                } else if (dcel.isEdge(diag)) {
                    System.out.println("Not Possible");
                }
            }

            Vertex v1 = face1.getMidPoint();
            Vertex v2 = face2.getMidPoint();
            dualEdges.add(new Segment(v1.toPoint(), v2.toPoint()));

            adjList.get(face1).add(face2);
            adjList.get(face2).add(face1);
        }
    }

    public void solve() {
        int temp;

        colour = new HashMap<>();

        for (DCEL dcel : dcelArrayList) {
            for (Vertex ver : dcel.vertices) {
                Point v = ver.toPoint();
                colour.put(v, 0);
            }
        }

        ct = new int[4];
        ArrayList<Vertex> vList = start.getVertices();
        int i = 1;
        for (Vertex ver : vList) {
            Point v = ver.toPoint();
            colour.put(v, i);
            ct[i]++;
            i++;
        }
        dfs(start, null);

        int min = Integer.MAX_VALUE;
        for (int j = 1; j <= 3; j++) {
            if (ct[j] < min) {
                min = ct[j];
                minc = j;
            }
        }
    }

    private void dfs(Face face, Face par) {
        Point rem = null;
        Boolean b1, b2, b3;
        for (Face ftemp : adjList.get(face)) {
            if (ftemp != par) {
                ArrayList<Vertex> vList = ftemp.getVertices();
                b1 = false;
                b2 = false;
                b3 = false;
                for (Vertex ver : vList) {
                    Point v = ver.toPoint();
                    if (colour.get(v) == 0)
                        rem = v;
                    else if (colour.get(v) == 1)
                        b1 = true;
                    else if (colour.get(v) == 2)
                        b2 = true;
                    else
                        b3 = true;
                }
                if (!b1) {
                    colour.put(rem, 1);
                    ct[1]++;
                }
                if (!b2) {
                    colour.put(rem, 2);
                    ct[2]++;
                }
                if (!b3) {
                    colour.put(rem, 3);
                    ct[3]++;
                }

                dfs(ftemp, face);
            }
        }
    }
}
