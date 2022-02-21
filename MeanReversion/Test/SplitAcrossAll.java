/*
 * Short losers, buy winners, exit next day
 * 
 * */

public class SplitAcrossAll {
  public static void main(String[] args) {
    
    In data = new In(args[0]);
    data.readLine();
    In copy = data;
    
    int length = 0;
    int width = 0;
    
    while (!copy.isEmpty()) {
      String oneLine = copy.readLine();
      if (length == 0) {
        String[] split = oneLine.split(",");
        width = split.length - 1;
      }
      length++;
    }
    
    data = new In(args[0]);
    data.readLine();
    
    int[][] deltas = new int[length][width];
    
    for (int i = 0; i < length - 1; i++) {
      String oneLine = data.readLine();
      String[] split = oneLine.split(",");
      for (int j = 1; j <= width; j++) {
        deltas[i][j-1] = Integer.parseInt(split[j]); 
      }
    }
    
    int netChange = 0;
    int maxGain = 0;
    int maxLoss = 0;
    
    boolean reenter = true;
    int currentIndex = 0;
    boolean isLong = true;
    int tradeChange = 0;
    
    for (int x = length - 1; x > 0; x--) {
      
      if (x < length - 1) reenter = false;
      int todayChange = deltas[x][currentIndex];
      tradeChange = tradeChange + todayChange;
      
      if (isLong && todayChange > 0) {
        netChange = netChange + tradeChange;
        if (tradeChange > maxGain) maxGain = tradeChange;
        if (tradeChange < maxLoss) maxLoss = tradeChange;
        reenter = true;
        tradeChange = 0;
      }
      if (!isLong && todayChange < 0) {
        tradeChange = tradeChange * -1;
        netChange = netChange + tradeChange;
        if (tradeChange > maxGain) maxGain = tradeChange;
        if (tradeChange < maxLoss) maxLoss = tradeChange;
        reenter = true;
        tradeChange = 0;
      }
      
      if (reenter) {
        int winnerIndex = 0;
        int days = 0;
        int totalChange = 0;
        for  (int y = 0; y < width; y++) {
          int testDays = 1;
          int testChange = deltas[x][y];
          int day = x;
          if (deltas[x][y] > 0) {
            while (day < length && deltas[day][y] > 0) {
              testDays++;
              testChange = testChange + deltas[day][y];
              day++;
            }
          }
          if (deltas[x][y] < 0) {
            while (day < length && deltas[day][y] < 0) {
              testDays++;
              testChange = testChange + deltas[day][y];
              day++;
            }
          }
          if (testChange < 0) testChange = testChange * -1;
          if (testDays > days) {
            winnerIndex = y;
            days = testDays;
            totalChange = testChange;
          }
          if (testDays == days && testChange > totalChange) {
            winnerIndex = y;
            days = testDays;
            totalChange = testChange;
          }
        }
        currentIndex = winnerIndex;
        if (deltas[x][winnerIndex] < 0) isLong = true;
        else isLong = false;
      }
      
    }
    
    System.out.println("Net Change: " + netChange + " pips");
    System.out.println("Biggest Gain: " + maxGain + " pips");
    System.out.println("Biggest Loss: " + maxLoss + " pips");
    double averageChange = netChange / (365.0 * Integer.parseInt(args[1]));
    System.out.println("Average Change: " + averageChange + " pips/day");
    
  }
}