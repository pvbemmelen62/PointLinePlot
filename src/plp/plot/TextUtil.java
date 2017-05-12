package plp.plot;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

public class TextUtil {

  /**
   * Center one or two lines of text in a rectangle.
   * @param s1 first line of text
   * @param s2 second line of text; may be null.
   * @param g  Graphics object.
   * @param metrics FontMetrics object.
   * @param x x coordinate of upperleft of rectangle.
   * @param y y coordinate of upperleft of rectangle.
   * @param w width of rectangle.
   * @param h height of rectangle.
   * @see /JavaExamples3/src/je3/graphics/GraphicsSampler.java
   */
  public static void centerText(String s1, String s2, Graphics g,
      FontMetrics metrics, int x, int y, int w, int h) {
    int height = metrics.getHeight();
    int ascent = metrics.getAscent();
    int width1 = 0, width2 = 0, x0 = 0, x1 = 0, y0 = 0, y1 = 0;
    width1 = metrics.stringWidth(s1);
    if (s2 != null)
      width2 = metrics.stringWidth(s2);
    x0 = x + (w - width1) / 2;
    x1 = x + (w - width2) / 2;
    if (s2 == null) {
      y0 = y + (h - height) / 2 + ascent;
    }
    else {
      y0 = y + (h - (int) (height * 2.2)) / 2 + ascent;
      y1 = y0 + (int) (height * 1.2);
    }
    g.drawString(s1, x0, y0);
    if (s2 != null)
      g.drawString(s2, x1, y1);
  }
  public static enum Anchor {
    UL, UC, UR, ML, MR, LL, LM, LR
  }
  /** From base origin (=point in drawString call) to anchor point.
   * @param rect obtained from font.getStringBounds(String, FontRenderContext).
   * @param margin width of space around rect.
   */
  private static Point2D.Double anchorVector(Anchor anchor, Rectangle2D rect,
      double margin) {
    // rect upperleft corresponds to r.getMinX(), r.getMinY().
    Rectangle2D r = rect;
    double m = margin;
    switch(anchor) {
    case UL: return new Point2D.Double(r.getMinX()-m,  r.getMinY()-m);
    case UC: return new Point2D.Double(r.getCenterX(), r.getMinY()-m);
    case UR: return new Point2D.Double(r.getMaxX()+m,  r.getMinY()-m);
    case ML: return new Point2D.Double(r.getMinX()-m,  r.getCenterY());
    case MR: return new Point2D.Double(r.getMaxX()+m,  r.getCenterY());
    case LL: return new Point2D.Double(r.getMinX()-m,  r.getMaxY()+m);
    case LM: return new Point2D.Double(r.getCenterX(), r.getMaxY()+m);
    case LR: return new Point2D.Double(r.getMaxX()+m,  r.getMaxY()+m);
    default: throw new IllegalStateException();
    }
  }
  /** Draw string such that specified anchor point is at point p .
   */
  public static void drawAnchoredText(String s, Point2D.Double p, Graphics2D g2,
      FontMetrics metrics, Anchor anchor, Color background) {

    Font font = g2.getFont();
    FontRenderContext frc = g2.getFontRenderContext();
    Rectangle2D rect = font.getStringBounds(s, frc);
    // rect.x,rect.y are upperleft of bounds, and rect.y < 0
    // draw at q -> anchor at q + vector(anchor)
    // p = q + vector(anchor) -> q = p - vector(anchor)
    double margin = 5;
    Point2D.Double v = anchorVector(anchor, rect, margin);
    Point2D.Double q = new Point2D.Double(p.x - v.x, p.y - v.y);
    if(background!=null) {
      Color colorOld = g2.getColor();
      g2.setColor(background);
      Rectangle2D.Double bgRect = GeomUtil.translateRect(rect, q);
      g2.fill(bgRect);
      g2.setColor(colorOld);
    }
    g2.drawString(s, (float)q.x, (float)q.y);
  }
}
