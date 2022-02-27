import java.util.*;
import java.math.*;

public class USDJPYtester30 {
  public static void main(String[] args) {
    
    int counter = 0;
    
    int year = 2011;
    int limit = 2012;
    int streak = 2;
    
    for (int a = year; a < limit; a++) {
      In data = new In(a + ".csv");
      while (!data.isEmpty()) {
        String oneLine = data.readLine();
        counter++;
      }
    }
    
    double[] bids = new double[counter];
    double[] asks = new double[counter];
    
    int i = 0;
    for (int b = year; b < limit; b++) {
      In data = new In(b + ".csv");
      while (!data.isEmpty()) {
        String oneLine = data.readLine();
        String[] split = oneLine.split(",");
        bids[i] = Double.parseDouble(split[1]) * 100.0;
        asks[i] = Double.parseDouble(split[2]) * 100.0;
        i++;
      }
    }
    
    double netChange = 0.0;
    int counter2 = 0;
    
    for (int x = 380; x < counter - 100; x = x + 60) {
      
      if (bids[x] > bids[x-60] && bids[x-60] > bids[x-120] && bids[x-120] > bids[x-180]) {
        netChange = netChange + bids[x] - asks[x+60];
        counter2++;
      }

      
    }
    
    System.out.println("net: " + netChange);
    System.out.println("counter: " + counter2);
  }
}