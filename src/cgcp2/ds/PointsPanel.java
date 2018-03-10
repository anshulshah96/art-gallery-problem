package cgcp2.ds;

import java.util.ArrayList;
import javax.swing.JPanel;


import java.awt.*;
import java.awt.Point;
import java.util.HashMap;

public class PointsPanel extends JPanel {
    private static DCEL dcel;
    private ArrayList<DCEL> dcelArrayList;
    private ArrayList<Point> pointArrayList;
    private ArrayList<Point> circularPointList;
    private ArrayList<Segment> trapezoidalLines;
    private ArrayList<Segment> partitionLines;
    private ArrayList<Edge> monoPartDiagonals;
    private ArrayList<Segment> triangulationLines;
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

        pointArrayList = new ArrayList<>();
        circularPointList = new ArrayList<>();
        trapezoidalLines = new ArrayList<>();
        partitionLines = new ArrayList<>();
        monoPartDiagonals = new ArrayList<>();
        triangulationLines = new ArrayList<>();

        //setting the background black
        setBackground(Color.black);

        //setting the canvas size
        setPreferredSize(new Dimension(1200, 800));
    }

    public void initPoints(int n) {
        clear();

        int ll = 50;
        int ul = 250;

        for (int i = 0; i < n; i++) {
            int x = ll + (int) (Math.random() * (ul - ll));
            int y = ll + (int) (Math.random() * (ul - ll));
            y = -y;
            pointArrayList.add(new Point(3 * x, 3 * y));
        }
        System.out.println(pointArrayList);

//        DCEL not partitioned
//        [java.awt.Point[x=160,y=-89], java.awt.Point[x=98,y=-182], java.awt.Point[x=298,y=-340], java.awt.Point[x=120,y=-291], java.awt.Point[x=130,y=-272], java.awt.Point[x=148,y=-163]]

//        Merging DCEL
//        [java.awt.Point[x=102,y=-160], java.awt.Point[x=55,y=-124], java.awt.Point[x=273,y=-286], java.awt.Point[x=228,y=-321], java.awt.Point[x=73,y=-176], java.awt.Point[x=193,y=-182]]

//        Triangulation issues
//        [java.awt.Point[x=63,y=-247], java.awt.Point[x=165,y=-274], java.awt.Point[x=100,y=-197], java.awt.Point[x=132,y=-212], java.awt.Point[x=187,y=-245], java.awt.Point[x=312,y=-283], java.awt.Point[x=344,y=-98], java.awt.Point[x=237,y=-178], java.awt.Point[x=107,y=-137], java.awt.Point[x=243,y=-259]]

        repaint();
    }

    public void generatePolygon() {
        dcel = new DCEL();
        trapezoidalLines.clear();
        partitionLines.clear();
        triangulationLines.clear();
        polygonSolution.pointArrayList = pointArrayList;
        polygonSolution.dcel = dcel;
        polygonSolution.generatePolygon();
        circularPointList = polygonSolution.circularPointList;

        repaint();
    }

    public void trapezoidalization() {
        trapezoidalLines.clear();
        partitionLines.clear();
        trapezoidSolution.dcel = dcel;
        trapezoidSolution.trapezoidalLines = trapezoidalLines;
        trapezoidSolution.generateTrap();

        repaint();
    }

    public void monotonePartition() {
        trapezoidalLines.clear();
        partitionLines.clear();
        monoPartDiagonals.clear();
        monotonePartition.dcel = dcel;
        monotonePartition.monoPartLines = partitionLines;
        monotonePartition.monoPartDiagonals = monoPartDiagonals;
        monotonePartition.generate();

        dcelArrayList = monotonePartition.getSeparateDCEL();

        repaint();
    }

    public void triangulatePartitions() {
        triangulationLines.clear();
        trapezoidalLines.clear();
        for (DCEL dcel : dcelArrayList) {
            Triangulation trisol = new Triangulation(dcel, triangulationLines);
            trisol.generate();
        }

        repaint();
    }

    public void dualGraph() {
        dualitySolution.dcelArrayList = dcelArrayList;
        dualitySolution.partitionDiagonals = monoPartDiagonals;
        dualitySolution.generate();

        repaint();
    }

    public void threecolor() {
        triangulationLines.clear();
        partitionLines.clear();
        dualitySolution.solve();
        colour = dualitySolution.colour;
        finale = true;
        dualitySolution.dualEdges.clear();
        dualitySolution.dualVertices.clear();

        repaint();
    }

    //-----------------------------------------------------------
    //  Draws all of the points stored in the list.
    //-----------------------------------------------------------
    public void paintComponent(Graphics page) {
        super.paintComponent(page);

        //Showing the Points
        page.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        page.setColor(Color.green);
        for (Point spot : pointArrayList) {
            if (finale) { // If min vertex guard solution is present
                if (colour.get(spot) == 1)
                    page.setColor(Color.RED);
                else if (colour.get(spot) == 2)
                    page.setColor(Color.BLUE);
                else
                    page.setColor(Color.GREEN);
                if (colour.get(spot) == dualitySolution.minc) {
                    page.fillOval(spot.x - 8, -1 * spot.y - 9, 19, 19);
                    page.setColor(Color.WHITE);
                    page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
                } else {
                    page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
                }
                page.setColor(Color.GREEN);
                page.drawString("Vertex Guards: " + dualitySolution.ct[dualitySolution.minc], 5, 42);
            } else
                page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
        }

        //rendering the polygon constructed
        page.setColor(Color.orange);
        if (circularPointList != null && circularPointList.size() > 1) {
            for (int ii = 0; ii < circularPointList.size() - 1; ii++) {
                page.drawLine(circularPointList.get(ii).x, -1 * circularPointList.get(ii).y, circularPointList.get(ii + 1).x, -1 * circularPointList.get(ii + 1).y);
            }
            page.drawLine(circularPointList.get(circularPointList.size() - 1).x, -1 * circularPointList.get(circularPointList.size() - 1).y, circularPointList.get(0).x, -1 * circularPointList.get(0).y);
        }
        //rendering the Trapezoidalization segments
        page.setColor(Color.white);
        if (trapezoidalLines != null && trapezoidalLines.size() > 0) {
            for (int ii = 0; ii < trapezoidalLines.size(); ii++)
                page.drawLine(trapezoidalLines.get(ii).a.x, -1 * trapezoidalLines.get(ii).a.y, trapezoidalLines.get(ii).b.x, -1 * trapezoidalLines.get(ii).b.y);
        }

        //rendering the partition segments
        page.setColor(Color.GREEN);
        if (partitionLines != null && partitionLines.size() > 0) {
            for (int ii = 0; ii < partitionLines.size(); ii++)
                page.drawLine(partitionLines.get(ii).a.x, -1 * partitionLines.get(ii).a.y, partitionLines.get(ii).b.x, -1 * partitionLines.get(ii).b.y);
        }

        //rendering the trig segments
        page.setColor(Color.GREEN);
        if (triangulationLines != null && triangulationLines.size() > 0) {
            for (int ii = 0; ii < triangulationLines.size(); ii++)
                page.drawLine(triangulationLines.get(ii).a.x, -1 * triangulationLines.get(ii).a.y, triangulationLines.get(ii).b.x, -1 * triangulationLines.get(ii).b.y);
        }

        //rendering dual tree edges and vertices
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

        page.setColor(Color.GREEN);
        //display the points count
        page.drawString("Vertices: " + pointArrayList.size(), 5, 20);
    }

    //this method clears the canvas
    public void clear() {
        //clear all list of points and waypoints
        pointArrayList.clear();
        circularPointList.clear();
        trapezoidalLines.clear();
        partitionLines.clear();
        triangulationLines.clear();
        dualitySolution = new DualitySolution();
        //clear the canvas
        finale = false;
        repaint();
    }
}

