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
   
   
   final int EMPTY = 5;
   final int NUMPLAYER = 2;    // number  of players
   final int NUMROW = 6;       // number of rows on the game board
   final int NUMCOL = 7;       // number of columns on the game board
   //final int MAXGAME;      // number of games needed to win to win a match

   int numMove;            // num of move that has been made in this game
   int curPlayer;          // the id number of the current player
   int numGamesRecorded;
   int positionCount = -1;
   int playedCol = 3;
   int numLoops = 0;

   
   int grid[][];           // represents the grid of the game board
   int score[];            // represents the scores of the players
   int gameData [][][] = new int [42][6][7];
   int lossesDataBase [][][] = new int [100000][6][7];
   int winsDataBase [][][] = new int [100000][6][7];
   
   boolean ai;
   
   
  // int grid [][] = { {0,5,0,0,1,5,5 }, {1,5,1,1,1,5,5}, {0,5,0,0,0,5,5 }, {1,5,0,0,1,5,5 }, {0,0,1,1,0,5,5 }, {1,1,0,1,0,1,5 }};



   public static void main (String [] args) {

      Scanner sc = new Scanner (System.in); 
      grid = new int [NUMROW][NUMCOL];
      score = new int [2];
      score [0] = 0;
      score [1] = 0;      
      for (int i = 0; i < NUMROW; i++){
         for (int j = 0; j < NUMCOL; j++){
            grid [i][j] = EMPTY;
         }
      }  

      while (true){
         curPlayer = 1;
         ai ();
         curPlayer = 0;
         ai2 ();

         String a = sc.nextLine();

      }
   } 
   
 


