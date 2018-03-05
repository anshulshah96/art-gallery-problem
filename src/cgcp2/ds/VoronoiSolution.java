package cgcp2.ds;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author raja.oktovin
 */

import java.util.ArrayList;
import java.util.PriorityQueue;

public class VoronoiSolution {

    int top = 20;
    int bottom = 0;
    int right = 0;
    int left = 20;
    DCEL solution;
    PriorityQueue<Event> event;
    AvlNode status;
    PriorityQueue<Vertex> upside = new PriorityQueue<Vertex>();
    PriorityQueue<Vertex> rightside = new PriorityQueue<Vertex>();
    PriorityQueue<Vertex> downside = new PriorityQueue<Vertex>();
    PriorityQueue<Vertex> leftside = new PriorityQueue<Vertex>();
    Point firstSite = null;

    /*
     * Constructor of solution to Voronoi Diagram.
     */
    public VoronoiSolution(ArrayList<java.awt.Point> pp, int top, int bottom, int left, int right) {
        //set out boundaries
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        //Initialize event queue with all site event
        event = new PriorityQueue<Event>();
        ArrayList<Point> convertedPoint = new ArrayList<Point>();
        for (java.awt.Point p : pp) {
            convertedPoint.add(new Point(p.x, p.y));
        }
        //insert all points to queue
        for (Point p : convertedPoint) {
            event.add(new Event(p, null));
        }
        //Initialize empty DCEL
        solution = new DCEL();
        //While queue is not empty
        while (!event.isEmpty()) {
            //Remove event with largest y coordinate
            Event e = event.remove();
            //step 5
            if (e.isSiteEvent()) {
                handleSiteEvent(e.getLocation());
                //step 6
            } else {
                handleCircleEvent(e.getCircleArc());
            }
        }
        //finishing the remaining break points
        traverseRemainingBreakPoint(status);
        //debugging
        //printDCEL();
    }

    void printDCEL() {
        System.out.println("Faces : ");
        for (Face f : solution.faces.values()) {
            System.out.println(f.toString());
        }
        System.out.println("Halfedge : ");
        for (HalfEdge e : solution.edges) {
            System.out.println(e.getOrigin().toString() + " " + e.getTwin().getOrigin().toString());
        }
        System.out.println("Vertex : ");
        for (Vertex v : solution.vertices) {
            System.out.println(v.toString());
        }
    }

    /*
     * Method to handle when sweep-line meets the site event.
     */
    private void handleSiteEvent(Point p) {
        handleArc(status, p);
    }

