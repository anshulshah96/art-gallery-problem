

import cgcp2.ds.PointsPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Main extends JFrame {
    PointsPanel pane;

    public static void main(String[] args) {

        Main app = new Main();

        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        app.pack();
        app.setVisible(true);
    }

    /*
     * This constructor creates the GUI for this application.
     */
    public Main() {
        super("Anshul Shah & Suraj Gupta CP-2");

        // All content of a JFrame (except for the menubar)
        // goes in the Frame's internal "content pane",
        // not in the frame itself.

        Container contentPane = this.getContentPane();

        // Specify a layout manager for the content pane
        contentPane.setLayout(new BorderLayout());

        // Create the main component, give it a border, and
        // a background color, and add it to the content pane
        pane = new PointsPanel();
        pane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        contentPane.add(pane, BorderLayout.CENTER);

        // Create a menubar and add it to this window.
        JMenuBar menubar = new JMenuBar();  // Create a menubar
        this.setJMenuBar(menubar);  // Display it in the JFrame

        // Create menus and add to the menubar
        JMenu filemenu = new JMenu("File");
        menubar.add(filemenu);
		
      /* Create some Action objects for use in the menus 
         and toolbars.
         An Action combines a menu title and/or icon with 
         an ActionListener.
         These Action classes are defined as inner 
         classes below.
      */

        Action clear = new ClearAction();
        Action quit = new QuitAction();
        Action random = new RandomPoints();
        Action polygon = new PolygonAction();
        Action trapezoid = new TrapezoidAction();
//        Action showConvex = new ConvexAction();
//        Action showClosestPair = new ClosestPairAction();
//        Action showArea = new AreaAction();
//        Action showVoronoi = new VoronoiAction();


        // Populate the menus using Action objects
        filemenu.add(clear);
        filemenu.add(quit);

        // Now create a toolbar, add actions to it, and add
        // it to the top of the frame (where it appears
        // underneath the menubar)
        JToolBar toolbar = new JToolBar();
        toolbar.add(clear);
        toolbar.add(quit);
        toolbar.add(random);
        toolbar.add(polygon);
        toolbar.add(trapezoid);
//        toolbar.add(showConvex);
//        toolbar.add(showClosestPair);
//        toolbar.add(showArea);
//        toolbar.add(showVoronoi);
        contentPane.add(toolbar, BorderLayout.NORTH);
    }

    /* This inner class defines the "clear" action */
    class ClearAction extends AbstractAction {

        public ClearAction() {
            super("Clear");  // Specify the name of the action
        }

        public void actionPerformed(ActionEvent e) {

            pane.clear();

        }
    }

    class RandomPoints extends AbstractAction {

        public RandomPoints() {
            super("Random");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int n = 10;
            pane.clear();
            pane.initPoints(n);
        }
    }

    class PolygonAction extends AbstractAction {
        public PolygonAction() {
            super("Polygon");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pane.generatePolygon();
        }
    }

    class TrapezoidAction extends  AbstractAction {
        public TrapezoidAction() {
            super("Trapezoid");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    class ConvexAction extends AbstractAction {

        public ConvexAction() {
            super("Convex");
        }

        public void actionPerformed(ActionEvent e) {
            pane.showConvex();
        }
    }

    class ClosestPairAction extends AbstractAction {

        public ClosestPairAction() {
            super("ClosestPair");
        }

        public void actionPerformed(ActionEvent e) {
            pane.showClosestPair();
        }
    }

    class AreaAction extends AbstractAction {

        public AreaAction() {
            super("Area");
        }

        public void actionPerformed(ActionEvent e) {
            pane.showArea();
        }
    }

    class VoronoiAction extends AbstractAction {

        public VoronoiAction() {
            super("Voronoi");
        }

        public void actionPerformed(ActionEvent e) {
            pane.showVoronoi();
        }
    }

    /* This inner class defines the "quit" action */
    class QuitAction extends AbstractAction {
        public QuitAction() {
            super("Quit");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

}
