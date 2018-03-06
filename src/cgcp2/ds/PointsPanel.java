package cgcp2.ds;

import java.util.ArrayList;
import javax.swing.JPanel;


import java.awt.*;
import java.text.*;
import java.awt.event.*;
import java.util.Collections;
import java.awt.Point;

public class PointsPanel extends JPanel {
    private DCEL dcel;
    private ArrayList<Point> pointList;
    private PolygonSolution polygonSolution;
    private ArrayList<Point> circularLineList;
    private ArrayList<Segment> closestLines;
    private ConvexHullSolution solusi1;
    private ClosestPairSolution solusi2;
    private VoronoiSolution solusi3;
    private boolean showConvex;
    private boolean showClosest;
    private boolean showVoronoi;
    private boolean showArea;

    //-----------------------------------------------------------
    //  Constructor:
    //  Sets up this panel to listen for mouse events.
    //-----------------------------------------------------------
    public PointsPanel() {
        dcel = new DCEL();
        polygonSolution = new PolygonSolution();
        solusi1 = new ConvexHullSolution();
        solusi2 = new ClosestPairSolution();
        //contains all points clicked
        pointList = new ArrayList<Point>();

        //contains all convex hulls waypoints
        circularLineList = new ArrayList<Point>();

        //contains all closest segments
        closestLines = new ArrayList<Segment>();

        //adding new mouse listener
        addMouseListener(new PointsListener());

        //setting the background black
        setBackground(Color.black);

        //setting the canvas size
        setPreferredSize(new Dimension(400, 400));
    }

    public void initPoints(int n) {
        pointList.clear();

        int ll = 50;
        int ul = 350;

        for (int i = 0; i < n; i++) {
            int x = ll + (int) (Math.random() * (ul - ll));
            int y = ll + (int) (Math.random() * (ul - ll));
            y = -y;
            pointList.add(new Point(x, y));
        }

        repaint();
    }

    public void generatePolygon() {
        dcel = new DCEL();
        polygonSolution.pointArrayList = pointList;
        polygonSolution.dcel = dcel;
        polygonSolution.generatePolygon();
        circularLineList = polygonSolution.linePointList;

        repaint();
    }

    public void trapezoidalization() {

    }

    //------------------------------------------------------------
    //  Draws all of the points stored in the list.
    //-----------------------------------------------------------
    public void paintComponent(Graphics page) {
        super.paintComponent(page);

        //showing the spot
        page.setColor(Color.green);
        for (Point spot : pointList) page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);

        //rendering the convex hull demostration
        page.setColor(Color.orange);
        if (circularLineList != null && circularLineList.size() > 1) {
            for (int ii = 0; ii < circularLineList.size() - 1; ii++)
                page.drawLine(circularLineList.get(ii).x, -1 * circularLineList.get(ii).y, circularLineList.get(ii + 1).x, -1 * circularLineList.get(ii + 1).y);
            page.drawLine(circularLineList.get(circularLineList.size() - 1).x, -1 * circularLineList.get(circularLineList.size() - 1).y, circularLineList.get(0).x, -1 * circularLineList.get(0).y);
        }
        //rendering the closest segments
        page.setColor(Color.white);
        if (closestLines != null && closestLines.size() > 0 && showClosest) {
            for (int ii = 0; ii < closestLines.size(); ii++)
                page.drawLine(closestLines.get(ii).a.x, -1 * closestLines.get(ii).a.y, closestLines.get(ii).b.x, -1 * closestLines.get(ii).b.y);
            DecimalFormat df = new DecimalFormat("#.##");
            page.drawString("Shortest Segment: " + df.format(closestLines.get(0).l), 5, 45);
        }

        //display the area
        page.setColor(Color.white);
        if (showArea) page.drawString("Area: " + polygonArea(), 5, 30);

        //display the points count
        page.drawString("Count: " + pointList.size(), 5, 20);

