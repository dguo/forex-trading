import java.util.*;
import java.math.*;

public class EURUSDtesterNoStreak {
  public static void main(String[] args) {
    
    int counter = 0;
    
    int year = 2011;
    int limit = 2012;
    double pips = 20.0;
    
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
        bids[i] = Double.parseDouble(split[1]) * 10000.0;
        asks[i] = Double.parseDouble(split[2]) * 10000.0;
        i++;
      }
    }
        
    int successCounter = 0;
    int failCounter = 0;
    
    for (int x = 100; x < counter - 1; x++) {
      
      double newPrice = (asks[x] + bids[x]) / 2.0;
      double oldPrice = (asks[x-10] + bids[x-10]) / 2.0;
      
      if (newPrice > oldPrice + 10) {
        int y = 1;
        boolean hit = false;
        while (!hit && x+y < counter - 2) {
          double futurePrice = (asks[x+y] + bids[x+y]) / 2.0;
          if (futurePrice > newPrice + pips) {
           successCounter++;
           hit = true;
          }
          if (futurePrice < newPrice - pips) {
            failCounter++;
            hit = true;
          }
          y++; 
        }
      }
    }
    
    System.out.println("success: " + successCounter);
    System.out.println("fail: " + failCounter);
  }
}