//    BEGINNING OF ENGINE


   public int findRow (int column){
   
      int row = NUMROW - 1; 
      boolean loop = true;
      
      while (loop) {
         
         if (grid [row][column] == EMPTY) {
            return row;
         } else if (row <= 0){
            return -1;
         }  
         row--;
      } 
      return row;					    
   }
   
   // MAKE ARRAY FOR REGULAR MOVE TO KNOW WHERE NOT TO PLACE
   
   
   public void ai () {
         
      boolean moveFound = false;
      curPlayer = 1;
      gui.setNextPlayer(curPlayer);
      numLoops = 0;
      System.out.println("\nAI: ");
      if (!winningMove()) {
         
         if (numMove >= 5){ 
            moveFound = block ();
         
         }
      
         if (numMove >=3 && !moveFound) {
            moveFound = advancedBlock();
         
         } 
         
         if (!moveFound) {
            moveFound = defensiveMove ();
         } 
         
         if (!moveFound) {
            moveFound = thirdEye ();
         }
                 
         if (!moveFound) {
            moveFound = advancedMove ();
         }
         
         if (!moveFound) {
            moveFound = forceMove ();
         }
         
         if (!moveFound) {
            moveFound = godsEye ();
         }
         
         
         if (!moveFound) {
            moveFound = positionalMove ();
         } 
         
         if (!moveFound) {
            moveFound = attackingMove ();
         }  
      
         if (!moveFound) {
            regularMove ();  
         }  
      }   
      
      //gameData [numMove] = grid.clone();
      for (int i = 0; i < NUMROW; i++){
         for (int j = 0; j < NUMCOL; j++){
            gameData [numMove][i][j] = grid [i][j];
         }
      }
      numMove++;
      
      System.out.println("Number of loops: " + numLoops);
      
      ifWin();
      
      curPlayer = 0;
      
   }
   
   public void loadDatabase () {
        
      String lossPosition = "";
      String winPosition = "";
      
      String [] lossRows;
      String [] winRows;
      
      // Create a variable for the connection string.
      String connectionUrl = "jdbc:sqlserver://USER:1433;databaseName=Connect4Memory;user=Ranuja;password=timetwist";
   
      try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            
         String losses = "SELECT * FROM [Losses]"; 
         String wins =  "SELECT * FROM [Wins]";
         
                 
         ResultSet rs = stmt.executeQuery(losses); 
       //  ResultSet wrs = stmt.executeQuery(wins);
         
         //Statement statement = con.createStatement();
      
         while (rs.next() /*&& wrs.next()*/) {
               
            positionCount++;
            
            lossPosition = rs.getString ("position");
           
            lossRows = lossPosition.split(" ");
         
                      
            for (int i = 0; i < NUMROW; i++) {
               for (int j = 0; j < NUMCOL; j++) {
                  numLoops++;
                  lossesDataBase [positionCount][i][j] = Integer.parseInt (lossRows[i].substring(j,j+1));
                 
               }
            }
              
             
         }
         
         rs = stmt.executeQuery(wins);
         
         while (rs.next()) {
            
            winPosition = rs.getString ("position");
            winRows = winPosition.split(" ");
         
                      
            for (int i = 0; i < NUMROW; i++) {
               for (int j = 0; j < NUMCOL; j++) {
                  numLoops++;
                  
                  winsDataBase [positionCount][i][j] = Integer.parseInt (winRows[i].substring(j,j+1));
               }
            }
              
             
         }
         rs.close(); 
         //statement.executeUpdate("INSERT INTO [ABC] (abc) " + "VALUES ('abcdefg')");
      
      }
      
      catch (SQLException e) {
         e.printStackTrace();
      }
   }
   
   
   public void uploadDatabase () {
            
      String lossPosition = "";
      String winPosition = "";
      
      String [] lossRows;
      String [] winRows;
      
      String lossConcat = "";
      String winConcat = "";
      // Create a variable for the connection string.
      String connectionUrl = "jdbc:sqlserver://USER:1433;databaseName=Connect4Memory;user=Ranuja;password=timetwist";
   
      try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            
         String losses = "SELECT * FROM [Losses]"; 
         String wins =  "SELECT * FROM [Wins]";
         
         int inverseGameData [][][] = new int [42][6][7];
      
         
         Statement statement = con.createStatement();
         
         if (numMove > 5) {
            
            if (curPlayer == 0) {
               
               for (int k = numMove - 6; k < numMove - 1; k++) {
                  
                  lossConcat = "";
                  winConcat = "";
                  
                  for (int i = 0; i < NUMROW; i++) {
                     for (int j = 0; j < NUMCOL; j++) {
                     
                        lossConcat += "" +gameData [k][i][j];
                        
                        if (gameData [k][i][j] == 5) {
                           inverseGameData [k][i][j] = 5;
                        } else if (gameData [k][i][j] == 1) {
                           inverseGameData [k][i][j] = 0;
                        }  else {
                           inverseGameData [k][i][j] = 1;
                        }
                        
                        winConcat += "" +inverseGameData [k][i][j];
                        
                     }
                     if (i != NUMROW - 1) {
                        lossConcat += " ";
                        winConcat += " ";
                     }
                     
                     
                  }
                  lossConcat = "'" +  lossConcat + "'";
                  winConcat = "'" +  winConcat + "'";
                  
                  System.out.println("lossConcat " + lossConcat);
                  System.out.println("winConcat " + winConcat);
                  
                  statement.executeUpdate("INSERT INTO [Losses] (position) " + "VALUES (" + lossConcat + ")"); 
                  System.out.println("AAAAAA");
                  statement.executeUpdate("INSERT INTO [Wins] (position) " + "VALUES (" + winConcat + ")"); 
               }        
            } else {
               for (int k = numMove - 5; k < numMove - 1; k++) {
               
                  lossConcat = "";
                  winConcat = "";
                  
                  for (int i = 0; i < NUMROW; i++) {
                     for (int j = 0; j < NUMCOL; j++) {
                     
                        winConcat += "" +gameData [k][i][j];
                        
                        if (gameData [k][i][j] == 5) {
                           inverseGameData [k][i][j] = 5;
                        } else if (gameData [k][i][j] == 1) {
                           inverseGameData [k][i][j] = 0;
                        }  else {
                           inverseGameData [k][i][j] = 1;
                        }
                        
                        lossConcat += "" +inverseGameData [k][i][j];
                        
                     }
                     if (i != NUMROW - 1) {
                        lossConcat += " ";
                        winConcat += " ";
                     }
                     
                     
                  }
                  lossConcat = "'" +  lossConcat + "'";
                  winConcat = "'" +  winConcat + "'";
                  
                  System.out.println("lossConcat " + lossConcat);
                  System.out.println("winConcat " + winConcat);
                  
                  statement.executeUpdate("INSERT INTO [Losses] (position) " + "VALUES (" + lossConcat + ")"); 
                  System.out.println("BBBBB");
                  statement.executeUpdate("INSERT INTO [Wins] (position) " + "VALUES (" + winConcat + ")"); 
               } 
            }
         
         }
      }
      
      catch (SQLException e) {
         e.printStackTrace();
      }
   }
   
   public boolean lossesMemoryCheck () {
      
      for (int i = 0; i < positionCount; i++) {
         numLoops++;
         if (Arrays.equals(grid, lossesDataBase [i])) {                  
            return true;
         } 
      }
      
      return false;
      
   }
   
   public boolean winsMemoryCheck () {
      
      for (int i = 0; i < positionCount; i++) {
        
      
         if (Arrays.equals(grid, winsDataBase [i])) {                  
                  
                  
            for (int j = 0; i < NUMROW; j++) {
                     
               for (int k = 0; k < NUMCOL; k++) {
                  numLoops++;
                  if (i != positionCount - 2) {
                        
                     
                  
                     if (winsDataBase [i + 1][j][k] != grid [j][k]) {
                     
                     
                        curPlayer = 1;
                        System.out.println("DATABASE PLAYS: COLUMN: " + j + " ROW: " + k);                        
                        grid [k][j] = 1;
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
   
   public boolean winningMove () {
   
      boolean win = false;
      int row = -1;
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         row = findRow (i);
         numLoops++;
         if (row != -1) {
            grid [row][i] = 1;
            curPlayer = 1;							
            win = checkIfWin();
            grid [row][i] = EMPTY;
            if (win){

               System.out.println("WINNING MOVE PLAYS: COLUMN: " + i + " ROW: " + row);
               grid [row][i] = 1;
               return true;
            }         
         }
      }
            
      return false;   
   }

// BLOCKS A 4TH PIECE FROM BEING PLACED IN A ROW

   public boolean block () {
   
      boolean win = false;
      int row = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--){ 
      
         row = findRow(i);
         numLoops++;
            
         if (row != -1) {
            grid [row][i] = 0;
            curPlayer = 0;							
            win = checkIfWin();
            grid [row][i] = EMPTY;
            curPlayer = 1;	
                  
            if (win){

               System.out.println("BLOCK PLAYS: COLUMN: " + i + " ROW: " + row);
               grid [row][i] = 1;
               playedCol = i;
               return true;
            }
         }
        
      }     
      return false;
   }



// PREVENTS THE CREATION OF A DOUBLE ATTACK

   public boolean advancedBlock () {
   
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
            grid [primaryRow][i] = 0;
            winMethodAchieved = false;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;        
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                                    
                  grid [secondaryRow][p] = 0;
                  curPlayer = 0;		
                  win = checkIfWin();
                  grid [secondaryRow][p] = EMPTY;
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                  
                  if (numWinMethods == 1 && !winMethodAchieved) {
                     recordColumn = p;
                     recordRow = secondaryRow;
                     winMethodAchieved = true;
                  }
                           
                  if (numWinMethods >= 2) {
                  
                     grid [primaryRow][i] = 1;
                     grid[primaryRow - 1][i] = 0;
                     curPlayer = 0;
                     win = checkIfWin();
                     grid[primaryRow - 1][i] = EMPTY;
                     grid [primaryRow][i] = EMPTY;
                                    
                     if (win){
                        
                        curPlayer = 1;
                        playedCol = i;
                        grid[primaryRow][i] = EMPTY;
                           
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
                     
                        grid [primaryRow][recordColumn] = 1;
                           
                        System.out.println("ADVANCED BLOCK FORCES: COLUMN: " + recordColumn + " ROW: " + primaryRow );
                        return true;
                     } else {
                        moveFound = true;
                     }
                  
                                 
                     if (moveFound) {
                        curPlayer = 1;
                        System.out.println("ADVANCED BLOCK PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                     
                        grid [primaryRow][i] = 1;
                        playedCol = i;
                        grid [secondaryRow][p] = EMPTY;
                        return true;
                     }
                  } 
               }
            
                     
            }
                  
            grid[primaryRow][i] = EMPTY;      
         }             
      
      }
   
      return false;
   }
   
// PREVENTS A MOVE THAT WOULD FORCE A BLOCK WHICH LETS 
// THE OPPONENT WIN BY PLACING A PIECE ON TOP OF THE BLOCK   
   
   public boolean defensiveMove () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
         
            grid [primaryRow][i] = 0;
            
            if (primaryRow > 0) {
               
               curPlayer = 0;	
               grid [primaryRow][i] = 1;
               grid [primaryRow - 1][i] = 0;	
               win = checkIfWin();
               grid [primaryRow - 1][i] = EMPTY;
               
               if (!win) {
               
                  grid [primaryRow][i] = 0;
               
                   
                  for (int p = NUMCOL - 1; p >= 0; p--){
                     
                     win = false;        
                     secondaryRow = findRow (p);
                     numLoops++;
                     
                     if (secondaryRow != -1 && secondaryRow != 0) {
                        
                        grid [secondaryRow][p] = 0;
                        curPlayer = 0;		
                        win = checkIfWin();
                        grid [secondaryRow][p] = EMPTY;
                                    
                        if (win) {                              
                           
                           grid [secondaryRow][p] = 1;
                           
                        
                           
                                      
                           if (secondaryRow > 0) {
                           
                              grid[secondaryRow - 1][p] = 0;
                              curPlayer = 0;
                              win = checkIfWin();
                              grid[secondaryRow - 1][p] = EMPTY;
                              
                              if (win) {
                                 curPlayer = 1;
                                 System.out.println("DEFENSIVE MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        
                                 grid[primaryRow][i] = 1;
                                 playedCol = i;
                                 grid [secondaryRow][p] = EMPTY;
                                 return true;
                              }
                              
                           }   
                             
                           grid [secondaryRow][p] = EMPTY;
                        }
                     
                     }                     
                           
                  }
                  grid[primaryRow][i] = EMPTY;  
               }
               
            }
         
            grid[primaryRow][i] = EMPTY;      
         }             
      
      }
      System.out.println("DEFENSIVE MOVE PASSES");
      return false;
   }
   
  // BLOCKS A THIRD MOVE COMBINATION 
   
   public boolean thirdEye () {
      
      boolean win = false;
      boolean combination = false;
      int primaryRow = -1;
      int secondaryRow = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--) {
         
         primaryRow = findRow (i);         
         
         if (primaryRow != -1) {
            grid [primaryRow] [i] = 0;
         
            for (int j = NUMCOL - 1; j >= 0; j--){ 
            
               secondaryRow = findRow(j);
               numLoops++;
            
               if (secondaryRow != -1) {
               
                  grid [secondaryRow][j] = 0;
                  curPlayer = 0;							
                  win = checkIfWin();
                  grid [secondaryRow][j] = 1;
                  curPlayer = 0;	
                  
                  if (win){
                     
                     combination = defensiveForesight (); 
                  
                     if (combination == true) {
                        
                        if (primaryRow != 0) {
                           
                           grid[primaryRow][i] = 1;
                           grid[primaryRow - 1][i] = 0;
                           curPlayer = 0;
                           win = checkIfWin ();
                           grid[primaryRow][i] = 0;
                           grid[primaryRow - 1][i] = EMPTY;
                           
                           if (!win) {
                              
                              curPlayer = 1;
                              System.out.println("THIRD EYE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                      
                              grid[primaryRow][i] = 1;
                              playedCol = i;
                              grid [secondaryRow][j] = EMPTY;
                              return true; 
                              
                           }
                           
                        }  
                          
                     }
                  
                  }
                  grid [secondaryRow][j] = EMPTY;
               }
            
            }
         
            grid [primaryRow] [i] = EMPTY;
         
         }
      }
   
      return false;        
   }
   
   
   