    /*
     * Method to handle Circle Event.
     */
    private void handleCircleEvent(AvlNode a) {

        //step 1
        //delete from tree
        AvlNode rightBreakPoint = a.getRightPredecessor();
        AvlNode leftBreakPoint = a.getLeftPredecessor();

        AvlNode leftArc = leftBreakPoint.getLeftSuccessor();
        AvlNode rightArc = rightBreakPoint.getRightSuccessor();

        AvlNode newBreakPoint = null;

        HalfEdge leftHalf = leftBreakPoint.getIncidentEdge();
        HalfEdge rightHalf = rightBreakPoint.getIncidentEdge();

        if (a.isLeftChildofParent()) {
            //update tree structure
            if (rightBreakPoint.isLeftChildofParent()) {
                rightBreakPoint.getParent().setLeftChild(rightBreakPoint.getRightChild());
            } else {
                rightBreakPoint.getParent().setRightChild(rightBreakPoint.getRightChild());
            }
            rightBreakPoint.getRightChild().setParent(rightBreakPoint.getParent());
            //update voronoi property
            leftBreakPoint.setRightParabolaFocus(rightArc.getLeftParabolaFocus());
            newBreakPoint = leftBreakPoint;
            if (leftArc.getLeftParabolaFocus().getY() >= rightArc.getLeftParabolaFocus().getY()) {
                leftBreakPoint.setAsLeftIntersection();
            } else {
                leftBreakPoint.setAsRightIntersection();
            }
        } else {
            //update tree structure
            if (leftBreakPoint.isRightChildofParent()) {
                leftBreakPoint.getParent().setRightChild(leftBreakPoint.getLeftChild());
            } else {
                leftBreakPoint.getParent().setLeftChild(leftBreakPoint.getLeftChild());
            }
            leftBreakPoint.getLeftChild().setParent(leftBreakPoint.getParent());
            //update voronoi property
            rightBreakPoint.setLeftParabolaFocus(leftArc.getRightParabolaFocus());
            newBreakPoint = rightBreakPoint;
            if (leftArc.getLeftParabolaFocus().getY() >= rightArc.getLeftParabolaFocus().getY()) {
                rightBreakPoint.setAsLeftIntersection();
            } else {
                rightBreakPoint.setAsRightIntersection();
            }
        }


        //delete from queue
        event.remove(leftArc.getCircleEvent());
        event.remove(rightArc.getCircleEvent());

        //We create a point that corresponds to the center, which
        Point circleCenter = a.getCircleEvent().getCircleCenter();
        //will become Voronoi Vertex.        
        //Voronoi vertex happens
        Vertex vv = new Vertex(circleCenter);
        solution.vertices.add(vv);
        //Create edge
        HalfEdge e1 = new HalfEdge();
        HalfEdge e2 = new HalfEdge();
        e1.setIncident(solution.faces.get(leftArc.getLeftParabolaFocus()));
        e2.setIncident(solution.faces.get(rightArc.getLeftParabolaFocus()));
        e1.setTwin(e2);
        e2.setTwin(e1);
        e1.setNext(leftHalf);
        e2.setPrev(rightHalf.getTwin());
        e2.setOrigin(vv);
        vv.setIncident(e2);
        //connect the incoming half edge
        leftHalf.setOrigin(vv);
        leftHalf.setPrev(e1);
        leftHalf.getTwin().setNext(rightHalf);
        rightHalf.setOrigin(vv);
        rightHalf.setPrev(leftHalf.getTwin());
        rightHalf.getTwin().setNext(e2);
        //update halfedge on the new breakpoint
        newBreakPoint.setIncidentEdge(e1);
        //Update edge list
        solution.edges.add(e1);
        solution.edges.add(e2);
        // Checking three consecutive arcs if they will
        // form a circle event.
        double ly = a.getCircleEvent().getLocation().getY();
        if (leftArc.getLeftSibling() != null) {
            AvlNode checkedArc = leftArc;
            AvlNode leftBP = checkedArc.getLeftPredecessor();
            AvlNode rightBP = checkedArc.getRightPredecessor();
            if (!a.getCircleEvent().isTheSameCocircular(leftBP.getLeftParabolaFocus(), leftBP.getRightParabolaFocus(), rightBP.getRightParabolaFocus()) && isConverge(leftBP, rightBP, ly)) {
                Point circleEventPoint = getCircleEventPoint(leftBP, rightBP);
                Event circleEvent = new Event(circleEventPoint, checkedArc);
                circleEvent.setCocirculars(leftBP.getLeftParabolaFocus(), leftBP.getRightParabolaFocus(), rightBP.getRightParabolaFocus());
                checkedArc.setCircleEvent(circleEvent);
                event.add(circleEvent);
            }
        }
        if (rightArc.getRightSibling() != null) {
            AvlNode checkedArc = rightArc;
            AvlNode leftBP = checkedArc.getLeftPredecessor();
            AvlNode rightBP = checkedArc.getRightPredecessor();
            if (!a.getCircleEvent().isTheSameCocircular(leftBP.getLeftParabolaFocus(), leftBP.getRightParabolaFocus(), rightBP.getRightParabolaFocus()) && isConverge(leftBP, rightBP, ly)) {
                Point circleEventPoint = getCircleEventPoint(leftBP, rightBP);
                Event circleEvent = new Event(circleEventPoint, checkedArc);
                circleEvent.setCocirculars(leftBP.getLeftParabolaFocus(), leftBP.getRightParabolaFocus(), rightBP.getRightParabolaFocus());
                checkedArc.setCircleEvent(circleEvent);
                event.add(circleEvent);
            }
        }
    }

