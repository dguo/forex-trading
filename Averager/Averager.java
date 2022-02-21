//+------------------------------------------------------------------+
//|                                                     Averager.mq4 |
//|                                      Copyright © 2011, Danny Guo |
//|                                                                  |
//+------------------------------------------------------------------+
#property copyright "Copyright © 2011, Danny Guo"
#property link      ""

//+------------------------------------------------------------------+
//| global variables                                                 |
//+------------------------------------------------------------------+
double margin;
int numberOfPairs;
double percentIn;
bool stopTrades;
int transitionHour;
string pairs[13];

//+------------------------------------------------------------------+
//| expert initialization function                                   |
//+------------------------------------------------------------------+
int init()
  {
//----
   margin = 50.0;
   numberOfPairs = 13;
   percentIn = .50;
   stopTrades = false; 
   transitionHour = 19;
   
   pairs[0] = "CADJPY";     pairs[7] = "GBPAUD";
   pairs[1] = "EURAUD";     pairs[8] = "GBPCAD";
   pairs[2] = "EURCAD";     pairs[9] = "GBPCHF";
   pairs[3] = "EURCHF";     pairs[10] = "GBPJPY"; 
   pairs[4] = "EURGBP";     pairs[11] = "GBPUSD";
   pairs[5] = "EURJPY";     pairs[12] = "ZARJPY";
   pairs[6] = "EURUSD";   
//----
   return(0);
  }
//+------------------------------------------------------------------+
//| expert deinitialization function                                 |
//+------------------------------------------------------------------+
int deinit()
  {
//----
   
//----
   return(0);
  }