// CREATES A DOUBLE WIN THREAT

   public boolean advancedMove () {
   
      boolean win = false;
      boolean moveFound = false;
      int numWinMethods = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         primaryRow = findRow(i);
         if (primaryRow != -1 && primaryRow != 0){
            numWinMethods = 0;
            grid [primaryRow][i] = 1;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               win = false;         
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  grid [secondaryRow][p] = 1;
                  curPlayer = 1;		
                  win = checkIfWin();
                  grid [secondaryRow][p] = EMPTY;
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                           
                  if (numWinMethods >= 2) {
                     if (primaryRow > 0){
                        grid[primaryRow - 1][i] = 0;
                        curPlayer = 0;
                        win = checkIfWin();
                        grid[primaryRow - 1][i] = EMPTY;
                                    
                        if (!win){
                           moveFound = true;
                        }
                     } else {
                        moveFound = true;
                     }
                                 
                     if (moveFound) {
                        curPlayer = 1;
                        System.out.println("ADVANCED MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                    
                        grid [primaryRow][i] = 1;
                        playedCol = i;
                        grid [secondaryRow][p] = EMPTY;
                        return true;
                     }
                  }
               } 
            
                     
            }
                  
            grid[primaryRow][i] = EMPTY;      
         }             
      
      }
      System.out.println("ADVANCED MOVE PASSES");
      return false;
   }
   
