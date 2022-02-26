import java.util.*;

public class Tester4 {
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
    int[][] deltas = new int[length - 1][width];
    for (int i = 0; i < length - 1; i++) {
      String oneLine = data.readLine();
      String[] split = oneLine.split(",");
      for (int j = 0; j < width; j++) {
        deltas[i][j] = Integer.parseInt(split[j+1]); 
      }
    }
    
    double netChange = 0;
    double maxGain = 0;
    double maxLoss = 0;
    int toSubtract = Integer.parseInt(args[1]);
    double[] dailyChanges = new double[length - toSubtract];
    
    for (int x = length - toSubtract; x > 1; x--) {
      
      double todayChange = 0.0;
      int pairsEntered = 0;
      double[] changes = new double[width];
      
      for (int y = 0; y < width; y++) {
        if (deltas[x][y] > 0) {
          changes[y] = 1.0 * deltas[x][y];
          pairsEntered++;
        }
        if (deltas[x][y] < 0) {
          changes[y] = 1.0 * deltas[x][y];
          pairsEntered++;
        }
      }
      
      for (int e = 0; e < width; e++) {
        if (deltas[x][e] > 0) todayChange = todayChange + deltas[x-1][e] - 2.5;
        if (deltas[x][e] < 0) todayChange = todayChange - deltas[x-1][e] - 2.5;
      }
      
      if (pairsEntered > 0) {
        double delta = todayChange / pairsEntered;
        dailyChanges[x-1] = delta;
        netChange = netChange + delta;
        if (delta < maxLoss) maxLoss = delta;
        if (delta > maxGain) maxGain = delta;
      }
      
    }
    
    System.out.println("Net Change: " + netChange + " pips");
    System.out.println("Biggest Gain: " + maxGain + " pips");
    System.out.println("Biggest Loss: " + maxLoss + " pips");
    double averageChange = netChange / (length - toSubtract);
    System.out.println("Average Change: " + averageChange + " pips/day");
    Arrays.sort(dailyChanges);
    System.out.println("10th percentile: " + dailyChanges[(length-toSubtract)/10] + " pips");
    System.out.println("25th percentile: " + dailyChanges[(length-toSubtract)/4] + " pips");
    System.out.println("Median Change: " + dailyChanges[(length-toSubtract)/2] + " pips");
    System.out.println("75th percentile: " + dailyChanges[3 * (length-toSubtract) / 4] + " pips");
    System.out.println("10th percentile: " + dailyChanges[9 * (length-toSubtract) / 10] + " pips");
  }
}