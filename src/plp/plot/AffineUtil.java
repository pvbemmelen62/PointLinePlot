package plp.plot;

import java.awt.geom.*;
import java.util.*;

public class AffineUtil {

  /**
   * Returns a transform that maps rectangle in to rectangle out.
   * @param preserveAspect If true will preserve aspect ratio.
   *   Because one of sx,sy will be "clipped", some space will result
   *   around transform of "in"; the transform of "in" will be centered
   *   in "out".
   */
  public static AffineTransform getScaleTranslateInstance(
      Rectangle2D.Double in, Rectangle2D.Double out, boolean preserveAspect) {

    AffineTransform t = new AffineTransform();

    if(preserveAspect) {
      out = createRectangleWithAspect(out, Math.abs(in.width/in.height));
    }
    t.translate(out.x, out.y);
    t.scale(out.width/in.width, out.height/in.height);
    t.translate(-in.x,-in.y);

    return t;
  }
  /**
   *  Returns largest rectangle with specified aspect, entirely in rect.
   *  Aspect is defined as "width/height".
   */
  private static Rectangle2D.Double createRectangleWithAspect(
      Rectangle2D.Double in, double aspect) {

    double sxAbs = Math.abs(in.width/aspect);
    double syAbs = Math.abs(in.height/1.0);
    double sAbs = Math.min(sxAbs, syAbs);

    Rectangle2D.Double r = new Rectangle2D.Double();
    r.width = in.width*(sAbs/sxAbs);
    r.x = in.x + (in.width - r.width)/2.0;
    r.height = in.height*(sAbs/syAbs);
    r.y = in.y + (in.height - r.height)/2.0;

    return r;
  }

  /**
   * Transform that maps "from rectangle" given by pF0,pF1 to
   * "to rectangle" given by pT0, pT1 .
   * @param pF0 One corner of "from rectangle" .
   * @param pT0 Image of pF0 .
   * @param pF1 Corner of "from rectangle" opposite to pF0.
   * @param pT1 Image of pF1 .
   * @return transform that maps "from rectangle" to "to rectangle" and
   * pF0 to pT0 and pF1 to pT1.
   */
  public static AffineTransform getScaleTranslateInstance(
      Point2D.Double pF0, Point2D.Double pT0,
      Point2D.Double pF1, Point2D.Double pT1) {
    // pF0 = (xf0,yf0)         -> pT0 = (xt0,yt0)
    // pF1 = (xf0+wf,yf0+hf)   -> pT1 = (xt0+wt,yt0+ht)
    double wf = pF1.x-pF0.x;
    double hf = pF1.y-pF0.y;
    double wt = pT1.x-pT0.x;
    double ht = pT1.y-pT0.y;
    Rectangle2D.Double rf = new Rectangle2D.Double(pF0.x, pF0.y, wf, hf);
    Rectangle2D.Double rt = new Rectangle2D.Double(pT0.x, pT0.y, wt, ht);
    AffineTransform t = getScaleTranslateInstance(rf, rt);
    return t;
  }
  /**
   * Gets transform that maps <code>in</code> to <code>out</code> without
   * preservation of aspect ratio.
   */
  public static AffineTransform getScaleTranslateInstance(
      Rectangle2D.Double in, Rectangle2D.Double out) {
    return getScaleTranslateInstance(in,out,false);
  }

  public static class Test0 {
    public static void main(String[] args) {
      double x0 = 100.0;
      double y0 = 40.0;
      double w0 = 30000.0;
      double h0 = 200.0;
      Rectangle2D.Double from = new Rectangle2D.Double(x0, y0, w0, h0);
      Rectangle2D.Double to = new Rectangle2D.Double(200, 600, 400, -400);
      AffineTransform transform =
        getScaleTranslateInstance(from, to, false);
      double coords[][] = {
          { x0,    y0    },
          { x0+w0, y0    },
          { x0+w0, y0+h0 },
          { x0,    y0+h0 }
      };
      Point2D pIn = null;
      Point2D pOut = null;
      System.out.println("from " + from + " to " + to + " :" );
      System.out.println("");
      for(int i=0; i<coords.length; ++i) {
        pIn = new Point2D.Double(coords[i][0], coords[i][1]);
        pOut = transform.transform(pIn, null);
        System.out.println( pIn + " --> " + pOut);
      }
    }
  }
  public static class Test1 {
    public static void main(String[] args) {
      Point2D.Double pf0 = new Point2D.Double(0.0, 1.3637);
      Point2D.Double pf1 = new Point2D.Double(599.0, 1.3652);
      Point2D.Double pt0 = new Point2D.Double(0.0, 199.0);
      Point2D.Double pt1 = new Point2D.Double(299.0, 0.0);
      AffineTransform t = getScaleTranslateInstance(pf0, pt0, pf1, pt1);
      for(Point2D.Double pf : Arrays.asList(pf0,pf1)) {
        Point2D.Double pt = (Point2D.Double)t.transform(pf,
            new Point2D.Double());
        System.out.println("" + pf + " -> " + pt);
      }
    }
  }
}
