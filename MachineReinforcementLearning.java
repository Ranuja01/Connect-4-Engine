/**
* The ConnectFour class.
*
* This class represents a Connect Four (TM)
* game, which allows two players to drop
* checkers into a grid until one achieves
* four checkers in a straight line.
*/	
import java.util.*;
import java.lang.*;
import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
public class MachineReinforcementLearning {
   
   
   final static byte EMPTY = 5;
   final static int NUMPLAYER = 2;    // number  of players
   final static int NUMROW = 6;       // number of rows on the game board
   final static int NUMCOL = 7;       // number of columns on the game board
   final static int NUM_ITERATIONS = 1;
   
   static byte numMove;            // num of move that has been made in this game
   static int curPlayer;          // the id number of the current player
   static boolean game = true;
   static int playedCol = 3;
   static int playedCol2 = 3;
   static int numGamesRecorded;
   static int positionCount = 0; 
   static int numLoops = 0;
   static int numDuplicates = 0;
   
   static int gameCount = 0;
   //int numLossesRecorded;
   
   static Positions grid = new Positions();   // represents the grid of the game board
   static byte score[];            // represents the scores of the players
   
   static Games gameData = new Games (42);
   static Games lossesDataBase = new Games (100000);
   static Games winsDataBase = new Games (100000);
   
   static Games uploadLossesDataBase = new Games (NUM_ITERATIONS * 4);
   static Games uploadWinsDataBase = new Games (NUM_ITERATIONS * 4);
   
   
   // byte gameData [][][] = new byte [42][6][7];
//    byte lossesDataBase [][][] = new byte [100000][6][7];
//    byte winsDataBase [][][] = new byte [100000][6][7];


/**
* Constructor:  ConnectFour
*/
   public static void main (String [] args) throws Exception {
      
      Scanner sc = new Scanner (System.in);   
   
      score = new byte [2];
      score [0] = 0;
      score [1] = 0;      
      for (int i = 0; i < NUMROW; i++){
         for (int j = 0; j < NUMCOL; j++){
            grid.setCell (i,j,EMPTY);
         }
      }
      
      loadDatabase ();
      sc.nextLine();
      double startTime = System.currentTimeMillis();  
   
      for (int j = 0; j < NUM_ITERATIONS; j++){
         game = true;
         //System.out.println(j);
         while (game) {
         //   System.out.println(j + ": " + numMove + " : " + game);
            ai ();
         //   System.out.println(j + ": " + numMove + " : " + game + " ABCD");
            if (game) {
            //    System.out.println("before");
               ai2 ();  
            //      System.out.println("after");
            }
          //  System.out.println(j + ": " + numMove + " : " + game + " WAAAAA");
           //sc.nextLine();
         }
         
      }
      double stopTime = System.currentTimeMillis();
      // for (int i = 0; i < gameCount; i++) {
   //          for (int j = 0; j < NUMROW; j++) {
   //                      
   //             for (int k = 0; k < NUMCOL; k++) {
   //                System.out.print(uploadLossesDataBase.game [i].getCell(j,k) + " ");
   //             }
   //             System.out.println();
   //          }
   //          System.out.println("\n");
   //       }
      
      uploadDatabase (); 
      System.out.println("Number of positions added: " + gameCount);
      System.out.println("Number of loops: " + numLoops);
      System.out.println("Elapsed time was " + (stopTime - startTime)/1000.0 + " seconds."); 
      System.out.println("Elapsed time was " + (System.currentTimeMillis() - stopTime)/1000.0 + " seconds."); 
      System.out.println("Number of duplicates: " + numDuplicates);
      curPlayer = 0;
   }

//    BEGINNING OF ENGINE


   public static int findRow (int column){
   
      int row = NUMROW - 1; 
      boolean loop = true;
      
      while (loop) {
         
         if (row < 0){
            return -1;
         } else if (grid.getCell(row,column) == EMPTY) {
            return row;
         } 
         row--;
      } 
      return row;					    
   }
   
   // MAKE ARRAY FOR REGULAR MOVE TO KNOW WHERE NOT TO PLACE
   
   
   public static void ai () throws Exception {
         
      boolean moveFound = false;
      curPlayer = 1;
     // numLoops = 0;
     // //System.out.println("\nAI: ");
    //  System.out.println("winning move1");
      if (!winningMove()) {
       //  System.out.println("Block1");
         if (numMove >= 5){ 
            moveFound = block ();
            
         }
       //  System.out.println("advanced Block1");
         if (numMove >=3 && !moveFound) {
            moveFound = advancedBlock();
         
         } 
      //   System.out.println("defensive move1");
         if (!moveFound) {
            moveFound = defensiveMove ();
         } 
      //   System.out.println("third eye1");
         if (!moveFound) {
            moveFound = thirdEye ();
         }
       //  System.out.println("advanced move1");        
         if (!moveFound) {
            moveFound = advancedMove ();
         }
      //   System.out.println("force move1");
         if (!moveFound) {
            moveFound = forceMove ();
         }
      //   System.out.println("gods eye1");
         if (!moveFound) {
            moveFound = godsEye ();
         }
         
       //  System.out.println("positional move1");
         if (!moveFound) {
            moveFound = positionalMove ();
         } 
      //   System.out.println("attacking move1");
         if (!moveFound) {
            moveFound = attackingMove ();
         }  
      //   System.out.println("regular move1");
         if (!moveFound) {
            regularMove ();  
         }  
      }  
      
      //gameData [numMove] = grid.clone();
      for (int i = 0; i < NUMROW; i++){
         for (int j = 0; j < NUMCOL; j++){
            gameData.game [numMove].setCell (i,j,grid.getCell (i,j));
         }
      }
      numMove++;
      
    //  //System.out.println("Number of loops: " + numLoops);
      
      ifWin();
      
      curPlayer = 0;
   
      
   }
   
   public static void ai2 () throws Exception {
         
      boolean moveFound = false;
      curPlayer = 0;
      //numLoops = 0;
      //System.out.println("\nAI 2: ");
    //  System.out.println("winning move");
      if (!winningMove2()) {
       //  System.out.println("Block");
         if (numMove >= 5){ 
            moveFound = block2 ();
            
         }
      //     System.out.println("advanced Block");
         if (numMove >=3 && !moveFound) {
            moveFound = advancedBlock2();
         
         } 
      //     System.out.println("defensive move");
         if (!moveFound) {
            moveFound = defensiveMove2 ();
         } 
      //     System.out.println("third eye");
         if (!moveFound) {
            moveFound = thirdEye2 ();
         }
      //    System.out.println("advanced move");        
         if (!moveFound) {
            moveFound = advancedMove2 ();
         }
      //    System.out.println("force move");
         if (!moveFound) {
            moveFound = forceMove2 ();
         }
      //   System.out.println("gods eye");
         if (!moveFound) {
            moveFound = godsEye2 ();
         }
         
       //  System.out.println("positional move");
         if (!moveFound) {
            moveFound = positionalMove2 ();
         } 
      //   System.out.println("attacking move");
         if (!moveFound) {
            moveFound = attackingMove2 ();
         }  
      //   System.out.println("regular move");
         if (!moveFound) {
            regularMove2 ();  
         }  
      }   
   //System.out.println("DONE!");
      for (int i = 0; i < NUMROW; i++){
         for (int j = 0; j < NUMCOL; j++){
            gameData.game [numMove].setCell (i,j,grid.getCell (i,j));
         }
      }
      
      numMove++;
      
      //System.out.println("Number of loops: " + numLoops);
      
      ifWin();
      
      //curPlayer = 0;
   
      
   }

