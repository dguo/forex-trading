import java.util.Arrays;

public class Tester2 {
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
    
    int[] closes = new int[length];
    
    boolean reenter = true;
    int currentIndex = 0;
    boolean isLong = true;
    int tradeChange = 0;
    
    int toSubtract = Integer.parseInt(args[1]);
    
    for (int x = length - toSubtract; x > 0; x--) {
      
      int todayChange = deltas[x][currentIndex];
      if (!isLong) todayChange = todayChange * -1;
      
      netChange = netChange + todayChange;
      if (todayChange > maxGain) maxGain = todayChange;
      if (todayChange < maxLoss) maxLoss = todayChange;
      
      closes[x-1] = todayChange;
      
      if (reenter) {
        int winnerIndex = 0;
        int winnerChange = 0;
        for  (int y = 0; y < width; y++) {
          int testChange = deltas[x][y];
          int compareWinner = winnerChange;
          if (compareWinner < 0) compareWinner = compareWinner * -1;
          int compareTest = testChange;
          if (compareTest < 0) compareTest = compareTest * -1;
          if (compareTest > compareWinner) {
            winnerIndex = y;
            winnerChange = testChange;
          }
        }
        currentIndex = winnerIndex;
        if (winnerChange > 0) isLong = true;
        else isLong = false;
      }
      
    }
    
    System.out.println("Net Change: " + netChange + " pips");
    System.out.println("Biggest Gain: " + maxGain + " pips");
    System.out.println("Biggest Loss: " + maxLoss + " pips");
    double averageChange = netChange / (365.0 * 20);
    System.out.println("Average Change: " + averageChange + " pips/day");
    
    Arrays.sort(closes);
    System.out.println("Median Change: " + closes[(length - toSubtract)/2] + " pips");
    
  }
}