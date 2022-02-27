import java.util.*;
import java.math.*;

public class EURUSDStreaks {
  public static void main(String[] args) {
    
    int counter = 0;
    
    int year = 2004;
    int limit = 2012;
    
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
    
    double lockPrice = bids[100];
    
    int maxStreak = 0;
    int streak = 0;
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
    
    for (int x = 100; x < counter - 1500; x = x + 1440) {
      
      if (bids[x] > lockPrice) {
        streak++;
      }
      
      if (Math.abs(streak) > maxStreak) maxStreak = streak;
      
      if (bids[x] < lockPrice && streak > 0) {
        if (streak == 1) oneCounter++;
        if (streak == 2) twoCounter++;
        if (streak == 3) threeCounter++;
        if (streak == 4) fourCounter++;
        if (streak == 5) fiveCounter++;
        if (streak == 6) sixCounter++;
        if (streak == 7) sevenCounter++;
        if (streak == 8) eightCounter++;
        if (streak == 9) nineCounter++;
        if (streak == 10) tenCounter++;
        streak = 0;
      }
      
      lockPrice = bids[x];
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
  }
}