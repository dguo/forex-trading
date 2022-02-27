import java.util.*;
import java.math.*;

public class Tester4 {
  public static void main(String[] args) {
    
    for (int a = 2004; a < 2012; a++) {
    
    In data = new In(a + ".csv");
    int counter = 0;
    while (!data.isEmpty()) {
      String oneLine = data.readLine();
      counter++;
    }
    data = new In(a + ".csv");
    double[] bids = new double[counter];
    double[] asks = new double[counter];
    
    for (int i = 0; i < counter - 1; i++) {
      String oneLine = data.readLine();
      String[] split = oneLine.split(",");
      bids[i] = Double.parseDouble(split[1]) * 10000.0;
      asks[i] = Double.parseDouble(split[2]) * 10000.0;
    }
    
    double netChange = 0.0;
    double entryPrice = 0.0;
    boolean isLong = true;
    boolean inAPosition = false;
    int count = 0;
    
    
 
    for (int x = 2000; x < counter - 2000; x++) {
      
      if (!inAPosition && (bids[x] - bids[x-10]) > 0.0 && (asks[x] - bids[x]) < 2.0) {
        isLong = true;
        inAPosition = true;
        entryPrice = asks[x];
      }
      if (!inAPosition && (bids[x-10] - bids[x]) > 0.0 && (asks[x] - bids[x]) < 2.0) {
        isLong = false;
        inAPosition = true;
        entryPrice = bids[x];
      }
      if (inAPosition && isLong && (bids[x] - entryPrice) > 70.0) {
        netChange = netChange + 70.0;
        inAPosition = false;
        count++;
      }
      if (inAPosition && isLong && (entryPrice - bids[x]) > 30.0) {
        netChange = netChange - 30.0;
        inAPosition = false;
        count++;
      }
      if (inAPosition && !isLong && (entryPrice - asks[x]) > 70.0) {
        netChange = netChange + 70.0;
        inAPosition = false;
        count++;
      }
      if (inAPosition && !isLong && (asks[x] - entryPrice) > 30.0) {
        netChange = netChange - 30.0;
        inAPosition = false;
        count++;
      }
      
    }
    System.out.println(a + ":");
    System.out.println("Netchange: " + netChange);
    System.out.println("Count: " + count);
    }
  }
}