//+------------------------------------------------------------------+
//|                                          MartingaleRebounder.mq4 |
//|                                                        Danny Guo |
//|                                                                  |
//+------------------------------------------------------------------+
#property copyright "Danny Guo"
#property link      ""

extern double StartPercentage = 0.020;
extern double Margin = 50.0;

//+------------------------------------------------------------------+
//| global variables                                                 |
//+------------------------------------------------------------------+
double PositionSize;
double AveragePrice;
double LimitPrice;
bool Long;
double StopIntervals[7];
double StopDistances[7];

//+------------------------------------------------------------------+
//| expert initialization function                                   |
//+------------------------------------------------------------------+
int init()
  {
//----
   Long = false;
   ResetVariables();
   StopIntervals[7] = {0.000, 0.030, 0.070, 0.120, 0.180, 0.250, 0.330};
   StopDistances[7] = {0.010, 0.020, 0.030, 0.040, 0.050, 0.060, 0.070};
   UpdateVariables();
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
//+------------------------------------------------------------------+
//| trailing stop                                                    |
//+------------------------------------------------------------------+
if ((OrdersTotal() > 0) && LimitHit())
  {
//----
   if (Long) {
      int index = 0;
      double difference = NormalizeDouble(Bid - LimitPrice, 3);
      for (int g = 0; g < 7; g++) {
         if (FirstGreater(difference, StopIntervals[g]) || DoublesSame(difference, StopIntervals[g])) index = g;
      }
      double StopPrice = Bid - StopDistances[index];
      double LockPrice = Bid;
      
      while (OrdersTotal() > 0) {
         RefreshRates();
         if (DoublesSame(Bid, StopPrice) || FirstGreater(StopPrice, Bid)) {
            CloseAllOrders();
            ResetVariables();
         }
         else {
            if (FirstGreater(Bid, LockPrice)) {
               LockPrice = Bid;
               double difference2 = NormalizeDouble(Bid - LimitPrice, 3);
               int index2 = g;
               for (int h = g; h < 7; h++) {
                  if (FirstGreater(difference2, StopIntervals[h]) || DoublesSame(difference2, StopIntervals[h])) index2 = h;
               }
               StopPrice = LockPrice - StopDistances[index2];
            }
         }
      }
   }
   
   else {
      int index3 = 0;
      double difference3 = NormalizeDouble(LimitPrice - Ask, 3);
      for (int i = 0; i < 7; i++) {
         if (FirstGreater(difference, StopIntervals[i]) || DoublesSame(difference, StopIntervals[i])) index3 = i;
         }
      double StopPrice2 = Ask + StopDistances[0];
      double LockPrice2 = Ask;
      
      while (OrdersTotal() > 0) {
         RefreshRates();
         if (DoublesSame(Ask, StopPrice2) || FirstGreater(Ask, StopPrice2)) {
            CloseAllOrders();
            ResetVariables();
         }
         else {
            if (FirstGreater(LockPrice2, Ask)) {
               LockPrice2 = Ask;
               double difference4 = NormalizeDouble(LimitPrice - Ask, 3);
               int index4 = i;
               for (int j = i; j < 7; j++) {
                  if (FirstGreater(difference4, StopIntervals[j]) || DoublesSame(difference4, StopIntervals[j])) index4 = j;
               }
               StopPrice2 = LockPrice2 + StopDistances[index4];
            }
         }
      }
   }
//----
  }
//+------------------------------------------------------------------+
//| stop loss                                                        |
//+------------------------------------------------------------------+
if ((OrdersTotal() > 0) && FirstGreater(5.00, AccountFreeMargin()))
  {
//----
   CloseAllOrders();
   ResetVariables();
   SendMail("Margin Call", "No more margin left.");  // send an email
//----
  }
//+------------------------------------------------------------------+
//| regular check                                                    |
//+------------------------------------------------------------------+
if ((OrdersTotal() > 0) && FirstGreater(DistanceFromLimit(), 0.150) && SpreadGood())
  {
//----
     if (Long) {
         bool StayIn = true;
         double Stop = Bid;
         while (StayIn) {
            RefreshRates();
            if (FirstGreater(Bid, Stop + 0.010) || DoublesSame(Bid, Stop + 0.010)) {
               double multiplier = (DistanceFromLimit() - 0.060) * 100 / 6.000;
               double CurrentPositionInUnits = PositionSize * 100000.0;
               double NewOrderInUnits = multiplier * CurrentPositionInUnits;
               double NewOrderInLots = UnitsToLots(NewOrderInUnits);
               double AmountToLeave = AccountBalance() * 0.1;
               double MaxPositionSize = (AccountFreeMargin() - AmountToLeave) * Margin;
               if (FirstGreater(NewOrderInUnits, MaxPositionSize)) NewOrderInLots = UnitsToLots(MaxPositionSize);
               PlaceOrder(NewOrderInLots, Long);
               UpdateVariables();
               StayIn = false;
            }
            else {
               if (FirstGreater(Stop, Bid)) Stop = Bid;
            }
         }
     }
     
     else {
         bool StayIn2 = true;
         double Stop2 = Ask;
         while (StayIn2) {
            RefreshRates();
            if (FirstGreater(Stop2 - 0.010, Ask) || DoublesSame(Ask, Stop2 - 0.010)) {
               double multiplier2 = (DistanceFromLimit() - 0.060) * 100 / 6.000;
               double CurrentPositionInUnits2 = PositionSize * 100000.0;
               double NewOrderInUnits2 = multiplier2 * CurrentPositionInUnits2;
               double NewOrderInLots2 = UnitsToLots(NewOrderInUnits2);
               double AmountToLeave2 = AccountBalance() * 0.1;
               double MaxPositionSize2 = (AccountFreeMargin() - AmountToLeave2) * Margin;
               if (FirstGreater(NewOrderInUnits, MaxPositionSize)) NewOrderInLots2 = UnitsToLots(MaxPositionSize2);
               PlaceOrder(NewOrderInLots2, Long);
               UpdateVariables();
               StayIn2 = false;
            }
            else {
               if (FirstGreater(Ask, Stop2)) Stop2 = Ask;
            }
         }
     }
//----
  }
//+------------------------------------------------------------------+
//| open a position                                                  |
//+------------------------------------------------------------------+
if ((OrdersTotal() == 0) && TimeGood() && FirstGreater(AccountFreeMargin(), 5000.00) && SpreadGood())
  {
//----
   double PositionInUnits = AccountFreeMargin() * StartPercentage * Margin;
   double PositionInLots = UnitsToLots(PositionInUnits);
   PlaceOrder(PositionInLots, Long);
   UpdateVariables();
//----
  }
//----
   return(0);
  }
