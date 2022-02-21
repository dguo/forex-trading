import java.util.*;

public class Optimizer {
  public static void main(String[] args) {
    
    In data = new In("all3.csv");
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
    data = new In("all3.csv");
    data.readLine();
    int[][] deltas = new int[length][width];
    for (int i = 0; i < length; i++) {
      String oneLine = data.readLine();
      String[] split = oneLine.split(",");
      for (int j = 0; j < width; j++) {
        deltas[i][j] = Integer.parseInt(split[j+1]); 
      }
    }
    
    int[] toSubtracts = new int[5];
    toSubtracts[0] = 2;
    toSubtracts[1] = 3000;
    toSubtracts[2] = 5000;
    toSubtracts[3] = 6000;
    toSubtracts[4] = 7000;
    
    for (int aa = 0; aa < width; aa++) {
    //  for (int bb = aa + 1; bb < width; bb++) {
     //   for (int cc = bb + 1; cc < width; cc++) {
        //  for (int dd = cc + 1; dd < width; dd++) {
         //   for (int ee = dd + 1; ee < width; ee++) {
              //for (int ff = ee + 1; ff < width; ff++) {
               // for (int gg = ff + 1; gg < width; gg++) {
                  //for (int hh = gg + 1; hh < width; hh++) {
                 //   for (int ii = hh + 1; ii < width; ii++) {
                      //    for (int jj = ii + 1; jj < width; jj++) {
                      //  for (int kk = jj + 1; kk < width; kk++) {
                      //  for (int ll = kk + 1; ll < width; ll++) {
                      //   for (int mm = ll + 1; mm < width; mm++) {
                      // for (int nn = mm + 1; nn < width; nn++) {
                      //   for (int oo = nn + 1; oo < width; oo++) {
                      //    for (int pp = oo + 1; pp < width; pp++) {
                      //      for (int qq = pp + 1; qq < width; qq++) {
                      //         for (int rr = qq + 1; rr < width; rr++) {
                      //     for (int ss = rr + 1; ss < width; ss++) {
                      //     for (int tt = ss + 1; tt < width; tt++) {
                      //  for (int uu = tt + 1; uu < width; uu++) {
                      // for (int vv = uu + 1; vv < width; vv++) {
                      // for (int ww = vv + 1; ww < width; ww++) {
                      
                      boolean[] invalid = new boolean[23];
                      for (int wtf = 0; wtf < width; wtf++) {
                        invalid[wtf] = true;
                       if (wtf == aa || wtf == aa || wtf == aa) invalid[wtf] = false;
                  //      if (wtf == dd || wtf == ee || wtf == ff) invalid[wtf] = false;
                        //if (wtf == gg || wtf == hh || wtf == ii) invalid[wtf] = false;
                        //   if (wtf == jj || wtf == kk || wtf == ll) invalid[wtf] = false;
                        //   if (wtf == mm || wtf == nn || wtf == oo) invalid[wtf] = false;
                        // if (wtf == pp || wtf == qq || wtf == rr) invalid[wtf] = false;
                        // if (wtf == ss || wtf == tt || wtf == uu) invalid[wtf] = false;
                        //   if (wtf == vv || wtf == ww) invalid[wtf] = false;
                      }
                      
                      for (int d = 2; d < 3; d++) {
                        
                        double netChange = 0;
                        double maxGain = 0;
                        double maxLoss = 0;
                        int toSubtract = toSubtracts[d];
                        double[] dailyChanges = new double[length - toSubtract];
                        
                        for (int x = length - toSubtract; x > 1; x--) {
                          
                          double todayChange = 0.0;
                          int pairsEntered = 0;
                          double[] changes = new double[width];
                          
                          for (int y = 0; y < width; y++) {
                            if (deltas[x][y] > 0 && !invalid[y]) {
                              changes[y] = 1.0 * deltas[x][y];
                              pairsEntered++;
                            }
                            if (deltas[x][y] < 0 && !invalid[y]) {
                              changes[y] = 1.0 * deltas[x][y];
                              pairsEntered++;
                            }
                          }
                          
                          double[] modulusOfChanges = new double[width];
                          for (int z = 0; z < width; z++) {
                            if (!invalid[z]) modulusOfChanges[z] = Math.abs(changes[z]);
                          }
                          double sum = 0.0;
                          for (int m = 0; m < width; m++) {
                            if (!invalid[m]) sum = sum + modulusOfChanges[m];
                          }
                          double average = sum / pairsEntered;
                          
                          double[] ratios = new double[width];
                          for (int n = 0; n < width; n++) {
                            if (!invalid[n]) ratios[n] = modulusOfChanges[n] / average; 
                          }
                          
                          for (int e = 0; e < width; e++) {
                            if (deltas[x][e] > 0) todayChange = todayChange + (ratios[e] * deltas[x-1][e]) - 2.5;
                            if (deltas[x][e] < 0) todayChange = todayChange - (ratios[e] * deltas[x-1][e]) - 2.5;
                          }
                          
                          if (pairsEntered > 0) {
                            double delta = todayChange / pairsEntered;
                            dailyChanges[x-1] = delta;
                            netChange = netChange + delta;
                            if (delta < maxLoss) maxLoss = delta;
                            if (delta > maxGain) maxGain = delta;
                          }
                        }
                        
                        double averageChange = netChange / (length - toSubtract);
                        if (averageChange > -40.0) {
                          for (int z = 0; z < width; z++) {
                            if (!invalid[z]) System.out.print(z + " "); 
                          }
                          System.out.println();
                          System.out.println("Net Change: " + netChange + " pips");
                          System.out.println("Biggest Gain: " + maxGain + " pips");
                          System.out.println("Biggest Loss: " + maxLoss + " pips");
                          System.out.println("Average Change: " + averageChange + " pips/day");
                          Arrays.sort(dailyChanges);
                          System.out.println("25th percentile: " + dailyChanges[(length-toSubtract)/4] + " pips");
                          System.out.println("Median Change: " + dailyChanges[(length-toSubtract)/2] + " pips");
                          System.out.println("75th percentile: " + dailyChanges[3 * (length-toSubtract) /4] + " pips");
                          System.out.println();
                        }
                        
                      }
                      
                    }
        //          }
       //         }
       //       }
       //     }
     //     }
     //   }
    //  }
   // }
    //             }
    //              }
    //            }
    //          }
    //        }
    //       }
    //     }
    //     }
    //   }
    //  }
    //  }
    // }
    //    }
    //   }
    
  }
}