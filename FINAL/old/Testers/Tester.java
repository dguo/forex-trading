import java.util.*;
import java.math.*;

public class Tester {
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

    double netChange = 0;
    double maxGain = 0;
    double maxLoss = 0;
    double[] dailyChanges = new double[counter];
    int switches = 0;
    
    int min = 1;
    
    
    for (int x = (3 * min); x < counter - 500; x = x + min) {
      
    //  double changeInPips = Math.abs(bids[x] - bids[x-min]);
      double changeInPips = 0.0;
      if ((bids[x] - bids[x - min] > 20.0)) {
        changeInPips = bids[x + 30] - bids[x];
        x = x + 30;
      }
      if ((bids[x] - bids[x - min] < 20.0)) {
        changeInPips = bids[x] - bids[x + 30];
        x = x + 30;
      }
      
      /*
      if (bids[x] > bids[x-min] && bids[x-min] > bids[x-(2 * min)]) {
        changeInPips = changeInPips * (-1.0);
        switches++;
      }
      if (bids[x] < bids[x-min] && bids[x-min] < bids[x-(2 * min)]) {
        changeInPips = changeInPips * (-1.0);
        switches++;
      }  */
      
      double delta = changeInPips;
  //    dailyChanges[x] = delta;
      netChange = netChange + delta;
      if (delta < maxLoss) maxLoss = delta;
      if (delta > maxGain) maxGain = delta;
      
    }
    
    System.out.println("Net Change: " + netChange + " pips");
    System.out.println("Biggest Gain: " + maxGain + " pips");
    System.out.println("Biggest Loss: " + maxLoss + " pips");
    System.out.println("Switches: " + switches);
  //  double averageChange = netChange / (counter);
  //  System.out.println("Average Change: " + averageChange + " pips/day");
  //  Arrays.sort(dailyChanges);
  //  System.out.println("10th percentile: " + dailyChanges[(counter)/10] + " pips");
  //  System.out.println("25th percentile: " + dailyChanges[(counter)/4] + " pips");
  //  System.out.println("Median Change: " + dailyChanges[(counter)/2] + " pips");
 //  System.out.println("75th percentile: " + dailyChanges[3 * (counter) / 4] + " pips");
   // System.out.println("10th percentile: " + dailyChanges[9 * (counter) / 10] + " pips");
  }
}