    /*
     * Method to destroy an arc and replacing it with some new arcs.
     */
    private void handleArc(AvlNode node, Point p) {
        //This method works recursively.
        //If it is leaf already, then we replace the arc with new breakpoints and arcs.
        //If it is not, we look for it to the left or right.
        //step 1
        if (node == null) {
            status = new AvlNode(p, p, 0);
            //make first debut
            firstSite = p;
            Face f = new Face(p);
            solution.faces.put(p, f);

        }
        //step 2
        else if (node.isLeaf()) {
            //step 3
            //We remove all false alarms of circle event related to the
            //arc.

            if (node.hasCircleEvent()) {
                event.remove(node.getCircleEvent());
            }
            //step 4
            //special code for the same y coordinate
            if (firstSite.getY() == p.getY()) {
                //It is already the arc
                Point l = node.getLeftParabolaFocus();
                //Now we update the three new arcs (destroyed, new, destroyed).
                AvlNode left = null;
                AvlNode right = null;
                if (l.getX() <= p.getX()) {
                    left = new AvlNode(l, l, 0);
                    right = new AvlNode(p, p, 0);
                } else {
                    left = new AvlNode(p, p, 0);
                    right = new AvlNode(l, l, 0);
                }
                //Replace arc being the middlebp
                //Create new breakpoint
                AvlNode breakPoint = new AvlNode(left.getLeftParabolaFocus(), right.getRightParabolaFocus(), 0);
                //set the child
                breakPoint.setLeftChild(left);
                breakPoint.setRightChild(right);
                //Update variables in left and child of node.
                left.setParent(breakPoint);
                right.setParent(breakPoint);
                //update to up
                if (node.hasParent()) {
                    if (node.isLeftChildofParent()) {
                        node.getParent().setLeftChild(breakPoint);
                    } else {
                        node.getParent().setRightChild(breakPoint);
                    }
                    breakPoint.setParent(node.getParent());
                } else {
                    status = breakPoint;
                }

                //step 4
                //Voronoi operation
                //Create new half edges in Voronoi Diagram
                //that will be traced out by breakpoints.
                //Create edge
                HalfEdge e1 = new HalfEdge();
                HalfEdge e2 = new HalfEdge();
                solution.edges.add(e1);
                solution.edges.add(e2);
                //create face
                Face f = new Face(p);
                solution.faces.put(p, f);
                //update the edge
                e1.setIncident(solution.faces.get(left.getLeftParabolaFocus()));
                e2.setIncident(solution.faces.get(right.getRightParabolaFocus()));
                e1.setTwin(e2);
                e2.setTwin(e1);
                //update the face
                solution.faces.get(left.getLeftParabolaFocus()).setOuter(e1);
                solution.faces.get(right.getRightParabolaFocus()).setOuter(e2);
                //Set the vertex on boundaries
                double x = breakPoint.getLocation(p.getY()).getX();
                Vertex bvertex = new Vertex(new Point(x, top));
                solution.vertices.add(bvertex);
                //update the vertex
                bvertex.setIncident(e2);
                //update the edge
                e2.setOrigin(bvertex);
                //Update Voronoi variables in two new breakpoints
                breakPoint.setIncidentEdge(e1);
            } else {
                //It is already the arc
                Point l = node.getLeftParabolaFocus();
                //Now we update the three new arcs (destroyed, new, destroyed).
                AvlNode left = new AvlNode(l, l, 0);
                AvlNode middle = new AvlNode(p, p, 0);
                AvlNode right = new AvlNode(l, l, 0);
                //Now we update one new breakpoints
                AvlNode leftbp = new AvlNode(l, p, 0);
                AvlNode rightbp = new AvlNode(p, l, 1);
                //set the child
                leftbp.setLeftChild(left);
                leftbp.setRightChild(rightbp);
                //Update the child
                left.setParent(leftbp);
                rightbp.setParent(leftbp);
                //set the child
                rightbp.setLeftChild(middle);
                rightbp.setRightChild(right);
                //Update the child
                middle.setParent(rightbp);
                //Update the variables in right arc.
                right.setParent(rightbp);
                //update to up
                if (node.hasParent()) {
                    if (node.isLeftChildofParent()) {
                        node.getParent().setLeftChild(leftbp);
                    } else {
                        node.getParent().setRightChild(leftbp);
                    }
                    leftbp.setParent(node.getParent());
                } else {
                    status = leftbp;
                }
                //Voronoi operation
                //Create new half edges in Voronoi Diagram
                //that will be traced out by breakpoints.

                //Create edge
                HalfEdge e1 = new HalfEdge();
                HalfEdge e2 = new HalfEdge();
                solution.edges.add(e1);
                solution.edges.add(e2);
                //create face
                Face f = new Face(p);
                solution.faces.put(p, f);
                //update the edge
                e1.setIncident(solution.faces.get(p));
                e2.setIncident(solution.faces.get(l));
                e1.setTwin(e2);
                e2.setTwin(e1);
                //update the face
                solution.faces.get(p).setOuter(e1);
                solution.faces.get(l).setOuter(e2);
                //connect the voronoi property and the tree
                rightbp.setIncidentEdge(e1);
                leftbp.setIncidentEdge(e2);

                //step5
                //Check triple consecutive (left and right)
                //if these new arcs will produce a new circle event.
                double ly = p.getY();
                if (left.getLeftSibling() != null) {
                    AvlNode checkedArc = left;
                    AvlNode leftBreakPoint = checkedArc.getLeftPredecessor();
                    AvlNode rightBreakPoint = checkedArc.getRightPredecessor();
                    if (isConverge(leftBreakPoint, rightBreakPoint, ly)) {
                        Point circleEventPoint = getCircleEventPoint(leftBreakPoint, rightBreakPoint);
                        Point circlePoint = getCirclePoint(leftBreakPoint, rightBreakPoint);
                        Event circleEvent = new Event(circleEventPoint, checkedArc);
                        circleEvent.setCocirculars(leftBreakPoint.getLeftParabolaFocus(), leftBreakPoint.getRightParabolaFocus(), rightBreakPoint.getRightParabolaFocus());
                        checkedArc.setCircleEvent(circleEvent);
                        event.add(circleEvent);
                    }
                }
                if (right.getRightSibling() != null) {
                    AvlNode checkedArc = middle.getRightSibling();
                    AvlNode leftBreakPoint = checkedArc.getLeftPredecessor();
                    AvlNode rightBreakPoint = checkedArc.getRightPredecessor();
                    if (isConverge(leftBreakPoint, rightBreakPoint, ly)) {
                        Point circleEventPoint = getCircleEventPoint(leftBreakPoint, rightBreakPoint);
                        Point circlePoint = getCirclePoint(leftBreakPoint, rightBreakPoint);
                        Event circleEvent = new Event(circleEventPoint, checkedArc);
                        circleEvent.setCocirculars(leftBreakPoint.getLeftParabolaFocus(), leftBreakPoint.getRightParabolaFocus(), rightBreakPoint.getRightParabolaFocus());
                        checkedArc.setCircleEvent(circleEvent);
                        event.add(circleEvent);
                    }
                }
            }

        } else {
            double ly = p.getY();
            Point brpoint = node.getLocation(ly);
            if (brpoint.getX() < p.getX()) {
                handleArc(node.getRightChild(), p);
            } else {
                handleArc(node.getLeftChild(), p);
            }
        }
    }

