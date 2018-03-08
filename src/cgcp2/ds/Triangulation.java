package cgcp2.ds;

import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;

public class Triangulation {

    public DCEL dcel;
    public ArrayList<Segment> trigLines;
    public ArrayList<Edge> edgeArrayList;

    enum side {
        left, right;
    }

    public Triangulation(DCEL dcel, ArrayList<Segment> trigLines) {
        this.dcel = dcel;
        this.trigLines = trigLines;
    }

    public void generate() {
        edgeArrayList = new ArrayList<>();
        TreeMap<Vertex, side> vSide = new TreeMap<>();
        Vertex topmost = dcel.vertices.first();
        Vertex bottom = dcel.vertices.last();
        Vertex current = topmost;
        while(current!= bottom) {
            vSide.put(current, side.left);
            current = current.incidentEdge.dest;
        }
        while(current != topmost) {
            vSide.put(current, side.right);
            current = current.incidentEdge.dest;
        }
        Stack<Vertex> vst = new Stack<>();
        int i = 0;
        for(Vertex vertex : dcel.vertices) {
            i++;
            if(i <= 2) {
                vst.push(vertex);
                continue;
            }
            if(vSide.get(vertex) == vSide.get(vst.peek())) {
                while(!isReflex(vertex, vst, vSide))
                {
                    vst.pop();
                    if(!dcel.isEdge(vertex, vst.peek())) {
                        trigLines.add(new Segment(vertex.toPoint(), vst.peek().toPoint()));
                        edgeArrayList.add(new Edge(vertex, vst.peek()));
                    }
                }
                vst.push(vertex);
            }
            else {
                Vertex temp = vst.peek();
                while(!vst.isEmpty())
                {
                    if(!dcel.isEdge(vertex, vst.peek())) {
                        trigLines.add(new Segment(vertex.toPoint(),vst.peek().toPoint()));
                        edgeArrayList.add(new Edge(vertex, vst.peek()));
                    }
                    vst.pop();
                }
                vst.push(temp);
                vst.push(vertex);
            }
        }

        dcel.addEdges(edgeArrayList);
    }

    private boolean isReflex(Vertex vertex, Stack<Vertex> vst, TreeMap<Vertex, side> vSide) {
        if(vst.size() < 2)
            return true;
        Point v3 = vertex.toPoint();
        Vertex vp2 = vst.peek();
        Point v2 = vp2.toPoint();
        vst.pop();
        Point v1 = vst.peek().toPoint();
        vst.push(vp2);

        double ans = (v1.x*v2.y + v2.x*v3.y + v3.x*v1.y) - (v1.y*v2.x + v2.y*v3.x + v3.y*v1.x);
        if(vSide.get(vp2) == side.right) {
            return ans > 0;
        }
        else
            return ans < 0;
    }

}
