package cgcp2.ds;

import java.util.ArrayList;
import javax.swing.JPanel;


import java.awt.*;
import java.text.*;
import java.awt.event.*;
import java.util.Collections;
import java.awt.Point;

public class PointsPanel extends JPanel {
    private static DCEL dcel;
    private ArrayList<DCEL> dcelArrayList;
    private ArrayList<Point> pointList;
    private PolygonSolution polygonSolution;
    private TrapezoidSolution trapezoidSolution;
    private ArrayList<Point> circularLineList;
    private ArrayList<Segment> closestLines;
    private MonotonePartition monotonePartition;
    private ArrayList<Segment> partitionLines;
    private ArrayList<Segment> trigLines;

    //-----------------------------------------------------------
    //  Constructor:
    //  Sets up this panel to listen for mouse events.
    //-----------------------------------------------------------
    public PointsPanel() {
        dcel = new DCEL();
        polygonSolution = new PolygonSolution();
        trapezoidSolution = new TrapezoidSolution();
        monotonePartition = new MonotonePartition();
        //contains all points clicked
        pointList = new ArrayList<Point>();

        //contains all convex hulls waypoints
        circularLineList = new ArrayList<Point>();

        //contains all closest segments
        closestLines = new ArrayList<Segment>();

        partitionLines = new ArrayList<Segment>();

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

        pointList.add(new Point(200, -50));
        pointList.add(new Point(300, -125));
        pointList.add(new Point(100, -175));
        pointList.add(new Point(200, -250));


        repaint();
    }

    public void generatePolygon() {
        dcel = new DCEL();
        closestLines.clear();
        partitionLines.clear();
        trigLines.clear();
        polygonSolution.pointArrayList = pointList;
        polygonSolution.dcel = dcel;
        polygonSolution.generatePolygon();
        circularLineList = polygonSolution.linePointList;

        repaint();
    }

    public void trapezoidalization() {
        trapezoidSolution.dcel = dcel;
        partitionLines.clear();
        trapezoidSolution.closestLines = closestLines;
        trapezoidSolution.generateTrap();
        closestLines = trapezoidSolution.closestLines;

        repaint();
    }

    public void monotonePartition() {
        monotonePartition.dcel = dcel;
        partitionLines.clear();
        monotonePartition.partitionLines = partitionLines;
        monotonePartition.generate();
        partitionLines = monotonePartition.partitionLines;
//        monotonePartition.printAllFaces();
        dcelArrayList = monotonePartition.getSeparateDCEL();

        repaint();
    }

    public void triangulatePartitions() {
        trigLines.clear();
        for (DCEL dcel : dcelArrayList) {
            Triangulation trisol = new Triangulation(dcel, trigLines);
            trisol.generate();
        }
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
        if (closestLines != null && closestLines.size() > 0) {
            for (int ii = 0; ii < closestLines.size(); ii++)
                page.drawLine(closestLines.get(ii).a.x, -1 * closestLines.get(ii).a.y, closestLines.get(ii).b.x, -1 * closestLines.get(ii).b.y);
        }

        //rendering the partition segments
        page.setColor(Color.GREEN);
        if (partitionLines != null && partitionLines.size() > 0) {
            for (int ii = 0; ii < partitionLines.size(); ii++)
                page.drawLine(partitionLines.get(ii).a.x, -1 * partitionLines.get(ii).a.y, partitionLines.get(ii).b.x, -1 * partitionLines.get(ii).b.y);
        }

        //rendering the partition segments
        page.setColor(Color.RED);
        if (trigLines != null && trigLines.size() > 0) {
            for (int ii = 0; ii < trigLines.size(); ii++)
                page.drawLine(trigLines.get(ii).a.x, -1 * trigLines.get(ii).a.y, trigLines.get(ii).b.x, -1 * trigLines.get(ii).b.y);
        }

        //display the points count
        page.drawString("Count: " + pointList.size(), 5, 20);
    }

    //this method clears the canvas
    public void clear() {
        //clear all list of points and waypoints
        pointList.clear();
        circularLineList.clear();
        closestLines.clear();
        partitionLines.clear();
        trigLines.clear();
        //clear the canvas
        repaint();
    }
}

