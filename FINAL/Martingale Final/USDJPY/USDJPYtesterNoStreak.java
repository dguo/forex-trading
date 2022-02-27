import java.util.*;
import java.math.*;

public class USDJPYtesterNoStreak {
  public static void main(String[] args) {
    
    int counter = 0;
    
    int year = 2011;
    int limit = 2012;
    double pips = 50.0;
    
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
    
    double lockPrice = (asks[100] + bids[100]) / 2.0;
    
    int lockDirection = 0;
    int maxStreak = 0;
    int currentStreak = 0;
    
    int rangeCounter = 0;
    int trendCounter = 0;
    
    for (int x = 100; x < counter - 1; x++) {
      
      double newPrice = (bids[x] + asks[x]) / 2.0;
      boolean gain = false;
      boolean loss = false;
      
      if (lockDirection == 0) {
        if (newPrice - lockPrice > pips) {
          gain = true;
          lockPrice = newPrice;
          trendCounter++;
        }
        if (lockPrice - newPrice > pips) {
          loss = true;
          lockPrice = newPrice;
          rangeCounter++;
          lockDirection = 1;
        }
      }
      
      if (lockDirection == 1) {
        if (newPrice - lockPrice > pips) {
          gain = true;
          lockPrice = newPrice; 
          rangeCounter++;
          lockDirection = 0;
        }
        if (lockPrice - newPrice > pips) {
          loss = true;
          lockPrice = newPrice;
          trendCounter++;
        }
      }
      
      
    }
    
    System.out.println("trend: " + trendCounter);
    System.out.println("range: " + rangeCounter);
  }
}