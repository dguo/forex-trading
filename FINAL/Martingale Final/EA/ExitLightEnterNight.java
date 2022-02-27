//+------------------------------------------------------------------+
//|                                          ExitLightEnterNight.mq4 |
//|                                      Copyright © 2011, Danny Guo |
//|                                                                  |
//+------------------------------------------------------------------+
#property copyright "Copyright © 2011, Danny Guo"
#property link      ""
  
//+------------------------------------------------------------------+
//| global variables                                                 |
//+------------------------------------------------------------------+
double firstProportionIn;
bool stopTrades;
double pipLimit;

//+------------------------------------------------------------------+
//| expert initialization function                                   |
//+------------------------------------------------------------------+
int init()
{
//----
  firstProportionIn = .044;
  stopTrades = true;
  pipLimit = 10.0;
//----
  return(0);
}
//+------------------------------------------------------------------+

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

//+------------------------------------------------------------------+
//| expert start function                                            |
//+------------------------------------------------------------------+
int start()
{
//----
  double roundZeroMargin;
  double roundOneMargin;
  double roundTwoMargin;
  double roundThreeMargin;
  double roundFourMargin;
  int currentStreak;
  int lockDirection;
  double lockPrice;
  int inMotion;
  
  int handle = FileOpen(Symbol() + ".csv", FILE_CSV|FILE_READ, ",");
  FileReadString(handle);
  FileReadString(handle);
  FileReadString(handle);
  roundZeroMargin = StrToDouble(FileReadString(handle));
  FileReadString(handle);
  roundOneMargin = StrToDouble(FileReadString(handle));
  FileReadString(handle);
  roundTwoMargin = StrToDouble(FileReadString(handle));
  FileReadString(handle);
  roundThreeMargin = StrToDouble(FileReadString(handle));
  FileReadString(handle);
  roundFourMargin = StrToDouble(FileReadString(handle));
  FileReadString(handle);
  currentStreak = StrToInteger(FileReadString(handle));
  FileReadString(handle);
  lockDirection = StrToInteger(FileReadString(handle));
  FileReadString(handle);
  lockPrice = StrToDouble(FileReadString(handle));
  FileReadString(handle);
  inMotion = StrToInteger(FileReadString(handle));
  FileClose(handle);
  
  double multiplier = 10000.0;
  if (Symbol() == "USDJPY") multiplier = 100.0;
  double newBid = NormalizeDouble(MarketInfo(Symbol(), MODE_BID) * multiplier, 1);
  double newAsk = NormalizeDouble(MarketInfo(Symbol(), MODE_ASK) * multiplier, 1);
  bool gain = false;
  bool loss = false;
  
  if (lockDirection == 0) {
    if (FirstGreater(newBid - lockPrice, pipLimit)) {
      gain = true;
      lockPrice = newAsk; 
    }
    if (FirstGreater(lockPrice - newBid, pipLimit)) {
      loss = true;
      lockPrice = newBid;
    }
  }
  
  if (lockDirection == 1) {
    if (FirstGreater(newAsk - lockPrice, pipLimit)) {
      gain = true;
      lockPrice = newAsk; 
    }
    if (FirstGreater(lockPrice - newAsk, pipLimit)) {
      loss = true;
      lockPrice = newBid;
    }
  }
  
  if (gain) {
    if (lockDirection == 1) {
      currentStreak++;
      CloseAllOrders();
      if (currentStreak < 5 && inMotion == 1) {
        double marginNeeded;
        if (currentStreak == 1) marginNeeded = roundOneMargin;
        if (currentStreak == 2) marginNeeded = roundTwoMargin;
        if (currentStreak == 3) marginNeeded = roundThreeMargin;
        if (currentStreak == 4) marginNeeded = roundFourMargin;
        if (FirstGreater(0.93 * AccountFreeMargin(), marginNeeded)) PlaceOrder(Symbol(), MarginToLots(marginNeeded));
        else {
          PlaceOrder(Symbol(), MarginToLots(0.85 * AccountFreeMargin()));
          SendMail(Symbol() + " problem", "Not enough margin.");
        }
      }
      if (currentStreak > 4) inMotion = 0;
    }
    if (lockDirection == 0) {
      currentStreak = 0;
      roundZeroMargin = AccountEquity() * firstProportionIn;
      roundOneMargin = roundZeroMargin * 2.0;
      roundTwoMargin = roundOneMargin * 2.0;
      roundThreeMargin = roundTwoMargin * 2.0;
      roundFourMargin = roundThreeMargin * 2.0;
      if (!TimeGood() || stopTrades) {
        CloseAllOrders();
        inMotion = 0; 
      }
      else {
        Enter(roundZeroMargin, 0);
        inMotion = 1;
      }
    }
    lockDirection = 0;
  }
  
  if (loss) {
    if (lockDirection == 0) {
      currentStreak++;
      CloseAllOrders();
      if (currentStreak < 5 && inMotion == 1) {
        double marginNeeded2;
        if (currentStreak == 1) marginNeeded2 = roundOneMargin;
        if (currentStreak == 2) marginNeeded2 = roundTwoMargin;
        if (currentStreak == 3) marginNeeded2 = roundThreeMargin;
        if (currentStreak == 4) marginNeeded2 = roundFourMargin;
        if (FirstGreater(0.93 * AccountFreeMargin(), marginNeeded2)) PlaceOrder(Symbol(), (-1.0 * MarginToLots(marginNeeded2)));
        else {
          PlaceOrder(Symbol(), (-1.0 * MarginToLots(0.85 * AccountFreeMargin())));
          SendMail(Symbol() + " problem", "Not enough margin.");
        }
      }
      if (currentStreak > 4) inMotion = 0;
    }
    if (lockDirection == 1) {
      currentStreak = 0;
      roundZeroMargin = AccountEquity() * firstProportionIn;
      roundOneMargin = roundZeroMargin * 2.0;
      roundTwoMargin = roundOneMargin * 2.0;
      roundThreeMargin = roundTwoMargin * 2.0;
      roundFourMargin = roundThreeMargin * 2.0;
      if (!TimeGood() || stopTrades) {
        CloseAllOrders();
        inMotion = 0; 
      }
      else {
          Enter(roundZeroMargin, 1);
          inMotion = 1;
      }
    }
    lockDirection = 1;
  }
  
  int handle2 = FileOpen(Symbol() + ".csv", FILE_CSV|FILE_WRITE, ",");
  FileWrite(handle2, "round", "margin");
  FileWrite(handle2, "0", roundZeroMargin);
  FileWrite(handle2, "1", roundOneMargin);
  FileWrite(handle2, "2", roundTwoMargin);
  FileWrite(handle2, "3", roundThreeMargin);
  FileWrite(handle2, "4", roundFourMargin);
  FileWrite(handle2, "currentStreak", currentStreak);
  FileWrite(handle2, "lockDirection", lockDirection);
  FileWrite(handle2, "lockPrice", lockPrice);
  FileWrite(handle2, "inMotion", inMotion);
  FileClose(handle2);
  
  if (Symbol() == "EURUSD") {
    int handle3 = FileOpen("McheckHour.csv", FILE_CSV|FILE_READ, ",");
    int checkHour = StrToInteger(FileReadString(handle3));
    FileClose(handle3);
    
    if (checkHour != Hour()) {
      SendMail(StringConcatenate(Month(), "/", Day(), "/", Year()), StringConcatenate("Hour: ", Hour(), ", Equity: ", AccountEquity()));
      int handle4 = FileOpen("McheckHour.csv", FILE_CSV|FILE_WRITE, ",");
      FileWrite(handle4, Hour());
      FileClose(handle4);
    }
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
  if (NormalizeDouble(number1 - number2, 2) > 0) return (true);  
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
  if (NormalizeDouble(number1 - number2, 2) == 0) return (true);  
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
  if (DayOfWeek() == 5 && Hour() > 13) return (false);
  if (DayOfWeek() == 0 && Hour() < 18) return (false); 
  return (true);
//----
}
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to close all open orders for the pair                   |
//+------------------------------------------------------------------+
void CloseAllOrders()
{
//----
  for (int k = 0; k < OrdersTotal(); k++) {
    OrderSelect(k, SELECT_BY_POS);
    if (OrderSymbol() == Symbol()) {
      OrderClose(OrderTicket(), OrderLots(), MarketInfo(pair, MODE_BID), 20000, Red);
      k = -1;
    }
  } 
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
      OrderSend(pair, 0, lots, askPrice, 20000, 0, 0, NULL, 8, 0, Green);
    }
    else {
      OrderSend(pair, 1, lots, bidPrice, 20000, 0, 0, NULL, 8, 0, Green); 
    }
  }
  
  // otherwise, split it up into as many orders as needed
  else {
    while (FirstGreater(lots, 100.00)) {
      if (goLong) {
        OrderSend(pair, 0, 100.00, askPrice, 20000, 0, 0, NULL, 8, 0, Green);
      }
      else {
        OrderSend(pair, 1, 100.00, bidPrice, 20000, 0, 0, NULL, 8, 0, Green);
      }
      lots = NormalizeDouble(lots - 100.00, 2);
    }
    if (goLong) {
      OrderSend(pair, 0, lots, askPrice, 20000, 0, 0, NULL, 8, 0, Green);
    }
    else {
      OrderSend(pair, 1, lots, bidPrice, 20000, 0, 0, NULL, 8, 0, Green);
    }
  }
