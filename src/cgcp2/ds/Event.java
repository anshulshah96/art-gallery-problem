package cgcp2.ds;

import java.util.ArrayList;

class Event implements Comparable<Event> {
    private Point coordinate;
    private AvlNode circlearc;
    private ArrayList<Point> cocirculars;


    public Event(Point p, AvlNode a) {
        coordinate = p;
        circlearc = a;
    }

    @Override
    public int compareTo(Event e) {
        return this.getLocation().compareTo(e.getLocation());
    }

    public boolean isSiteEvent() {
        return circlearc == null;
    }

    public Point getLocation() {
        return coordinate;
    }

    public AvlNode getCircleArc() {
        return circlearc;
    }

    public void setCocirculars(Point a, Point b, Point c) {
        cocirculars = new ArrayList<Point>();
        cocirculars.add(a);
        cocirculars.add(b);
        cocirculars.add(c);

    }

    public ArrayList<Point> getCocircular() {
        return cocirculars;
    }

    public boolean isTheSameCocircular(Point a, Point b, Point c) {
        if (a == cocirculars.get(0) && b == cocirculars.get(1) && c == cocirculars.get(2)) return true;
        if (a == cocirculars.get(0) && b == cocirculars.get(2) && c == cocirculars.get(1)) return true;
        if (a == cocirculars.get(1) && b == cocirculars.get(0) && c == cocirculars.get(2)) return true;
        if (a == cocirculars.get(1) && b == cocirculars.get(2) && c == cocirculars.get(0)) return true;
        if (a == cocirculars.get(2) && b == cocirculars.get(0) && c == cocirculars.get(1)) return true;
        if (a == cocirculars.get(2) && b == cocirculars.get(1) && c == cocirculars.get(0)) return true;
        return false;
    }

}