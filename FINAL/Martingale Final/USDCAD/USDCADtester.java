import java.util.*;
import java.math.*;

public class USDCADtester {
  public static void main(String[] args) {
    
    int counter = 0;
    
    int year = 2004;
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
    
    double lockPrice = asks[100];
    
    int lockDirection = 0;
    int maxStreak = 0;
    int currentStreak = 0;
    
    int oneCounter = 0;
    int twoCounter = 0;
    int threeCounter = 0;
    int fourCounter = 0;
    int fiveCounter = 0;
    int sixCounter = 0;
    int sevenCounter = 0;
    int eightCounter = 0;
    int nineCounter = 0;
    int tenCounter = 0;
    int streakCounter = 0;
    
    for (int x = 100; x < counter - 1; x++) {
      
      double newBid = bids[x];
      double newAsk = asks[x];
      boolean gain = false;
      boolean loss = false;
      
      if (lockDirection == 0) {
        if (newBid - lockPrice > pips) {
          gain = true;
          lockPrice = newAsk; 
        }
        if (lockPrice - newBid > pips) {
          loss = true;
          lockPrice = newBid;
        }
      }
      
      if (lockDirection == 1) {
        if (newAsk - lockPrice > pips) {
          gain = true;
          lockPrice = newAsk; 
        }
        if (lockPrice - newAsk > pips) {
          loss = true;
          lockPrice = newBid;
        }
      }
      
      if (gain) {
        if (lockDirection == 1) currentStreak++;
        if (lockDirection == 0) {
          if (currentStreak == 1) oneCounter++;
          if (currentStreak == 2) twoCounter++;
          if (currentStreak == 3) threeCounter++;
          if (currentStreak == 4) fourCounter++;
          if (currentStreak == 5) fiveCounter++;
          if (currentStreak == 6) sixCounter++;
          if (currentStreak == 7) sevenCounter++;
          if (currentStreak == 8) eightCounter++;
          if (currentStreak == 9) nineCounter++;
          if (currentStreak == 10) tenCounter++;
          currentStreak = 0;
          streakCounter++;
        }
        lockDirection = 0;
      }
      if (loss) {
        if (lockDirection == 0) currentStreak++;
        if (lockDirection == 1) {
          if (currentStreak == 1) oneCounter++;
          if (currentStreak == 2) twoCounter++;
          if (currentStreak == 3) threeCounter++;
          if (currentStreak == 4) fourCounter++;
          if (currentStreak == 5) fiveCounter++;
          if (currentStreak == 6) sixCounter++;
          if (currentStreak == 7) sevenCounter++;
          if (currentStreak == 8) eightCounter++;
          if (currentStreak == 9) nineCounter++;
          if (currentStreak == 10) tenCounter++;
          currentStreak = 0;
          streakCounter++;
        }
        lockDirection = 1;
      }
      
      if (currentStreak > maxStreak) maxStreak = currentStreak;
    }
    
    System.out.println("Max streak: " + maxStreak);
    System.out.println("1: " + oneCounter);
    System.out.println("2: " + twoCounter);
    System.out.println("3: " + threeCounter);
    System.out.println("4: " + fourCounter);
    System.out.println("5: " + fiveCounter);
    System.out.println("6: " + sixCounter);
    System.out.println("7: " + sevenCounter);
    System.out.println("8: " + eightCounter);
    System.out.println("9: " + nineCounter);
    System.out.println("10: " + tenCounter);
    System.out.println("streak: " + streakCounter);
  }
}