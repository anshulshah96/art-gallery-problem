package cgcp2.ds;

import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.JPanel;


import java.awt.*;
import java.text.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.awt.Point;

public class PointsPanel extends JPanel {
    private ArrayList<Point> pointList;
    private ArrayList<Point> lineList;
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

        solusi1 = new ConvexHullSolution();
        solusi2 = new ClosestPairSolution();
        //contains all points clicked
        pointList = new ArrayList<Point>();

        //contains all convex hulls waypoints
        lineList = new ArrayList<Point>();

        //contains all closest segments
        closestLines = new ArrayList<Segment>();

        //adding new mouse listener
        addMouseListener(new PointsListener());

        //setting the background black
        setBackground(Color.black);
        pointList.add(new Point(71, -97));
        pointList.add(new Point(222, -115));
        pointList.add(new Point(132, -243));
        pointList.add(new Point(132, -99));
        pointList.add(new Point(92, -159));
        pointList.add(new Point(171, -158));
        pointList.add(new Point(305, -158));
        pointList.add(new Point(211, -232));
        pointList.add(new Point(126, -169));
        pointList.add(new Point(63, -215));

        //setting the canvas size
        setPreferredSize(new Dimension(400, 400));
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
        if (lineList != null && lineList.size() > 1 && showConvex) {
            for (int ii = 0; ii < lineList.size() - 1; ii++)
                page.drawLine(lineList.get(ii).x, -1 * lineList.get(ii).y, lineList.get(ii + 1).x, -1 * lineList.get(ii + 1).y);
            page.drawLine(lineList.get(lineList.size() - 1).x, -1 * lineList.get(lineList.size() - 1).y, lineList.get(0).x, -1 * lineList.get(0).y);
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
                HalfEdge e = f.getOuter();
                HalfEdge start = e;
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
                lineList = solusi1.div(pointList);

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
        lineList.clear();
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
        for (int ii = 0; ii < lineList.size(); ii++) {
            area += lineList.get(ii).x * lineList.get((ii + 1) % lineList.size()).y - lineList.get(ii).y * lineList.get((ii + 1) % lineList.size()).x;
        }
        area = -0.5 * area;
        return Math.abs(area);
    }
}


//segment class for the second problem
class Segment implements Comparable<Segment> {

    Point a;
    Point b;
    double l;

    public Segment(Point p, Point q) {
        a = p;
        b = q;
        l = Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public int compareTo(Segment o) {
        if (this.l > o.l) {
            return 1;
        } else if (this.l < o.l) {
            return -1;
        }
        return 0;
    }

    public boolean equals(Segment o) {
        return this.l == o.l;
    }

    public String toString() {
        return a + " " + b;
    }
}

class XComparator implements Comparator<Point> {
    public int compare(Point p1, Point p2) {
        if (p1.x == p2.x) return p2.y - p1.y;
        return p1.x - p2.x;
    }
}

class YComparator implements Comparator<Point> {
    public int compare(Point p1, Point p2) {
        if (p1.y == p2.y) return p1.x - p2.x;
        return p1.y - p2.y;
    }
}

class ConvexHullSolution {
    //this method calculate the area of three vectors
    private int areaSign(Point a, Point b, Point c) {
        double area = (b.x - a.x) * (double) (c.y - a.y) -
                (c.x - a.x) * (double) (b.y - a.y);
        if (area > 0.1) return 1;
        else if (area < -0.1) return -1;
        else return 0;
    }

    //then the area count determine whether the three vectors turning left or right
    //this return true if the segments form left turn
    private boolean isLeft(Point a, Point b, Point c) {
        return areaSign(a, b, c) >= 0;
    }

    //this return true if the segments form right turn
    private boolean isRight(Point a, Point b, Point c) {
        return areaSign(a, b, c) <= 0;
    }


    //we declare the divide and conquer method
    public ArrayList<Point> div(ArrayList<Point> points) {
        //this is the base case
        if (points.size() <= 2) {
            return points;
        }

        //create new list for the partition
        ArrayList<Point> left = new ArrayList<Point>();
        ArrayList<Point> right = new ArrayList<Point>();
        //then we divide the points into 2 equally-sized partition
        for (int ii = 0; ii < points.size(); ii++) {
            if (ii < points.size() / 2) {
                left.add(points.get(ii));
            } else {
                right.add(points.get(ii));
            }
        }
        //each partition we apply divide and conquer
        left = div(left);
        right = div(right);
        //this will return the joined partition
        return conq(left, right);
    }