//+------------------------------------------------------------------+
//| expert start function                                            |
//+------------------------------------------------------------------+
int start()
  {
//----
   int handle = FileOpen("checkMinute.csv", FILE_CSV|FILE_READ, ",");
   int checkMinute = StrToInteger(FileReadString(handle));
   FileClose(handle);
   
   //+------------------------------------------------------------------+
   //| new minute                                                       |
   //+------------------------------------------------------------------+
   if (Minute() != checkMinute && TimeGood())
   {
   //----
      double currentAverages[13];
      double counter;
      
      int handle2 = FileOpen("currentAverages.csv", FILE_CSV|FILE_READ, ",");
      for (int a = 0; a < numberOfPairs; a++) {
         currentAverages[a] = StrToDouble(FileReadString(handle2)) + MarketInfo(pairs[a], MODE_BID);
      }
      counter = StrToDouble(FileReadString(handle2)) + 1.00000;
      FileClose(handle2);
      
      int handle3 = FileOpen("currentAverages.csv", FILE_CSV|FILE_WRITE, ",");
      for (int b = 0; b < numberOfPairs; b++) {
         FileWrite(handle3, currentAverages[b]);
      }
      FileWrite(handle3, counter);
      FileClose(handle3);
      
      int handle4 = FileOpen("checkMinute.csv", FILE_CSV|FILE_WRITE, ",");
      FileWrite(handle4, Minute());
      FileClose(handle4);
      
      Sleep(30000);
   //----
   }
   //+------------------------------------------------------------------+
   
   int handle5 = FileOpen("checkHour.csv", FILE_CSV|FILE_READ, ",");
   int checkHour = StrToInteger(FileReadString(handle5));
   FileClose(handle5);
   
   //+------------------------------------------------------------------+
   //| new hour                                                         |
   //+------------------------------------------------------------------+
   if (Hour() != checkHour && TimeGood() && !stopTrades)
   {
   //----
      // determine amount to close
      double amountsToClose[13];
      double amountsToCloseLeft[13];
      
      int handle6 = FileOpen("oldPositions.csv", FILE_CSV|FILE_READ, ",");
      for (int c = 0; c < numberOfPairs; c++) {
         amountsToClose[c] = StrToDouble(FileReadString(handle6));
         amountsToCloseLeft[c] = StrToDouble(FileReadString(handle6));
      }
      FileClose(handle6);   
      
      double firstHalf[13];
      
      for (int d = 0; d < numberOfPairs; d++) {
         firstHalf[d] = 0.0;
         double total = NormalizeDouble(MathAbs(amountsToClose[d]), 1);
         while (FirstGreater(total, 24000.0) || DoublesEqual(total, 24000.0)) {
            firstHalf[d] = firstHalf[d] + 1000.0;
            total = total - 24000.0;
         }
         if (DoublesEqual(total, 1000.0) && Hour() == 7) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 2000.0) && (Hour() == 3 || Hour() == 11)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 3000.0) && (Hour() == 1 || Hour() == 7 || Hour() == 13)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 4000.0) && (Hour() == 0 || Hour() == 5 || Hour() == 10 || Hour() == 15)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 5000.0) && (Hour() == 23 || Hour() == 3 || Hour() == 7 || Hour() == 11 || Hour() == 15)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 6000.0) && (Hour() == 22 || Hour() == 1 || Hour() == 4 || Hour() == 7 || Hour() == 10 || Hour() == 13)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 7000.0) && (Hour() == 22 || Hour() == 1 || Hour() == 4 || Hour() == 7 || Hour() == 10 || Hour() == 13 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 8000.0) && (Hour() == 22 || Hour() == 1 || Hour() == 3 || Hour() == 6 || Hour() == 9 || Hour() == 12 || Hour() == 15 || Hour() == 18)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 9000.0) && (Hour() == 22 || Hour() == 1 || Hour() == 2 || Hour() == 5 || Hour() == 7 || Hour() == 10 || Hour() == 13 || Hour() == 16 || Hour() == 18)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 10000.0) && (Hour() == 21 || Hour() == 0 || Hour() == 1 || Hour() == 3 || Hour() == 5 || Hour() == 7 || Hour() == 10 || Hour() == 12 || Hour() == 14 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 11000.0) && (Hour() == 21 || Hour() == 23 || Hour() == 1 || Hour() == 3 || Hour() == 5 || Hour() == 7 || Hour() == 9 || Hour() == 11 || Hour() == 13 || Hour() == 15 || Hour() == 17)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 12000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 2 || Hour() == 3 || Hour() == 5 || Hour() == 7 || Hour() == 9 || Hour() == 11 || Hour() == 13 || Hour() == 15 || Hour() == 17)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 13000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 6 || Hour() == 8 || Hour() == 10 || Hour() == 12 || Hour() == 14 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 14000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 9 || Hour() == 10 || Hour() == 12 || Hour() == 14 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 15000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 9 || Hour() == 12 || Hour() == 13 || Hour() == 15 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 16000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 8 || Hour() == 11 || Hour() == 12 || Hour() == 14 || Hour() == 15 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 17000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 8 || Hour() == 10 || Hour() == 11 || Hour() == 13 || Hour() == 14 || Hour() == 15 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 18000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 8 || Hour() == 9 || Hour() == 10 || Hour() == 11 || Hour() == 12 || Hour() == 13 || Hour() == 15 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 19000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 8 || Hour() == 9 || Hour() == 10 || Hour() == 11 || Hour() == 12 || Hour() == 13 || Hour() == 14 || Hour() == 15 || Hour() == 16)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 20000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 8 || Hour() == 9 || Hour() == 10 || Hour() == 11 || Hour() == 12 || Hour() == 13 || Hour() == 14 || Hour() == 15 || Hour() == 16 || Hour() == 17)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 21000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 8 || Hour() == 9 || Hour() == 10 || Hour() == 11 || Hour() == 12 || Hour() == 13 || Hour() == 14 || Hour() == 15 || Hour() == 16 || Hour() == 17 || Hour() == 18)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 22000.0) && (Hour() == 21 || Hour() == 22 || Hour() == 23 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 8 || Hour() == 9 || Hour() == 10 || Hour() == 11 || Hour() == 12 || Hour() == 13 || Hour() == 14 || Hour() == 15 || Hour() == 16 || Hour() == 17 || Hour() == 18)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (DoublesEqual(total, 23000.0) && (Hour() == 20 || Hour() == 21 || Hour() == 22 || Hour() == 23 || Hour() == 0 || Hour() == 1 || Hour() == 2 || Hour() == 3 || Hour() == 4 || Hour() == 5 || Hour() == 6 || Hour() == 7 || Hour() == 8 || Hour() == 9 || Hour() == 10 || Hour() == 11 || Hour() == 12 || Hour() == 13 || Hour() == 14 || Hour() == 15 || Hour() == 16 || Hour() == 17 || Hour() == 18)) firstHalf[d] = firstHalf[d] + 1000.0;
         if (FirstGreater(amountsToClose[d], 0.0)) firstHalf[d] = firstHalf[d] * (-1.0);
      }
      
      for (int e = 0; e < numberOfPairs; e++) {
         amountsToCloseLeft[e] = amountsToCloseLeft[e] + firstHalf[e];
      }
      
      // determine new positions
      double oldAverages[13];
      double newPositionSizes[13];
      
      int handle7 = FileOpen("oldAverages.csv", FILE_CSV|FILE_READ, ",");
      int handle8 = FileOpen("newPositionSizes.csv", FILE_CSV|FILE_READ, ",");
      for (int f = 0; f < numberOfPairs; f++) {
         oldAverages[f] = StrToDouble(FileReadString(handle7));
         newPositionSizes[f] = StrToDouble(FileReadString(handle8));
      }
      FileClose(handle7);
      FileClose(handle8);
      
      double secondHalf[13];
      
      for (int g = 0; g < numberOfPairs; g++) {
         double price = MarketInfo(pairs[g], MODE_BID);
         secondHalf[g] = 0.0;
         if (FirstGreater(oldAverages[g], price) && FirstGreater(oldAverages[g], 0.2000)) secondHalf[g] = (-1.0) * newPositionSizes[g];
         if (FirstGreater(price, oldAverages[g]) && FirstGreater(oldAverages[g], 0.2000)) secondHalf[g] = newPositionSizes[g];
      }   
      
      // determine net actions
      double net[13];
      for (int h = 0; h < numberOfPairs; h++) {
         net[h] = firstHalf[h] + secondHalf[h];
      }
      
      // complete net actions
      for (int i = 0; i < numberOfPairs; i++) {
      
         int numberOfOrders = OrdersTotal();
         int counter2 = 0;
         int direction = -1;
         double totalSize = 0.00; 
         for (int j = 0; j < numberOfOrders; j++) {
            OrderSelect(j, SELECT_BY_POS);
            if (OrderSymbol() == pairs[i]) {
               counter2++;
               direction = OrderType();
               totalSize = totalSize + OrderLots();
            }
         }
         
         double newSize = UnitsToLots(net[i]);
         
         if (counter2 == 0 && !DoublesEqual(newSize, 0.00)){
            PlaceOrder(pairs[i], newSize); 
         }
         
         else {
         
            if (FirstGreater(newSize, 0.00) && !DoublesEqual(newSize, 0.00)) {
               if (direction == 0) PlaceOrder(pairs[i], newSize);
               else {
                  if (FirstGreater(newSize, totalSize)) {
                    CloseAll(pairs[i]);
                    PlaceOrder(pairs[i], newSize - totalSize);
                  }
                  else {
                     for (int k = 0; k < OrdersTotal(); k++) {
                        OrderSelect(k, SELECT_BY_POS);
                        if (OrderSymbol() == pairs[i] && FirstGreater(newSize, 0.00)) {
                           bool firstCondition = FirstGreater(newSize, OrderLots());
                           bool secondCondition = DoublesEqual(newSize, OrderLots());
                           bool thirdCondition = FirstGreater(OrderLots(), newSize);
                           if (firstCondition) {
                              newSize = newSize - OrderLots();
                              OrderClose(OrderTicket(), OrderLots(), MarketInfo(pairs[i], MODE_BID), 2000, Red);
                              k = -1;
                           }
                           if (secondCondition) {
                              newSize = 0.00;
                              OrderClose(OrderTicket(), OrderLots(), MarketInfo(pairs[i], MODE_BID), 2000, Red);
                           }
                           if (thirdCondition) {
                              OrderClose(OrderTicket(), newSize, MarketInfo(pairs[i], MODE_BID), 2000, Red);
                              newSize = 0.00;
                           }
                        }
                     }
                  }
               }
            }
         
            if (FirstGreater(0.00, newSize) && !DoublesEqual(0.00, newSize)) {
               if (direction == 1) PlaceOrder(pairs[i], newSize);
               else {
                  if (FirstGreater(MathAbs(newSize), totalSize)) {
                     CloseAll(pairs[i]);
                     PlaceOrder(pairs[i], newSize + totalSize);
                  }
                  else {
                     for (int l = 0; l < OrdersTotal(); l++) {
                        OrderSelect(l, SELECT_BY_POS);
                        if (OrderSymbol() == pairs[i] && FirstGreater(MathAbs(newSize), 0.00)) {
                           bool fourthCondition = FirstGreater(MathAbs(newSize), OrderLots());
                           bool fifthCondition = DoublesEqual(MathAbs(newSize), OrderLots());
                           bool sixthCondition = FirstGreater(OrderLots(), MathAbs(newSize));
                           if (fourthCondition) {
                              newSize = newSize + OrderLots();
                              OrderClose(OrderTicket(), OrderLots(), MarketInfo(pairs[i], MODE_BID), 2000, Red);
                              l = -1;
                           }
                           if (fifthCondition) {
                              newSize = 0.00;
                              OrderClose(OrderTicket(), OrderLots(), MarketInfo(pairs[i], MODE_BID), 2000, Red);
                           }
                           if (sixthCondition) {
                              OrderClose(OrderTicket(), MathAbs(newSize), MarketInfo(pairs[i], MODE_BID), 2000, Red);
                              newSize = 0.00;
                           }
                        }
                     }  
                  }
               }
            }
            
         }
         
      }
      
      // update checkHour.csv
      int handle9 = FileOpen("checkHour.csv", FILE_CSV|FILE_WRITE, ",");
      FileWrite(handle9, Hour());
      FileClose(handle9);
      
      // update oldPositions.csv
      int handle10 = FileOpen("oldPositions.csv", FILE_CSV|FILE_WRITE, ",");
      for (int m = 0; m < numberOfPairs; m++) {
         FileWrite(handle10, amountsToClose[m], amountsToCloseLeft[m]);
      }
      FileClose(handle10);
      
      // update currentPositions.csv
      double currentPositions[13];
      int handle11 = FileOpen("currentPositions.csv", FILE_CSV|FILE_READ, ",");
      for (int n = 0; n < numberOfPairs; n++) {
         currentPositions[n] = StrToDouble(FileReadString(handle11)) + secondHalf[n];
      }
      FileClose(handle11);
      
      int handle12 = FileOpen("currentPositions.csv", FILE_CSV|FILE_WRITE, ",");
      for (int o = 0; o < numberOfPairs; o++) {
         FileWrite(handle12, currentPositions[o]);
      }
      FileClose(handle12);
      
      // send email
      SendMail(StringConcatenate(Month(), "/", Day(), "/", Year()), StringConcatenate("Hour: ", Hour(), ", Equity: ", AccountEquity()));
   //----
   }
   //+------------------------------------------------------------------+
   
   int handle13 = FileOpen("checkDay.csv", FILE_CSV|FILE_READ, ",");
   int checkDay = StrToInteger(FileReadString(handle13));
   FileClose(handle13);
   
   //+------------------------------------------------------------------+
   //| new day                                                          |
   //+------------------------------------------------------------------+
   if (Day() != checkDay && Hour() == transitionHour && TimeGood())
   {
   //----
      // update oldAverages.csv using currentAverages.csv
      double sums[13];
      double counter3;
      int handle14 = FileOpen("currentAverages.csv", FILE_CSV|FILE_READ, ",");
      for (int p = 0; p < numberOfPairs; p++) {
         sums[p] = StrToDouble(FileReadString(handle14));
      }
      counter3 = StrToDouble(FileReadString(handle14));
      FileClose(handle14);
      
      int handle15 = FileOpen("oldAverages.csv", FILE_CSV|FILE_WRITE, ",");
      for (int q = 0; q < numberOfPairs; q++) {
         FileWrite(handle15, sums[q] / counter3);
      }
      FileClose(handle15);
      
      // reset currentAverages.csv
      int handle16 = FileOpen("currentAverages.csv", FILE_CSV|FILE_WRITE, ",");
      for (int r = 0; r < numberOfPairs; r++) {
         FileWrite(handle16, 0.00000);
      }
      FileWrite(handle16, 0.00000);
      FileClose(handle16);
      
      // update oldPositions.csv with currentPositions.csv
      double remainders[13];
      int handle17 = FileOpen("oldPositions.csv", FILE_CSV|FILE_READ, ",");
      for (int s = 0; s < numberOfPairs; s++) {   
         FileReadString(handle17);
         remainders[s] = StrToDouble(FileReadString(handle17));
      }
      FileClose(handle17);
      
      double new[13];
      int handle18 = FileOpen("currentPositions.csv", FILE_CSV|FILE_READ, ",");
      for (int t = 0; t < numberOfPairs; t++) {
         new[13] = StrToDouble(FileReadString(handle18));
      }
      FileClose(handle18);
      
      int handle19 = FileOpen("oldPositions.csv", FILE_CSV|FILE_WRITE, ",");
      for (int u = 0; u < numberOfPairs; u++) {
         FileWrite(handle19, new[u] + remainders[u], new[u] + remainders[u]);
      }
      FileClose(handle19);
      
      // reset currentPositions.csv
      int handle20 = FileOpen("currentPositions.csv", FILE_CSV|FILE_WRITE, ",");
      for (int v = 0; v < numberOfPairs; v++) {
         FileWrite(handle20, 0.00);
      }
      FileClose(handle20);
      
      // update newPositionSizes.csv
      double unitsFirst[13];
      for (int w = 0; w < numberOfPairs; w++) {
         double baseToHome = BaseToHomeBid(pairs[w]);
         unitsFirst[w] = (100.0 * margin) / baseToHome; 
      }
      
      double pipValuesFirst[13];
      for (int x = 0; x < numberOfPairs; x++) {
         double quoteToHome = QuoteToHomeBid(pairs[x]);
         double difference = 0.0001;
         if (StringSubstr(pairs[x], 3, 3) == "JPY") difference = 0.01;
         pipValuesFirst[x] = unitsFirst[x] * quoteToHome * difference;
      }
      
      double sum = 0.0;
      for (int y = 0; y < numberOfPairs; y++) {
         sum = sum + pipValuesFirst[y]; 
      }
      double onePipAverageValue = sum / numberOfPairs;
      
      double marginsSecond[13];
      for (int z = 0; z < numberOfPairs; z++) {
         double ratio = onePipAverageValue / pipValuesFirst[z];
         marginsSecond[z] = ratio * 100.0;
      }
      
      double sumMargins = 0.0;
      for (int aa = 0; aa < numberOfPairs; aa++) {
         sumMargins = sumMargins + marginsSecond[aa];
      }
      
      double lastRatio = (percentIn * AccountEquity()) / sumMargins;
      
      double marginsThird[13];
      for (int bb = 0; bb < numberOfPairs; bb++) {
         marginsThird[bb] = lastRatio * marginsSecond[bb];
      }
      
      double newPositions[13];
      for (int cc = 0; cc < numberOfPairs; cc++) {
         double baseToHome2 = BaseToHomeBid(pairs[cc]);
         newPositions[cc] = (marginsThird[cc] * margin) / baseToHome2;
         newPositions[cc] = newPositions[cc] / 24.0;
         newPositions[cc] = MathRound(newPositions[cc] / 1000.0);
         newPositions[cc] = newPositions[cc] * 1000.0;
      }
      
      int handle21 = FileOpen("newPositionSizes.csv", FILE_CSV|FILE_WRITE, ",");
      for (int dd = 0; dd < numberOfPairs; dd++) {
         FileWrite(handle21, newPositions[dd]);
      }
      FileClose(handle21);
      
      // update checkDay.csv
      int handle22 = FileOpen("checkDay.csv", FILE_CSV|FILE_WRITE, ",");
      FileWrite(handle22, Day());
      FileClose(handle22); 
   //----
   }
   //+------------------------------------------------------------------+
   