//----
}
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to return the lot size                                  |
//+------------------------------------------------------------------+
double MarginToLots(double margin)
{
//----
  double baseToHome = 1.0;
  if (Symbol() == EURUSD) baseToHome = MarketInfo("EURUSD", MODE_BID);
  if (Symbol() == GBPUSD) baseToHome = MarketInfo("GBPUSD", MODE_BID):
  double PositionExact = (margin * 50.0) / baseToHome;
  double PositionToRound = PositionExact / 1000.0;
  double RoundedPosition = MathCeil(PositionToRound);
  double PositionInLots = RoundedPosition / 100.0;
  double FinalLots = NormalizeDouble(PositionInLots, 2);
  return (FinalLots);
//----
}
//+------------------------------------------------------------------+ 

//+------------------------------------------------------------------+
//| function to enter                                                |
//+------------------------------------------------------------------+
void Enter(double roundZeroMargin, int direction)
{
//----
  double newPosition = MarginToLots(roundZeroMargin);
  if (direction == 1) newPosition = newPosition * (-1.0);
  
  int counter = 0;
  int oldDirection = -1;
  double oldPosition = 0.00; 
  for (int a = 0; a < OrdersTotal(); a++) {
    OrderSelect(a, SELECT_BY_POS);
    if (OrderSymbol() == Symbol()) {
      counter++;
      oldDirection = OrderType();
      oldPosition = oldPosition + OrderLots();
    }
  }
  if (oldDirection == 1) oldPosition = oldPosition * (-1.0);
  
  if (counter == 0) PlaceOrder(Symbol(), newPosition);
  
  else {
    
    if (direction == 0) {
      if (FirstGreater(newPosition, oldPosition)) PlaceOrder(Symbol(), newPosition - oldPosition);
      if (FirstGreater(oldPosition, newPosition)) {
        double amountToClose = oldPosition - newPosition;
        for (int i = 0; i < OrdersTotal(); i++) {
          OrderSelect(i, SELECT_BY_POS);
          if (OrderSymbol() == Symbol() && FirstGreater(amountToClose, 0.00)) {
            bool firstCondition = FirstGreater(amountToClose, OrderLots());
            bool secondCondition = DoublesEqual(amountToClose, OrderLots());
            bool thirdCondition = FirstGreater(OrderLots(), amountToClose);
            if (firstCondition) {
              OrderClose(OrderTicket(), OrderLots(), MarketInfo(Symbol(), MODE_BID), 20000, Red);
              amountToClose = amountToClose - OrderLots();
              i = -1;
            }
            if (secondCondition) {
              OrderClose(OrderTicket(), OrderLots(), MarketInfo(Symbol(), MODE_BID), 20000, Red);
              amountToClose = 0.00;
            }
            if (thirdCondition) {
              OrderClose(OrderTicket(), amountToClose, MarketInfo(Symbol(), MODE_BID), 20000, Red);
              amountToClose = 0.00;
            }
          }
        }
      }
    }
    
    if (direction == 1) {
      if (FirstGreater(oldPosition, newPosition)) PlaceOrder(Symbol(), newPosition - oldPosition);
      if (FirstGreater(newPosition, oldPosition)) {
        double amountToClose2 = MathAbs(oldPosition - newPosition);
        for (int j = 0; j < OrdersTotal(); j++) {
          OrderSelect(j, SELECT_BY_POS);
          if (OrderSymbol() == Symbol() && FirstGreater(amountToClose2, 0.00)) {
            bool fourthCondition = FirstGreater(amountToClose2, OrderLots());
            bool fifthCondition = DoublesEqual(amountToClose2, OrderLots());
            bool sixthCondition = FirstGreater(OrderLots(), amountToClose2);
            if (fourthCondition) {
              OrderClose(OrderTicket(), OrderLots(), MarketInfo(Symbol(), MODE_BID), 20000, Red);
              amountToClose2 = amountToClose2 - OrderLots();
              j = -1;
            }
            if (fifthCondition) {
              OrderClose(OrderTicket(), OrderLots(), MarketInfo(Symbol(), MODE_BID), 20000, Red);
              amountToClose2 = 0.00;
            }
            if (sixthCondition) {
              OrderClose(OrderTicket(), amountToClose2, MarketInfo(Symbol(), MODE_BID), 20000, Red);
              amountToClose2 = 0.00;
            }
          }
        }
      }
    }
    
  }
//----
  }
//+------------------------------------------------------------------+ 