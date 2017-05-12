package plp.plot;

import static java.awt.event.KeyEvent.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import javax.swing.*;

import plp.*;
import plp.Point;
import plp.plot.TextUtil.*;

@SuppressWarnings("serial")
public class Plotter {
  private static final String nl = System.getProperty("line.separator");
  
  private JFrame frame;
  private Canvas canvas;
  FontMetrics metrics;
  String fileName;
  Point[] points;
  Line[] lines;
  int[] assignments;
  BoundingBox bbox;
  private Font fontPlain;
//  private Font fontBold;
  AffineTransform Tzoom;
  AffineTransform Ttrans;
  boolean showNumbers;
  boolean showLines = true;
  
  public static void usage() {
    String msg =
      "java -cp bin plp.plot.Plotter -file=<fileName> " + nl +
      "where" + nl +
      "  file : contains points and lines"
      + nl;
    System.out.println(msg);
  }
  public static void main(String[] args) {
    // TODO: make sure that changes to points and solution are visible to
    //  event handling thread.
    Plotter plotter = new Plotter();
    for(String arg : args) {
      if (arg.startsWith("-file=")) {
        String fileName = arg.substring("-file=".length());
        plotter.readPointsAndLines(fileName);
      }
    }
    if(plotter.points==null || plotter.lines==null) {
      usage();
      System.exit(1);
    }
    SwingUtilities.invokeLater(plotter.new GUIRunnable());
  }
  public Plotter() {
    assignments = new int[0];
  }
  void setFileName(String fileName) {
    this.fileName = fileName;
  }
  private class GUIRunnable implements Runnable {
    public void run() {
      frame = new JFrame("Plotter " + fileName);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      fontPlain = null;
      metrics = null;
      
      Tzoom = new AffineTransform();
      
      Container cp = frame.getContentPane();
      canvas = new Canvas();
      cp.add(canvas, BorderLayout.CENTER);

      frame.pack();
      frame.setSize(800, 600);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    }
  }
  private class MyMouseWheelListener extends MouseAdapter {
    java.awt.Point pointDown;
    AffineTransform TzoomDown;
    
    public MyMouseWheelListener() {
      pointDown = null;
    }
    public void mouseWheelMoved(MouseWheelEvent e) {
      if(pointDown!=null) {
        return;  // don't try to handle simultaneous zoom and move.
      }
      int notches = e.getWheelRotation();
      double s = Math.pow(1.2, -notches);
      int x = e.getX();
      int y = e.getY();
      Tzoom.preConcatenate(AffineTransform.getTranslateInstance(-x, -y));
      Tzoom.preConcatenate(AffineTransform.getScaleInstance(s, s));
      Tzoom.preConcatenate(AffineTransform.getTranslateInstance(x, y));
      canvas.repaint();
    }
    @Override
    public void mousePressed(MouseEvent e) {
      pointDown = new java.awt.Point(e.getX(), e.getY());
      TzoomDown = new AffineTransform(Tzoom);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
      pointDown = null;
      TzoomDown = null;
    }
    public void mouseDragged(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      int tx = x-pointDown.x;
      int ty = y-pointDown.y;
      AffineTransform Tt = AffineTransform.getTranslateInstance(tx, ty);
      Tzoom = new AffineTransform(TzoomDown);
      Tzoom.preConcatenate(Tt);
      canvas.repaint();
    }
    public void mouseMoved(MouseEvent e) {
    }
    
  }
  private class Canvas extends JPanel {
    
    Canvas() {
      MouseAdapter ma = new MyMouseWheelListener();
      addMouseWheelListener(ma);
      addMouseMotionListener(ma);
      addMouseListener(ma);
      ActionMap acMap = getActionMap();
      InputMap inMap = getInputMap();
      acMap.put("ToggleNumbers", new ToggleNumbersAction());
      inMap.put(KeyStroke.getKeyStroke(VK_N, 0), "ToggleNumbers");
      acMap.put("ToggleLines", new ToggleLinesAction());
      inMap.put(KeyStroke.getKeyStroke(VK_L, 0), "ToggleLines");
      acMap.put("Dispose", new DisposeAction());
      inMap.put(KeyStroke.getKeyStroke(VK_X, 0), "Dispose");
    }