//+------------------------------------------------------------------+

//+------------------------------------------------------------------+
//| function to close all open orders and email the balance          |
//+------------------------------------------------------------------+
void CloseAllOrders()
   {
//----
   while (OrdersTotal() > 0) {
      OrderSelect(0, SELECT_BY_POS);
      OrderClose(OrderTicket(), OrderLots(), Bid, 200, Red);
      }
   SendMail(DoubleToStr(AccountBalance(), 2), "Position Closed");  // send an email
//----
   }
   
//+------------------------------------------------------------------+
//| function to reset the global variables                           |
//+------------------------------------------------------------------+
void ResetVariables() 
   {
//---
   PositionSize = 0.000;
   AveragePrice = 0.000;
   LimitPrice = 0.000;
   if (Long) {
      Long = false;
      }
   else {
      Long = true;
      }
//---
   }
   
//+------------------------------------------------------------------+
//| function to check if the time is before 1 pm on Friday           |
//+------------------------------------------------------------------+
bool TimeGood()
   {
//----
   if (DayOfWeek() == 5 && Hour() >= 13) {
      return (false);
      }
   else {
      return (true);
      }
//----
   }
   
//+------------------------------------------------------------------+
//| function to check if the limit price has been hit                |
//+------------------------------------------------------------------+
bool LimitHit()
   {
//----
   if (Long) {
      if (FirstGreater(Bid, LimitPrice) || DoublesSame(Bid, LimitPrice)) {
         return (true);
         }
      else {
         return (false);
         }
      }
   
   else {
      if (FirstGreater(LimitPrice, Ask) || DoublesSame(Ask, LimitPrice)) {
         return (true);
         }
      else {
         return (false);
         }
      }
//----
   }
   
//+------------------------------------------------------------------+
//| function to check if two doubles are the same                    |
//+------------------------------------------------------------------+
bool DoublesSame(double number1, double number2)
   {
//----
   if (NormalizeDouble(number1 - number2, 3) == 0) {
      return (true);
      }
   else {
      return (false);
      }
//----
   }
   
