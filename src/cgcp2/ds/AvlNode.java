package cgcp2.ds;

class AvlNode {

    //Voronoi
    private Event circleEvent = null;
    private HalfEdge incidentEdge = null;
    private Point leftParabolaFocus = null;
    private Point rightParabolaFocus = null;
    int side;

    //Tree structure
    private AvlNode leftChild = null;
    private AvlNode rightChild = null;
    private AvlNode parent = null;


    AvlNode(Point l, Point r, int side) {
        this.leftParabolaFocus = l;
        this.rightParabolaFocus = r;
        this.side = side;
    }

    //Data Structure Method
    public boolean isLeaf() {
        return this.getLeftChild() == null && this.getRightChild() == null;
    }

    public boolean hasParent() {
        return this.getParent() != null;
    }

    public boolean isLeftChildofParent() {
        return this.hasParent() && this.getParent().getLeftChild() == this;
    }

    public boolean isRightChildofParent() {
        return this.hasParent() && this.getParent().getRightChild() == this;
    }

    public AvlNode getParent() {
        return parent;
    }

    public AvlNode getLeftChild() {
        return leftChild;
    }

    public AvlNode getRightChild() {
        return rightChild;
    }

    public void setLeftChild(AvlNode n) {
        this.leftChild = n;
    }

    public void setRightChild(AvlNode n) {
        this.rightChild = n;
    }

    public void setParent(AvlNode n) {
        this.parent = n;
    }

    public AvlNode getLeftPredecessor() {
        if (this.isRightChildofParent()) {
            return this.getParent();
        } else if (this.isLeftChildofParent()) {
            AvlNode pointer = this.getParent();
            while (pointer.isLeftChildofParent()) {
                pointer = pointer.getParent();
            }
            return pointer.getParent();
        } else return null;

    }

    public AvlNode getRightPredecessor() {
        if (this.isLeftChildofParent()) {
            return this.getParent();
        } else if (this.isRightChildofParent()) {
            AvlNode pointer = this.getParent();
            while (pointer.isRightChildofParent()) {
                pointer = pointer.getParent();
            }
            return pointer.getParent();
        } else return null;
    }

    public AvlNode getLeftSibling() {
        AvlNode leftPredecessor = this.getLeftPredecessor();
        if (leftPredecessor != null)
            return leftPredecessor.getLeftSuccessor();
        else return null;
    }

    public AvlNode getRightSibling() {
        AvlNode rightPredecessor = this.getRightPredecessor();
        if (rightPredecessor != null)
            return rightPredecessor.getRightSuccessor();
        else return null;
    }

    public AvlNode getLeftSuccessor() {
        return getMostRightLeaf(this.getLeftChild());
    }

    public AvlNode getRightSuccessor() {
        return getMostLeftLeaf(this.getRightChild());
    }

    private AvlNode getMostLeftLeaf(AvlNode node) {
        if (node.isLeaf())
            return node;
        else
            return getMostLeftLeaf(node.getLeftChild());
    }

    private AvlNode getMostRightLeaf(AvlNode node) {
        if (node.isLeaf())
            return node;
        else
            return getMostRightLeaf(node.getRightChild());
    }

    //Voronoi Diagram Method
    public void setAsLeftIntersection() {
        this.side = 0;
    }

    public void setAsRightIntersection() {
        this.side = 1;
    }

    public void setIncidentEdge(HalfEdge h) {
        this.incidentEdge = h;
    }

    public void setCircleEvent(Event e) {
        this.circleEvent = e;
    }

    public Event getCircleEvent() {
        return this.circleEvent;
    }

    public HalfEdge getIncidentEdge() {
        return this.incidentEdge;
    }

    private boolean isLeftIntersection() {
        return side == 0;
    }

    public Point getLeftParabolaFocus() {
        return this.leftParabolaFocus;
    }

    public Point getRightParabolaFocus() {
        return this.rightParabolaFocus;
    }

    public void setLeftParabolaFocus(Point p) {
        this.leftParabolaFocus = p;
    }

    public void setRightParabolaFocus(Point p) {
        this.rightParabolaFocus = p;
    }

    public Point getLocation(double ly) {
        double px, py;
        double qx, qy;
        double x1, x2;

        px = getLeftParabolaFocus().getX();
        py = getLeftParabolaFocus().getY();
        qx = getRightParabolaFocus().getX();
        qy = getRightParabolaFocus().getY();

        if (py == ly) {
            if (py == qy) {
                double x = (px + qx) / 2.0;
                double y = getYParabola(qx, qy, ly, x);
                return new Point(x, y);
            } else {
                double x = px;
                double y = getYParabola(qx, qy, ly, x);
                return new Point(x, y);
            }
        } else if (qy == ly) {
            if (py == qy) {
                double x = (px + qx) / 2;
                double y = getYParabola(px, py, ly, x);
                return new Point(x, y);
            } else {
                double x = qx;
                double y = getYParabola(px, py, ly, x);
                return new Point(x, y);
            }
        } else if (py == qy) {
            double x = (px + qx) / 2.0;
            double y = getYParabola(px, py, ly, x);
            return new Point(x, y);
        } else {
            double a = (0.5 / (py - ly)) - (0.5 / (qy - ly));//(qy - py) / (2 * (py - ly) * (qx - ly));
            double b = -2 * px * (0.5 / (py - ly)) - -2 * qx * (0.5 / (qy - ly));//(py * qx - ly * qx - px * qy + px * ly) / ((qy - ly) * (py - ly));
            double c = (0.5 / (py - ly)) * (px * px + py * py - ly * ly) - (0.5 / (qy - ly)) * (qx * qx + qy * qy - ly * ly);//((px * px + py * py - ly * ly) / (2 * (py - ly))) - ((qx * qx + qy * qy - ly * ly) / (2 * (qy - ly)));
            x1 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
            x2 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);

            if (this.isLeftIntersection()) {
                if (x1 < x2) {
                    double y = getYParabola(px, py, ly, x1);
                    return new Point(x1, y);
                } else {
                    double y = getYParabola(px, py, ly, x2);
                    return new Point(x2, y);
                }
            } else {
                if (x1 > x2) {
                    double y = getYParabola(px, py, ly, x1);
                    return new Point(x1, y);
                } else {
                    double y = getYParabola(px, py, ly, x2);
                    return new Point(x2, y);
                }
            }
        }
    }

    private double getYParabola(double px, double py, double ly, double x) {
        double result = (x * x - 2 * px * x + px * px + py * py - ly * ly) / (2 * (py - ly));
        return result;
    }

    public boolean hasCircleEvent() {
        return getCircleEvent() != null;
    }


}