    @Override
    protected void paintComponent(Graphics g) {
      int h = getHeight();
      int w = getWidth();
      Graphics2D g2 = (Graphics2D)g;
      g2.setColor(Color.WHITE);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
          RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      g2.fillRect(0,0,w,h);
      g2.setColor(Color.BLACK);
      int fontSize = 12;
      if(fontPlain==null) {
        fontPlain = new Font("SansSerif", Font.PLAIN, fontSize);
//      fontBold = new Font("SansSerif", Font.BOLD, (int)(fontSize*1.2));
      }
      g2.setFont(fontPlain);
      if(metrics==null) {
        metrics = g2.getFontMetrics(fontPlain);
      }
      if(points==null) {
        return;
      }
      BoundingBox bb = bbox;
      Rectangle2D.Double rectIn = new Rectangle2D.Double(
          bb.minX, bb.minY, bb.width(), bb.height());
      double m = 10; // margin
      Rectangle2D.Double rectOut = new Rectangle2D.Double(
          0+m, 0+m, w-2*m, h-2*m);
      AffineTransform T0 =
          AffineUtil.getScaleTranslateInstance(rectIn, rectOut, true);
      AffineTransform T = new AffineTransform(T0);
      T.preConcatenate(Tzoom);
      Point2D.Double ptDst = new Point2D.Double();
      g2.setColor(Color.BLUE);
      for(Point p : points) {
        transform(T, p, ptDst);
        drawPoint(g2, new Point(ptDst.x, ptDst.y));
      }
      g2.setColor(Color.BLACK);
      if(showLines) {
        Stroke stroke = new BasicStroke(1.0f);
        g2.setStroke(stroke);
        for(int i=0; i<lines.length; ++i) {
          Line line = lines[i];
          Point p0 = points[line.i0];
          Point p1 = points[line.i1];
          transform(T, p0, ptDst);
          Point q0 = new Point(ptDst.x, ptDst.y);
          transform(T, p1, ptDst);
          Point q1 = new Point(ptDst.x, ptDst.y);
          drawLine(g2, q0, q1);
        }
      }
      if(showNumbers) {
        for(int i=0; i<points.length; ++i) {
          Point p = points[i];
          transform(T, p, ptDst);
          String s = null;
          s = ""+i;
          drawText(g2, new Point(ptDst.x, ptDst.y), s);
        }
      }
      drawCornerCoordinates(g2, T, h, w);
    }
  }
  private void drawCornerCoordinates(Graphics2D g2, AffineTransform T,
      int h, int w) {
    Anchor[] anchors = { Anchor.UL, Anchor.LL, Anchor.UR, Anchor.LR };
    AffineTransform Tinv;
    try { Tinv = T.createInverse(); }
    catch (NoninvertibleTransformException e) {
      throw new IllegalStateException(e);
    }
    Point[] g2Corners = { new Point(0,0), new Point(0,h), new Point(w,0),
        new Point(w,h)
    };
    for(int i=0; i<4; ++i) {
      Point2D.Double q = new Point2D.Double();
      transform(Tinv, g2Corners[i], q);
      String s = String.format("%g,%g", q.x, q.y);
      TextUtil.drawAnchoredText(s, g2Corners[i].toPoint2D(), g2, metrics,
          anchors[i], g2.getBackground());
    }
  }
  private void transform(AffineTransform at, Point p, Point2D p2) {
    Point2D ptSrc = new Point2D.Double(p.x, p.y);
    at.transform(ptSrc, p2);
  }
  private void drawPoint(Graphics2D g2, Point p) {
    fillSquare(g2, p, 8);
  }
  private void drawLine(Graphics2D g2, Point p, Point q) {
    g2.drawLine((int)p.x, (int)p.y, (int)q.x, (int)q.y);
  }
  private void drawText(Graphics2D g2, Point p, String text) {
    Anchor anchor = Anchor.ML;
    TextUtil.drawAnchoredText(text, p.toPoint2D(), g2, metrics, anchor, null);
  }
  @SuppressWarnings("unused")
  private void fillCircle(Graphics2D g2, Point p, double radius) {
    int x = (int)(p.x - radius);
    int y = (int)(p.y - radius);
    int width = (int)(2*radius);
    int height = (int)(2*radius);
    int startAngle = 0;
    int arcAngle = 360;
    g2.fillArc(x, y, width, height, startAngle, arcAngle);
  }
  private void fillSquare(Graphics2D g2, Point p, double size) {
    double s2 = size/2;
    int x = (int)(p.x - s2);
    int y = (int)(p.y - s2);
    g2.fillRect(x, y, (int)size, (int)size);
  }
  class ToggleNumbersAction extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
      showNumbers = !showNumbers;
      canvas.repaint();
    }
  }
  class ToggleLinesAction extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
      showLines = !showLines;
      canvas.repaint();
    }
  }
  class DisposeAction extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
      frame.dispose();
    }
  }
  private void readPointsAndLines(String fileName) {
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      PointsAndLines pal = PointsAndLines.fromBufferedReader(br);
      points = pal.points;
      lines = pal.lines;
      bbox = BoundingBox.fromPoints(points);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