    //here we delare how to conquer the divided problems
    private ArrayList<Point> conq(ArrayList<Point> polyLeft, ArrayList<Point> polyRight) {
        //we find the most right point from left polygon
        int mostRight = 0;
        for (int ii = 0; ii < polyLeft.size(); ii++) {
            //we will find the biggest x'es
            if (polyLeft.get(mostRight).x < polyLeft.get(ii).x) {
                mostRight = ii;
            }
            //if the comparison equals, we will compare the y'es
            else if (polyLeft.get(mostRight).x == polyLeft.get(ii).x) {
                if (polyLeft.get(mostRight).y > polyLeft.get(ii).y)
                    mostRight = ii;
            }
        }

        //then we find the most left point from right polygon
        int mostLeft = 0;
        for (int ii = 0; ii < polyRight.size(); ii++) {
            //we will find the point with smallest x'es
            if (polyRight.get(mostLeft).x > polyRight.get(ii).x) {
                mostLeft = ii;
            }
            //if the comparison equals, we will compare the y'es
            else if (polyRight.get(mostLeft).x == polyRight.get(ii).x) {
                if (polyRight.get(mostLeft).y < polyRight.get(ii).y)
                    mostLeft = ii;
            }
        }

        //we find the bottom points pair
        int[] botPoint = botPoints(mostRight, mostLeft, polyLeft, polyRight);
        //we find the top points pair
        int[] topPoint = topPoints(mostRight, mostLeft, polyLeft, polyRight);
        //then we join the both polygons
        return join(polyLeft, polyRight, topPoint, botPoint);
    }

    //this is how we find the bottom points
    private int[] botPoints(int rr, int ll, ArrayList<Point> polyLeft, ArrayList<Point> polyRight) {
        //this will anchor the iteration
        int startrr = rr;
        int startll = ll;
        //check whether we are exactly found the bottom points.
        //from the left polygon, we'll check whether we've already explore the bottom point of the right polygon or the end point
        //from the right polygon, we'll check wheter we've already explore the bottom point of the left polygon or the end point
        while (((ll != mod(startll + 1, polyRight.size())) && (isRight(polyLeft.get(rr), polyRight.get(ll), polyRight.get(mod(ll - 1, polyRight.size()))))) || ((rr != mod(startrr - 1, polyLeft.size())) && (isLeft(polyRight.get(ll), polyLeft.get(rr), polyLeft.get(mod((rr + 1), polyLeft.size())))))) {
            //this will iterate the tracing of the right polygon convex hull
            while (ll != mod(startll + 1, polyRight.size()) && isRight(polyLeft.get(rr), polyRight.get(ll), polyRight.get(mod((ll - 1), polyRight.size())))) {
                ll = mod(ll - 1, polyRight.size());
            }
            //this will iterate the tracing of the left polygon convex hull
            while (rr != mod(startrr - 1, polyLeft.size()) && isLeft(polyRight.get(ll), polyLeft.get(rr), polyLeft.get(mod((rr + 1), polyLeft.size())))) {
                rr = mod(rr + 1, polyLeft.size());
            }
        }
        return new int[]{rr, ll};
    }

    //this is how we find the top points
    private int[] topPoints(int rr, int ll, ArrayList<Point> polyLeft, ArrayList<Point> polyRight) {
        //this will anchor the iteration
        int startrr = rr;
        int startll = ll;
        //check whether we are exactly found the top points.
        //from the left polygon, we'll check whether we've already explore the top point of the right polygon or the end point
        //from the right polygon, we'll check wheter we've already explore the top point of the left polygon or the end point
        while (((ll != mod(startll - 1, polyRight.size())) && isLeft(polyLeft.get(rr), polyRight.get(ll), polyRight.get(mod(ll + 1, polyRight.size())))) || ((rr != mod(startrr + 1, polyLeft.size())) && isRight(polyRight.get(ll), polyLeft.get(rr), polyLeft.get(mod(rr - 1, polyLeft.size()))))) {
            //this will iterate the tracing of the right polygon convex hull
            while ((ll != mod(startll - 1, polyRight.size())) && isLeft(polyLeft.get(rr), polyRight.get(ll), polyRight.get(mod(ll + 1, polyRight.size())))) {
                ll = mod(ll + 1, polyRight.size());
            }
            //this will iterate the tracing of the left polygon convex hull
            while ((rr != mod(startrr + 1, polyLeft.size())) && isRight(polyRight.get(ll), polyLeft.get(rr), polyLeft.get(mod(rr - 1, polyLeft.size())))) {
                rr = mod(rr - 1, polyLeft.size());
            }
        }
        return new int[]{rr, ll};
    }

    //this is how we join the points
    private ArrayList<Point> join(ArrayList<Point> polyLeft, ArrayList<Point> polyRight, int[] topPoint, int[] botPoint) {
        //we create the new list to contain the new convex hull candidate
        ArrayList<Point> res = new ArrayList<Point>();
        //here we will 'sew' the both polygons based on the the top and bottom points information
        //we will iterate the right poly convex hull from the top point to the bottom point
        //after that we jump directly to the left convex hull from the bottom point to the top point
        //we determine the start point of the right poly
        int i = topPoint[1];
        //we determine the end point of the right poly
        int j = botPoint[1];
        //here we iterate them
        while (i != j) {
            res.add(polyRight.get(i));
            i = mod(++i, polyRight.size());
        }
        //we add the end point
        res.add(polyRight.get(j));
        //then we trace the polyLeft
        //we determine the start point of the left poly
        i = botPoint[0];
        //we determine the end point of the left poly
        j = topPoint[0];
        //here we iterate them
        while (i != j) {
            res.add(polyLeft.get(i));
            i = mod(++i, polyLeft.size());
        }
        //we add the end point
        res.add(polyLeft.get(j));
        //then we return the new convex hull
        return res;
    }