//----
   return(0);
  }
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to check if the first of two doubles is greater         |
//+------------------------------------------------------------------+
bool FirstGreater(double number1, double number2)
   {
//----
   if (NormalizeDouble(number1 - number2, 5) > 0) return (true);  
   else return (false);
//----
   }
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to check if two doubles are equal                       |
//+------------------------------------------------------------------+
bool DoublesEqual(double number1, double number2)
   {
//----
   if (NormalizeDouble(number1 - number2, 5) == 0) return (true);  
   else return (false);
//----
   }
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to return the base to home bid value                    |
//+------------------------------------------------------------------+
double BaseToHomeBid(string pair)
   {
//----
   string base = StringSubstr(pair, 0, 3);
   double rate = 1.0;
   if (base == "EUR")rate = MarketInfo("EURUSD", MODE_BID);
   if (base == "AUD")rate = MarketInfo("AUDUSD", MODE_BID);
   if (base == "GBP")rate = MarketInfo("GBPUSD", MODE_BID);
   if (base == "NZD")rate = MarketInfo("NZDUSD", MODE_BID);
   if (base == "CAD")rate = 1.0 / MarketInfo("USDCAD", MODE_BID);
   if (base == "CHF")rate = 1.0 / MarketInfo("USDCHF", MODE_BID);
   if (base == "SGD")rate = 1.0 / MarketInfo("USDSGD", MODE_BID);
   if (base == "ZAR")rate = 1.0 / MarketInfo("USDZAR", MODE_BID);
   return (rate);
//----
   }