    /*
     * Method to find the intersection point of two arcs which is used to find the
     * arc exactly above an event site.
     */
    private void traverseRemainingBreakPoint(AvlNode node) {
        if (node != null && !node.isLeaf()) {
            connectBoundaries(node);
            traverseRemainingBreakPoint(node.getLeftChild());
            traverseRemainingBreakPoint(node.getRightChild());
        }
    }

    private void connectBoundaries(AvlNode node) {
        //dapatkan pseudo vorvex
        Point p1 = node.getIncidentEdge().getIncident().getSite();
        Point p2 = node.getIncidentEdge().getTwin().getIncident().getSite();
        double xc = (p1.getX() + p2.getX()) / 2.0;
        double yc = (p1.getY() + p2.getY()) / 2.0;
        Point pseudover = new Point(xc, yc);
        if (p1.getX() - p2.getX() == 0) {
            double y0 = yc;
            double x0 = left;
            double y1 = yc;
            double x1 = right;
            Point leftpoint = new Point(x0, y0);
            Point rightpoint = new Point(x1, y1);
            if (!isLeft(pseudover, leftpoint, node.getIncidentEdge().getIncident().site)) {
                Vertex left = new Vertex(leftpoint);
                node.getIncidentEdge().setOrigin(left);
                left.setIncident(node.getIncidentEdge());
                solution.vertices.add(left);
            } else {
                Vertex right = new Vertex(rightpoint);
                node.getIncidentEdge().setOrigin(right);
                right.setIncident(node.getIncidentEdge());
                solution.vertices.add(right);
            }
        } else if (p1.getY() - p2.getY() == 0) {
            double y0 = top;
            double x0 = xc;
            double y1 = bottom;
            double x1 = xc;
            Point uppoint = new Point(x0, y0);
            Point downpoint = new Point(x1, y1);
            if (!isLeft(pseudover, uppoint, node.getIncidentEdge().getIncident().site)) {
                Vertex up = new Vertex(uppoint);
                node.getIncidentEdge().setOrigin(up);
                up.setIncident(node.getIncidentEdge());
                solution.vertices.add(up);
            } else {
                Vertex down = new Vertex(downpoint);
                node.getIncidentEdge().setOrigin(down);
                down.setIncident(node.getIncidentEdge());
                solution.vertices.add(down);
            }
        } else {
            double mc = getGradientSeparator(p1, p2);
            //cek perpotongan dengan side kiri
            double x1 = left;
            double y1 = mc * x1 + mc * -xc + yc;
            //cek klo y nya feasible
            if (y1 <= top && y1 >= bottom) {
                Point p = new Point((int) x1, (int) y1);
                if (!isLeft(pseudover, p, node.getIncidentEdge().getIncident().site)) {
                    Vertex vp = new Vertex(p);
                    node.getIncidentEdge().setOrigin(vp);
                    vp.setIncident(node.getIncidentEdge());
                    solution.vertices.add(vp);
                }
            }
            //cek perpotongan dengan side kanan
            double x2 = right;
            double y2 = mc * x2 + mc * -xc + yc;
            if (y2 <= top && y2 >= bottom) {
                Point p = new Point((int) x2, (int) y2);
                if (!isLeft(pseudover, p, node.getIncidentEdge().getIncident().site)) {
                    Vertex vp = new Vertex(p);
                    node.getIncidentEdge().setOrigin(vp);
                    vp.setIncident(node.getIncidentEdge());
                    solution.vertices.add(vp);
                }
            }
            //cek perpotongan dengan side atas
            double y3 = top;
            double x3 = (y3 + mc * xc - yc) / mc;
            if (x3 >= left && x3 <= right) {
                Point p = new Point((int) x3, (int) y3);
                if (!isLeft(pseudover, p, node.getIncidentEdge().getIncident().site)) {
                    Vertex vp = new Vertex(p);
                    node.getIncidentEdge().setOrigin(vp);
                    vp.setIncident(node.getIncidentEdge());
                    solution.vertices.add(vp);
                }
            }
            //cek perpotongan dengan side bawah
            double y4 = bottom;
            double x4 = (y4 + mc * xc - yc) / mc;
            if (x4 >= left && x4 <= right) {
                Point p = new Point((int) x4, (int) y4);
                if (!isLeft(pseudover, p, node.getIncidentEdge().getIncident().site)) {
                    Vertex vp = new Vertex(p);
                    node.getIncidentEdge().setOrigin(vp);
                    vp.setIncident(node.getIncidentEdge());
                    solution.vertices.add(vp);
                }
            }
        }
    }

