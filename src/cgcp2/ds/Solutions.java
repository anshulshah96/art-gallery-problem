package cgcp2.ds;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Solutions {
}



//segment class for the second problem
class Segment implements Comparable<Segment> {

    java.awt.Point a;
    java.awt.Point b;
    double l;

    public Segment(java.awt.Point p, java.awt.Point q) {
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

class XComparator implements Comparator<java.awt.Point> {
    public int compare(java.awt.Point p1, java.awt.Point p2) {
        if (p1.x == p2.x) return p2.y - p1.y;
        return p1.x - p2.x;
    }
}

class YComparator implements Comparator<java.awt.Point> {
    public int compare(java.awt.Point p1, java.awt.Point p2) {
        if (p1.y == p2.y) return p1.x - p2.x;
        return p1.y - p2.y;
    }
}

class ConvexHullSolution {
    //this method calculate the area of three vectors
    private int areaSign(java.awt.Point a, java.awt.Point b, java.awt.Point c) {
        double area = (b.x - a.x) * (double) (c.y - a.y) -
                (c.x - a.x) * (double) (b.y - a.y);
        if (area > 0.1) return 1;
        else if (area < -0.1) return -1;
        else return 0;
    }

    //then the area count determine whether the three vectors turning left or right
    //this return true if the segments form left turn
    private boolean isLeft(java.awt.Point a, java.awt.Point b, java.awt.Point c) {
        return areaSign(a, b, c) >= 0;
    }

    //this return true if the segments form right turn
    private boolean isRight(java.awt.Point a, java.awt.Point b, java.awt.Point c) {
        return areaSign(a, b, c) <= 0;
    }


    //we declare the divide and conquer method
    public ArrayList<java.awt.Point> div(ArrayList<java.awt.Point> points) {
        //this is the base case
        if (points.size() <= 2) {
            return points;
        }

        //create new list for the partition
        ArrayList<java.awt.Point> left = new ArrayList<java.awt.Point>();
        ArrayList<java.awt.Point> right = new ArrayList<java.awt.Point>();
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
    private ArrayList<java.awt.Point> conq(ArrayList<java.awt.Point> polyLeft, ArrayList<java.awt.Point> polyRight) {
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
    private int[] botPoints(int rr, int ll, ArrayList<java.awt.Point> polyLeft, ArrayList<java.awt.Point> polyRight) {
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
    private int[] topPoints(int rr, int ll, ArrayList<java.awt.Point> polyLeft, ArrayList<java.awt.Point> polyRight) {
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
    private ArrayList<java.awt.Point> join(ArrayList<java.awt.Point> polyLeft, ArrayList<java.awt.Point> polyRight, int[] topPoint, int[] botPoint) {
        //we create the new list to contain the new convex hull candidate
        ArrayList<java.awt.Point> res = new ArrayList<java.awt.Point>();
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
    public ArrayList<Segment> findClosestPair(ArrayList<java.awt.Point> PP, ArrayList<java.awt.Point> QQ) {
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
            ArrayList<java.awt.Point> XL = new ArrayList<java.awt.Point>();
            ArrayList<java.awt.Point> XR = new ArrayList<java.awt.Point>();

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
            ArrayList<java.awt.Point> YL = new ArrayList<java.awt.Point>();
            ArrayList<java.awt.Point> YR = new ArrayList<java.awt.Point>();
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
            ArrayList<java.awt.Point> Ymid = new ArrayList<java.awt.Point>();
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

    private double distance(java.awt.Point a, Point b) {
        double l = Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
        return l;
    }
}
