package cgcp2.ds;

import java.util.*;

public class Triangulation {

    public DCEL dcel;
    public ArrayList<Segment> trigLines;

    enum side {
        left, right;
    }

    public Triangulation(DCEL dcel, ArrayList<Segment> trigLines) {
        this.dcel = dcel;
        this.trigLines = trigLines;
    }

    public void generate() {
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
                while(vertex.isReflex())
                {
                    vst.pop();
                    if(!dcel.edges.contains(new Edge(vertex, vst.peek()))) {
                        trigLines.add(new Segment(vertex.toPoint(), vst.peek().toPoint()));
                    }
                }
                vst.push(vertex);
            }
            else {
                Vertex temp = vst.peek();
                while(!vst.isEmpty())
                {
                    if(!dcel.edges.contains(new Edge(vertex, vst.peek()))) {
                        trigLines.add(new Segment(vertex.toPoint(),vst.peek().toPoint()));
                    }
                    vst.pop();
                }
                vst.push(temp);
                vst.push(vertex);
            }
        }
    }
}