// FORCES A BLOCK TO CREATE A WINNING THREAT

   public boolean forceMove () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1){
         
            grid [primaryRow][i] = 1;
            
            if (primaryRow > 0) {
               
               if (primaryRow != 0) {
                  grid [primaryRow - 1][i] = 0;
                  curPlayer = 0;		
                  win = checkIfWin();
                  grid [primaryRow - 1][i] = EMPTY;
               }
               
               
               if (!win) {
                  grid [primaryRow][i] = 0;
                   
                  for (int p = NUMCOL - 1; p >= 0; p--){
                  
                     win = false;        
                     secondaryRow = findRow (p);
                     numLoops++;
                     
                     if (secondaryRow != -1 && secondaryRow != 0) {
                        
                        grid [secondaryRow][p] = 1;
                        curPlayer = 1;		
                        win = checkIfWin();
                        grid [secondaryRow][p] = EMPTY;
                                    
                        if (win) {                              
                           
                           grid [secondaryRow][p] = 0;
                                                              
                           if (secondaryRow > 0) {
                           
                              grid[secondaryRow - 1][p] = 1;
                              curPlayer = 1;
                              win = checkIfWin();
                              grid[secondaryRow - 1][p] = EMPTY;
                              
                              if (win) {
                              
                                 curPlayer = 1;
                                 System.out.println("FORCE MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        
                                 grid[primaryRow][i] = 1;
                                 playedCol = i;
                                 grid [secondaryRow][p] = EMPTY;
                                 return true;                            
                              }                       
                           }                    
                           grid [secondaryRow][p] = EMPTY;
                        }    
                     }                             
                  }
                   
                  grid[primaryRow][i] = EMPTY;  
               } 
            }    
         
            grid [primaryRow][i] = EMPTY;      
         }             
      
      }
      System.out.println("FORCE MOVE PASSES");
      return false;
   }
   
   public boolean godsEye () {
      
      boolean win = false;
      boolean combination = false;
      int primaryRow = -1;
      int secondaryRow = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--) {
         
         primaryRow = findRow (i);         
         
         if (primaryRow != -1) {
            grid [primaryRow] [i] = 1;
         
            for (int j = NUMCOL - 1; j >= 0; j--){ 
            
               secondaryRow = findRow(j);
               numLoops++;
            
               if (secondaryRow != -1) {
               
                  grid [secondaryRow][j] = 1;
                  curPlayer = 1;							
                  win = checkIfWin();
                  grid [secondaryRow][j] = 0;
                  curPlayer = 1;	///
                  
                  if (win){
                     //System.out.println("AAAAAHH: " + i + " ROW: " + primaryRow);
                     combination = defensiveForesight (); 
                  
                     if (combination == true) {
                        
                        if (primaryRow != 0) {
                           
                           grid[primaryRow][i] = 1;
                           grid[primaryRow - 1][i] = 0;
                           curPlayer = 0;                          
                           win = checkIfWin ();
                           grid[primaryRow - 1][i] = EMPTY;
                           
                           if (!win) {
                              
                              curPlayer = 1;
                              System.out.println("GODS EYE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        
                              grid[primaryRow][i] = 1;
                              playedCol = i;
                              grid [secondaryRow][j] = EMPTY;
                              return true; 
                              
                           }
                           
                        }
                     
                          
                     }
                  
                  }
                  grid [secondaryRow][j] = EMPTY;
               }
            
            }
         
            grid [primaryRow] [i] = EMPTY;
         
         }
      }
   
      return false;        
   }
   
