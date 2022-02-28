/* Danny Guo
 * 12/24/10
 * Java ForexTracker
 * Javac ForexTracker.java
 * ForexTracker continuously updates which currency
 * pair to buy or short. The program depends on In.java
 * and StdDraw.java. */

import java.math.BigDecimal;
import java.io.*;
import java.awt.Color;

public class ForexTracker {
  public static void main(String[] args) throws IOException {
    
    while (true) {
      
      // read in the page source
      In page = new In("http://oanda.com/currency/real-time-rates"); 
      String input = page.readAll(); 
      
      // find the day, date, and time
      int start = input.indexOf("date", 0);
      int end = input.indexOf("EST", 0);
      String complete = input.substring(start + 6, end - 1);
      String[] separated = complete.split(",");
      String day = separated[0];
      String date = separated[1] + "," + separated[2];
      String time = separated[3];
      date = date.substring(1);
      time = time.substring(1);
      
      // initialize the arrays for currencies, bids, and asks
      String[] currencies = new String[10];
      BigDecimal[] bids = new BigDecimal[10];
      BigDecimal[] asks = new BigDecimal[10];
      
      // find the currencies, bids, and asks
      int j = 0;
      for (int i = 0; i < 10; i++) {
        int startTwo = input.indexOf("rates_row", j);
        String completeTwo = input.substring(startTwo + 32, startTwo + 140);
        String[] separatedTwo = completeTwo.split("<");
        String pair = separatedTwo[0];
        String bid = separatedTwo[2];
        String ask = separatedTwo[4];
        bid = bid.substring(18);
        ask = ask.substring(18);
        currencies[i] = pair;
        bids[i] = new BigDecimal(bid);
        asks[i] = new BigDecimal(ask);
        j = startTwo + 150;
      }
      
      // find the previous date and time
      In check = new In("check.txt");
      String currentDate = check.readLine();
      String currentTime = check.readLine();
      
      // update check.txt
      Writer output = null;
      File file = new File("check.txt");
      output = new BufferedWriter(new FileWriter(file));
      output.write(date);
      output.write("\n");
      output.write(time);
      output.write("\n");
      output.close();
      
      // check if it is a new day
      boolean newDay = false;
      if (!date.equals(currentDate)) newDay = true;
      
      // check if it is a new time
      boolean newTime = false;
      if (!time.equals(currentTime)) newTime = true;
      
      /*
      // if it is a new day, create a new history
      if (newDay) {
        Writer outputTwo = null;
        File fileTwo = new File(date + "-history.csv");
        outputTwo = new BufferedWriter(new FileWriter(fileTwo));
        String pairs = "pairs:";
        for (int a = 0; a < 10; a++) {
          pairs = pairs + "," + currencies[a];
        }
        outputTwo.write(pairs);
        outputTwo.write("\n");
        String bidAsk = "";
        for (int b = 0; b < 10; b++) {
          bidAsk = bidAsk + "," + bids[b] + "/" + asks[b];
        }
        outputTwo.write(time + bidAsk);
        outputTwo.write("\n");
        outputTwo.close();
      }
      
      // if it is the same day but a new time, update the current day's history
      if (!newDay && newTime) {
        In currentHistory = new In(date + "-history.csv");
        if (currentHistory.isEmpty()) System.out.println("wtf1");
        Writer outputThree = null;
        File fileThree = new File(date + "-history.csv");
        outputThree = new BufferedWriter(new FileWriter(fileThree));
        if (currentHistory.isEmpty()) System.out.println("wtf2");
        while (!currentHistory.isEmpty()) {
          String oneLine = currentHistory.readLine();
          outputThree.write(oneLine);
          outputThree.write("\n");
        }
        String bidAsk = "";
        for (int b = 0; b < 10; b++) {
          bidAsk = bidAsk + "," + bids[b] + "/" + asks[b];
        }
        outputThree.write(time + bidAsk);
        outputThree.write("\n");
        outputThree.close();
      }
      */
      
      // change the bids and asks to whole numbers
      for (int x = 0; x < 10; x++) {
        bids[x] = bids[x].movePointRight(5);
        asks[x] = asks[x].movePointRight(5);
      }
      
      // change the bids to strings and add a leading zero if necessary
      String[] stringBids = new String[10];
      for (int s = 0; s < 10; s++) {
        stringBids[s] = bids[s].toString();
        if ((stringBids[s].length() < 6) || (stringBids[s].length() == 7)) stringBids[s] = "0" + stringBids[s];
      }
      
      // drop unnecessary digits in the bids and change to integers
      int[] intBids = new int[10];
      for (int y = 0; y < 10; y++) {
        stringBids[y] = stringBids[y].substring(0, 5);
        intBids[y] = Integer.parseInt(stringBids[y]);
      }
      
      // update the tally
      if (newTime) {
        
        In tally = new In("tally.txt");
        String firstLine = tally.readLine();
        
        String[] times = new String[12];
        for (int c = 0; c < 12; c++) {
          times[c] = tally.readLine();
        }
        
        String minute = time.substring(3, 5);
        
        int currentIndex = 0;
        for (int d = 0; d < 12; d++) {
          String matchTime = times[d].substring(0, 2);
          if (matchTime.equals(minute)) currentIndex = d;
        }
        
        String[] countersAndTotals = times[currentIndex].split(","); 
        
        int[] counters = new int[10];
        int[] totals = new int[10];
        int[] tallyBids = new int[10];
        
        for (int u = 1; u < 11; u++) {
          String[] onePair = countersAndTotals[u].split("/");
          counters[u-1] = Integer.parseInt(onePair[0]);
          totals[u-1] = Integer.parseInt(onePair[1]);
          tallyBids[u-1] = Integer.parseInt(onePair[2]);
        }
        
        for (int v = 0; v < 10; v++) {
          
          if (tallyBids[v] == intBids[v]) {
            counters[v] = 0;
            totals[v] = 0;
            tallyBids[v] = intBids[v];
          }
          
          if (intBids[v] > tallyBids[v]) {
            if (counters[v] >= 0) {
              counters[v]++;
              int pipIncrease = intBids[v] - tallyBids[v];
              totals[v] = totals[v] + pipIncrease;
              tallyBids[v] = intBids[v];
            }
            if (counters[v] < 0) {
              counters[v] = 1;
              int pipIncrease = intBids[v] - tallyBids[v];
              totals[v] = pipIncrease;
              tallyBids[v] = intBids[v];
            }
          }
          
          if (intBids[v] < tallyBids[v]) {
            if (counters[v] <= 0) {
              counters[v]--;
              int pipIncrease = tallyBids[v] - intBids[v];
              totals[v] = totals[v] + pipIncrease;
              tallyBids[v] = intBids[v];
            }
            if (counters[v] > 0) {
              counters[v] = -1;
              int pipIncrease = tallyBids[v] - intBids[v];
              totals[v] = pipIncrease;
              tallyBids[v] = intBids[v];
            }
          }
          
        }
        
        times[currentIndex] = countersAndTotals[0];
        for (int e = 0; e < 10; e++) {
          times[currentIndex] = times[currentIndex] + "," + counters[e] + "/" + totals[e] + "/" + tallyBids[e];
        }
        
        Writer outputFour = null;
        File fileFour = new File("tally.txt");
        outputFour = new BufferedWriter(new FileWriter(fileFour));
        outputFour.write(firstLine);
        outputFour.write("\n");
        for (int m = 0; m < 12; m++) {
          outputFour.write(times[m]);
          outputFour.write("\n");
        }
        outputFour.close(); 
      } 
      
      // order the currency pairs in tally.txt from best one to worst one
      In tally = new In("tally.txt");
      String firstLine = tally.readLine();
      
      String[] times = new String[12];
      for (int c = 0; c < 12; c++) {
        times[c] = tally.readLine();
      }
      
      String minute = time.substring(3, 5);
      
      int currentIndex = 0;
      for (int d = 0; d < 12; d++) {
        String matchTime = times[d].substring(0, 2);
        if (matchTime.equals(minute)) currentIndex = d;
      }
      
      String[] countersAndTotals = times[currentIndex].split(","); 
      
      int[] counters = new int[10];
      int[] totals = new int[10];
      int[] tallyBids = new int[10];
      
      for (int u = 1; u < 11; u++) {
        String[] onePair = countersAndTotals[u].split("/");
        counters[u-1] = Integer.parseInt(onePair[0]);
        totals[u-1] = Integer.parseInt(onePair[1]);
        tallyBids[u-1] = Integer.parseInt(onePair[2]);
      }
      
      String[] winners = new String[10];
      
      int h = 0;
      while (h < 10) {
        int winnerIndex = -1;
        int winnerCounter = -1;
        int winnerTotal = -1;
        for (int v = 0; v < 10; v++) {
          if (counters[v] != -50) {
            int absValue = counters[v];
            if (absValue < 0) absValue = -1 * absValue;
            if (absValue > winnerCounter) {
              winnerIndex = v;
              winnerCounter = absValue;
              winnerTotal = totals[v];
            }
            if (absValue == winnerCounter && winnerTotal < totals[v]) {
              winnerIndex = v;
              winnerCounter = absValue;
              winnerTotal = totals[v];
            }
          }
        }
        winners[h] = currencies[winnerIndex] + ": " + counters[winnerIndex] + ", " + totals[winnerIndex] + ", " + tallyBids[winnerIndex];
        counters[winnerIndex] = -50;
        h++;
      }
      
      // list the day, date, time, and pairs in descending order in StdDraw
      StdDraw.setXscale(-30, 30);
      StdDraw.setYscale(-50, 50);
      StdDraw.setPenColor(StdDraw.MAGENTA);
      StdDraw.clear();
      String stamp = day + ", " + date + ", " + time;
      StdDraw.text(0, 35, stamp);
      int yPos = 25;
      for (int w = 0; w < 10; w++) {
        StdDraw.text(0, yPos, winners[w]);
        yPos = yPos - 5;
      }
      
      // delay by 4 minutes if it is a new time
      if (newTime) {
        try {
          Thread.sleep(240000); 
        }
        catch (InterruptedException ie) {
          System.out.println(ie.getMessage());
        }
      }
      
      // delay by one second
      try {
        Thread.sleep(1000); 
      }
      catch (InterruptedException ie) {
        System.out.println(ie.getMessage());
      }
      
    }
  }
}