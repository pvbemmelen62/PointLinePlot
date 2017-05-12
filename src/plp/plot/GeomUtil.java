package plp.plot;

import java.awt.geom.*;

public class GeomUtil {

  public static Rectangle2D.Double translateRect(Rectangle2D rect,
      java.awt.geom.Point2D.Double q) {
    Rectangle2D r = rect;
    Rectangle2D.Double rt = 
      new Rectangle2D.Double(
          r.getX()+q.x, r.getY()+q.y, r.getWidth(), r.getHeight());
    return rt;
  }

}
