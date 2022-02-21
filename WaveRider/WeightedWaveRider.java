import java.io.*;

public class WeightedWaveRider {
  public static void main(String[] args) throws IOException {
    
    int numberOfPairs = 13;
    double percentIn = 0.50;
    double accountEquity = Double.parseDouble(args[0]);
    
    double AUDtoBuck = 1.08292;
    double CADtoBuck = 1.0 / 0.95140;
    double CHFtoBuck = 1.0 / 0.81315;
    double EURtoBuck = 1.43914;
    double GBPtoBuck = 1.63010;
    double JPYtoBuck = 1.0 / 78.377;
    double ZARtoBuck = 1.0 / 6.78971;
    
    In oanda = new In("oanda.csv");
    oanda.readLine();
    In record = new In("record.csv");
    record.readLine();
    
    String[] pairs = new String[numberOfPairs];
    double[] oldPositions = new double[numberOfPairs];
    double[] oldPrices = new double[numberOfPairs];
    double[] newPositions = new double[numberOfPairs];
    double[] newPrices = new double[numberOfPairs];
    
    for (int a = 0; a < numberOfPairs; a++) {
      
      String oandaLine = oanda.readLine();
      String[] split = oandaLine.split(",");
      pairs[a] = split[1];
      oldPositions[a] = Double.parseDouble(split[2]);
      if (split[0].equals("Short")) oldPositions[a] = -1.0 * oldPositions[a];
      newPrices[a] = Double.parseDouble(split[5]);
      
      String recordLine = record.readLine();
      String[] split2 = recordLine.split(",");
      oldPrices[a] = Double.parseDouble(split2[1]);
    }
    
    double[] unitsFirst = new double[numberOfPairs];
    for (int b = 0; b < numberOfPairs; b++) {
      double baseToHome = 1.0;
      String base = pairs[b].substring(0, 3);
      if (base.equals("AUD")) baseToHome = AUDtoBuck;
      if (base.equals("CAD")) baseToHome = CADtoBuck;
      if (base.equals("CHF")) baseToHome = CHFtoBuck;
      if (base.equals("EUR")) baseToHome = EURtoBuck;
      if (base.equals("GBP")) baseToHome = GBPtoBuck;
      if (base.equals("JPY")) baseToHome = JPYtoBuck;
      if (base.equals("ZAR")) baseToHome = ZARtoBuck;
      unitsFirst[b] = (100.0 * 50.0) / baseToHome;
    }
    
    double[] pipValuesFirst = new double[numberOfPairs];
    for (int c = 0; c < numberOfPairs; c++) {
      double quoteToHome = 1.0;
      String quote = pairs[c].substring(4, 7);
      if (quote.equals("AUD")) quoteToHome = AUDtoBuck;
      if (quote.equals("CAD")) quoteToHome = CADtoBuck;
      if (quote.equals("CHF")) quoteToHome = CHFtoBuck;
      if (quote.equals("EUR")) quoteToHome = EURtoBuck;
      if (quote.equals("GBP")) quoteToHome = GBPtoBuck;
      if (quote.equals("JPY")) quoteToHome = JPYtoBuck;
      if (quote.equals("ZAR")) quoteToHome = ZARtoBuck;
      double difference = 0.0001;
      if (quote.equals("JPY")) difference = 0.01;
      pipValuesFirst[c] = unitsFirst[c] * quoteToHome * difference;
    }
    
    double sumPipValues = 0.0;
    for (int d = 0; d < numberOfPairs; d++) {
      sumPipValues = sumPipValues + pipValuesFirst[d];
    }
    double onePipAverageValue = sumPipValues / numberOfPairs;
    
    double[] marginsSecond = new double[numberOfPairs];
    for (int e = 0; e < numberOfPairs; e++) {
      double ratio = onePipAverageValue / pipValuesFirst[e];
      marginsSecond[e] = ratio * 100.0;
    }
    
    double sumMargins = 0.0;
    for (int f = 0; f < numberOfPairs; f++) {
      sumMargins = sumMargins + marginsSecond[f];
    }
    
    double lastRatio = (percentIn * accountEquity) / sumMargins;
    
    double[] marginsThird = new double[numberOfPairs];
    for (int g = 0; g < numberOfPairs; g++) {
      marginsThird[g] = lastRatio * marginsSecond[g];
    }
    
    double[] absPipChanges = new double[numberOfPairs];
    double sumPipChanges = 0.0;
    for (int m = 0; m < numberOfPairs; m++) {
     absPipChanges[m] = Math.abs(oldPrices[m] - newPrices[m]);
     String quote = pairs[m].substring(4, 7);
     if (quote.equals("JPY")) absPipChanges[m] = absPipChanges[m] * 100.0;
     else absPipChanges[m] = absPipChanges[m] * 10000.0;
     sumPipChanges = sumPipChanges + absPipChanges[m];
    }
    
    double averagePipChange = sumPipChanges / numberOfPairs;
    
    for (int n = 0; n < numberOfPairs; n++) {
     marginsThird[n] = marginsThird[n] * (absPipChanges[n] / averagePipChange); 
    }
    
    for (int h = 0; h < numberOfPairs; h++) {
      double baseToHome = 1.0;
      String base = pairs[h].substring(0, 3);
      if (base.equals("AUD")) baseToHome = AUDtoBuck;
      if (base.equals("CAD")) baseToHome = CADtoBuck;
      if (base.equals("CHF")) baseToHome = CHFtoBuck;
      if (base.equals("EUR")) baseToHome = EURtoBuck;
      if (base.equals("GBP")) baseToHome = GBPtoBuck;
      if (base.equals("JPY")) baseToHome = JPYtoBuck;
      if (base.equals("ZAR")) baseToHome = ZARtoBuck;
      newPositions[h] = (marginsThird[h] * 50.0) / baseToHome;
      if (oldPrices[h] > newPrices[h]) newPositions[h] = -1.0 * newPositions[h];
      newPositions[h] = Math.rint(newPositions[h] / 1000.0) * 1000.0;
    }
    
    double[] ordersToPlace = new double[numberOfPairs];
    for (int i = 0; i < numberOfPairs; i++) {
      ordersToPlace[i] = newPositions[i] - oldPositions[i];
    }
    
    Writer output = null;
    File file = new File("ordersToPlace.csv");
    output = new BufferedWriter(new FileWriter(file));
    for (int j = 0; j < numberOfPairs; j++) {
      String oneLine = pairs[j] + "," + Double.toString(ordersToPlace[j]);
      output.write(oneLine);
      output.write("\n");
    }
    output.close();
    
    Writer output2 = null;
    File file2 = new File("record.csv");
    output2 = new BufferedWriter(new FileWriter(file2));
    output2.write("Old prices, New prices");
    output2.write("\n");
    for (int k = 0; k < numberOfPairs; k++) {
      output2.write(Double.toString(oldPrices[k]) + "," + Double.toString(newPrices[k]));
      output2.write("\n");
    }
    output2.close();
    
  }
}