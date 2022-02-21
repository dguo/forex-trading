/* Danny Guo
 * 9/25/11
 * Java RecordUpdater
 * Javac RecordUpdater.java
 * RecordUpdate updates record.csv. */

import java.io.*;

public class RecordUpdater {
  public static void main(String[] args) throws IOException {
    
    int numberOfPairs = 53;
    
    // read in the two pages of information
    In prices = new In("prices.csv"); 
    In record = new In("record.csv"); 
    
    // create arrays to hold the data
    String[] tickers = new String[numberOfPairs];
    double[] newRates = new double[numberOfPairs];
    double[] oldRates = new double[numberOfPairs];
    int[] streaks = new int[numberOfPairs];
    double[] percentDeltas = new double[numberOfPairs];
    
    // put the data into the arrays
    record.readLine();
    prices.readLine();
    for (int a = 0; a < numberOfPairs; a++) {
      String[] split = record.readLine().split(",");
      tickers[a] = split[0];
      oldRates[a] = Double.parseDouble(split[1]);
      streaks[a] = Integer.parseInt(split[2]);
      percentDeltas[a] = Double.parseDouble(split[3]);
      String[] split2 = prices.readLine().split(",");
      newRates[a] = Double.parseDouble(split2[5]);
    }
    
    // write to oldRecord.csv
    Writer output = null;
    File file = new File("oldRecord.csv");
    output = new BufferedWriter(new FileWriter(file));
    output.write("pair,price,streak,percent deltas");
    output.write("\n");
    for (int c = 0; c < numberOfPairs; c++) {
      output.write(tickers[c] + "," + oldRates[c] + "," + Integer.toString(streaks[c]) + "," + percentDeltas[c]);
      output.write("\n");
    }
    output.close();
    
    // update streaks and percent deltas
    for (int b = 0; b < numberOfPairs; b++) {
      if (streaks[b] == 0) {
        if (newRates[b] < oldRates[b]) {
          streaks[b] = -1;
          percentDeltas[b] = (oldRates[b] - newRates[b]) * (100.0 / oldRates[b]);
        }
        if (oldRates[b] < newRates[b]) {
          streaks[b] = 1;
          percentDeltas[b] = (newRates[b] - oldRates[b]) * (100.0 / oldRates[b]);
        }
      }
      else {
        if ((streaks[b] > 0 && newRates[b] < oldRates[b]) || (streaks[b] < 0 && newRates[b] > oldRates[b])) {
          if (newRates[b] < oldRates[b]) {
            streaks[b] = -1;
            percentDeltas[b] = (oldRates[b] - newRates[b]) * (100.0 / oldRates[b]);
          }
          if (newRates[b] > oldRates[b]) {
            streaks[b] = 1;
            percentDeltas[b] = (newRates[b] - oldRates[b]) * (100.0 / oldRates[b]);
          }
        }
        else {
          if (streaks[b] > 0 && newRates[b] > oldRates[b]) {
            streaks[b]++;
            percentDeltas[b] = percentDeltas[b] + ((newRates[b] - oldRates[b]) * (100.0 / oldRates[b]));
          }
          if (streaks[b] < 0 && newRates[b] < oldRates[b]) {
            streaks[b]--;
            percentDeltas[b] = percentDeltas[b] + ((oldRates[b] - newRates[b]) * (100.0 / oldRates[b]));
          }
        }
      }
    }
    
    // write to record.csv
    Writer output2 = null;
    File file2 = new File("record.csv");
    output2 = new BufferedWriter(new FileWriter(file2));
    output2.write("pair,price,streak,percent deltas");
    output2.write("\n");
    for (int d = 0; d < numberOfPairs; d++) {
      output2.write(tickers[d] + "," + newRates[d] + "," + Integer.toString(streaks[d]) + "," + percentDeltas[d]);
      output2.write("\n");
    }
    output2.close();
    
    // print out success line
    System.out.println("Success");
  }
}