// RETAINS POSITIONAL CONTROL BY TAKING AND BLOCKING THE CONTROL OF SQUARES
   
   public boolean positionalMove () {
      
      boolean win = false;
      boolean moveFound = false;
      boolean databaseCheck = false;
   
      
      int primaryRow = -1;
            
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         databaseCheck = false;
         primaryRow = findRow(i);
         numLoops++;
         
         if (primaryRow != -1){
            
            
            if (primaryRow > 0) {
                         
               grid [primaryRow - 1][i] = 0;
               curPlayer = 0;
               win = checkIfWin ();
               grid [primaryRow - 1][i] = EMPTY;
               
               if (!win) {
                        
                  grid [primaryRow][i] = 1;
                  curPlayer = 1;
                  
                  if (primaryRow != NUMROW -1) {
                     
                     if (i != 0 && grid [primaryRow][i - 1] == EMPTY) {
                        grid [primaryRow][i - 1] = 1;
                        win = ifHorizontal ();
                        grid [primaryRow][i - 1] = EMPTY;
                     }
                  
                     if (win) {
                        moveFound = true;
                     }
                                     
                     if (i != NUMCOL - 1 && grid [primaryRow][i + 1] == EMPTY) {
                        grid [primaryRow][i + 1] = 1;
                        win = ifHorizontal ();
                        grid [primaryRow][i + 1] = EMPTY;
                     }
                  
                     if (win) {
                        moveFound = true;
                     }
                  
                  }
                  
                  grid [primaryRow][i] = 0;
                  curPlayer = 0;  
               
                  grid [primaryRow - 1][i] = 0;
                  win = ifVertical ();
                  grid [primaryRow - 1][i] = EMPTY;
               
                  if (win) {
                     moveFound = true;
                  }
               }
               
                     
               if (i >= 1 && i <= NUMCOL - 1) {
                        
                  if (primaryRow > 0 && grid [primaryRow - 1][i - 1] == EMPTY) {
                           
                     grid [primaryRow - 1][i - 1] = 0;
                     win = upLeft();
                     grid [primaryRow - 1][i - 1] = EMPTY;
                           
                     if (win) {
                        //System.out.println ("AAAAA");
                        moveFound = true;
                     }
                  }
                        
                        
                  if (primaryRow < NUMROW - 1 && grid [primaryRow + 1][i - 1] == EMPTY) {
                        
                     grid [primaryRow + 1][i - 1] = 0;
                     win = upRight();
                     grid [primaryRow + 1][i - 1] = EMPTY;
                        
                     if (win) {
                        //System.out.println ("BBB");
                        moveFound = true;
                     }
                  }
                        
                        
               }
                  
               if (i >= 0 && i <= NUMCOL - 2 ) {
                        
                  if (primaryRow < NUMROW - 1 && grid [primaryRow + 1][i + 1] == EMPTY) {
                        
                     grid [primaryRow + 1][i + 1] = 0;
                     win = upLeft();
                     grid [primaryRow + 1][i + 1] = EMPTY;
                        
                     if (win) {
                        //System.out.println ("CCC");
                        moveFound = true;
                     }
                  }
                        
                  if (primaryRow > 0 && grid [primaryRow - 1][i + 1] == EMPTY) {
                        
                     grid [primaryRow - 1][i + 1] = 0;
                     win = upRight();
                     grid [primaryRow - 1][i + 1] = EMPTY;
                        
                     if (win) {
                        //System.out.println ("D");
                        moveFound = true;
                     }
                        
                  }
               }
                  
               grid [primaryRow][i] = EMPTY;
                  
               if (findRow (3) == 2) {
                  
                  moveFound = true;
                  i = 3;
                  primaryRow = 2;
                     
               } else if (grid [2][3] == 0 && grid [3][2] == EMPTY && grid [4][1] == EMPTY && grid [5][0] == EMPTY) {
                     
                  if (grid [4][2] != 0 || grid [4][3] != 0) {
                     moveFound = true;
                     i = 0;
                     primaryRow = 5;
                  }
                     
               } else if (grid [2][3] == 0 && grid [3][4] == EMPTY && grid [4][5] == EMPTY && grid [5][6] == EMPTY) {
                     
                  if (grid [4][5] != 0 || grid [4][4] != 0) {
                     moveFound = true;
                     i = 6;
                     primaryRow = 5;
                  }
               }
                  
               if (moveFound) {
                  curPlayer = 1;
                  grid [primaryRow - 1][i] = 1;
                  win = checkIfWin ();
                  grid [primaryRow - 1][i] = EMPTY;
                  if (win) {
                     moveFound = false;
                  }
               }
                  
               if (moveFound) {
                  
                  grid[primaryRow][i] = 1;
                  databaseCheck = lossesMemoryCheck ();
                  grid[primaryRow][i] = EMPTY;
                  
                  if (!databaseCheck) {
                     if (winsMemoryCheck ()){
                        return true;
                     }
                  } else {
                     System.out.println("DATABASE PLAYS REDIRECTS: COLUMN: " + i + " ROW: " + primaryRow);
                  }
                  
                  
                  if (!databaseCheck) {
                     curPlayer = 1;
                     System.out.println("POSITIONAL MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        
                     grid[primaryRow][i] = 1;
                     playedCol = i;
                     return true;   
                  }
                  
                    
               }
            } 
            grid [primaryRow][i] = EMPTY;        
         }
      }
      return false;
   }
  
