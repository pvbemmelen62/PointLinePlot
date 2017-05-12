package plp;

public class Line {

  public int i0;
  public int i1;

  public Line(int i0, int i1) {
    this.i0 = i0;
    this.i1 = i1;
  }
  public Line(Line line) {
    Line that = line;
    this.i0 = that.i0;
    this.i1 = that.i1;
  }
  public String toString() {
    return "Line [i0=" + i0 + ", i1=" + i1 + "]";
  }
}
