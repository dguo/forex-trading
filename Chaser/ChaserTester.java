//+------------------------------------------------------------------+
//|                                                 ChaserTester.mq4 |
//|                                      Copyright © 2011, Danny Guo |
//|                                                                  |
//+------------------------------------------------------------------+
#property copyright "Copyright © 2011, Danny Guo"
#property link      ""

//+------------------------------------------------------------------+
//| global variables                                                 |
//+------------------------------------------------------------------+
int numberOfPairs;
bool stopTrades;
string pairs[13];

//+------------------------------------------------------------------+
//| expert initialization function                                   |
//+------------------------------------------------------------------+
int init()
  {
//----
   numberOfPairs = 13;
   stopTrades = true;
   
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
   if (TimeGood()) {
      
      double basePrices[13];
      double lockPrices[13];
      double currentPrices[13];
      double distances[13];
      string leadPair;
      int leadDirection;
      int leadIndex;
   
      int handle = FileOpen("record.csv", FILE_CSV|FILE_READ, ",");
      for (int a = 0; a < 5; a++) {
         FileReadString(handle);
      }
      for (int b = 0; b < numberOfPairs; b++) {
         FileReadString(handle);
         basePrices[b] = StrToDouble(FileReadString(handle));
         lockPrices[b] = StrToDouble(FileReadString(handle));
         currentPrices[b] = MarketInfo(pairs[b], MODE_BID);
         FileReadString(handle);
         FileReadString(handle);
      }
      leadPair = FileReadString(handle);
      leadDirection = StrToInteger(FileReadString(handle));
      leadIndex = StrToInteger(FileReadString(handle));
      FileClose(handle);
      
      for (int c = 0; c < numberOfPairs; c++) {
         if (FirstGreater(lockPrices[c], basePrices[c])) {
            if (FirstGreater(currentPrices[c], lockPrices[c])) lockPrices[c] = currentPrices[c];
            else {
               double distance = 0.75 * (lockPrices[c] - basePrices[c]);
               double lowestPrice = distance + basePrices[c];
               if (FirstGreater(lowestPrice, currentPrices[c])) {
                  basePrices[c] = lockPrices[c];
                  lockPrices[c] = currentPrices[c];
               }
            }
         }
         if (FirstGreater(basePrices[c], lockPrices[c])) {
            if (FirstGreater(lockPrices[c], currentPrices[c])) lockPrices[c] = currentPrices[c];
            else {
               double distance2 = 0.75 * (basePrices[c] - lockPrices[c]);
               double highestPrice = basePrices[c] - distance2;
               if (FirstGreater(currentPrices[c], highestPrice)) {
                  basePrices[c] = lockPrices[c];
                  lockPrices[c] = currentPrices[c];
               }
            }
         }
      }
      
      
  //    int leadIndex = 0;
  //    for (int z = 0; z < numberOfPairs; z++) {
  //       if (pairs[z] == leadPair) leadIndex = z;
  //    }
      
      for (int d = 0; d < numberOfPairs; d++) {
         distances[d] = 0.0;
         double multiplier = 10000.0;
         if (StringSubstr(pairs[d], 3, 3) == "JPY") multiplier = 100.0;
         distances[d] = MathAbs((currentPrices[d] - basePrices[d]) * multiplier);
   //      if (d == leadIndex) distances[d] = distances[d] + 5.0;
      }
      
      if (leadIndex != -1) {
        if (currentPrices[leadIndex] > basePrices[leadIndex] && leadDirection == 0) distances[leadIndex] = distances[leadIndex] + 5.0;
        if (currentPrices[leadIndex] < basePrices[leadIndex] && leadDirection == 1) distances[leadIndex] = distances[leadIndex] + 5.0;
      }
        
      int winnerIndex = 0;
      int winnerDirection = 0;
      if (FirstGreater(basePrices[0], currentPrices[0])) winnerDirection = 1;
      for (int e = 1; e < numberOfPairs; e++) {
         if (FirstGreater(distances[e], distances[winnerIndex])) {
            winnerIndex = e;
            if (FirstGreater(currentPrices[e], basePrices[e])) winnerDirection = 0;
            if (FirstGreater(basePrices[e], currentPrices[e])) winnerDirection = 1;
         }
      }
      
      if ((leadPair !=  "none" && leadDirection != winnerDirection) || (distances[winnerIndex] > distances[leadIndex])) {
        if (leadDirection == 0) SendMail("ChaserTester", StringConcatenate("close ", leadPair, " at ", MarketInfo(leadPair, MODE_BID)));
        if (leadDirection == 1) SendMail("ChaserTester", StringConcatenate("close ", leadPair, " at ", MarketInfo(leadPair, MODE_ASK)));
        leadPair = "none";
        leadDirection = -1;
        leadIndex = -1;
      }
      
      if (leadPair == "none" && distances[winnerIndex] > 25.0) {
        if (winnerDirection == 0) SendMail("ChaserTester", StringConcatenate("open ", pairs[winnerIndex], ", ", winnerDirection, ", ", MarketInfo(pairs[winnerIndex], MODE_ASK)));
        if (winnerDirection == 1) SendMail("ChaserTester", StringConcatenate("open ", pairs[winnerIndex], ", ", winnerDirection, ", ", MarketInfo(pairs[winnerIndex], MODE_BID)));
        leadPair = pairs[winnerIndex];
        leadDirection = winnerDirection;
        leadIndex = winnerIndex;
      }
      
   //   if (pairs[winnerIndex] != leadPair || (pairs[winnerIndex] == leadPair && winnerDirection != leadDirection)) {
   //      if (!stopTrades && FirstGreater(distances[winnerIndex], 25.0)) {
   //         SendMail("ChaserTester", StringConcatenate("close ", leadPair, " at ", MarketInfo(leadPair, MODE_BID), " and open ", pairs[winnerIndex], ", ", winnerDirection, ", ", MarketInfo(pairs[winnerIndex], MODE_BID)));
   //       }
   //       leadPair = pairs[winnerIndex];
   //      leadDirection = winnerDirection;      
   //    }
      
      int handle2 = FileOpen("record.csv", FILE_CSV|FILE_WRITE, ",");
      FileWrite(handle2, "pair", "basePrice", "lockPrice", "currentPrice", "distance");
      for (int f = 0; f < numberOfPairs; f++) {
         FileWrite(handle2, pairs[f], basePrices[f], lockPrices[f], currentPrices[f], distances[f]);
      }
      FileWrite(handle2, leadPair, leadDirection, leadIndex);
      FileClose(handle2);
  } 
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
//| function to check if the time is good                            |
//+------------------------------------------------------------------+
bool TimeGood()
   {
//----
   if (DayOfWeek() == 6) return (false); 
   if (DayOfWeek() == 5 && Hour() > 16) return (false);
   // if (DayOfWeek() == 0 && Hour() < 17) return (false); 
   return (true);
//----
   }
//+------------------------------------------------------------------+