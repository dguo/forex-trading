/* Danny Guo
 * 01/12/11
 * Java ReboundForex
 * Javac ReboundForex.java
 * ReboundForex tests for the viability of a forex speculation strategy 
 * that depends on taking advantage of rate rebounds to make 
 * gains.Dependencies are In.java, Forex.java, and ForexSlot.java.*/

public class ReboundForex2 {
  public static void main(String[] args) {
    
    int[] start = new int[119];
    int[] end = new int[119];
    
    start[0] = 10;
    start[1] = 497;
    start[2] = 970;
    start[3] = 1489;
    start[4] = 1960;
    start[5] = 2477;
    start[6] = 2971;
    start[7] = 3488;
    start[8] = 4029;
    start[9] = 4497;
    start[10] = 5041;
    start[11] = 5559;
    start[12] = 5983;
    start[13] = 6503;
    start[14] = 6975;
    start[15] = 7468;
    start[16] = 7988;
    start[17] = 8530;
    start[18] = 9002;
    start[19] = 9546;
    start[20] = 10064;
    start[21] = 10560;
    start[22] = 11102;
    start[23] = 11594;
    start[24] = 12089;
    start[25] = 12607;
    start[26] = 13079;
    start[27] = 13576;
    start[28] = 14094;
    start[29] = 14613;
    start[30] = 15109;
    start[31] = 15653;
    start[32] = 16147;
    start[33] = 16667;
    start[34] = 17209;
    start[35] = 17681;
    start[36] = 18190;
    start[37] = 18684;
    start[38] = 19156;
    start[39] = 19700;
    start[40] = 20218;
    start[41] = 20715;
    start[42] = 21239;
    start[43] = 21762;
    start[44] = 22286;
    start[45] = 22810;
    start[46] = 23309;
    start[47] = 23832;
    start[48] = 24379;
    start[49] = 24879;
    start[50] = 25355;
    start[51] = 25903;
    start[52] = 26402;
    start[53] = 26926;
    start[54] = 27450;
    start[55] = 27949;
    start[56] = 28497;
    start[57] = 29020;
    start[58] = 29520;
    start[59] = 30044;
    start[60] = 30567;
    start[61] = 31091;
    start[62] = 31567;
    start[63] = 32114;
    start[64] = 32590;
    start[65] = 33138;
    start[66] = 33661;
    start[67] = 34161;
    start[68] = 34709;
    start[69] = 35208;
    start[70] = 35732;
    start[71] = 36256;
    start[72] = 36732;
    start[73] = 37257;
    start[74] = 37733;
    start[75] = 38256;
    start[76] = 38756;
    start[77] = 39304;
    start[78] = 39803;
    start[79] = 40327;
    start[80] = 40874;
    start[81] = 41350;
    start[82] = 41898;
    start[83] = 42421;
    start[84] = 42894;
    start[85] = 43419;
    start[86] = 43918;
    start[87] = 44418;
    start[88] = 44942;
    start[89] = 45465;
    start[90] = 45965;
    start[91] = 46513;
    start[92] = 47012;
    start[93] = 47536;
    start[94] = 48083;
    start[95] = 48559;
    start[96] = 49077;
    start[97] = 49577;
    start[98] = 50053;
    start[98] = 50577;
    start[99] = 51101;
    start[100] = 51600;
    start[101] = 52124;
    start[102] = 52671;
    start[103] = 53171;
    start[104] = 53695;
    start[105] = 54218;
    start[106] = 54718;
    start[107] = 55235;
    start[108] = 55711;
    start[109] = 56187;
    start[110] = 56735;
    start[111] = 57258;
    start[112] = 57758;
    start[113] = 58258;
    start[114] = 58781;
    start[115] = 59305;
    start[116] = 59829;
    start[117] = 60328;
    start[118] = 60852;
    
    for (int x = 1; x < 118; x++) {
      end[x] = start[x+1] + 1;
    }
    
    end[118] = 61400;
    
    int threshold = Integer.parseInt(args[1]);
    int hours = Integer.parseInt(args[2]);
    
    int[][] data = new int[16][61472];
    
    In rawTable = new In(args[0]);
    
    int counter = 0;
    while (!rawTable.isEmpty()) {
      String oneLine = rawTable.readLine();
      String[] separated = oneLine.split(",");
      for (int a = 0; a < 16; a++) {
        data[a][counter] = Integer.parseInt(separated[a]);
      }
      counter++;
    }
    
    for (int m = 0; m < 119; m++) {
      
      int pipTotal = 0;
      int totalTrades = 0;
      int index = -1;
      boolean up = true;
      
      for (int i = start[m]; i < end[m]; i = i + hours) {
        
        for (int b = 0; b < 16; b++) {
          if (index == b) {
            int change = 0;
            for (int z = 0; z < hours; z++) {
              change = change + data[b][i-z];
            }
            if (up) {
              if (change < 0) {
                pipTotal = pipTotal + (-1*change);
                totalTrades++;
                index = -1;
              }
              else {
                pipTotal = pipTotal - change;
              }
            } 
            else { 
              if (change > 0) {
                pipTotal = pipTotal + change;
                totalTrades++;
                index = -1;
              }
              else {
                pipTotal = pipTotal + change;
              }
            } 
          }
        }
        
        if (index == -1) {
          
          int winner = -1;
          int winnerCounter = 1;
          int pipCounter = 1;
          boolean winnerUp = true;
          
          for (int j = 0; j < 16; j++) {
            
            int tempCounter = 1;
            int tempPipCounter = data[j][i];
            boolean tempWinnerUp = true;
            
            int change = 0;
            for (int y = 0; y < hours; y++) {
              change = change + data[j][i-y];
            }
          
            if (change < 0) {
              int groupsOfHours = 0; 
              tempWinnerUp = false;
              int tempChange = 0;
              do {
                tempChange = 0;
                int goBack = groupsOfHours * hours;
                for (int p = 0; p < hours; p++) {
                  tempChange = tempChange + data[j][i-p-goBack];
                }
                tempCounter++;
                tempPipCounter = tempPipCounter + tempChange;
                groupsOfHours++;
              }
              while (tempChange < 0 && i - (hours*groupsOfHours) > 0); 
            }  
            
            if (change > 0) {
              int groupsOfHours = 0; 
              int tempChange = 0;
              do {
                tempChange = 0;
                int goBack = groupsOfHours * hours;
                for (int n = 0; n < hours; n++) {
                  tempChange = tempChange + data[j][i-n-goBack];
                }
                tempCounter++;
                tempPipCounter = tempPipCounter + tempChange;
                groupsOfHours++;
              }
              while (tempChange > 0 && i - (hours*groupsOfHours) > 0);
            }  
            
            if (tempPipCounter < 0) tempPipCounter = tempPipCounter * -1;
            
            if (tempCounter > winnerCounter && tempCounter > threshold) {
              winner = j;
              winnerCounter = tempCounter;
              pipCounter = tempPipCounter;
              winnerUp = tempWinnerUp;
            }
            
            if (tempCounter == winnerCounter && tempPipCounter > pipCounter && tempCounter > threshold) {
              winner = j;
              pipCounter = tempPipCounter;
              winnerUp = tempWinnerUp;
            }
            
          }
          
          index = winner;
          up = winnerUp;
        }
        
      }
      
      System.out.println(pipTotal + ", " + totalTrades);
    }
    
  }
}