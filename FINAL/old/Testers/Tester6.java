import java.util.*;
import java.math.*;

public class Tester6 {
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
    
    boolean isLong = true;
    boolean inAPosition = false;
    int count = 0;
    double netChange = 0.0;
    double entryPrice = 0.0;
    
    for (int x = 20000; x < counter; x++) {
      
      double longSum = 0.0;
      for (int m = 0; m < 300; m++) {
        longSum = longSum + bids[x-m];
      }
      double longMA = longSum / 300.0;
      
      double shortSum = 0.0;
      for (int n = 0; n < 120; n++) {
       shortSum = shortSum + bids[x-n]; 
      }
      double shortMA = shortSum / 120.0;
      
      if (inAPosition && isLong && shortMA > longMA) {
        inAPosition = false;
        netChange = netChange + bids[x] - entryPrice;
        count++;
      }
      if (inAPosition && !isLong && shortMA < longMA) {
        inAPosition = false;
        netChange = netChange + entryPrice - asks[x];
        count++;
      }
      if (!inAPosition && shortMA > longMA && (asks[x] - bids[x]) < 2.0) {
        inAPosition = true;
        isLong = false;
        entryPrice = bids[x];
      }
      if (!inAPosition && shortMA < longMA && (asks[x] - bids[x]) < 2.0) {
        inAPosition = true;
        isLong = true;
        entryPrice = asks[x];
      }
 
    }
    System.out.println(a + ":");
    System.out.println("Netchange: " + netChange);
    System.out.println("Count: " + count);
    }
  }
}