// IMPROVES POSITIONAL PLAY BY FORCING 
// BLOCKS IN ORDER TO GET MORE SPACIAL CONTROL 
  
   public boolean attackingMove () {
   
      boolean win = false;
      boolean horizontalWin = false;
      boolean moveFound = false;
      boolean databaseCheck = false;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         databaseCheck = false;
         primaryRow = findRow(i);
         
         if (primaryRow != -1){
            
            grid [primaryRow][i] = 1;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;         
               secondaryRow = findRow (p);
               numLoops++;
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  
                  curPlayer = 1;		
                     
                  if (primaryRow != 0) {
                     curPlayer = 0;
                     grid [primaryRow - 1][i] = 0;
                     win = checkIfWin ();
                     grid [primaryRow - 1][i] = EMPTY;
                  }      
                     
                  if (!win) {
                        
                     win = false;
                     curPlayer = 1;
                     if (primaryRow >= NUMROW - 3 && p != i) {
                        grid [secondaryRow][p] = 1;  
                        horizontalWin = ifHorizontal();
                        win = horizontalWin;
                        grid [secondaryRow][p] = EMPTY;                   
                     } 
                        
                     if (primaryRow >= 1 && primaryRow <= NUMROW - 2 && !horizontalWin) {
                        grid [secondaryRow][p] = 1;
                        win = ifVertical();
                        grid [secondaryRow][p] = EMPTY;  
                     }
                        
                     if (win) {
                        
                        
                        grid[primaryRow][i] = 1;
                        databaseCheck = lossesMemoryCheck ();
                        grid[primaryRow][i] = EMPTY;
                        
                        if (!databaseCheck) {
                           if (winsMemoryCheck ()){
                              return true;
                           }
                        } else {
                           System.out.println("DATABASE PLAYS REDIRECTS: COLUMN: " + i + " ROW: " + primaryRow);
                        }
                        
                        if (!databaseCheck) {
                        
                           System.out.println("ATTACKING MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                      
                           grid [primaryRow][i] = 1;                           
                           grid [secondaryRow][p] = EMPTY;
                           playedCol = i;
                           return true;
                        }
                     }
                        
                  }
               
                  grid [secondaryRow][p] = EMPTY; 
               }
            
            }   
                      
            grid[primaryRow][i] = EMPTY;   
         }
         
      }
      System.out.println("ATTACKING MOVE PASSES");
      return false;
   }