   public static void loadDatabase () throws Exception {
        
      Games lossGame = null;
      Games winGame = null;
      int count = 0;
      positionCount = 0;
      
      // Create a variable for the connection string.
      String connectionUrl = "jdbc:sqlserver://USER:1433;databaseName=Connect4Memory;user=Ranuja;password=timetwist";
      try {
         Connection con = DriverManager.getConnection(connectionUrl); 
      
         
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT positions FROM Losses");
         
         while (rs.next()) {
         
            byte[] st = (byte[]) rs.getObject(1);
         
            ByteArrayInputStream baip = new ByteArrayInputStream(st);
            ObjectInputStream ois = new ObjectInputStream(baip);
         
            lossGame = (Games) ois.readObject();          
            
            count = 0;
            while (count < lossGame.game.length) {                   
              // System.out.println(lossGame.game.length + " AAAAAA");
              
               for (int j = 0; j < NUMROW; j++) {
                     
                  for (int k = 0; k < NUMCOL; k++) {
                     lossesDataBase.game [positionCount].setCell(j,k,lossGame.game [count].getCell(j,k));
                  }
               }
               count++;
               positionCount++;
            }
         }
      
      
         rs = stmt.executeQuery("SELECT positions FROM Wins");
         positionCount = 0;
         
         
         while (rs.next()) {
         
            byte[] st = (byte[]) rs.getObject(1);
         
            ByteArrayInputStream bais = new ByteArrayInputStream(st);
            ObjectInputStream ois = new ObjectInputStream(bais);
         
            winGame = (Games) ois.readObject();
            count = 0;
            while (count < winGame.game.length) {
            
               for (int j = 0; j < NUMROW; j++) {
                     
                  for (int k = 0; k < NUMCOL; k++) {
                     winsDataBase.game [positionCount].setCell(j,k,winGame.game [count].getCell(j,k));
                  }
               }
               count++;
               positionCount++;
            }
         
         }
      /////
      
         // for (int i = 0; i < positionCount; i++) {
      //             for (int j = 0; j < NUMROW; j++) {
      //                      
      //                for (int k = 0; k < NUMCOL; k++) {
      //                   System.out.print(lossesDataBase.game [i].getCell(j,k) + " ");
      //                }
      //                System.out.println();
      //             }
      //             System.out.println("\n");
      //          }
      
         //positionCount++;       
         //positionCount *= 4;
         System.out.println ("Number of positions: " + positionCount + "\nPress to continue");
      
      
         stmt.close();
         rs.close();
         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   
   }
   
   
   public static void uploadDatabase () throws Exception {
      
      boolean duplicateFound = false;
      int count = 0;
      int index = 0;
      
     // int positionNum = 0;
      
      Games requiredLossUpload = new Games (gameCount);
      Games requiredWinUpload = new Games (gameCount);
      
      //byte inverseGameData [][][] = new byte [42][6][7];   
        
    
      String connectionUrl = "jdbc:sqlserver://USER:1433;databaseName=Connect4Memory;user=Ranuja;password=timetwist";
      try {
         
      
         Connection con = DriverManager.getConnection(connectionUrl); 
            
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
      
      ////////////////////////////////////////
      
         
         // for (int i = 0; i < gameCount; i++) {
      //             for (int j = 0; j < NUMROW; j++) {
      //                      
      //                for (int k = 0; k < NUMCOL; k++) {
      //                   System.out.print(uploadLossesDataBase.game [i].getCell(j,k) + " ");
      //                }
      //                System.out.println();
      //             }
      //             System.out.println("\n");
      //          }
         
      
            
         requiredLossUpload.game = Arrays.copyOfRange(uploadLossesDataBase.game,0, gameCount);
         requiredWinUpload.game = Arrays.copyOfRange(uploadWinsDataBase.game ,0, gameCount);
            
         oos.writeObject(requiredLossUpload);
            
         byte[] lossesAsBytes = baos.toByteArray();
         PreparedStatement pstmt = con.prepareStatement("INSERT INTO Losses (positions) VALUES(?)");
            
         ByteArrayInputStream bais = new ByteArrayInputStream(lossesAsBytes);
            
         pstmt.setBinaryStream(1, bais, lossesAsBytes.length);
         pstmt.executeUpdate();
            
            /////
            
         oos.writeObject(requiredWinUpload);
            
         byte[] winsAsBytes = baos.toByteArray();
         pstmt = con.prepareStatement("INSERT INTO Wins (positions) VALUES(?)");
            
         bais = new ByteArrayInputStream(winsAsBytes);
            
         pstmt.setBinaryStream(1, bais, winsAsBytes.length);
         pstmt.executeUpdate();
         pstmt.close();  
      
      } catch (SQLException e) {
         e.printStackTrace();
      }
            
         
   }

   
   public static boolean lossesMemoryCheck () {
   
      for (int i = 0; i < positionCount; i++) {
         if (Arrays.deepEquals(lossesDataBase.game [i].position, grid.position)) {
            return true;
         }  
      }   
      return false;
      
   }
   
   public static boolean winsMemoryCheck () {
      
   
      for (int i = 0; i < positionCount; i++) {
         if (Arrays.deepEquals(winsDataBase.game [i].position, grid.position)) {                       
                  
            for (int j = 0; j < NUMROW; j++) {
                     
               for (int k = 0; k < NUMCOL; k++) {
                  numLoops++;
                  if (i != positionCount - 2) {
                  
                     if (winsDataBase.game [i + 1].getCell(j,k) != grid.getCell(j,k)) {
                     
                        curPlayer = 1;
                        //System.out.println("DATABASE PLAYS: COLUMN: " + j + " ROW: " + k);                       
                        grid.setCell(j,k,1);
                        playedCol = j;
                        return true;
                           
                     }
                  }
               }
                     
            }
         
         } 
      }
      return false;
      
   }
   

// CHECKS FOR WINNING MOVES TO PLAY
   
   public static boolean winningMove () {
   
      boolean win = false;
      int row = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         row = findRow (i);
         numLoops++;
         
         if (row != -1) {
         
            grid.setCell(row,i,1);
            curPlayer = 1;							
            win = checkIfWin();
            grid.setCell(row,i,EMPTY);
            
            if (win){
               //System.out.println("WINNING MOVE PLAYS: COLUMN: " + i + " ROW: " + row);
               grid.setCell(row,i,1);
               return true;
            }         
         }
      }
            
      return false;   
   }

// BLOCKS A 4TH PIECE FROM BEING PLACED IN A ROW

   public static boolean block () {
   
      boolean win = false;
      int row = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--){ 
      
         row = findRow(i);
         numLoops++;
            
         if (row != -1) {
         
            grid.setCell(row,i,0);
            curPlayer = 0;							
            win = checkIfWin();
            grid.setCell(row,i,EMPTY);
            curPlayer = 1;	
                  
            if (win){
            
            
               //System.out.println("BLOCK PLAYS: COLUMN: " + i + " ROW: " + row);
               grid.setCell(row,i,1);
               playedCol = i;
               return true;
               
            }
         }
        
      }     
      return false;
   }



// PREVENTS THE CREATION OF A DOUBLE ATTACK

   public static boolean advancedBlock () {
   
      boolean win = false;
      boolean moveFound = false;
      boolean winMethodAchieved = false;
      
      int numWinMethods = 0;
      int recordColumn = 0;
      int recordRow = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
      
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            numWinMethods = 0;
            grid.setCell(primaryRow,i,0);
            winMethodAchieved = false;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;        
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  grid.setCell(secondaryRow,p,0);                  
                  curPlayer = 0;		
                  win = checkIfWin();
                  grid.setCell(secondaryRow,p,EMPTY); 
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                  
                  if (numWinMethods == 1 && !winMethodAchieved) {
                     recordColumn = p;
                     recordRow = secondaryRow;
                     winMethodAchieved = true;
                  }
                           
                  if (numWinMethods >= 2) {
                     
                     grid.setCell(primaryRow,i,1);
                     grid.setCell(primaryRow - 1,i,0);
                     curPlayer = 0;
                     win = checkIfWin();
                     grid.setCell(primaryRow,i,EMPTY);
                     grid.setCell(primaryRow - 1,i,EMPTY);
                                    
                     if (win){
                        
                        curPlayer = 1;
                        playedCol = i;
                        grid.setCell(primaryRow,i,EMPTY);
                           
                        if (recordRow != primaryRow - 1 && recordRow != primaryRow) {
                           
                           
                           if (findRow (recordColumn) == recordRow) {
                              primaryRow = recordRow;
                           } 
                           
                        } else {
                        
                           if (findRow (p) == secondaryRow) {
                              primaryRow = secondaryRow;
                              recordColumn = p;
                           }
                        
                        }
                     
                        grid.setCell(primaryRow,recordColumn,1);                         
                           
                        //System.out.println("ADVANCED BLOCK FORCES: COLUMN: " + recordColumn + " ROW: " + primaryRow );
                        return true;
                     } else {
                        moveFound = true;
                     }
                  
                                 
                     if (moveFound) {
                        curPlayer = 1;
                        //System.out.println("ADVANCED BLOCK PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        
                                               
                        grid.setCell(primaryRow,i,1);
                        playedCol = i;
                        grid.setCell(secondaryRow,p,EMPTY);
                        return true;
                     }
                  } 
               }
                     
            }
                  
            grid.setCell(primaryRow,i,EMPTY);      
         }             
      
      }
   
      return false;
   }
   
