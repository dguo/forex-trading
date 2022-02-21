//+------------------------------------------------------------------+
//|                                                  PairUpdater.mq4 |
//|                                      Copyright © 2011, Danny Guo |
//|                                                                  |
//+------------------------------------------------------------------+
#property copyright "Copyright © 2011, Danny Guo"
#property link      ""

//+------------------------------------------------------------------+
//| expert initialization function                                   |
//+------------------------------------------------------------------+
int init()
  {
//----
   
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
    int handle = FileOpen("checkUpdate.csv", FILE_CSV|FILE_READ, ",");
    int update = StrToInteger(FileReadString(handle));
    FileClose(handle);
    
    if (update == 1) {
      
      int numberOfPairs = 106;   
      string firstPairs[106];
      string secondPairs[106];
      double firstBasePrices[106];
      double secondBasePrices[106];
      int topPairs[106];
      int streaks[106];
      double oldSpreads[106];
      double newSpreads[106];
      
      int numberOfPairs2 = 27;
      string pairs2[27];
      double oldPrices2[27];
      double newPrices2[27];
      int streaks2[27]; 
      
      double AUDCHFbid = 0.0;
      double NZDCHFbid = 0.0;
      
      int handle8 = FileOpen("CHF.csv", FILE_CSV|FILE_READ, ",");
      AUDCHFbid = StrToDouble(FileReadString(handle8));
      NZDCHFbid = StrToDouble(FileReadString(handle8));
      FileClose(handle8);
       
      int handle2 = FileOpen("record.csv", FILE_CSV|FILE_READ, ",");
      for (int a = 0; a < 7; a++) {
         FileReadString(handle2);
      }
      for (int b = 0; b < numberOfPairs; b++) {
         firstPairs[b] = FileReadString(handle2);
         secondPairs[b] = FileReadString(handle2);
         firstBasePrices[b] = StrToDouble(FileReadString(handle2));
         secondBasePrices[b] = StrToDouble(FileReadString(handle2));
         topPairs[b] = StrToInteger(FileReadString(handle2));
         streaks[b] = StrToInteger(FileReadString(handle2));
         oldSpreads[b] = StrToDouble(FileReadString(handle2));
      }
      FileReadString(handle2);
      FileReadString(handle2);
      FileReadString(handle2);
      for (int x = 0; x < numberOfPairs2; x++) {
         pairs2[x] = FileReadString(handle2);
         oldPrices2[x] = StrToDouble(FileReadString(handle2));
         streaks2[x] = StrToInteger(FileReadString(handle2));
      }
      FileClose(handle2);
      
      for (int c = 0; c < numberOfPairs; c++) {
         double firstNewPrice = MarketInfo(firstPairs[c], MODE_BID);
         if (firstPairs[c] == "AUDCHF") firstNewPrice = AUDCHFbid;
         if (firstPairs[c] == "NZDCHF") firstNewPrice = NZDCHFbid;
         double secondNewPrice = MarketInfo(secondPairs[c], MODE_BID);
         if (secondPairs[c] == "AUDCHF") secondNewPrice = AUDCHFbid;
         if (secondPairs[c] == "NZDCHF") secondNewPrice = NZDCHFbid;
         double firstPercentDelta = 100.0 * ((firstNewPrice - firstBasePrices[c]) / firstBasePrices[c]);
         double secondPercentDelta = 100.0 * ((secondNewPrice - secondBasePrices[c]) / secondBasePrices[c]);
         
         bool start = topPairs[c] == 0;
         bool neutral = DoublesEqual(firstPercentDelta, secondPercentDelta);
         bool noFlip = false;
         if (FirstGreater(firstPercentDelta, secondPercentDelta) && topPairs[c] == 1) noFlip = true;
         if (FirstGreater(secondPercentDelta, firstPercentDelta) && topPairs[c] == 2) noFlip = true;
         bool flip = false;
         if (FirstGreater(firstPercentDelta, secondPercentDelta) && topPairs[c] == 2) flip = true;
         if (FirstGreater(secondPercentDelta, firstPercentDelta) && topPairs[c] == 1) flip = true;
         
         if (start) {
            if (DoublesEqual(firstPercentDelta, secondPercentDelta)) {
               topPairs[c] = 0;
               streaks[c] = 0;
               newSpreads[c] = 0.0;
            }
            if (FirstGreater(firstPercentDelta, secondPercentDelta)) {
               topPairs[c] = 1;
               streaks[c] = 1;
               newSpreads[c] = MathAbs(firstPercentDelta - secondPercentDelta);
            }
            if (FirstGreater(secondPercentDelta, firstPercentDelta)) {
               topPairs[c] = 2;
               streaks[c] = 1;
               newSpreads[c] = MathAbs(firstPercentDelta - secondPercentDelta);
            }
         }
         
         if (neutral) {
            topPairs[c] = 0;
            streaks[c] = 0;
            newSpreads[c] = 0.0;
         }
         
         if (flip) {
            if (topPairs[c] == 1) topPairs[c] = 2;
            else topPairs[c] = 1;
            streaks[c] = 0;
            newSpreads[c] = MathAbs(firstPercentDelta - secondPercentDelta);
         }
         
         if (noFlip) {
            newSpreads[c] = MathAbs(firstPercentDelta - secondPercentDelta);
            if (FirstGreater(newSpreads[c], oldSpreads[c])) streaks[c]++;
            else streaks[c] = 0;
         }
         
      }
      
      for (int y = 0; y < numberOfPairs2; y++) {
         double newPrice = MarketInfo(pairs2[y], MODE_BID);
         if (pairs2[y] == "AUDCHF") newPrice = AUDCHFbid;
         if (pairs2[y] == "NZDCHF") newPrice = NZDCHFbid;
         if (FirstGreater(newPrice, oldPrices2[y]) && streaks2[y] > (-1)) streaks2[y]++;
         if (FirstGreater(oldPrices2[y], newPrice) && streaks2[y] < 1) streaks2[y]--;
         if (FirstGreater(newPrice, oldPrices2[y]) && streaks2[y] < 0) streaks2[y] = 0;
         if (FirstGreater(oldPrices2[y], newPrice) && streaks2[y] > 0) streaks2[y] = 0;
         newPrices2[y] = newPrice;
      }
    
      int handle3 = FileOpen("record.csv", FILE_CSV|FILE_WRITE, ",");
      FileWrite(handle3, "1st pair", "2nd pair", "1st base price", "2nd base price", "top pair", "streak" , "spread");
      for (int d = 0; d < numberOfPairs; d++) {
         FileWrite(handle3, firstPairs[d], secondPairs[d], firstBasePrices[d], secondBasePrices[d], topPairs[d], streaks[d], newSpreads[d]);
      }
      FileWrite(handle3, "pair", "previous price", "streak");
      for (int z = 0; z < numberOfPairs2; z++) {
         FileWrite(handle3, pairs2[z], newPrices2[z], streaks2[z]);
      }
      FileWrite(handle3, StringConcatenate(Month(), "/", Day(), "/", Year()));
      FileClose(handle3);
      
      int handle4 = FileOpen("checkUpdate.csv", FILE_CSV|FILE_WRITE, ",");
      FileWrite(handle4, "0");
      FileClose(handle4);
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
  if (NormalizeDouble(number1 - number2, 4) > 0) return (true);  
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
  if (NormalizeDouble(number1 - number2, 4) == 0) return (true);  
  else return (false);
//----
}
//+------------------------------------------------------------------+