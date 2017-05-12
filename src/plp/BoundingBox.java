package plp;

public class BoundingBox {

  public double minX;
  public double maxX;
  public double minY;
  public double maxY;
  
  public static BoundingBox fromPoints(Point[] points) {
    double minX = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;
    for(Point p : points) {
      minX = Math.min(minX, p.x);
      maxX = Math.max(maxX, p.x);
      minY = Math.min(minY, p.y);
      maxY = Math.max(maxY, p.y);
    }
    return new BoundingBox(minX, maxX, minY, maxY);
  }
  public static BoundingBox union(BoundingBox bb0, BoundingBox bb1) {
    BoundingBox bb = new BoundingBox(
        Math.min(bb0.minX, bb1.minX),
        Math.max(bb0.maxX, bb1.maxX),
        Math.min(bb0.minY, bb1.minY),
        Math.max(bb0.maxY, bb1.maxY)
    );
    return bb;
  }
  public BoundingBox(double minX, double maxX, double minY, double maxY) {
    this.minX = minX;
    this.maxX = maxX;
    this.minY = minY;
    this.maxY = maxY;
  }
  /** Returns ((minX + maxX)/2, (minY+maxY)/2). */
  public Point center() {
    return new Point((minX + maxX)/2, (minY+maxY)/2); 
  }
  public double width() {
    return maxX - minX;
  }
  public double height() {
    return maxY - minY;
  }
  public double surface() {
    return width()*height();
  }
}