        page.setColor(Color.red);
        if (showVoronoi && pointList.size() > 1) {
            solusi3 = new VoronoiSolution(pointList, 0, -getHeight(), 0, getWidth());
            for (Face f : solusi3.solution.faces.values()) {
                Edge e = f.getOuter();
                Edge start = e;
                while (true) {
                    if (e.getTwin().getOrigin() != null)
                        page.drawLine((int) e.getOrigin().getCoordinate().getX(), (int) -e.getOrigin().getCoordinate().getY(), (int) e.getTwin().getOrigin().getCoordinate().getX(), (int) -e.getTwin().getOrigin().getCoordinate().getY());
                    else continue;
                    if (e.getNext() != null && e.getNext() != start) e = e.getNext();
                    else break;
                }
                e = start;
                while (true) {
                    if (e.getTwin().getOrigin() != null)
                        page.drawLine((int) e.getOrigin().getCoordinate().getX(), (int) -e.getOrigin().getCoordinate().getY(), (int) e.getTwin().getOrigin().getCoordinate().getX(), (int) -e.getTwin().getOrigin().getCoordinate().getY());
                    else continue;
                    if (e.getPrev() != null && e.getPrev() != start) e = e.getPrev();
                    else break;
                }
            }
        }
//
//    if(vor.solution.vertices.size()>0)
//    {
//    	for (Vertex vertex : vor.solution.vertices) {
//			page.fillOval((int)(vertex.getCoordinate().getX()-3), (int)(-vertex.getCoordinate().getY()-3), 7,7);
//		}
//    }
    }

    //***********************************************************
    //  Represents the listener for mouse events.
    //***********************************************************
    private class PointsListener implements MouseListener {
        //-------------------------------------------------------
        //  Adds the current point to the list of points
        //  and redraws
        //  the panel whenever the mouse button is pressed.
        //------------------------------------------------------
        public void mousePressed(MouseEvent event) {
            //create new point object
            //in JFrame, we face a little bit problems with coordinate system.
            //the anchor point for the coordinate system located at the up left corner
            //the x coordinate satisfy our perspective
            //while the y coordinate doesn't
            //we mirror the polygon on the x-axis, so that the orientation fits our perspective
            Point newPoint = new Point(event.getPoint().x, -event.getPoint().y);

            System.out.println(newPoint);

            //check whether the same located point not exist. if exist then add to point list
            if (!pointList.contains(newPoint)) pointList.add(newPoint);

            //if the available points are more than one, then continue. we have to acertain the points is elligible to create minimum polygon
            if (pointList.size() > 1) {
                //here we\ find the convex hull
                //we sort them first
                Collections.sort(pointList, new XComparator());
                //then divide and conquer
                circularLineList = solusi1.div(pointList);

                //here we find the closest segments
                //sort by x
                ArrayList<Point> Xlist = new ArrayList<Point>();
                for (Point x : pointList) Xlist.add(x);
                Collections.sort(Xlist, new XComparator());

                //sort by y
                ArrayList<Point> Ylist = new ArrayList<Point>();
                for (Point y : pointList) Ylist.add(y);
                Collections.sort(Ylist, new YComparator());

                //we start to find the closest segments
                closestLines = solusi2.findClosestPair(Xlist, Ylist);

            }
            //refresh the canvas
            repaint();
        }

        //-----------------------------------------------------
        //  Provide empty definitions for unused event methods.
        //-----------------------------------------------------
        public void mouseClicked(MouseEvent event) {
        }

        public void mouseReleased(MouseEvent event) {
        }

        public void mouseEntered(MouseEvent event) {
        }

        public void mouseExited(MouseEvent event) {
        }
    }

    //this method clears the canvas
    public void clear() {
        //clear all list of points and waypoints
        pointList.clear();
        circularLineList.clear();
        closestLines.clear();
        //clear the canvas
        repaint();
    }

    //this method is to toggle the area count
    public void showArea() {
        this.showArea = !this.showArea;
        repaint();
    }

    //this method is to toggle the convex hull lines
    public void showConvex() {
        this.showConvex = !this.showConvex;
        repaint();
    }

    //this method is to toggle the closest segments
    public void showClosestPair() {
        this.showClosest = !this.showClosest;
        repaint();
    }


    //this method is to show voronoi
    public void showVoronoi() {
        this.showVoronoi = !this.showVoronoi;
        repaint();
    }

    //this calculates the polygon area
    public double polygonArea() {
        double area = 0.0;
        //we use the Sarrus method. we sum all the x(i)*y(i+1)-y(i)-x(i+1)
        for (int ii = 0; ii < circularLineList.size(); ii++) {
            area += circularLineList.get(ii).x * circularLineList.get((ii + 1) % circularLineList.size()).y - circularLineList.get(ii).y * circularLineList.get((ii + 1) % circularLineList.size()).x;
        }
        area = -0.5 * area;
        return Math.abs(area);
    }
}

