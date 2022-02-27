import java.util.*;
import java.math.*;

public class Tester2 {
  public static void main(String[] args) {
    
    In data = new In(args[0]);
    int counter = 0;
    while (!data.isEmpty()) {
      String oneLine = data.readLine();
      counter++;
    }
    data = new In(args[0]);
    double[] bids = new double[counter];
    double[] asks = new double[counter];
    
    for (int i = 0; i < counter - 1; i++) {
      String oneLine = data.readLine();
      String[] split = oneLine.split(",");
      bids[i] = Double.parseDouble(split[1]) * 10000.0;
      asks[i] = Double.parseDouble(split[2]) * 10000.0;
    }
    
    double netChange = 0;
    boolean isLong = true;
    
    for (int x = 100; x < counter - 2000; x++) {
      
      double basePrice = bids[x-10];
      if (bids[x] < basePrice) isLong = true;
      if (bids[x] > basePrice) isLong = false;
      if (isLong) netChange = netChange + bids[x + 10] - asks[x];
      if (!isLong) netChange = netChange + bids[x] - asks[x + 10];
      
    }
    
    System.out.println("Netchange: " + netChange);
  }
}