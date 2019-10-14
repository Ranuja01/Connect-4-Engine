import java.util.*;
public class ThreadTest implements Runnable{
   
   //private String name; 
      
  //  public ThreadTest (String s) 
//    { 
//       name = s; 
//    } 
      
    // Prints task name and sleeps for 1s 
    // This Whole process is repeated 5 times 
   public void run() 
   { 
         long count = 0;
         double startTime = System.currentTimeMillis();
         for (int i = 0; i < 2000000; i++) {
            for (int j = 0; j < (1000000/40); j++) {
               count++;
            }
         }
          System.out.println(count);
         double stopTime = System.currentTimeMillis();
         System.out.println("Elapsed time was " + (stopTime - startTime)/1000.0 + " seconds."); 

   } 
   
   
   
   // public static void main (String [] args) {
//       
//    }
   

}