// PREVENTS A MOVE THAT WOULD FORCE A BLOCK WHICH LETS 
// THE OPPONENT WIN BY PLACING A PIECE ON TOP OF THE BLOCK   
   
   public static boolean defensiveMove () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            grid.setCell(primaryRow,i,0); 
            
            if (primaryRow > 0) {
               
               curPlayer = 0;	
               grid.setCell(primaryRow,i,1);                
               grid.setCell(primaryRow - 1,i,0); 
               win = checkIfWin();
               grid.setCell(primaryRow - 1,i,EMPTY); 
               
               if (!win) {
               
                  grid.setCell(primaryRow,i,0);
               
                  for (int p = NUMCOL - 1; p >= 0; p--){
                     
                     win = false;        
                     secondaryRow = findRow (p);
                     numLoops++;
                     
                     if (secondaryRow != -1 && secondaryRow != 0) {
                        
                        grid.setCell(secondaryRow,p,0);
                        curPlayer = 0;		
                        win = checkIfWin();
                        grid.setCell(secondaryRow,p,EMPTY);
                                    
                        if (win) {                              
                           
                           grid.setCell(secondaryRow,p,1);
                        
                                      
                           if (secondaryRow > 0) {
                           
                              grid.setCell(secondaryRow - 1,p,0);
                              curPlayer = 0;
                              win = checkIfWin();
                              grid.setCell(secondaryRow - 1,p,EMPTY);
                              
                              if (win) {
                                 curPlayer = 1;
                                 //System.out.println("DEFENSIVE MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                              
                                 grid.setCell(primaryRow,i,1);                         
                                 playedCol = i;
                                 grid.setCell(secondaryRow,p,EMPTY);
                                 return true;
                              }
                              
                           }   
                             
                           grid.setCell(secondaryRow,p,EMPTY);
                        }
                     
                     }                     
                           
                  }
                  grid.setCell(primaryRow,i,EMPTY);  
               }
               
            }
         
            grid.setCell(primaryRow,i,EMPTY);      
         }             
      
      }
      //System.out.println("DEFENSIVE MOVE PASSES");
      return false;
   }
   
  // BLOCKS A THIRD MOVE COMBINATION 
   
   public static boolean thirdEye () {
      
      boolean win = false;
      boolean combination = false;
      int primaryRow = -1;
      int secondaryRow = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--) {
         
         primaryRow = findRow (i);         
         
         if (primaryRow != -1) {
         
            grid.setCell(primaryRow,i,0);
         
            for (int j = NUMCOL - 1; j >= 0; j--){ 
            
               secondaryRow = findRow(j);
               numLoops++;
            
               if (secondaryRow != -1) {
                  
                  grid.setCell(secondaryRow,j,0);
                  curPlayer = 0;							
                  win = checkIfWin();
                  grid.setCell(secondaryRow,j,1);
                  curPlayer = 0;	
                  
                  if (win){
                     
                     combination = defensiveForesight (); 
                  
                     if (combination == true) {
                        
                        if (primaryRow != 0) {
                           
                           grid.setCell(primaryRow,i,1);
                           grid.setCell(primaryRow - 1,i,0);
                        
                           curPlayer = 0;
                           win = checkIfWin ();
                           
                           grid.setCell(primaryRow,i,0);
                           grid.setCell(primaryRow - 1,i,EMPTY);
                           
                           if (!win) {
                              
                              curPlayer = 1;
                              //System.out.println("THIRD EYE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                           
                              grid.setCell(primaryRow,i,1);
                              playedCol = i;
                              grid.setCell(secondaryRow,j,EMPTY);
                              return true; 
                              
                           }
                           
                        }  
                          
                     }
                  
                  }
                  grid.setCell(secondaryRow,j,EMPTY);
               }
            
            }
         
            grid.setCell(primaryRow,i,EMPTY);
         
         }
      }
   
      return false;        
   }
   
   
   

// CREATES A DOUBLE WIN THREAT

   public static boolean advancedMove () {
   
      boolean win = false;
      boolean moveFound = false;
      int numWinMethods = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            numWinMethods = 0;
            grid.setCell(primaryRow,i,1);
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;         
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  grid.setCell(secondaryRow,p,1);
                  curPlayer = 1;		
                  win = checkIfWin();
                  grid.setCell(secondaryRow,p,EMPTY);
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                           
                  if (numWinMethods >= 2) {
                  
                     if (primaryRow > 0){
                        grid.setCell(primaryRow - 1,i,0);
                     
                        curPlayer = 0;
                        win = checkIfWin();
                        grid.setCell(primaryRow - 1,i,EMPTY);
                                    
                        if (!win){
                           moveFound = true;
                        }
                     } else {
                        moveFound = true;
                     }
                                 
                     if (moveFound) {
                     
                        curPlayer = 1;
                        //System.out.println("ADVANCED MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                     
                        grid.setCell(primaryRow,i,1);                         
                        playedCol = i;
                        grid.setCell(secondaryRow,p,EMPTY);
                        return true;
                        
                     }
                  }
               } 
            
                     
            }
                  
            grid.setCell(primaryRow,i,EMPTY);      
         }             
      
      }
      //System.out.println("ADVANCED MOVE PASSES");
      return false;
   }
   
// FORCES A BLOCK TO CREATE A WINNING THREAT

   public static boolean forceMove () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1){
            
            grid.setCell(primaryRow,i,1);
            
            if (primaryRow > 0) {
               
               if (primaryRow != 0) {
                  
                  grid.setCell(primaryRow - 1,i,0);
                  curPlayer = 0;		
                  win = checkIfWin();
                  grid.setCell(primaryRow - 1,i,EMPTY);
                  
               }
               
               
               if (!win) {
                  grid.setCell(primaryRow,i,0);
                   
                  for (int p = NUMCOL - 1; p >= 0; p--){
                  
                     win = false;        
                     secondaryRow = findRow (p);
                     numLoops++;
                     
                     if (secondaryRow != -1 && secondaryRow != 0) {
                        
                        grid.setCell(secondaryRow,p,1);
                        curPlayer = 1;		
                        win = checkIfWin();
                        grid.setCell(secondaryRow,p,EMPTY);
                                    
                        if (win) {                              
                           
                           grid.setCell(secondaryRow,p,0);
                                                              
                           if (secondaryRow > 0) {
                              
                              grid.setCell(secondaryRow - 1,p,1);
                              curPlayer = 1;
                              win = checkIfWin();
                              grid.setCell(secondaryRow -1,p,EMPTY);
                              
                              if (win) {
                              
                                 curPlayer = 1;
                                 //System.out.println("FORCE MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                              
                                 grid.setCell(primaryRow,i,1);                      
                                 playedCol = i;
                                 grid.setCell(secondaryRow,p,EMPTY);
                                 return true;                            
                              }                       
                           }                    
                           grid.setCell(secondaryRow,p,EMPTY);
                        }    
                     }                             
                  }
                   
                  grid.setCell(primaryRow,i,EMPTY);  
               } 
            }    
         
            grid.setCell(primaryRow,i,EMPTY);     
         }             
      
      }
      //System.out.println("FORCE MOVE PASSES");
      return false;
   }
   
   public static boolean godsEye () {
      
      boolean win = false;
      boolean combination = false;
      int primaryRow = -1;
      int secondaryRow = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--) {
         
         primaryRow = findRow (i);         
         
         if (primaryRow != -1) {
            grid.setCell(primaryRow,i,1); 
         
            for (int j = NUMCOL - 1; j >= 0; j--){ 
            
               secondaryRow = findRow(j);
               numLoops++;
            
               if (secondaryRow != -1) {
                  
                  grid.setCell(secondaryRow,j,1); 
                  curPlayer = 1;							
                  win = checkIfWin();
                  grid.setCell(secondaryRow,j,0);
                  curPlayer = 1;	///
                  
                  if (win){
                     ////System.out.println("AAAAAHH: " + i + " ROW: " + primaryRow);
                     combination = defensiveForesight (); 
                  
                     if (combination == true) {
                        
                        if (primaryRow != 0) {
                           grid.setCell(primaryRow,i,1);
                           grid.setCell(primaryRow - 1,i,0);
                           curPlayer = 0;                          
                           win = checkIfWin ();
                           grid.setCell(primaryRow - 1,i,EMPTY);
                           
                           if (!win) {
                              
                              curPlayer = 1;
                              //System.out.println("GODS EYE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                           
                              grid.setCell(primaryRow,i,1);
                              playedCol = i;
                              grid.setCell(secondaryRow,j,EMPTY);                              
                              return true; 
                              
                           }
                           
                        }
                     
                          
                     }
                  
                  }
                  grid.setCell(secondaryRow,j,EMPTY);
               }
            
            }
            
            grid.setCell(primaryRow,i,EMPTY);
         
         }
      }
   
      return false;        
   }
   
