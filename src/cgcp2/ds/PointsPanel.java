package cgcp2.ds;

import java.util.ArrayList;
import javax.swing.JPanel;


import java.awt.*;
import java.awt.Point;

public class PointsPanel extends JPanel {
    private static DCEL dcel;
    private ArrayList<DCEL> dcelArrayList;
    private ArrayList<Point> pointList;
    private ArrayList<Point> circularLineList;
    private ArrayList<Segment> trapLines;
    private ArrayList<Segment> partitionLines;
    private ArrayList<Edge> partitionDiagonals;
    private ArrayList<Segment> trigLines;
    private DCEL mergedDCEL;

    private PolygonSolution polygonSolution;
    private TrapezoidSolution trapezoidSolution;
    private MonotonePartition monotonePartition;
    private DualitySolution dualitySolution;


    //-----------------------------------------------------------
    //  Constructor:
    //  Sets up this panel to listen for mouse events.
    //-----------------------------------------------------------
    public PointsPanel() {
        dcel = new DCEL();
        polygonSolution = new PolygonSolution();
        trapezoidSolution = new TrapezoidSolution();
        monotonePartition = new MonotonePartition();
        dualitySolution = new DualitySolution();

        //contains all points clicked
        pointList = new ArrayList<Point>();

        //contains all convex hulls waypoints
        circularLineList = new ArrayList<Point>();

        //contains all closest segments
        trapLines = new ArrayList<Segment>();

        partitionLines = new ArrayList<Segment>();
        partitionDiagonals = new ArrayList<>();

        trigLines = new ArrayList<>();

        //setting the background black
        setBackground(Color.black);

        //setting the canvas size
        setPreferredSize(new Dimension(800, 600));
    }

    public void initPoints(int n) {
        pointList.clear();

//        int ll = 50;
//        int ul = 350;
//
//        for (int i = 0; i < n; i++) {
//            int x = ll + (int) (Math.random() * (ul - ll));
//            int y = ll + (int) (Math.random() * (ul - ll));
//            y = -y;
//            pointList.add(new Point(x, y));
//        }
//        System.out.println(pointList);

//        DCEL not partitioned
//        [java.awt.Point[x=160,y=-89], java.awt.Point[x=98,y=-182], java.awt.Point[x=298,y=-340], java.awt.Point[x=120,y=-291], java.awt.Point[x=130,y=-272], java.awt.Point[x=148,y=-163]]

//        Non Simple Polygon
//       [java.awt.Point[x=165,y=-324], java.awt.Point[x=74,y=-236], java.awt.Point[x=119,y=-344], java.awt.Point[x=142,y=-236], java.awt.Point[x=116,y=-330], java.awt.Point[x=240,y=-327]]


        pointList.add(new Point(269, -277));
        pointList.add(new Point(252, -253));
        pointList.add(new Point(315, -266));
        pointList.add(new Point(313, -204));
        pointList.add(new Point(296, -115));
        pointList.add(new Point(224, -267));

//        pointList.add(new Point(200, -50));
//        pointList.add(new Point(300, -125));
//        pointList.add(new Point(100, -175));
//        pointList.add(new Point(200, -250));


        repaint();
    }

    public void generatePolygon() {
        dcel = new DCEL();
        trapLines.clear();
        partitionLines.clear();
        trigLines.clear();
        polygonSolution.pointArrayList = pointList;
        polygonSolution.dcel = dcel;
        polygonSolution.generatePolygon();
        circularLineList = polygonSolution.linePointList;

        repaint();
    }

    public void trapezoidalization() {
        trapLines.clear();
        trapezoidSolution.dcel = dcel;
        partitionLines.clear();
        trapezoidSolution.closestLines = trapLines;
        trapezoidSolution.generateTrap();
        trapLines = trapezoidSolution.closestLines;

        repaint();
    }

