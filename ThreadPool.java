import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 
public class ThreadPool {

   public static void main (String [] args) {
        
        final int POOLS = 40;
        
        // Runnable r1 = new ThreadTest(); 
//         Runnable r2 = new ThreadTest(); 
//         Runnable r3 = new ThreadTest(); 
//         Runnable r4 = new ThreadTest(); 
//         Runnable r5 = new ThreadTest();       
//         Runnable r6 = new ThreadTest(); 
//         Runnable r7 = new ThreadTest(); 
//         Runnable r8 = new ThreadTest(); 
//         Runnable r9 = new ThreadTest(); 
//         Runnable r10 = new ThreadTest();  
        
        Runnable r [] = new ThreadTest [POOLS];
        
        for (int i = 0; i < POOLS; i++) {
            r [i] = new ThreadTest(); 
        }
        
        // creates a thread pool with MAX_T no. of  
        // threads as the fixed pool size(Step 2) 
        ExecutorService pool = Executors.newFixedThreadPool(POOLS);   
         
        // passes the Task objects to the pool to execute (Step 3) 
        double startTime = System.currentTimeMillis();
        
        
        for (int i = 0; i < POOLS; i++) {
             pool.execute(r [i]); 
        }
        
         
        // pool.execute(r1); 
//         pool.execute(r2); 
//         pool.execute(r3); 
//         pool.execute(r4); 
//         pool.execute(r5);  
//         pool.execute(r6); 
//         pool.execute(r7); 
//         pool.execute(r8); 
//         pool.execute(r9); 
//         pool.execute(r10); 
        
        double stopTime = System.currentTimeMillis();
        
        // pool shutdown ( Step 4) 
        pool.shutdown();     
        System.out.println("ELAPSED TIME: " + (stopTime - startTime)/1000.0 + " seconds.");   
   }

}