// RETAINS POSITIONAL CONTROL BY TAKING AND BLOCKING THE CONTROL OF SQUARES
   
   public static boolean positionalMove () {
      
      boolean win = false;
      boolean moveFound = false;
      boolean databaseCheck = false;
      boolean foresight = false;
      int temp = 0;
      
      int primaryRow = -1;
            
      for (int i = NUMCOL - 1; i >= 0; i--){ 
        // System.out.println("Before check: " + i);
         temp = i;
         databaseCheck = false;
         foresight = false;
         primaryRow = findRow(i);
         numLoops++;
         
         if (primaryRow != -1){
            
            
            if (primaryRow > 0) {
                         
               grid.setCell(primaryRow - 1,i,0);
               curPlayer = 0;
               win = checkIfWin ();
               grid.setCell(primaryRow - 1,i,EMPTY);
               if (!win) {
                        
                  grid.setCell(primaryRow,i,1);
                  curPlayer = 1;
                  
                  if (primaryRow != NUMROW -1) {
                     
                     if (i != 0 && grid.getCell(primaryRow,i - 1) == EMPTY) {
                        grid.setCell(primaryRow,i - 1,1);
                        win = ifHorizontal ();
                        grid.setCell(primaryRow,i - 1,EMPTY);
                     }
                  
                     if (win) {
                        moveFound = true;
                     }
                                     
                     if (i != NUMCOL - 1 && grid.getCell(primaryRow,i + 1) == EMPTY) {
                     
                        
                        grid.setCell(primaryRow,i + 1,1);
                        win = ifHorizontal ();
                        grid.setCell(primaryRow,i + 1,EMPTY);
                     }
                  
                     if (win) {
                        moveFound = true;
                     }
                  
                  }
                  
                  grid.setCell(primaryRow,i,0);
                  curPlayer = 0;  
                  
                  grid.setCell(primaryRow - 1,i,0);
                  win = ifVertical ();
                  grid.setCell(primaryRow - 1,i,EMPTY);
               
                  if (win) {
                     moveFound = true;
                  }
               }
               
                     
               if (i >= 1 && i <= NUMCOL - 1) {
                        
                  if (primaryRow > 0 && grid.getCell(primaryRow - 1,i - 1) == EMPTY) {
                     
                     grid.setCell(primaryRow - 1,i - 1,0);      
                     win = upLeft();
                     grid.setCell(primaryRow - 1,i - 1,EMPTY);      
                           
                     if (win) {
                        ////System.out.println ("AAAAA");
                        moveFound = true;
                     }
                  }
                        
                        
                  if (primaryRow < NUMROW - 1 && grid.getCell(primaryRow + 1,i - 1) == EMPTY) {
                     
                     grid.setCell(primaryRow + 1,i - 1,0);      
                     win = upRight();
                     grid.setCell(primaryRow + 1,i - 1,EMPTY);
                        
                     if (win) {
                        ////System.out.println ("BBB");
                        moveFound = true;
                     }
                  }
                        
                        
               }
                  
               if (i >= 0 && i <= NUMCOL - 2 ) {
                        
                  if (primaryRow < NUMROW - 1 && grid.getCell(primaryRow + 1,i + 1) == EMPTY) {
                     
                     grid.setCell(primaryRow + 1,i + 1,0);      
                     win = upLeft();
                     grid.setCell(primaryRow + 1,i + 1,EMPTY); 
                        
                     if (win) {
                        ////System.out.println ("CCC");
                        moveFound = true;
                     }
                  }
                        
                  if (primaryRow > 0 && grid.getCell(primaryRow - 1,i + 1) == EMPTY) {
                     
                     grid.setCell(primaryRow - 1,i + 1,0);      
                     win = upRight();
                     grid.setCell(primaryRow - 1,i + 1,EMPTY);
                        
                     if (win) {
                        ////System.out.println ("D");
                        moveFound = true;
                     }
                        
                  }
               }
                  
               grid.setCell(primaryRow,i,EMPTY);
               
               
               
                  
               if (findRow (3) == 2) {
                  
                  moveFound = true;
                  i = 3;
                  primaryRow = 2;
                     
               } else if (grid.getCell(2,3) == 0 && grid.getCell(3,2) == EMPTY && grid.getCell(4,1) == EMPTY && grid.getCell(5,0) == EMPTY) {
                     
                  if (grid.getCell(4,2) != 0 || grid.getCell(4,3) != 0) {
                     moveFound = true;
                     i = 0;
                     primaryRow = 5;
                  }
                     
               } else if (grid.getCell(2,3) == 0 && grid.getCell(3,4) == EMPTY && grid.getCell(4,5) == EMPTY && grid.getCell(5,6) == EMPTY) {
                     
                  if (grid.getCell(4,5) != 0 || grid.getCell(4,4) != 0) {
                     moveFound = true;
                     i = 6;
                     primaryRow = 5;
                  }
               }
                  
               if (moveFound) {
                  curPlayer = 1;
                  
                  grid.setCell(primaryRow - 1,i,1);
                  win = checkIfWin ();
                  grid.setCell(primaryRow - 1,i,EMPTY);
                  
                  if (win) {
                     moveFound = false;
                  }
               }
                  
               if (moveFound) {
                  
                  grid.setCell(primaryRow,i,1);
                 // System.out.println("Before check: ");
                  databaseCheck = lossesMemoryCheck ();
                //  System.out.println("after check: ");
                  grid.setCell(primaryRow,i,EMPTY);
                  
                  if (!databaseCheck) {
                     if (winsMemoryCheck ()){
                        return true;
                     }
                  } else {
                     //System.out.println("DATABASE REDIRECTS: COLUMN: " + i + " ROW: " + primaryRow);
                  }
                  
                  
                  grid.setCell(primaryRow,i,1);
                  curPlayer = 0;
                  foresight = defensiveForesight ();                  
                  grid.setCell(primaryRow,i,EMPTY);
                        
                  if (!databaseCheck && !foresight) {
                     curPlayer = 1;
                     //System.out.println("POSITIONAL MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                     
                     grid.setCell(primaryRow,i,1);
                     playedCol = i;
                     return true;   
                  }
                  
                    
               }
               i = temp;
            } 
            grid.setCell(primaryRow,i,EMPTY);        
         }
      }
      return false;
   }
  
// IMPROVES POSITIONAL PLAY BY FORCING 
// BLOCKS IN ORDER TO GET MORE SPACIAL CONTROL 
  
   public static boolean attackingMove () {
   
      boolean win = false;
      boolean horizontalWin = false;
      boolean moveFound = false;
      boolean databaseCheck = false;
      boolean foresight = false;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         databaseCheck = false;
         foresight = false;
         primaryRow = findRow(i);
         
         if (primaryRow != -1){
            
            grid.setCell(primaryRow,i,1);
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;         
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  
                  curPlayer = 1;		
                     
                  if (primaryRow != 0) {
                     curPlayer = 0;
                     grid.setCell(primaryRow - 1,i,0);
                     win = checkIfWin ();
                     grid.setCell(primaryRow - 1,i,EMPTY);
                  }      
                     
                  if (!win) {
                        
                     win = false;
                     curPlayer = 1;
                     if (primaryRow >= NUMROW - 3 && p != i) {
                        grid.setCell(secondaryRow,p,1);
                        horizontalWin = ifHorizontal();
                        win = horizontalWin;
                        grid.setCell(secondaryRow,p,EMPTY);                   
                     } 
                        
                     if (primaryRow >= 1 && primaryRow <= NUMROW - 2 && !horizontalWin) {
                        grid.setCell(secondaryRow,p,1);
                        win = ifVertical();
                        grid.setCell(secondaryRow,p,EMPTY);  
                     }
                        
                     if (win) {
                        
                        grid.setCell(primaryRow,i,1);
                        databaseCheck = lossesMemoryCheck ();
                        grid.setCell(primaryRow,i,EMPTY);
                        
                        if (!databaseCheck) {
                           if (winsMemoryCheck ()){
                              return true;
                           }
                        } else {
                           //System.out.println("DATABASE REDIRECTS: COLUMN: " + i + " ROW: " + primaryRow);
                        }
                        
                        grid.setCell(primaryRow,i,1);
                        curPlayer = 0;
                        foresight = defensiveForesight ();                  
                        grid.setCell(primaryRow,i,EMPTY);
                        
                        if (!databaseCheck && !foresight) {
                        
                           //System.out.println("ATTACKING MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        
                           grid.setCell(primaryRow,i,1);
                           grid.setCell(secondaryRow,p,EMPTY);                           
                           playedCol = i;
                           return true;
                        }
                     }
                        
                  }
               
                  grid.setCell(secondaryRow,p,EMPTY); 
               }
            
            }   
                      
            grid.setCell(primaryRow,i,EMPTY);  
         }
         
      }
      //System.out.println("ATTACKING MOVE PASSES");
      return false;
   }


// PLAYS A REGULAR MOVE IF NO SPECIAL MOVES ARE AVAILABLE

   public static void regularMove () {
      
      boolean win = false;
      boolean loop = true;
      boolean foresight = true;
      boolean databaseCheck = false;
      
      int row = -1;
      int column;
      int numTries = 0;
      int length = 7;
      
   
      while (loop) {
         foresight = false;
         databaseCheck = false;
         numTries++;
         numLoops++;
         
         if (numTries % 200 == 0) {
            //System.out.println("NUMTRIES: " + numTries); 
         }
          
         column = chooseColumn (numTries);
      
         row = findRow (column);
         if (row != -1) {
            
            win = false;
            
            curPlayer = 1;         
            if (row > 0){
               
               grid.setCell(row - 1,column,0);
               curPlayer = 0;
               win = checkIfWin();
               grid.setCell(row - 1,column,EMPTY);
             
               if (numTries > 4000) {
                  curPlayer = 1;
               
                  grid.setCell(row,column,1);
                  playedCol = column;
                  //System.out.println("REGULAR MOVE FORCES: COLUMN: " + column + " ROW: " + row);
                  loop = false;
               }
                 
               if (loop && !win){
                  win = false;
                  
                  if (numTries < 2000) {
                     
                     grid.setCell(row,column,1);
                     databaseCheck = lossesMemoryCheck ();
                     grid.setCell(row,column,EMPTY);
                        
                     if (!databaseCheck) {
                        if (winsMemoryCheck ()){
                           loop = false;
                        }
                     } else {
                        //System.out.println("DATABASE REDIRECTS: COLUMN: " + column + " ROW: " + row);
                     
                     }
                     
                     grid.setCell(row - 1,column,1);
                     curPlayer = 1;
                     win = checkIfWin();
                     grid.setCell(row - 1,column,EMPTY);
                  } 
                  
                  if (numMove < 20) {
                     grid.setCell(row,column,1);
                     curPlayer = 0;
                     foresight = defensiveForesight ();
                     grid.setCell(row,column,EMPTY);
                  } else {
                     foresight = false;
                  }
                  if (!win  && !foresight &&!databaseCheck) {
                     curPlayer = 1;
                  
                     grid.setCell(row,column,1);
                     playedCol = column;
                     //System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
                     loop = false;
                  }
                  
                  
               }  
               
                
               
            } else {
               curPlayer = 1;
            
               grid.setCell(row,column,1);
               playedCol = column;
               //System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
               loop = false;
            } 
         }
            
         
      } 
   }


   public static int chooseColumn (int numTries) {
      int column = 3;
      int min = 0;
      int max = 0;
      int row = 0;;
      
      if (numTries > 50) {
      
         column = (int)(Math.random() * NUMCOL); 
               
      } else if (playedCol == 0 || playedCol == 1) {
         column = randomColumnGenerator (0, 2);
      } else if (playedCol == 2 ) {
         column = randomColumnGenerator (1, 3);
      } else if (playedCol == 0 || playedCol == 1) {
         column = randomColumnGenerator (1, 3);
      } else if (playedCol == 3) {
         column = randomColumnGenerator (2, 4);
      } else if (playedCol == 4) {
         column = randomColumnGenerator (3, 5);
      } else if (playedCol == 5 || playedCol == 6) {
         column = randomColumnGenerator (4, 6);
      }
         
      if (numMove < 25) {
         ////System.out.println("COLUMN: " + column);    
         if (column >= 1 && column <= 5) {
            row = findRow (column);
            if (row != -1) {
               
               if (row == NUMROW - 1) {
               
                  return column;
               }
               
               if (grid.getCell(row + 1,column) != 0) {
                  if (grid.getCell(row,column - 1) != 0 && grid.getCell(row,column + 1) != 0 && row > 1) {
                     return column;
                  }
               }  
            }
         }
         
      }   
         
      return column;  
   }   
   
   /////////////////////////////////////////////////////////////////////
   
   public static boolean winningMove2 () {
   
      boolean win = false;
      int row = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         row = findRow (i);
         numLoops++;
         
         if (row != -1) {
         
            grid.setCell(row,i,0);
            curPlayer = 0;							
            win = checkIfWin();
            grid.setCell(row,i,EMPTY);
            
            if (win){
               //System.out.println("WINNING MOVE 2 PLAYS: COLUMN: " + i + " ROW: " + row);
               grid.setCell(row,i,0);
               return true;
            }         
         }
      }
            
      return false;   
   }

