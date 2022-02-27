import java.util.*;
import java.math.*;

public class IndicatorTester {
  public static void main(String[] args) {
    
    int counter = 0;
    
    int year = 2008;
    int limit = 2011;
    
    for (int a = year; a < limit; a++) {
      In data = new In(a + ".csv");
      while (!data.isEmpty()) {
        String oneLine = data.readLine();
        counter++;
      }
    }
    
    String[] times = new String[counter];
    double[] bids = new double[counter];
    double[] asks = new double[counter];
    
    int i = 0;
    for (int b = year; b < limit; b++) {
      In data = new In(b + ".csv");
      while (!data.isEmpty()) {
        String oneLine = data.readLine();
        String[] split = oneLine.split(",");
        times[i] = split[0].substring(0, 14);
        bids[i] = Double.parseDouble(split[1]) * 10000.0;
        asks[i] = Double.parseDouble(split[2]) * 10000.0;
        i++;
      }
    }
    
    String timeToMatch = args[0] + " " + args[1];
    boolean goLong = true;
    if (args[2].equals("1")) goLong = false;
    
    for (int x = 1; x < counter - 1; x++) {
      if (times[x].equals(timeToMatch)) {
        boolean stop = false;
        for (int y = 0; y < 300; y++) {
          if (!stop && goLong && bids[x+y] - bids[x] > 50.0) {
            System.out.println("success");
            stop = true; 
          }
          if (!stop && !goLong && bids[x] - bids[x+y] > 50.0) {
            System.out.println("success");
            stop = true;
          }
        }  
        if (goLong) System.out.println(bids[x+300] - bids[x]);
        if (!goLong) System.out.println(bids[x] - bids[x+300]);
      }
    }
    
    System.out.println("done");
  }
}