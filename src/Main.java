

import cgcp2.ds.PointsPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main extends JFrame {
    PointsPanel pane;
    JSpinner spinner;

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
        menubar.add(new JButton("Vertices:"));

        spinner = new JSpinner();
        spinner.setValue(10);
        spinner.addChangeListener(e -> System.out.println(e));
        menubar.add(spinner);

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
        Action partition = new PartitionAction();
        Action triangulation = new TriangulationAction();
        Action dual = new DualGraphAction();
        Action threecolor = new ThreeColorAction();

        // Now create a toolbar, add actions to it, and add
        // it to the top of the frame (where it appears
        // underneath the menubar)
        JToolBar toolbar = new JToolBar();
        toolbar.add(random);
        toolbar.add(polygon);
        toolbar.add(trapezoid);
        toolbar.add(partition);
        toolbar.add(triangulation);
        toolbar.add(dual);
        toolbar.add(threecolor);
        toolbar.add(clear);
        toolbar.add(quit);
        contentPane.add(toolbar, BorderLayout.NORTH);
    }

    class RandomPoints extends AbstractAction {

        public RandomPoints() {
            super("Random Points");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int n = Integer.parseInt(spinner.getValue().toString());
            pane.clear();
            pane.initPoints(n);
        }
    }

    class PolygonAction extends AbstractAction {
        public PolygonAction() {
            super("Generate Polygon");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pane.generatePolygon();
        }
    }

    class TrapezoidAction extends AbstractAction {
        public TrapezoidAction() {
            super("Trapezoidalize");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pane.trapezoidalization();
        }
    }

    class PartitionAction extends AbstractAction {
        public PartitionAction() {
            super("Monotone Partition");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pane.monotonePartition();
        }
    }

    class TriangulationAction extends AbstractAction {
        public TriangulationAction() {
            super("Triangulation");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pane.triangulatePartitions();
        }
    }

    class DualGraphAction extends AbstractAction {
        public DualGraphAction() {
            super("Dual Graph");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pane.dualGraph();
        }
    }

    class ThreeColorAction extends AbstractAction {
        public ThreeColorAction() {
            super("Three Coloring");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pane.threecolor();
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

    /* This inner class defines the "clear" action */
    class ClearAction extends AbstractAction {

        public ClearAction() {
            super("Clear");  // Specify the name of the action
        }

        public void actionPerformed(ActionEvent e) {

            pane.clear();

        }
    }
}