// BLOCKS A 4TH PIECE FROM BEING PLACED IN A ROW

   public static boolean block2 () {
   
      boolean win = false;
      int row = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--){ 
      
         row = findRow(i);
         numLoops++;
            
         if (row != -1) {
         
            grid.setCell(row,i,1);
            curPlayer = 1;							
            win = checkIfWin();
            grid.setCell(row,i,EMPTY);
            curPlayer = 0;	
                  
            if (win){
            
            
               //System.out.println("BLOCK 2 PLAYS: COLUMN: " + i + " ROW: " + row);
               grid.setCell(row,i,0);
               playedCol2 = i;
               return true;
               
            }
         }
        
      }     
      return false;
   }



// PREVENTS THE CREATION OF A DOUBLE ATTACK

   public static boolean advancedBlock2 () {
   
      boolean win = false;
      boolean moveFound = false;
      boolean winMethodAchieved = false;
      
      int numWinMethods = 0;
      int recordColumn = 0;
      int recordRow = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
      
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            numWinMethods = 0;
            grid.setCell(primaryRow,i,1);
            winMethodAchieved = false;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;        
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  grid.setCell(secondaryRow,p,1);                  
                  curPlayer = 1;		
                  win = checkIfWin();
                  grid.setCell(secondaryRow,p,EMPTY); 
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                  
                  if (numWinMethods == 1 && !winMethodAchieved) {
                     recordColumn = p;
                     recordRow = secondaryRow;
                     winMethodAchieved = true;
                  }
                           
                  if (numWinMethods >= 2) {
                     
                     grid.setCell(primaryRow,i,0);
                     grid.setCell(primaryRow - 1,i,1);
                     curPlayer = 1;
                     win = checkIfWin();
                     grid.setCell(primaryRow,i,EMPTY);
                     grid.setCell(primaryRow - 1,i,EMPTY);
                                    
                     if (win){
                        
                        curPlayer = 1;
                        playedCol2 = i;
                        grid.setCell(primaryRow,i,EMPTY);
                           
                        if (recordRow != primaryRow - 1 && recordRow != primaryRow) {
                           
                           
                           if (findRow (recordColumn) == recordRow) {
                              primaryRow = recordRow;
                           } 
                           
                        } else {
                        
                           if (findRow (p) == secondaryRow) {
                              primaryRow = secondaryRow;
                              recordColumn = p;
                           }
                        
                        }
                     
                        grid.setCell(primaryRow,recordColumn,0);                         
                           
                        //System.out.println("ADVANCED BLOCK 2 FORCES: COLUMN: " + recordColumn + " ROW: " + primaryRow );
                        return true;
                     } else {
                        moveFound = true;
                     }
                  
                                 
                     if (moveFound) {
                        curPlayer = 0;
                        //System.out.println("ADVANCED BLOCK 2 PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        
                                               
                        grid.setCell(primaryRow,i,0);
                        playedCol2 = i;
                        grid.setCell(secondaryRow,p,EMPTY);
                        return true;
                     }
                  } 
               }
                     
            }
                  
            grid.setCell(primaryRow,i,EMPTY);      
         }             
      
      }
   
      return false;
   }
   
// PREVENTS A MOVE THAT WOULD FORCE A BLOCK WHICH LETS 
// THE OPPONENT WIN BY PLACING A PIECE ON TOP OF THE BLOCK   
   
   public static boolean defensiveMove2 () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            grid.setCell(primaryRow,i,1); 
            
            if (primaryRow > 0) {
               
               curPlayer = 1;	
               grid.setCell(primaryRow,i,0);                
               grid.setCell(primaryRow - 1,i,1); 
               win = checkIfWin();
               grid.setCell(primaryRow - 1,i,EMPTY); 
               
               if (!win) {
               
                  grid.setCell(primaryRow,i,1);
               
                  for (int p = NUMCOL - 1; p >= 0; p--){
                     
                     win = false;        
                     secondaryRow = findRow (p);
                     numLoops++;
                     
                     if (secondaryRow != -1 && secondaryRow != 0) {
                        
                        grid.setCell(secondaryRow,p,1);
                        curPlayer = 1;		
                        win = checkIfWin();
                        grid.setCell(secondaryRow,p,EMPTY);
                                    
                        if (win) {                              
                           
                           grid.setCell(secondaryRow,p,0);
                        
                                      
                           if (secondaryRow > 0) {
                           
                              grid.setCell(secondaryRow - 1,p,1);
                              curPlayer = 1;
                              win = checkIfWin();
                              grid.setCell(secondaryRow - 1,p,EMPTY);
                              
                              if (win) {
                                 curPlayer = 0;
                                 //System.out.println("DEFENSIVE MOVE 2 PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                              
                                 grid.setCell(primaryRow,i,0);                         
                                 playedCol2 = i;
                                 grid.setCell(secondaryRow,p,EMPTY);
                                 return true;
                              }
                              
                           }   
                             
                           grid.setCell(secondaryRow,p,EMPTY);
                        }
                     
                     }                     
                           
                  }
                  grid.setCell(primaryRow,i,EMPTY);  
               }
               
            }
         
            grid.setCell(primaryRow,i,EMPTY);      
         }             
      
      }
      //System.out.println("DEFENSIVE MOVE 2 PASSES");
      return false;
   }
   
  // BLOCKS A THIRD MOVE COMBINATION 
   
   public static boolean thirdEye2 () {
      
      boolean win = false;
      boolean combination = false;
      int primaryRow = -1;
      int secondaryRow = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--) {
         
         primaryRow = findRow (i);         
         
         if (primaryRow != -1) {
         
            grid.setCell(primaryRow,i,1);
         
            for (int j = NUMCOL - 1; j >= 0; j--){ 
            
               secondaryRow = findRow(j);
               numLoops++;
            
               if (secondaryRow != -1) {
                  
                  grid.setCell(secondaryRow,j,1);
                  curPlayer = 1;							
                  win = checkIfWin();
                  grid.setCell(secondaryRow,j,0);
                  curPlayer = 1;	
                  
                  if (win){
                     
                     combination = defensiveForesight (); 
                  
                     if (combination == true) {
                        
                        if (primaryRow != 0) {
                           
                           grid.setCell(primaryRow,i,0);
                           grid.setCell(primaryRow - 1,i,1);
                        
                           curPlayer = 1;
                           win = checkIfWin ();
                           
                           grid.setCell(primaryRow,i,1);
                           grid.setCell(primaryRow - 1,i,EMPTY);
                           
                           if (!win) {
                              
                              curPlayer = 1;
                              //System.out.println("THIRD EYE 2 PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                           
                              grid.setCell(primaryRow,i,0);
                              playedCol2 = i;
                              grid.setCell(secondaryRow,j,EMPTY);
                              return true; 
                              
                           }
                           
                        }  
                          
                     }
                  
                  }
                  grid.setCell(secondaryRow,j,EMPTY);
               }
            
            }
         
            grid.setCell(primaryRow,i,EMPTY);
         
         }
      }
   
      return false;        
   }
   
   
   

// CREATES A DOUBLE WIN THREAT

   public static boolean advancedMove2 () {
   
      boolean win = false;
      boolean moveFound = false;
      int numWinMethods = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            numWinMethods = 0;
            grid.setCell(primaryRow,i,0);
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;         
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  grid.setCell(secondaryRow,p,0);
                  curPlayer = 0;		
                  win = checkIfWin();
                  grid.setCell(secondaryRow,p,EMPTY);
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                           
                  if (numWinMethods >= 2) {
                  
                     if (primaryRow > 0){
                        grid.setCell(primaryRow - 1,i,1);
                     
                        curPlayer = 1;
                        win = checkIfWin();
                        grid.setCell(primaryRow - 1,i,EMPTY);
                                    
                        if (!win){
                           moveFound = true;
                        }
                     } else {
                        moveFound = true;
                     }
                                 
                     if (moveFound) {
                     
                        curPlayer = 0;
                        //System.out.println("ADVANCED MOVE 2 PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                     
                        grid.setCell(primaryRow,i,0);                         
                        playedCol2 = i;
                        grid.setCell(secondaryRow,p,EMPTY);
                        return true;
                        
                     }
                  }
               } 
            
                     
            }
                  
            grid.setCell(primaryRow,i,EMPTY);      
         }             
      
      }
      //System.out.println("ADVANCED MOVE 2 PASSES");
      return false;
   }
   
