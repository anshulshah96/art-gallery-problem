package cgcp2.ds;

import java.awt.Point;
import java.util.*;


public class DualitySolution {
    public DCEL dcel;
    public ArrayList<Segment> dualEdges;
    public HashMap<Face, ArrayList<Face>> adjList;
    public ArrayList<Vertex> dualVertices;
    public HashMap<Vertex, Integer> vCount;
    public TreeMap<Face, Integer> adjCount;
    public Face of;
    public void generate() {
        dualEdges = new ArrayList<>();
        adjList = new HashMap<>();
        dualVertices = new ArrayList<>();
        adjCount = new TreeMap<>();

        for(Face face : dcel.faces) {
            if(face.getNumVertices() == dcel.vertices.size()) {
                of = face;
                continue;
            }
            dualVertices.add(face.getMidPoint());
            adjList.put(face, new ArrayList<>());
            adjCount.put(face, 0);
        }

        for(Edge edge : dcel.edges) {
            if (edge.rFace != of) {
                Vertex v1 = edge.rFace.getMidPoint();
                Vertex v2 = edge.lFace.getMidPoint();
                dualEdges.add(new Segment(v1.toPoint(), v2.toPoint()));

                adjList.get(edge.rFace).add(edge.lFace);
                adjList.get(edge.lFace).add(edge.rFace);

                adjCount.put(edge.rFace, adjCount.get(edge.rFace)+1);
                adjCount.put(edge.lFace, adjCount.get(edge.lFace)+1);
            }
        }
    }

    public void solve() {
        int temp;
        vCount = new HashMap<>();
        for(Vertex v: dcel.vertices) {
            vCount.put(v,0);
        }
        for(Edge edge: dcel.edges) {
            temp = vCount.get(edge.dest);
            vCount.put(edge.dest,temp+1);

            temp = vCount.get(edge.origin);
            vCount.put(edge.origin,temp+1);
        }

        Stack<Face> stFace = new Stack<>();
        while(!adjCount.isEmpty())
        {
            Iterator<Map.Entry<Face, Integer>> it = adjCount.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<Face, Integer> pair = it.next();
                stFace.push(pair.getKey());


            }
        }
    }
}
