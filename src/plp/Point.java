package plp;

import java.awt.geom.*;

public class Point {

  public double x;
  public double y;
  
  /** Point.
   * @param x double
   * @param y double
   */
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }
  public Point(Point p) {
    this.x = p.x;
    this.y = p.y;
  }
  @Override
  public String toString() {
    return "Point [x=" + x + ", y=" + y + "]";
  }
  public double distance(Point p) {
    Point that = p;
    double rv = Math.sqrt(
        Util.sqr(this.x-that.x) + Util.sqr(this.y-that.y) );
    return rv;
  }
  public double distanceSqr(Point p) {
    Point that = p;
    double rv = Util.sqr(this.x-that.x) + Util.sqr(this.y-that.y);
    return rv;
  }
  public Point2D.Double toPoint2D() {
    return new Point2D.Double(x, y);
  }
}