// FORCES A BLOCK TO CREATE A WINNING THREAT

   public static boolean forceMove2 () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1){
            
            grid.setCell(primaryRow,i,0);
            
            if (primaryRow > 0) {
               
               if (primaryRow != 0) {
                  
                  grid.setCell(primaryRow - 1,i,1);
                  curPlayer = 1;		
                  win = checkIfWin();
                  grid.setCell(primaryRow - 1,i,EMPTY);
                  
               }
               
               
               if (!win) {
                  grid.setCell(primaryRow,i,1);
                   
                  for (int p = NUMCOL - 1; p >= 0; p--){
                  
                     win = false;        
                     secondaryRow = findRow (p);
                     numLoops++;
                     
                     if (secondaryRow != -1 && secondaryRow != 0) {
                        
                        grid.setCell(secondaryRow,p,0);
                        curPlayer = 0;		
                        win = checkIfWin();
                        grid.setCell(secondaryRow,p,EMPTY);
                                    
                        if (win) {                              
                           
                           grid.setCell(secondaryRow,p,1);
                                                              
                           if (secondaryRow > 0) {
                              
                              grid.setCell(secondaryRow - 1,p,0);
                              curPlayer = 0;
                              win = checkIfWin();
                              grid.setCell(secondaryRow -1,p,EMPTY);
                              
                              if (win) {
                              
                                 curPlayer = 1;
                                 //System.out.println("FORCE MOVE 2 PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                              
                                 grid.setCell(primaryRow,i,0);                      
                                 playedCol2 = i;
                                 grid.setCell(secondaryRow,p,EMPTY);
                                 return true;                            
                              }                       
                           }                    
                           grid.setCell(secondaryRow,p,EMPTY);
                        }    
                     }                             
                  }
                   
                  grid.setCell(primaryRow,i,EMPTY);  
               } 
            }    
         
            grid.setCell(primaryRow,i,EMPTY);     
         }             
      
      }
      //System.out.println("FORCE MOVE 2 PASSES");
      return false;
   }
   
   public static boolean godsEye2 () {
      
      boolean win = false;
      boolean combination = false;
      int primaryRow = -1;
      int secondaryRow = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--) {
         
         primaryRow = findRow (i);         
         
         if (primaryRow != -1) {
            grid.setCell(primaryRow,i,0); 
         
            for (int j = NUMCOL - 1; j >= 0; j--){ 
            
               secondaryRow = findRow(j);
               numLoops++;
            
               if (secondaryRow != -1) {
                  
                  grid.setCell(secondaryRow,j,0); 
                  curPlayer = 0;							
                  win = checkIfWin();
                  grid.setCell(secondaryRow,j,1);
                  curPlayer = 0;	///
                  
                  if (win){
                     ////System.out.println("AAAAAHH: " + i + " ROW: " + primaryRow);
                     combination = defensiveForesight (); 
                  
                     if (combination == true) {
                        
                        if (primaryRow != 0) {
                           grid.setCell(primaryRow,i,0);
                           grid.setCell(primaryRow - 1,i,1);
                           curPlayer = 1;                          
                           win = checkIfWin ();
                           grid.setCell(primaryRow - 1,i,EMPTY);
                           
                           if (!win) {
                              
                              curPlayer = 0;
                              //System.out.println("GODS EYE 2 PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                           
                              grid.setCell(primaryRow,i,0);
                              playedCol2 = i;
                              grid.setCell(secondaryRow,j,EMPTY);                              
                              return true; 
                              
                           }
                           
                        }
                     
                          
                     }
                  
                  }
                  grid.setCell(secondaryRow,j,EMPTY);
               }
            
            }
            
            grid.setCell(primaryRow,i,EMPTY);
         
         }
      }
   
      return false;        
   }
   
// RETAINS POSITIONAL CONTROL BY TAKING AND BLOCKING THE CONTROL OF SQUARES
   
   public static boolean positionalMove2 () {
      
      boolean win = false;
      boolean moveFound = false;
      boolean databaseCheck = false;
      boolean foresight = false;
      int temp = 0;
      
      int primaryRow = -1;
            
      for (int i = NUMCOL - 1; i >= 0; i--){ 
        // System.out.println("Before check: " + i);
         temp = i;
         databaseCheck = false;
         foresight = false;
         primaryRow = findRow(i);
         numLoops++;
         
         if (primaryRow != -1){
            
            
            if (primaryRow > 0) {
                         
               grid.setCell(primaryRow - 1,i,1);
               curPlayer = 1;
               win = checkIfWin ();
               grid.setCell(primaryRow - 1,i,EMPTY);
               if (!win) {
                        
                  grid.setCell(primaryRow,i,0);
                  curPlayer = 0;
                  
                  if (primaryRow != NUMROW -1) {
                     
                     if (i != 0 && grid.getCell(primaryRow,i - 1) == EMPTY) {
                        grid.setCell(primaryRow,i - 1,0);
                        win = ifHorizontal ();
                        grid.setCell(primaryRow,i - 1,EMPTY);
                     }
                  
                     if (win) {
                        moveFound = true;
                     }
                                     
                     if (i != NUMCOL - 1 && grid.getCell(primaryRow,i + 1) == EMPTY) {
                     
                        
                        grid.setCell(primaryRow,i + 1,0);
                        win = ifHorizontal ();
                        grid.setCell(primaryRow,i + 1,EMPTY);
                     }
                  
                     if (win) {
                        moveFound = true;
                     }
                  
                  }
                  
                  grid.setCell(primaryRow,i,1);
                  curPlayer = 0;  
                  
                  grid.setCell(primaryRow - 1,i,1);
                  win = ifVertical ();
                  grid.setCell(primaryRow - 1,i,EMPTY);
               
                  if (win) {
                     moveFound = true;
                  }
               }
               
                     
               if (i >= 1 && i <= NUMCOL - 1) {
                        
                  if (primaryRow > 0 && grid.getCell(primaryRow - 1,i - 1) == EMPTY) {
                     
                     grid.setCell(primaryRow - 1,i - 1,1);      
                     win = upLeft();
                     grid.setCell(primaryRow - 1,i - 1,EMPTY);      
                           
                     if (win) {
                        ////System.out.println ("AAAAA");
                        moveFound = true;
                     }
                  }
                        
                        
                  if (primaryRow < NUMROW - 1 && grid.getCell(primaryRow + 1,i - 1) == EMPTY) {
                     
                     grid.setCell(primaryRow + 1,i - 1,1);      
                     win = upRight();
                     grid.setCell(primaryRow + 1,i - 1,EMPTY);
                        
                     if (win) {
                        ////System.out.println ("BBB");
                        moveFound = true;
                     }
                  }
                        
                        
               }
                  
               if (i >= 0 && i <= NUMCOL - 2 ) {
                        
                  if (primaryRow < NUMROW - 1 && grid.getCell(primaryRow + 1,i + 1) == EMPTY) {
                     
                     grid.setCell(primaryRow + 1,i + 1,1);      
                     win = upLeft();
                     grid.setCell(primaryRow + 1,i + 1,EMPTY); 
                        
                     if (win) {
                        ////System.out.println ("CCC");
                        moveFound = true;
                     }
                  }
                        
                  if (primaryRow > 0 && grid.getCell(primaryRow - 1,i + 1) == EMPTY) {
                     
                     grid.setCell(primaryRow - 1,i + 1,1);      
                     win = upRight();
                     grid.setCell(primaryRow - 1,i + 1,EMPTY);
                        
                     if (win) {
                        ////System.out.println ("D");
                        moveFound = true;
                     }
                        
                  }
               }
                  
               grid.setCell(primaryRow,i,EMPTY);
               
               
               
                  
               if (findRow (3) == 2) {
                  
                  moveFound = true;
                  i = 3;
                  primaryRow = 2;
                     
               } else if (grid.getCell(2,3) == 1 && grid.getCell(3,2) == EMPTY && grid.getCell(4,1) == EMPTY && grid.getCell(5,0) == EMPTY) {
                     
                  if (grid.getCell(4,2) != 1 || grid.getCell(4,3) != 1) {
                     moveFound = true;
                     i = 0;
                     primaryRow = 5;
                  }
                     
               } else if (grid.getCell(2,3) == 1 && grid.getCell(3,4) == EMPTY && grid.getCell(4,5) == EMPTY && grid.getCell(5,6) == EMPTY) {
                     
                  if (grid.getCell(4,5) != 1 || grid.getCell(4,4) != 1) {
                     moveFound = true;
                     i = 6;
                     primaryRow = 5;
                  }
               }
                  
               if (moveFound) {
                  curPlayer = 0;
                  
                  grid.setCell(primaryRow - 1,i,0);
                  win = checkIfWin ();
                  grid.setCell(primaryRow - 1,i,EMPTY);
                  
                  if (win) {
                     moveFound = false;
                  }
               }
                  
               if (moveFound) {
                  
                  grid.setCell(primaryRow,i,0);
                  // System.out.println("Before check");
                  databaseCheck = lossesMemoryCheck ();
                  // System.out.println("after check");
                  grid.setCell(primaryRow,i,EMPTY);
                  
                  if (!databaseCheck) {
                     if (winsMemoryCheck ()){
                        return true;
                     }
                  } else {
                     //System.out.println("DATABASE 2 REDIRECTS: COLUMN: " + i + " ROW: " + primaryRow);
                  }
                  
                  grid.setCell(primaryRow,i,0);
                  curPlayer = 1;
                  foresight = defensiveForesight ();                  
                  grid.setCell(primaryRow,i,EMPTY);
                        
                     
                  
                  if (!databaseCheck && !foresight) {
                     curPlayer = 0;
                     //System.out.println("POSITIONAL MOVE 2 PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                     
                     grid.setCell(primaryRow,i,0);
                     playedCol2 = i;
                     return true;   
                  }
                  
                    
               }
               i = temp;
            } 
            grid.setCell(primaryRow,i,EMPTY);        
         }
      }
      return false;
   }
  
