package cgcp2.ds;

import java.util.ArrayList;
import javax.swing.JPanel;


import java.awt.*;
import java.awt.Point;
import java.util.HashMap;

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
    private HashMap<Point, Integer> colour;
    Boolean finale;
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
        clear();

        int ll = 25;
        int ul = 275;

        for (int i = 0; i < n; i++) {
            int x = ll + (int) (Math.random() * (ul - ll));
            int y = ll + (int) (Math.random() * (ul - ll));
            y = -y;
            pointList.add(new Point(2*x, 2*y));
        }
        System.out.println(pointList);

//        DCEL not partitioned
//        [java.awt.Point[x=160,y=-89], java.awt.Point[x=98,y=-182], java.awt.Point[x=298,y=-340], java.awt.Point[x=120,y=-291], java.awt.Point[x=130,y=-272], java.awt.Point[x=148,y=-163]]

//        Non Simple Polygon
//       [java.awt.Point[x=165,y=-324], java.awt.Point[x=74,y=-236], java.awt.Point[x=119,y=-344], java.awt.Point[x=142,y=-236], java.awt.Point[x=116,y=-330], java.awt.Point[x=240,y=-327]]
//          Merging DCEL
//        [java.awt.Point[x=102,y=-160], java.awt.Point[x=55,y=-124], java.awt.Point[x=273,y=-286], java.awt.Point[x=228,y=-321], java.awt.Point[x=73,y=-176], java.awt.Point[x=193,y=-182]]
//        Triangulation issues
//        [java.awt.Point[x=63,y=-247], java.awt.Point[x=165,y=-274], java.awt.Point[x=100,y=-197], java.awt.Point[x=132,y=-212], java.awt.Point[x=187,y=-245], java.awt.Point[x=312,y=-283], java.awt.Point[x=344,y=-98], java.awt.Point[x=237,y=-178], java.awt.Point[x=107,y=-137], java.awt.Point[x=243,y=-259]]
//
//        pointList.add(new Point(165, -324));
//        pointList.add(new Point(74, -236));
//        pointList.add(new Point(119, -344));
//        pointList.add(new Point(142, -236));
//        pointList.add(new Point(116, -330));
//        pointList.add(new Point(240, -327));

//        pointList.add(new Point(344, -98));
//        pointList.add(new Point(237, -178));
//        pointList.add(new Point(107, -137));
//        pointList.add(new Point(243, -259));


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
//        mergedDCEL = monotonePartition.mergePartitions(partitionDiagonals, dcelArrayList);
        repaint();
    }

    public void dualGraph() {
//        dualitySolution.dcel = mergedDCEL;
        dualitySolution.dcelArrayList = dcelArrayList;
        dualitySolution.partitionDiagonals = partitionDiagonals;
        dualitySolution.generate();
        repaint();
    }

    public void threecolor() {
        trigLines.clear();
        partitionLines.clear();
        dualitySolution.solve();
        colour = dualitySolution.colour;
        finale = true;
        dualitySolution.dualEdges.clear();
        dualitySolution.dualVertices.clear();
        repaint();
    }
    //------------------------------------------------------------
    //  Draws all of the points stored in the list.
    //-----------------------------------------------------------
    public void paintComponent(Graphics page) {
        super.paintComponent(page);

        //showing the spot
        page.setColor(Color.green);
        for (Point spot : pointList)
        {
            if (finale)
            {
                if(colour.get(spot) == 1)
                    page.setColor(Color.RED);
                else if(colour.get(spot) == 2)
                    page.setColor(Color.BLUE);
                else
                    page.setColor(Color.GREEN);
                if (colour.get(spot) == dualitySolution.minc) {
                    page.fillOval(spot.x - 8, -1 * spot.y - 8, 17, 17);
                }
                else {
                    page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
                }
            }
            else
            page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
        }

        //rendering the convex hull demonstration
        page.setColor(Color.orange);
        if (circularLineList != null && circularLineList.size() > 1) {
            for (int ii = 0; ii < circularLineList.size() - 1; ii++) {
                Point p = circularLineList.get(ii);
                Point q = circularLineList.get(ii+1);
                page.drawLine(circularLineList.get(ii).x, -1 * circularLineList.get(ii).y, circularLineList.get(ii + 1).x, -1 * circularLineList.get(ii + 1).y);
            }
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
            for (Vertex spot : dualitySolution.dualVertices) {
                page.fillOval(spot.toPoint().x - 3, -1 * spot.toPoint().y - 3, 7, 7);
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
        finale = false;
        repaint();
    }
}

