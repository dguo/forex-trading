/* Danny Guo
 * 01/27/11
 * Java Forex
 * Javac Forex.java
 * Forex prints out how far a currency pair
 * swings in one hour (opposite direction as
 * the change from the previous hour). The 
 * program depends on In.java.*/

import java.io.*;

public class Forex {
  public static void main(String[] args) throws IOException {
    
    In rawData = new In(args[0]);
    In copy = new In(args[0]);
    
    int counter = 0;
    while (!copy.isEmpty()) {
      String oneLine = copy.readLine();
      counter++;
    } 

    int[] open = new int[counter];
    int[] low = new int[counter];
    int[] high = new int[counter];
    
    if(rawData.isEmpty()) System.out.println("wtf");
   
    int counterTwo = 0;
    while (!rawData.isEmpty()) {
      String oneLine = rawData.readLine();
      String[] separated = oneLine.split(",");
      open[counterTwo] = Integer.parseInt(separated[0]);
      low[counterTwo] = Integer.parseInt(separated[1]);
      high[counterTwo] = Integer.parseInt(separated[2]);
      counterTwo++;
    }
 
    Writer output = null;
    File file = new File(args[1] +  ".csv");
    output = new BufferedWriter(new FileWriter(file));

    for (int i = 1; i < counter; i++) {
      if (open[i-1] < open[i]) {
        int difference = open[i] - low[i];
        output.write(Integer.toString(difference));
        output.write("\n");
      }
      if (open[i-1] > open[i]) {
        int difference = high[i] - open[i];
        output.write(Integer.toString(difference));
        output.write("\n");
      } 
    }
    
    output.close();
    System.out.println("Success");
  }
}