// IMPROVES POSITIONAL PLAY BY FORCING 
// BLOCKS IN ORDER TO GET MORE SPACIAL CONTROL 
  
   public static boolean attackingMove2 () {
   
      boolean win = false;
      boolean horizontalWin = false;
      boolean moveFound = false;
      boolean databaseCheck = false;
      boolean foresight = false;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         databaseCheck = false;
         foresight = false;
         primaryRow = findRow(i);
         
         if (primaryRow != -1){
            
            grid.setCell(primaryRow,i,0);
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;         
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  
                  curPlayer = 0;		
                     
                  if (primaryRow != 0) {
                     curPlayer = 1;
                     grid.setCell(primaryRow - 1,i,1);
                     win = checkIfWin ();
                     grid.setCell(primaryRow - 1,i,EMPTY);
                  }      
                     
                  if (!win) {
                        
                     win = false;
                     curPlayer = 0;
                     if (primaryRow >= NUMROW - 3 && p != i) {
                        
                        grid.setCell(secondaryRow,p,0);
                        horizontalWin = ifHorizontal();
                        win = horizontalWin;
                        grid.setCell(secondaryRow,p,EMPTY); 
                                          
                     } 
                        
                     if (primaryRow >= 1 && primaryRow <= NUMROW - 2 && !horizontalWin) {
                        grid.setCell(secondaryRow,p,0);
                        win = ifVertical();
                        grid.setCell(secondaryRow,p,EMPTY);  
                     }
                        
                     if (win) {
                        
                        grid.setCell(primaryRow,i,0);
                        databaseCheck = lossesMemoryCheck ();
                        grid.setCell(primaryRow,i,EMPTY);
                        
                        if (!databaseCheck) {
                           if (winsMemoryCheck ()){
                              return true;
                           }
                        } else {
                           //System.out.println("DATABASE 2 REDIRECTS: COLUMN: " + i + " ROW: " + primaryRow);
                        }
                        
                        grid.setCell(primaryRow,i,0);
                        curPlayer = 1;
                        foresight = defensiveForesight ();                  
                        grid.setCell(primaryRow,i,EMPTY);
                        
                     
                     
                        if (!databaseCheck && !foresight) {
                        
                           //System.out.println("ATTACKING MOVE 2 PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        
                           grid.setCell(primaryRow,i,0);
                           grid.setCell(secondaryRow,p,EMPTY);                           
                           playedCol2 = i;
                           return true;
                        }
                     }
                        
                  }
               
                  grid.setCell(secondaryRow,p,EMPTY); 
               }
            
            }   
                      
            grid.setCell(primaryRow,i,EMPTY);  
         }
         
      }
      //System.out.println("ATTACKING MOVE 2 PASSES");
      return false;
   }


// PLAYS A REGULAR MOVE IF NO SPECIAL MOVES ARE AVAILABLE

   public static void regularMove2 () {
      
      boolean win = false;
      boolean loop = true;
      boolean foresight = true;
      boolean databaseCheck = false;
      
      int row = -1;
      int column;
      int numTries = 0;
      int length = 7;
      
   
      while (loop) {
         
         databaseCheck = false;
         foresight = false;
         numTries++;
         numLoops++;
         
         if (numTries % 200 == 0) {
            //System.out.println("NUMTRIES: " + numTries); 
         }
          
         column = chooseColumn (numTries);
      
         row = findRow (column);
         if (row != -1) {
            
            win = false;
            
            curPlayer = 0;         
            if (row > 0){
               
               grid.setCell(row - 1,column,1);
               curPlayer = 1;
               win = checkIfWin();
               grid.setCell(row - 1,column,EMPTY);
             
               if (numTries > 4000) {
                  curPlayer = 0;
               
                  grid.setCell(row,column,0);
                  playedCol2 = column;
                  //System.out.println("REGULAR MOVE 2 FORCES: COLUMN: " + column + " ROW: " + row);
                  loop = false;
               }
                 
               if (loop && !win){
                  win = false;
                  
                  if (numTries < 2000) {
                     
                     grid.setCell(row,column,0);
                     databaseCheck = lossesMemoryCheck ();
                     grid.setCell(row,column,EMPTY);
                        
                     if (!databaseCheck) {
                        if (winsMemoryCheck ()){
                           loop = false;
                        }
                     } else {
                        //System.out.println("DATABASE 2 REDIRECTS: COLUMN: " + column + " ROW: " + row);
                     
                     }
                     
                     grid.setCell(row - 1,column,0);
                     curPlayer = 0;
                     win = checkIfWin();
                     grid.setCell(row - 1,column,EMPTY);
                  } 
                  
                  if (numMove < 20) {
                     grid.setCell(row,column,0);
                     curPlayer = 1;
                     foresight = defensiveForesight ();
                     grid.setCell(row,column,EMPTY);
                  } else {
                     foresight = false;
                  }
                  if (!win  && !foresight &&!databaseCheck) {
                     curPlayer = 0;
                  
                     grid.setCell(row,column,0);
                     playedCol2 = column;
                     //System.out.println("REGULAR MOVE 2 PLAYS: COLUMN: " + column + " ROW: " + row);
                     loop = false;
                  }
                  
                  
               }  
               
                
               
            } else {
               curPlayer = 0;
            
               grid.setCell(row,column,0);
               playedCol2 = column;
               //System.out.println("REGULAR MOVE 2 PLAYS: COLUMN: " + column + " ROW: " + row);
               loop = false;
            } 
         }
            
         
      } 
   }


   public static int chooseColumn2 (int numTries) {
      int column = 3;
      int min = 0;
      int max = 0;
      int row = 0;;
      
      if (numTries > 50) {
      
         column = (int)(Math.random() * NUMCOL); 
               
      } else if (playedCol2 == 0 || playedCol2 == 1) {
         column = randomColumnGenerator (0, 2);
      } else if (playedCol2 == 2 ) {
         column = randomColumnGenerator (1, 3);
      } else if (playedCol2 == 0 || playedCol2 == 1) {
         column = randomColumnGenerator (1, 3);
      } else if (playedCol2 == 3) {
         column = randomColumnGenerator (2, 4);
      } else if (playedCol2 == 4) {
         column = randomColumnGenerator (3, 5);
      } else if (playedCol2 == 5 || playedCol2 == 6) {
         column = randomColumnGenerator (4, 6);
      }
         
      if (numMove < 25) {
         ////System.out.println("COLUMN: " + column);    
         if (column >= 1 && column <= 5) {
            row = findRow (column);
            if (row != -1) {
               
               if (row == NUMROW - 1) {
               
                  return column;
               }
               
               if (grid.getCell(row + 1,column) != 0) {
                  if (grid.getCell(row,column - 1) != 0 && grid.getCell(row,column + 1) != 0 && row > 1) {
                     return column;
                  }
               }  
            }
         }
         
      }   
         
      return column;  
   }
   
   /////////////////////////////////////////////////////////////////////
   
   public static boolean defensiveForesight () {
   
      boolean win = false;
      boolean moveFound = false;
      int numWinMethods = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
      
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1) {
            
            numWinMethods = 0;
            grid.setCell(primaryRow,i,curPlayer);
            
            changeCurPlayer ();
            
            if (primaryRow > 0) {
               
               grid.setCell(primaryRow - 1,i,curPlayer);
               win = checkIfWin();
               grid.setCell(primaryRow - 1,i,EMPTY);
               
            } else {
               win = false;
            }
            
            changeCurPlayer ();
            
            if (!win) {
                   
               for (int p = NUMCOL - 1; p >= 0; p--){
               
                  win = false;         
                  secondaryRow = findRow (p);
                  numLoops++;
               
                  if (secondaryRow != -1) {
                     
                     grid.setCell(secondaryRow,p,curPlayer);
                  //curPlayer = 0;		
                     win = checkIfWin();
                     grid.setCell(secondaryRow,p,EMPTY);
                              
                     if (win) {                              
                        numWinMethods ++;
                     }
                           
                     if (numWinMethods >= 2) {
                        grid.setCell(secondaryRow,p,EMPTY);
                        grid.setCell(primaryRow,i,EMPTY);
                        return true;
                     }
                  } 
               
                  win = false;        
                  secondaryRow = findRow (p);
                     
                  if (secondaryRow != -1 && secondaryRow != 0) {
                        
                     grid.setCell(secondaryRow,p,curPlayer);
                  // curPlayer = 0;		
                     win = checkIfWin();
                     grid.setCell(secondaryRow,p,EMPTY);
                                    
                     if (win) {                              
                     
                        changeCurPlayer ();
                     
                        grid.setCell(secondaryRow,p,curPlayer);
                     
                        changeCurPlayer ();
                     
                        if (secondaryRow > 0) {
                           
                           grid.setCell(secondaryRow - 1,p,curPlayer);
                        //curPlayer = 0;
                           win = checkIfWin();
                           grid.setCell(secondaryRow - 1,p,EMPTY);
                              
                           if (win) {
                           
                              grid.setCell(secondaryRow,p,EMPTY);
                              grid.setCell(primaryRow,i,EMPTY);
                              return true;
                           }
                              
                        }   
                             
                        grid.setCell(secondaryRow,p,EMPTY);
                     }
                     
                  }
               
               }
            }      
            grid.setCell(primaryRow,i,EMPTY);     
         }             
      
      }
      return false;
   }
   
   public static int randomColumnGenerator (int min, int max) {
      return ThreadLocalRandom.current().nextInt(min, max + 1);
   }
   
   public static void changeCurPlayer () {
      if (curPlayer == 0) {
         curPlayer = 1;
      } else {
         curPlayer = 0;
      }
   }
   
   // MAKE A DATABASE FUNCTION THAT WILL STORE ALL GAMES PLAYED AND REFERENCES THEM TO SEE IF A PAST BAD MOVE WAS MADE 


   
  // END OF ENGINE 
   


   public static void ifWin () throws Exception{
      
      boolean win = checkIfWin();
      int positionNum = 0;
      int count = 0;
      if (win) {   
      
         score [curPlayer]++;
         game = false;
         Games inverseGameData = new Games (4);
         Games requiredGameData = new Games (4); 
         
         
         
         for (int k = numMove - 6; k < numMove - 2; k++) {
            for (int i = 0; i < NUMROW; i++) {
               for (int j = 0; j < NUMCOL; j++) {
                        
                  if (gameData.game [k].getCell(i,j) == 5) {
                     inverseGameData.game [k - (numMove - 6)].setCell(i,j,5);
                     requiredGameData.game [k - (numMove - 6)].setCell(i,j,5);
                  } else if (gameData.game [k].getCell(i,j) == 1) {
                     inverseGameData.game [k - (numMove - 6)].setCell(i,j,0);
                     requiredGameData.game [k - (numMove - 6)].setCell(i,j,1);
                  }  else {
                     inverseGameData.game [k - (numMove - 6)].setCell(i,j,1);
                     requiredGameData.game [k - (numMove - 6)].setCell(i,j,0);
                  }
               
               }                        
            }
              // System.out.println (gameCount);
                 
         }
      
         
         if (curPlayer == 0) {
         
            for (int i = 0; i < positionCount && positionNum == 4; i++) {
               if (Arrays.deepEquals(lossesDataBase.game [i].position, requiredGameData.game [count].position)) {
                  positionNum++;    
               } else {
                  positionNum = 0;
               }  
               count++;
               if (count == 4) {
                  count = 0;
               }
            } 
            
            if (positionNum != 4) {
               
               for (int k = 0; k < 4; k++) {
                  for (int i = 0; i < NUMROW; i++) {
                     for (int j = 0; j < NUMCOL; j++) {
                        uploadLossesDataBase.game [gameCount + k].setCell (i,j,requiredGameData.game [k].getCell (i,j));
                        uploadWinsDataBase.game [gameCount + k].setCell (i,j,inverseGameData.game [k].getCell (i,j));
                     }
                  }
                  
               }       
            
               gameCount += 4;
            } else {
               numDuplicates++;
            }
         
            
         } else {
                       
            
            for (int i = 0; i < positionCount && positionNum == 4; i++) {
               if (Arrays.deepEquals(winsDataBase.game [i].position, requiredGameData.game [count].position)) {
                  positionNum++;    
               } else {
                  positionNum = 0;
               }  
               count++;
               if (count == 4) {
                  count = 0;
               }
            } 
            
            if (positionNum != 4) {
            
               for (int k = 0; k < 4; k++) {
                  for (int i = 0; i < NUMROW; i++) {
                     for (int j = 0; j < NUMCOL; j++) {
                        uploadLossesDataBase.game [gameCount + k].setCell (i,j,inverseGameData.game [k].getCell (i,j));
                        uploadWinsDataBase.game [gameCount + k].setCell (i,j,requiredGameData.game [k].getCell (i,j));
                     }
                  }
                  
               }     
               gameCount += 4;
            }else {
               numDuplicates++;
            }
            
            
            
         }
        // //System.out.println(uploadLossesDataBase.game.length + "AAAAA");
         
         //uploadDatabase ();
        
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid.setCell(i,j,EMPTY);
            }
         }  
        
         curPlayer = 0;
         numMove = 0;
         playedCol = 3;
      } else if (numMove == 42) {
         game = false;
         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid.setCell(i,j,EMPTY);
            }
         }    
      
         curPlayer = 0;
         numMove = 0;
         playedCol = 3;
      } 
   }


	
   public static boolean checkIfWin (){
   	
      boolean horizontalChain,verticalChain,rightDiagonal,leftDiagonal;
      boolean win = false;    
      
      if ((horizontalChain = ifHorizontal()) == true) {
         return true;
      
      }	
      
      if ((verticalChain = ifVertical()) == true) {
         return true;
      
      }
   
      if ((rightDiagonal = upRight()) == true){
         return true;
      
      }
   	
      if ((leftDiagonal = upLeft()) == true){
         return true;
      
      }  
      return false;   		
   }

	/*
		These methods check for if a chain of 4 has been created
	*/
