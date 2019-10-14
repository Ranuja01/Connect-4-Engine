import java.sql.*;
import java.io.*;

public class Games implements Serializable{
   
   Positions game [];
   
   public Games (int numPositions) {
       game = new Positions [numPositions];
       for (int i = 0; i < numPositions; i++) {
         game [i] = new Positions ();
       }
   }
   
}