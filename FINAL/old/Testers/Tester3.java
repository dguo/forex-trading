import java.util.*;
import java.math.*;

public class Tester3 {
  public static void main(String[] args) {
    
    In data = new In(args[0]);
    int counter = 0;
    while (!data.isEmpty()) {
      String oneLine = data.readLine();
      counter++;
    }
    data = new In(args[0]);
    double[] bids = new double[counter];
    
    for (int i = 0; i < counter - 1; i++) {
      String oneLine = data.readLine();
      String[] split = oneLine.split(",");
      bids[i] = Double.parseDouble(split[1]) * 10000.0;
    }
    
    int gainCounter = 0;
    int lossCounter = 0;
    int totalCounter = 0;
    int neitherCounter = 0;
    double netChange = 0.0;
    
    for (int x = 2000; x < counter - 2000; x = x + 1440) {
      
      double basePrice = bids[x];
      boolean gain = false;
      boolean loss = false;
      if (basePrice > bids[x - 700]) System.out.println("A");
      else System.out.println("B");
      for (int y = 0; y < 1440; y++) {
        
        if ((bids[x+y] - basePrice) > 50.0 && !gain) {
          gainCounter++;
          gain = true;
        }
        if ((basePrice - bids[x+y]) > 50.0 && !loss) {
          lossCounter++;
          loss = true;
        }
        
      }
      
      if (!gain) {
        netChange = netChange + (bids[x+1440] - bids[x]);
      }
      
      if (!gain && !loss) neitherCounter++;
   //   if (gain) System.out.println(1);
   //   else System.out.println(0);
      totalCounter++;
    }
    
    System.out.println("Net Change: " + netChange + " pips");
    System.out.println("Gain: " + gainCounter);
    System.out.println("Loss: " + lossCounter);
    System.out.println("Total: " + totalCounter);
    System.out.println("Neither: " + neitherCounter);
  }
}