// start of horizontal check	
   
   

   public static boolean ifHorizontal (){
      boolean chain;
      int startPoint;
      int endPoint;
      for (int i = NUMROW - 1; i >= 0; i--) {
         startPoint = 0;
         endPoint = NUMCOL - 3;
         for (int j = 0; j < 4; j++){
            chain = true;
            for (int k = startPoint; k < endPoint; k++){
               if (!(grid.getCell (i,k) == curPlayer && chain == true)){
                  chain = false;
               } 			
            } 
         	
            if (chain == true){
               return true;
            }	
            startPoint++;
            endPoint++;
         } 
      
      } 
      return false;
   }
// End of horizontal check

// Start of vertical check
   public static boolean ifVertical (){
      boolean chain;
      int startPoint;
      int endPoint;
      for (int i = 0; i < NUMCOL; i++) {
         startPoint = 0;
         endPoint = NUMROW - 2;
         for (int j = 0; j < 3; j++){
            chain = true;
            for (int k = startPoint; k < endPoint; k++){
               if (!(grid.getCell (k,i) == curPlayer && chain == true)){
                  chain = false;
               } 
               	
            }
            if (chain == true){
               return true;
            } 	
            startPoint++;
            endPoint++;
         } 
      
      } 
      return false;
   }
// End of vertical check


// Start of Right Diagonal Check
   public static boolean upRight(){
      int rangeEnd,trans1,trans2;
      int endPoint = 0; 
      int startPoint = 0;
      int count = 3;
      int transition = 3;
      boolean chain;
      rangeEnd = 1;
      for (int i = 0; i < 6; i++) {
         if (i > 2){
            startPoint++;
            endPoint++;
         }  
         else{
            startPoint = 0;
            endPoint = 4;
         }      
         trans1 = startPoint;
         trans2 = endPoint;
         
         transition = count;
         for (int j = 0; j < rangeEnd ; j++){
            chain = true;
            for (int k = startPoint; k < endPoint; k++){
               if (!(grid.getCell (count,k) == curPlayer && chain == true)) {
                  chain = false;
               
               } 
               count--;	
            }
         
            if (chain == true) {
            
               return true;
            }
         
            count = transition - 1;
            if (j == 1){
               count--;
            }
            startPoint++;
            endPoint++;	
         }
         startPoint = trans1;
         endPoint = trans2;
         count = transition;
      
         
      
         if (count < 5){
            count++;
            rangeEnd++;
         } 
         else if (i > 2){
            rangeEnd--;
         }
      }
      return false;
   }
   
//	Start of left Diagonal Check
   
   public static boolean upLeft(){
      int trans1,trans2;
      int rangeEnd = 1;
      int endPoint = 0; 
      int startPoint = 0;
      int count = 3;
      int transition = 3;
      boolean chain;
      for (int i = 0; i < 6; i++) {
      
         if (i > 2){
            startPoint--;
            endPoint--;
         }  
         else{
            startPoint = 6;
            endPoint = 3;
         }      
         trans1 = startPoint;
         trans2 = endPoint;
      
         transition = count;	
      
         for (int j = 0; j < rangeEnd ; j++){
            chain = true;
            for (int k = startPoint; k >= endPoint; k--){
               if (!(grid.getCell (count,k) == curPlayer && chain == true)) {
                  chain = false;
               
               } 
               count--;	   
            }
            
         
            if (chain == true) {
               return true;
            }
         
            count = transition - 1;
            if (j == 1){
               count--;
            }
         
            startPoint--;
            endPoint--;
         		
            
         }
         
         startPoint = trans1;
         endPoint = trans2;
         count = transition;
      
      
      
         if (count < 5){
            count++;
            rangeEnd++;
         } 
         else if (i > 2){
            rangeEnd--;
         	
         }
         
      }
      return false;
   }

	
}