// PLAYS A REGULAR MOVE IF NO SPECIAL MOVES ARE AVAILABLE

   public void regularMove () {
      
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
         numTries++;
         numLoops++;
         
         if (numTries % 200 == 0) {
            System.out.println("NUMTRIES: " + numTries); 
         }
          
         column = chooseColumn (numTries);
      
         row = findRow (column);
         if (row != -1) {
            
            win = false;
            
            curPlayer = 1;         
            if (row > 0){
               
               grid[row - 1][column] = 0;
               curPlayer = 0;
               win = checkIfWin();
               grid[row - 1][column] = EMPTY;
             
               if (numTries > 4000) {
                  curPlayer = 1;

                  grid [row][column] = 1;
                  playedCol = column;
                  System.out.println("REGULAR MOVE FORCES: COLUMN: " + column + " ROW: " + row);
                  loop = false;
               }
                 
               if (loop && !win){
                  win = false;
                  
                  if (numTries < 2000) {
                     
                     grid[row][column] = 1;
                     databaseCheck = lossesMemoryCheck ();

                        
                     if (!databaseCheck) {
                        if (winsMemoryCheck ()){
                           loop = false;
                        }
                     } else {
                        System.out.println("DATABASE PLAYS REDIRECTS: COLUMN: " + column + " ROW: " + row);
                     
                     }
                        
                        
                     
                     
                     grid[row - 1][column] = 1;
                     curPlayer = 1;
                     win = checkIfWin();
                     grid[row - 1][column] = EMPTY;
                  } 
                  
                  if (numMove < 20) {
                     grid [row][column] = 1;
                     curPlayer = 0;
                     foresight = defensiveForesight ();
                     grid [row][column] = EMPTY;
                  } else {
                     foresight = false;
                  }
                  if (!win  && !foresight &&!databaseCheck) {
                     curPlayer = 1;

                     grid [row][column] = 1;
                     playedCol = column;
                     System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
                     loop = false;
                  }
                  
                  
               }  
               
                
               
            } else {
               curPlayer = 1;

               grid [row][column] = 1;
               playedCol = column;
               System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
               loop = false;
            } 
         }
            
         
      } 
   }


   public int chooseColumn (int numTries) {
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
         //System.out.println("COLUMN: " + column);    
         if (column >= 1 && column <= 5) {
            row = findRow (column);
            if (row != -1) {
               
               if (row == NUMROW - 1) {
               
                  return column;
               }
               
               if (grid [row + 1] [column] != 0) {
                  if (grid [row][column - 1] != 0 && grid [row][column + 1] != 0 && row > 1) {
                     return column;
                  }
               }  
            }
         }
         
      }   
         
      return column;  
   }
   
   public boolean defensiveForesight () {
   
      boolean win = false;
      boolean moveFound = false;
      int numWinMethods = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
      
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1) {
            
            numWinMethods = 0;
            grid [primaryRow][i] = curPlayer;
            
            changeCurPlayer ();
            
            if (primaryRow > 0) {
               
               grid [primaryRow - 1][i] = curPlayer;
               win = checkIfWin();
               grid [primaryRow - 1][i] = EMPTY;
               
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
                  
                     grid [secondaryRow][p] = curPlayer;
                  //curPlayer = 0;		
                     win = checkIfWin();
                     grid [secondaryRow][p] = EMPTY;
                              
                     if (win) {                              
                        numWinMethods ++;
                     }
                           
                     if (numWinMethods >= 2) {
                        grid [secondaryRow][p] = EMPTY;
                        grid [primaryRow][i] = EMPTY;
                        return true;
                     }
                  } 
               
                  win = false;        
                  secondaryRow = findRow (p);
                     
                  if (secondaryRow != -1 && secondaryRow != 0) {
                        
                     grid [secondaryRow][p] = curPlayer;
                  // curPlayer = 0;		
                     win = checkIfWin();
                     grid [secondaryRow][p] = EMPTY;
                                    
                     if (win) {                              
                     
                        changeCurPlayer ();
                     
                        grid [secondaryRow][p] = curPlayer;
                     
                        changeCurPlayer ();
                     
                        if (secondaryRow > 0) {
                           
                           grid[secondaryRow - 1][p] = curPlayer;
                        //curPlayer = 0;
                           win = checkIfWin();
                           grid[secondaryRow - 1][p] = EMPTY;
                              
                           if (win) {
                           
                              grid [secondaryRow][p] = EMPTY;
                              grid[primaryRow][i] = EMPTY;
                              return true;
                           }
                              
                        }   
                             
                        grid [secondaryRow][p] = EMPTY;
                     }
                     
                  }
               
               }
            }      
            grid[primaryRow][i] = EMPTY;      
         }             
      
      }
      return false;
   }
   
   public int randomColumnGenerator (int min, int max) {
      return ThreadLocalRandom.current().nextInt(min, max + 1);
   }
   
   public void changeCurPlayer () {
      if (curPlayer == 0) {
         curPlayer = 1;
      } else {
         curPlayer = 0;
      }
   }
   
   // MAKE A DATABASE FUNCTION THAT WILL STORE ALL GAMES PLAYED AND REFERENCES THEM TO SEE IF A PAST BAD MOVE WAS MADE 


   
  // END OF ENGINE 


   public void ifWin (){
      
      boolean win = checkIfWin();
      if (win) {   
      
         score [curPlayer]++;
                  
         uploadDatabase ();

         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid [i][j] = EMPTY;
            }
         }  

         curPlayer = 0;
         numMove = 0;
         playedCol = 3;
         
      } else if (numMove == 42) {
         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid [i][j] = EMPTY;
            }
         }  

         curPlayer = 0;
         numMove = 0;
         playedCol = 3;
      } 
   }


	
   public boolean checkIfWin (){
   	
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


   public boolean ifHorizontal (){
      boolean chain;
      int startPoint;
      int endPoint;
      for (int i = NUMROW - 1; i >= 0; i--) {
         startPoint = 0;
         endPoint = NUMCOL - 3;
         for (int j = 0; j < 4; j++){
            chain = true;
            for (int k = startPoint; k < endPoint; k++){
               if (!(grid[i][k] == curPlayer && chain == true)){
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
   public boolean ifVertical (){
      boolean chain;
      int startPoint;
      int endPoint;
      for (int i = 0; i < NUMCOL; i++) {
         startPoint = 0;
         endPoint = NUMROW - 2;
         for (int j = 0; j < 3; j++){
            chain = true;
            for (int k = startPoint; k < endPoint; k++){
               if (!(grid[k][i] == curPlayer && chain == true)){
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
   public boolean upRight(){
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
               if (!(grid [count][k] == curPlayer && chain == true)) {
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
   
   public boolean upLeft(){
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
               if (!(grid [count][k] == curPlayer && chain == true)) {
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