    public void monotonePartition() {
        monotonePartition.dcel = dcel;
        trapLines.clear();
        partitionLines.clear();
        partitionDiagonals.clear();
        monotonePartition.partitionLines = partitionLines;
        monotonePartition.partitionDiagonals = partitionDiagonals;
        monotonePartition.generate();

        dcelArrayList = monotonePartition.getSeparateDCEL();

        repaint();
    }

    public void triangulatePartitions() {
        trigLines.clear();
        trapLines.clear();
        for (DCEL dcel : dcelArrayList) {
            Triangulation trisol = new Triangulation(dcel, trigLines);
            trisol.generate();
        }
        mergedDCEL = monotonePartition.mergePartitions(partitionDiagonals, dcelArrayList);
        repaint();
    }

    public void dualGraph() {
        dualitySolution.dcel = mergedDCEL;
        dualitySolution.generate();

        repaint();
    }

    //------------------------------------------------------------
    //  Draws all of the points stored in the list.
    //-----------------------------------------------------------
    public void paintComponent(Graphics page) {
        super.paintComponent(page);

        //showing the spot
        page.setColor(Color.green);
        for (Point spot : pointList) page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);

        //rendering the convex hull demonstration
        page.setColor(Color.orange);
        if (circularLineList != null && circularLineList.size() > 1) {
            for (int ii = 0; ii < circularLineList.size() - 1; ii++)
                page.drawLine(circularLineList.get(ii).x, -1 * circularLineList.get(ii).y, circularLineList.get(ii + 1).x, -1 * circularLineList.get(ii + 1).y);
            page.drawLine(circularLineList.get(circularLineList.size() - 1).x, -1 * circularLineList.get(circularLineList.size() - 1).y, circularLineList.get(0).x, -1 * circularLineList.get(0).y);
        }
        //rendering the closest segments
        page.setColor(Color.white);
        if (trapLines != null && trapLines.size() > 0) {
            for (int ii = 0; ii < trapLines.size(); ii++)
                page.drawLine(trapLines.get(ii).a.x, -1 * trapLines.get(ii).a.y, trapLines.get(ii).b.x, -1 * trapLines.get(ii).b.y);
        }

        //rendering the partition segments
        page.setColor(Color.GREEN);
        if (partitionLines != null && partitionLines.size() > 0) {
            for (int ii = 0; ii < partitionLines.size(); ii++)
                page.drawLine(partitionLines.get(ii).a.x, -1 * partitionLines.get(ii).a.y, partitionLines.get(ii).b.x, -1 * partitionLines.get(ii).b.y);
        }

        //rendering the trig segments
        page.setColor(Color.GREEN);
        if (trigLines != null && trigLines.size() > 0) {
            for (int ii = 0; ii < trigLines.size(); ii++)
                page.drawLine(trigLines.get(ii).a.x, -1 * trigLines.get(ii).a.y, trigLines.get(ii).b.x, -1 * trigLines.get(ii).b.y);
        }

        if (dualitySolution.dualEdges != null && dualitySolution.dualEdges.size() > 0) {
            page.setColor(Color.YELLOW);
            if (dualitySolution.dualEdges != null && dualitySolution.dualEdges.size() > 0) {
                for (int ii = 0; ii < dualitySolution.dualEdges.size(); ii++)
                    page.drawLine(dualitySolution.dualEdges.get(ii).a.x, -1 * dualitySolution.dualEdges.get(ii).a.y,
                            dualitySolution.dualEdges.get(ii).b.x, -1 * dualitySolution.dualEdges.get(ii).b.y);
            }
            page.setColor(Color.RED);
            for (Point spot : dualitySolution.dualVertices) {
                page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
            }
        }

        //display the points count
        page.drawString("Count: " + pointList.size(), 5, 20);
    }

    //this method clears the canvas
    public void clear() {
        //clear all list of points and waypoints
        pointList.clear();
        circularLineList.clear();
        trapLines.clear();
        partitionLines.clear();
        trigLines.clear();
        dualitySolution = new DualitySolution();
        //clear the canvas
        repaint();
    }
}