    private int areaSign(Point a, Point b, Point c) {
        double area = (b.getX() - a.getX()) * (double) (c.getY() - a.getY()) -
                (c.getX() - a.getX()) * (double) (b.getY() - a.getY());
        if (area > 0) return 1;
        else if (area < 0) return -1;
        else return 0;
    }

    //then the area count determine whether the three vectors turning left or right
    //this return true if the segments form left turn
    private boolean isLeft(Point a, Point b, Point c) {
        return areaSign(a, b, c) > 0;
    }

    private boolean isLeftOn(Point a, Point b, Point c) {
        return areaSign(a, b, c) >= 0;
    }

    /*
     * Method to find the center (Voronoi vertex) and radius of a circle
     * of the points.
     */
    public static Point getCircleCenter(Point p1, Point p2, Point p3) {
        double ax = p1.getX();
        double bx = p2.getX();
        double cx = p3.getX();
        double ay = p1.getY();
        double by = p2.getY();
        double cy = p3.getY();

        double D = 2.0 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));
        double x = (((ax * ax + ay * ay) * (by - cy) + (bx * bx + by * by) * (cy - ay) + (cx * cx + cy * cy) * (ay - by)) / D);
        double y = (((ax * ax + ay * ay) * (cx - bx) + (bx * bx + by * by) * (ax - cx) + (cx * cx + cy * cy) * (bx - ax)) / D);
        return new Point(x, y);
    }

    private double getCircleRadius(Point p1, Point p2, Point p3) {
        Point center = getCircleCenter(p1, p2, p3);
        double r = Math.sqrt((center.getX() - p1.getX()) * (center.getX() - p1.getX()) + (center.getY() - p1.getY()) * (center.getY() - p1.getY()));
        return r;
    }

    private double getGradientSeparator(Point p1, Point p2) {
        if (p1.getY() - p2.getY() != 0) {
            return -1.0 * (p1.getX() - p2.getX()) / (double) (p1.getY() - p2.getY());
        } else {
            return Double.MAX_VALUE;
        }
    }

    public void restring(AvlNode node) {
        if (node != null && node.isLeaf())
            System.out.print("(" + node.getLeftParabolaFocus().getX() + "," + node.getLeftParabolaFocus().getY() + ") ");
        if (node.getLeftChild() != null) restring(node.getLeftChild());
        if (node.getRightChild() != null) restring(node.getRightChild());
    }

    private Point getCircleEventPoint(AvlNode br1, AvlNode br2) {
        Point left = br1.getLeftParabolaFocus();
        Point middle = br1.getRightParabolaFocus();
        Point right = br2.getRightParabolaFocus();
        Point intersection = getCircleCenter(left, middle, right);
        double radius = getCircleRadius(left, middle, right);
        return new Point(intersection.getX(), intersection.getY() - radius);
    }

    private Point getCirclePoint(AvlNode br1, AvlNode br2) {
        Point left = br1.getLeftParabolaFocus();
        Point middle = br1.getRightParabolaFocus();
        Point right = br2.getRightParabolaFocus();
        Point intersection = getCircleCenter(left, middle, right);
        return new Point(intersection.getX(), intersection.getY());
    }


    private boolean isConverge(AvlNode br1, AvlNode br2, double ly) {
        //cek apakah paralel
        Point site1br1 = br1.getIncidentEdge().getIncident().site;
        Point site2br1 = br1.getIncidentEdge().getTwin().getIncident().site;
        Point site1br2 = br2.getIncidentEdge().getIncident().site;
        Point site2br2 = br2.getIncidentEdge().getTwin().getIncident().site;
        //cek paralel horizontal
        if (site1br1.getY() == site2br1.getY() && site1br2.getY() == site2br2.getY()) {
            return false;
        }
        //cek paralel vertikal
        else if (site1br1.getX() == site2br1.getX() && site1br2.getX() == site2br2.getX()) {
            return false;
        } else {
            //cari titik tengah b1
            double x1 = br1.getLocation(ly).getX();
            double y1 = br1.getLocation(ly).getY();
            //cari titik tengah b2
            double x2 = br2.getLocation(ly).getX();
            double y2 = br2.getLocation(ly).getY();
            //cari titik konergensi garis
            Point left = br1.getLeftParabolaFocus();
            Point middle = br1.getRightParabolaFocus();
            Point right = br2.getRightParabolaFocus();
            Point intersection = getCircleCenter(left, middle, right);
            //degenerate cases number 3y
            if (Math.hypot(intersection.getX() - x1, intersection.getY() - y1) < 1 && Math.hypot(intersection.getX() - x2, intersection.getY() - y2) < 1) {
                return true;
            } else if (!isLeft(new Point(x1, y1), intersection, site1br1) && !isLeft(new Point(x2, y2), intersection, site1br2)) {
                return true;
            } else {
                return false;
            }

        }
    }

    private boolean isConvergeAfterEvent(AvlNode br1, AvlNode br2, double ly) {
        //cek apakah paralel
        Point site1br1 = br1.getIncidentEdge().getIncident().site;
        Point site2br1 = br1.getIncidentEdge().getTwin().getIncident().site;
        Point site1br2 = br2.getIncidentEdge().getIncident().site;
        Point site2br2 = br2.getIncidentEdge().getTwin().getIncident().site;
        //cek paralel horizontal
        if (site1br1.getY() == site2br1.getY() && site1br2.getY() == site2br2.getY()) {
            return false;
        }
        //cek paralel vertikal
        else if (site1br1.getX() == site2br1.getX() && site1br2.getX() == site2br2.getX()) {
            return false;
        } else {
            //cari titik tengah b1
            double x1 = br1.getLocation(ly).getX();
            double y1 = br1.getLocation(ly).getY();
            //cari titik tengah b2
            double x2 = br2.getLocation(ly).getX();
            double y2 = br2.getLocation(ly).getY();
            //cari titik konergensi garis
            Point intersection = getCirclePoint(br1, br2);
            //degenerate cases number 3
            if (!isLeftOn(new Point(x1, y1), intersection, site1br1) && !isLeftOn(new Point(x2, y2), intersection, site1br2)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isinBoundaries(Point p) {
        return p.getX() <= this.right && p.getX() >= this.left && p.getY() >= this.bottom && p.getY() <= this.top;
    }

    public boolean isinLine(double[] vector1, double[] vector2) {
        double normv1 = Math.sqrt(vector1[0] * vector1[0] + vector1[1] * vector1[1]);
        double normv2 = Math.sqrt(vector2[0] * vector2[0] + vector2[1] * vector2[1]);
        double dotprod = vector1[0] * vector2[0] + vector1[1] * vector2[1];
        double cos = dotprod / (normv1 * normv2);
        if (cos > 0) return true;
        else return false;
    }
}

