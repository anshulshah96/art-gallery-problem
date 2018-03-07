package cgcp2.ds;

import java.awt.Point;
import java.util.*;

public class MonotonePartition {
    public DCEL dcel;
    public ArrayList<Segment> partitionLines;

    public MonotonePartition() {
        partitionLines = new ArrayList<>();
    }

    public void generate() {
        partitionLines.clear();

        TreeSet<Edge> SList = new TreeSet<Edge>();
        TreeMap<Edge, Vertex> helper = new TreeMap<Edge, Vertex>();
        for (Vertex v : dcel.vertices) {
            if (v.type == vType.start) {
                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else if (v.type == vType.end) {
                Edge eprev = v.incidentEdge.pEdge;
                if(helper.get(eprev).type == vType.merge) {
                    partitionLines.add(new Segment(v.toPoint(), helper.get(eprev).toPoint()));
                }
                helper.remove(eprev);
                SList.remove(eprev);
            } else if (v.type == vType.split) {
                Edge dummyEdge = new Edge(v,v);
                Edge prev = SList.floor(dummyEdge);
                partitionLines.add(new Segment(v.toPoint(), helper.get(prev).toPoint()));
                helper.put(prev, v);
                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else if (v.type == vType.merge) {
                Edge eprev = v.incidentEdge.pEdge;
                if(helper.get(eprev).type == vType.merge) {
                    partitionLines.add(new Segment(v.toPoint(), helper.get(eprev).toPoint()));
                }
                helper.remove(eprev);
                SList.remove(eprev);

                Edge dummyEdge = new Edge(v,v);
                Edge prev = SList.floor(dummyEdge);
                if(helper.get(prev).type == vType.merge) {
                    partitionLines.add(new Segment(v.toPoint(), helper.get(prev).toPoint()));
                }
                helper.put(prev, v);
            } else if (v.type == vType.regL) {
                Edge eprev = v.incidentEdge.pEdge;
                if(helper.get(eprev).type == vType.merge) {
                    partitionLines.add(new Segment(v.toPoint(), helper.get(eprev).toPoint()));
                }
                helper.remove(eprev);
                SList.remove(eprev);

                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else {
                Edge dummyEdge = new Edge(v,v);
                Edge prev = SList.floor(dummyEdge);
                if(helper.get(prev).type == vType.merge) {
                    partitionLines.add(new Segment(v.toPoint(), helper.get(prev).toPoint()));
                }
                helper.put(prev, v);
            }
        }
    }
}