    //we modify the modulus operation a little bit, so that it satisfies the real modulus math
    private int mod(int a, int b) {
        if (a < 0) {
            return (b + (a % b)) % b;
        } else return a % b;
    }
}

class ClosestPairSolution {
    //here we declare closest pair method
    //PP is the array of points X-sorted, while QQ is the array of points Y-sorted
    public ArrayList<Segment> findClosestPair(ArrayList<Point> PP, ArrayList<Point> QQ) {
        //we prepare the result pairs
        ArrayList<Segment> resultPairs = new ArrayList<Segment>();

        //base case
        if (PP.size() == 2) {
            resultPairs.add(new Segment(PP.get(0), PP.get(1)));
        } else if (PP.size() == 3) {
            Segment segment1 = new Segment(PP.get(0), PP.get(1));
            Segment segment2 = new Segment(PP.get(1), PP.get(2));
            Segment segment3 = new Segment(PP.get(0), PP.get(2));
            ArrayList<Segment> temp = new ArrayList<Segment>();
            temp.add(segment1);
            temp.add(segment2);
            temp.add(segment3);
            Collections.sort(temp);
            Segment sample = temp.get(0);
            for (int ii = 0; ii < 3; ii++) if (sample.equals(temp.get(ii))) resultPairs.add(temp.get(ii));
        } else {
            //divide mode
            //divide the X-sorted
            ArrayList<Point> XL = new ArrayList<Point>();
            ArrayList<Point> XR = new ArrayList<Point>();

            for (int ii = 0; ii < PP.size(); ii++) {
                if (ii < PP.size() / 2) {
                    XL.add(PP.get(ii));
                } else {
                    XR.add(PP.get(ii));
                }
            }

            //divide the Y-sorted
            //lx is the line which divide the two part according to x
            double lx = 0.5 * (XL.get(XL.size() - 1).x + XR.get(0).x);
            //ly is the horizontal line which divide the same x's points
            double ly = XL.get(XL.size() - 1).y;
            ArrayList<Point> YL = new ArrayList<Point>();
            ArrayList<Point> YR = new ArrayList<Point>();
            for (int ii = 0; ii < QQ.size(); ii++) {
                if (QQ.get(ii).x < lx) {
                    YL.add(QQ.get(ii));
                } else if (QQ.get(ii).x > lx) {
                    YR.add(QQ.get(ii));
                } else {
                    if (QQ.get(ii).y >= ly) {
                        YL.add(QQ.get(ii));
                    } else {
                        YR.add(QQ.get(ii));
                    }
                }
            }


            //conquer mode
            //conquer the wings
            ArrayList<Segment> closestLeft = findClosestPair(XL, YL);
            ArrayList<Segment> closestRight = findClosestPair(XR, YR);

            double leftMin = closestLeft.get(0).l;
            double rightMin = closestRight.get(0).l;

            //conquer the middle part
            //define delta
            double delta = Math.min(leftMin, rightMin);

            //we find the members of the column delta
            //Ymid consists of points which lies in delta
            ArrayList<Point> Ymid = new ArrayList<Point>();
            for (int i = 0; i < QQ.size(); i++) {
                if (Math.abs(QQ.get(i).x - lx) < delta) {
                    Ymid.add(QQ.get(i));
                }
            }

            //find the closest segments in the middle column
            //midMin consists of closest segments
            ArrayList<Segment> closestMid = new ArrayList<Segment>();
            double midMin = delta;
            for (int i = 0; i < Ymid.size(); i++) {

                for (int j = i + 1; j < Math.min(i + 8, Ymid.size()); j++) {
                    double dist = distance(Ymid.get(i), Ymid.get(j));
                    if (dist < midMin) {
                        closestMid.clear();
                        closestMid.add(new Segment(Ymid.get(i), Ymid.get(j)));
                        midMin = dist;
                    } else if (dist == midMin) {
                        closestMid.add(new Segment(Ymid.get(i), Ymid.get(j)));
                    }

                }
            }

            //combine the three part
            double globMin = Math.min(delta, midMin);

            if (globMin == leftMin) {
                for (Segment s : closestLeft) {
                    resultPairs.add(s);
                }
            }
            if (globMin == rightMin) {
                for (Segment s : closestRight) {
                    resultPairs.add(s);
                }
            }
            if (globMin == midMin) {
                for (Segment s : closestMid) {
                    resultPairs.add(s);
                }
            }

        }
        return resultPairs;
    }

    private double distance(Point a, Point b) {
        double l = Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
        return l;
    }
}