//+------------------------------------------------------------------+
//| function to check if the first of two doubles is greater         |
//+------------------------------------------------------------------+
bool FirstGreater(double number1, double number2)
   {
//----
   if (NormalizeDouble(number1 - number2, 3) > 0) {
      return (true);
      }
   else {
      return (false);
      }
//----
   }
   
//+------------------------------------------------------------------+
//| function to update PositionSize, AveragePrice, and LimitPrice    |
//+------------------------------------------------------------------+
void UpdateVariables()
   {
//----
   int total = OrdersTotal();
   double PositionTemp = 0.000;
   double AverageTemp = 0.000;
   
   for (int x = 0; x < total; x++) {
      OrderSelect(x, SELECT_BY_POS);
      double SizeNew = OrderLots();
      double TempTotal = PositionTemp + SizeNew;
      double WeightOne = PositionTemp / TempTotal;
      double WeightTwo = SizeNew / TempTotal;
      double PartOne = WeightOne * AverageTemp;
      double PartTwo = WeightTwo * OrderOpenPrice();
      AverageTemp = PartOne + PartTwo;
      PositionTemp = PositionTemp + SizeNew;
      }
      
   AveragePrice = NormalizeDouble(AverageTemp, 3);
   PositionSize = NormalizeDouble(PositionTemp, 3);
   
   double LimitDistance = 0.040;
   
   if (total > 1) {
      LimitDistance = 0.020;
      }
   
   if (total > 0) {
      if (Long) {
         LimitPrice = NormalizeDouble(AveragePrice + LimitDistance, 3);
         }
      else {
         LimitPrice = NormalizeDouble(AveragePrice - LimitDistance, 3);
         }
      }
   
   if (total > 0) {
      OrderSelect(0, SELECT_BY_POS);
      if (OrderType() == 1) Long = false;
   }   
//----
   }
   
//+------------------------------------------------------------------+
//| function to determine the distance from the limit                |
//+------------------------------------------------------------------+
double DistanceFromLimit()
   {
//----
   double distance = 0.000;
   if (Long) {
      distance = Bid - LimitPrice;
      }
   else {
      distance = LimitPrice - Ask;
      }
   return (NormalizeDouble(MathAbs(distance), 3));
//----
   }

//+------------------------------------------------------------------+
//| function to check if the spread is good                          |
//+------------------------------------------------------------------+
bool SpreadGood()
   {
//----
   double difference = Ask - Bid;
   double spread = spread * 100.000;
   if (FirstGreater(3.2, spread)) {
      return (true);
      }
   else {   
      return (false);
      }
//----
   }

//+------------------------------------------------------------------+
//| function to place an order                                       |
//+------------------------------------------------------------------+
void PlaceOrder(double Size, bool LongOrShort)
   {
//----
   double Lots = NormalizeDouble(Size, 2);
   bool LongPosition = LongOrShort;
   
   // place the order if the lot size is below the maximum
   if (FirstGreater(100.0, Lots)) {
      if (LongPosition) {
         OrderSend(Symbol(), 0, Lots, Ask, 200, 0, 0, NULL, 8, 0, Green);
      }
      else {
         OrderSend(Symbol(), 1, Lots, Bid, 200, 0, 0, NULL, 8, 0, Green); 
      }
   }
   
   // otherwise, split it up into as many orders as needed
   else {
      while (FirstGreater(Lots, 100.0)) {
         if (LongPosition) {
            OrderSend(Symbol(), 0, 100.00, Ask, 200, 0, 0, NULL, 8, 0, Green);
         }
         else {
            OrderSend(Symbol(), 1, 100.00, Bid, 200, 0, 0, NULL, 8, 0, Green);
         }
         Lots = NormalizeDouble(Lots - 100.00, 2);
      }
      if (LongPosition) {
         OrderSend(Symbol(), 0, Lots, Ask, 200, 0, 0, NULL, 8, 0, Green);
      }
      else {
         OrderSend(Symbol(), 1, Lots, Bid, 200, 0, 0, NULL, 8, 0, Green);
      }
   }
//----
   }

//+------------------------------------------------------------------+
//| function to return the lot size                                  |
//+------------------------------------------------------------------+
double UnitsToLots(double ExactSize)
   {
//----
   double PositionExact = ExactSize;
   double PositionToRound = PositionExact / 1000;
   double RoundedPosition = MathRound(PositionToRound);
   double PositionInLots = RoundedPosition / 100.0;
   double FinalLots = NormalizeDouble(PositionInLots, 2);
   return (FinalLots);
//----
   }