//+------------------------------------------------------------------+ 

//+------------------------------------------------------------------+
//| function to return the quote to home bid value                   |
//+------------------------------------------------------------------+
double QuoteToHomeBid(string pair)
   {
//----
   string quote = StringSubstr(pair, 3, 3);
   double rate = 1.0;
   if (quote == "CHF")rate = 1.0 / MarketInfo("USDCHF", MODE_BID);
   if (quote == "JPY")rate = 1.0 / MarketInfo("USDJPY", MODE_BID);
   if (quote == "CAD")rate = 1.0 / MarketInfo("USDCAD", MODE_BID);
   if (quote == "AUD")rate = MarketInfo("AUDUSD", MODE_BID);
   if (quote == "GBP")rate = MarketInfo("GBPUSD", MODE_BID);
   return (rate);
//----
   }
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to check if the time is good                            |
//+------------------------------------------------------------------+
bool TimeGood()
   {
//----
   if (DayOfWeek() == 6) return (false); 
   if (DayOfWeek() == 5 && Hour() > 16) return (false);
   if (DayOfWeek() == 0 && Hour() < 17) return (false); 
   return (true);
//----
   }
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to return the lot size                                  |
//+------------------------------------------------------------------+
double UnitsToLots(double exactSize)
   {
//----
   double PositionExact = exactSize;
   double PositionToRound = PositionExact / 1000.0;
   double RoundedPosition = MathRound(PositionToRound);
   double PositionInLots = RoundedPosition / 100.0;
   double FinalLots = NormalizeDouble(PositionInLots, 2);
   return (FinalLots);
//----
   }
