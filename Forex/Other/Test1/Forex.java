/* Danny Guo
 * 01/04/11
 * Java Forex
 * Javac Forex.java
 * Forex creates a Forex object to handle 
 * historical forex data. */

import java.math.BigDecimal;

public class Forex {
  
  private String pair;
  private BigDecimal date;
  private String time;
  private BigDecimal open;
  
  public Forex(String complete) {
    String[] separate = complete.split(",");
    pair = separate[0];
    date = new BigDecimal(separate[1]);
    time = separate[2];
    open = new BigDecimal(separate[3]);
  }
  
  public String pair() {
    return pair;
  }
  
  public BigDecimal date() {
    return date;
  }
  
  public String time() {
    return time;
  }
  
  public BigDecimal open() {
    return open;
  }
  
  public static void main(String[] args) {
  }
}