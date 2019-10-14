import java.sql.*;
import java.io.*;

public class Positions implements Serializable{
   
   byte [][] position;
   
   public Positions () {
      position = new byte [6][7];
   } 
   
   public int getCell (int i,int j) {
      return position [i][j];
   }
   
   public void setCell (int i,int j,int val) {
       position [i][j] = (byte)val;
   }
   
   
}