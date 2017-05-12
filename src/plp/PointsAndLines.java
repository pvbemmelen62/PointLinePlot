package plp;

import java.io.*;
import java.util.*;

public class PointsAndLines {
  
  public Point[] points;
  public Line[] lines;

  public static PointsAndLines fromBufferedReader(BufferedReader reader)
      throws IOException {
    ArrayList<String> txtLines = new ArrayList<>();
    try {
      String line = null;
      while ((line = reader.readLine()) != null) {
        txtLines.add(line);
      }
    }
    finally {
      reader.close();
    }

    int txtLineNum = 0;
    // parse the data in the file
    String[] words;
    words = txtLines.get(txtLineNum++).trim().split("\\s+");
    Util.myAssert(words.length==1);
    int numPoints = Integer.parseInt(words[0]);
    ArrayList<Point> points = new ArrayList<>(numPoints);
    for(int i=0; i<numPoints; ++i) {
      words = txtLines.get(txtLineNum++).split("\\s+");
      double x = Double.parseDouble(words[0]);
      double y = Double.parseDouble(words[1]);
      Point p = new Point(x,y);
      points.add(p);
    }

    words = txtLines.get(txtLineNum++).trim().split("\\s+");
    Util.myAssert(words.length==1);
    int numLines = Integer.parseInt(words[0]);

    ArrayList<Line> lines = new ArrayList<>(numLines);
    for(int i=0; i<numLines; ++i) {
      words = txtLines.get(txtLineNum++).split("\\s+");
      int i0 = Integer.parseInt(words[0]);
      int i1 = Integer.parseInt(words[1]);
      Line line = new Line(i0,i1);
      lines.add(line);
    }
    Line[] _lines = lines.toArray(new Line[numLines]);
    Point[] _points = points.toArray(new Point[numPoints]);
    PointsAndLines pal = new PointsAndLines(_points, _lines);
    
    return pal;
  }
  public PointsAndLines(Point[] points, Line[] lines) {
    this.points = points;
    this.lines = lines;
  }
}
