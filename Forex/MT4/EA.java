// Preprocessor
// #property copyright "Danny Guo"

// External Variables
extern double StartPercentage = 0.02;
extern double AccountMargin = 50; 

// Global Variables
double PositionSize;
double AveragePrice;
double LimitPrice;
bool Long;
int StopIntervals[7];
double StopDistances[7];

// Init Function
int init() {
  ResetVariables();
  
  StopIntervals[0] = 0.00;
  StopIntervals[1] = 0.03;
  StopIntervals[2] = 0.07;
  StopIntervals[3] = 0.12;
  StopIntervals[4] = 0.18;
  StopIntervals[5] = 0.25;
  StopIntervals[6] = 0.33;
  
  StopDistances[0] = 0.01;
  StopDistances[1] = 0.02;
  StopDistances[2] = 0.03;
  StopDistances[3] = 0.04;
  StopDistances[4] = 0.05;
  StopDistances[5] = 0.06;
  StopDistances[6] = 0.07;
  
  return(0);
}

// Start Function
int start() {
  
  // Trailing Stop
  if ((OrdersTotal() > 0) && LimitHit()) {
    
    if (Long) {
      double StopPrice = LimitPrice - StopDistances[0];
      double LockPrice = LimitPrice;
      while (OrdersTotal() > 0) {
        RefreshRates();
        
        if (DoublesSame(Bid, StopPrice) || FirstGreater(StopPrice, Bid)) {
          CloseAllPositions();
          ResetVariables();
        }
        
        else {
          if (FirstGreater(Bid, LockPrice)) {
            LockPrice = Bid;
            double Distance = Bid - LimitPrice;
            int Winner = -1;
            
            for (int c = 0; c < 7; c++) {
              if (FirstGreater(Distance, StopIntervals[c]) || DoublesSame(Distance, StopIntervals[c])) Winner = c;
            }
            
            StopPrice = LockPrice - StopDistances[Winner];
          }
        }
      }
    }
    
    else {
      double StopPrice = LimitPrice + StopDistances[0];
      double LockPrice = LimitPrice;
      while (OrdersTotal() > 0) {
        RefreshRates();
        
        if (DoublesSame(Ask, StopPrice) || FirstGreater(Ask, StopPrice)) {
          CloseAllPositions(); 
          ResetVariables();
        }
        
        else {
          if (FirstGreater(LockPrice, Ask)) {
            LockPrice = Ask;
            double Distance = LimitPrice - Ask;
            int Winner = -1;
            
            for (int d = 0; d < 7; d++) {
              if (FirstGreater(Distance, StopIntervals[d]) || DoublesSame(Distance, StopIntervals[d])) Winner = d;
            }
            
            StopPrice = LockPrice + StopDistances[Winner];
          }
        }
      }
    }
    
  }
  
  // Stop Loss
  if ((OrdersTotal() > 0) && (MathMax(100.0, AccountFreeMargin()) == 100.0)) {
    
    // close all orders and reset variables
    CloseAllOrders();
    ResetVariables();
    
    // send emergency email
    SendMail("Margin Call!", "No more margin left."); 
    
    // stop the EA
    Sleep(100000000);
  } 
  
  // Regular Check
  if (OrdersTotal() > 0 && FirstGreater(DistanceFromLimit(), 10.0)) {
    
    if (Long) {
      bool StayIn = true;
      double StopTwoPrice = Bid;
      while (StayIn) {
        RefreshRates();
        if (FirstGreater(Bid, StopTwoPrice + 1.0) || DoublesSame(Bid, StopTwoPrice + 1.0)) {
          double multiplier = (DistanceFromLimit() - 6) / 6;
          double LotsExact = multiplier * PositionSize;
          StayIn = false;
        }
        
        else {
          if (FirstGreater(StopTwoPrice, Bid) {
            StopTwoPrice = Bid;
          }
              }
          
      }
    }
    
    else {
      bool StayIn = true;
      double StopTwoPrice = Ask;
      while (StayIn) {
        RefreshRates();
        
      }
      
    }
    
    }  
  
  // Open a position
  if ((OrdersTotal() == 0) && TimeGood() && AccountFreeMargin() > 5000.0) {
    
    // determine the number of lots for the initial position
    double PositionExact = AccountFreeMargin() * StartPercentage * AccountMargin;
    double RoundPosition = PositionExact/1000;
    double FloorPosition = MathFloor(RoundPosition);
    double difference = RoundPosition - FloorPosition; 
    bool AddFiveThousand = false;
    double FinalLots = FloorPosition;
    if (FirstGreater(difference, 0.5)) {
      AddFiveThousand = true;
    }
    if (AddFiveThousand) {
      FinalLots = FinalLots + 0.5;
    }
    FinalLots = FinalLots/10;
    
    // place the order if the lot size is below the maximum  
    if (FirstGreater(40, FinalLots) { 
      if (Long) {
        OrderSend(Symbol(), OP_BUY, FinalLots, Ask, 25);
      }
      else {
        OrderSend(Symbol(), OP_SELL, FinalLots, Bid, 25);
      }
    }
        
        // otherwise, split it up into as many orders as needed
        else {
          // open positions of 40 until FinalLots is less than 40
          while (FirstGreater(FinalLots, 40.0)) {
            if (Long) {
              OrderSend(Symbol(), OP_BUY, 40.0, Ask, 25)
            }
            else {
              OrderSend(Symbol(), OP_SELL, 40.0, Bid, 25);
            }
            FinalLots = FinalLots - 40.0;
          }
          
          // open the last position
          if (Long) {
            OrderSend(Symbol(), OP_BUY, FinalLots, Ask, 25)
          }
          else {
            OrderSend(Symbol(), OP_SELL, FinalLots, Bid, 25);
          }
        }
        
        // update all the variables
        UpdateVariables();     
        }
      
  }
  
  return(0);
  }

// function to close all open orders
void CloseAllOrders() {
  while (OrdersTotal() > 0) {
    OrderSelect(0, SELECT_BY_POS);
    OrderClose(OrderMagicNumber(), OrderLots(), Bid, 20, Red);
  }
}

// function to reset the global variables
void ResetVariables() {
  PositionSize = 0.000;
  AveragePrice = 0.000;
  LimitPrice = 0.000;
  if (Long) {
    Long = false;
  }
  else {
    Long = true;
  }
}

// function to check if the time is before 1 pm on Friday
bool TimeGood() {
  if (DayOfWeek() = 4 && Hour() >= 13) {
    return (false);
  }
  else {
    return (true);
  }
}

// function to check if the limit price has been hit
bool LimitHit() {
  if (Long)
  {
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
}

// function to check if two doubles are the same
bool DoublesSame(double number1, double number2) {
  if (NormalizeDouble(number1-number2, 4) == 0) {
    return (true);
  }
  else {
    return (false);
  }
}

// function to check if the first of two doubles is greater
bool FirstGreater(double number1, double number2) {
  if (NormalizeDouble(number1-number2, 4) > 0) {
    return (true);
  }
  else {
    return (false);
  }
}

// function to update PositionSize, AveragePrice, and LimitPrice
void UpdateVariables() {
  int total = OrdersTotal();
  for (int x = 0; x < total; x++) {
    OrderSelect(x, SELECT_BY_POS);
    double SizeNew = OrderLots();
    double TempTotal = PositionSize + SizeNew;
    double WeightOne = PositionSize / TempTotal;
    double WeightTwo = SizeNew / TempTotal;
    double PartOne = WeightOne * AveragePrice;
    double PartTwo = WeightTwo * OrderOpenPrice();
    AveragePrice = PartOne + PartTwo;
    PositionSize = PositionSize + SizeNew;
  }
  
  // update the LimitPrice
  if (Long) {
    LimitPrice = AveragePrice + .040;
  }
  else {
    LimitPrice = AveragePrice - .040; 
  }
}

// function to find the distance from the limit
double DistanceFromLimit() {
  double distance = 0.0;
  if (Long) {
    distance = Bid - LimitPrice();
  }
  else {
    distance = LimitPrice() - Ask;
  }
  return (distance);
}