//+------------------------------------------------------------------+ 

//+------------------------------------------------------------------+
//| function to place an order                                       |
//+------------------------------------------------------------------+
void PlaceOrder(string pairToTrade, double sizeOfTrade)
   {
//----
   string pair = pairToTrade;
   double lots = NormalizeDouble(MathAbs(sizeOfTrade), 2);
   bool goLong = true;
   if (FirstGreater(0.00, sizeOfTrade)) goLong = false;
   double bidPrice = MarketInfo(pair, MODE_BID);
   double askPrice = MarketInfo(pair, MODE_ASK);
   
   // place the order if the lot size is below the maximum
   if (FirstGreater(100.00, lots)) {
      if (goLong) {
         OrderSend(pair, 0, lots, askPrice, 2000, 0, 0, NULL, 8, 0, Green);
      }
      else {
         OrderSend(pair, 1, lots, bidPrice, 2000, 0, 0, NULL, 8, 0, Green); 
      }
   }
   
   // otherwise, split it up into as many orders as needed
   else {
      while (FirstGreater(lots, 100.00)) {
         if (goLong) {
            OrderSend(pair, 0, 100.00, askPrice, 2000, 0, 0, NULL, 8, 0, Green);
         }
         else {
            OrderSend(pair, 1, 100.00, bidPrice, 2000, 0, 0, NULL, 8, 0, Green);
         }
         lots = NormalizeDouble(lots - 100.00, 2);
      }
      if (goLong) {
         OrderSend(pair, 0, lots, askPrice, 2000, 0, 0, NULL, 8, 0, Green);
      }
      else {
         OrderSend(pair, 1, lots, bidPrice, 2000, 0, 0, NULL, 8, 0, Green);
      }
   }
//----
   }
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to close all open orders for a given pair               |
//+------------------------------------------------------------------+
void CloseAll(string pair)
   {
//----
   for (int k = 0; k < OrdersTotal(); k++) {
      OrderSelect(k, SELECT_BY_POS);
      if (OrderSymbol() == pair) {
         OrderClose(OrderTicket(), OrderLots(), MarketInfo(pair, MODE_BID), 20000, Red);
         k = -1;
      }
   } 
//----
   }
//+------------------------------------------------------------------+ 