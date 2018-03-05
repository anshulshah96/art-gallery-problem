package cgcp2.ds;

import java.awt.Point;
import java.util.ArrayList;

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
    }
}
