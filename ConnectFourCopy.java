/**
* The ConnectFour class.
*
* This class represents a Connect Four (TM)
* game, which allows two players to drop
* checkers into a grid until one achieves
* four checkers in a straight line.
*/	import java.util.Scanner;
import java.lang.*;
import java.util.concurrent.ThreadLocalRandom;
public class ConnectFourCopy {
   
   
   
   public static int EMPTY = 5;
   public static int NUMPLAYER = 2;    // number  of players
   public static int NUMROW = 6;       // number of rows on the game board
   public static int NUMCOL = 7;       // number of columns on the game board
   public static int MAXGAME;      // number of games needed to win to win a match
   
   public static int numMove;            // num of move that has been made in this game
   public static int curPlayer;          // the id number of the current player
   public static int grid[][];           // represents the grid of the game board
   public static int score[];            // represents the scores of the players
   public static boolean ai;
   public static int playedCol = 3; 
   public static int playedCol2 = 3;
      
   public static void main (String [] args) {
   // TO DO:  implement the logic of the game
      //int row = findRow (column);
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
     // curPlayer = 1; 
      while (true){
         curPlayer = 1;
         ai ();
         curPlayer = 0;
         ai2 ();
         //ifWin();	
         //System.out.println("SDF");
         String a = sc.nextLine();
         //System.out.println("SS");
      }
   }  


//    BEGINNING OF ENGINE


   public static int findRow (int column){
   
      int row = NUMROW -1; 
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
   
   
   
   public static void ai () {
         
      boolean moveFound = false;
   
      System.out.println("\nAI: " + curPlayer);
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
            moveFound = advancedMove ();
         }
         
         if (!moveFound) {
            moveFound = forceMove ();
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
      numMove++;  
      ifWin();
   
      
   }
   
   
// CHECKS FOR WINNING MOVES TO PLAY
   
   public static boolean winningMove () {
   
      boolean win = false;
      int row = -1;
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         row = findRow (i);
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

   public static boolean block () {
   
      boolean win = false;
      int row = -1;
      
      for (int i = NUMCOL - 1; i >= 0; i--){ 
      
         row = findRow(i);
            
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
            grid [primaryRow][i] = 0;
            winMethodAchieved = false;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
         
               win = false;        
               secondaryRow = findRow (p);
         
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
                     grid [primaryRow][i] = 0;
                                    
                     if (win){
                        
                        curPlayer = 1;
                        playedCol = i;
                        grid[primaryRow][i] = EMPTY;
                           
                        if (recordRow != primaryRow - 1) {
                           //System.out.println("AAAA");
                           primaryRow = recordRow;
                        } else {
                           //System.out.println("SSS");
                           primaryRow = secondaryRow;
                           recordColumn = p;
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
   
   public static boolean defensiveMove () {
   
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
            grid [primaryRow][i] = 1;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               win = false;         
               secondaryRow = findRow (p);
               
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

   public static boolean forceMove () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
         
            grid [primaryRow][i] = 1;
            
            if (primaryRow > 0) {
               
               grid [primaryRow - 1][i] = 0;
               curPlayer = 0;		
               win = checkIfWin();
               grid [primaryRow - 1][i] = EMPTY;
               
               if (!win) {
                  grid [primaryRow][i] = 0;
                   
                  for (int p = NUMCOL - 1; p >= 0; p--){
                  
                     win = false;        
                     secondaryRow = findRow (p);
                     
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
   
// RETAINS POSITIONAL CONTROL BY TAKING AND BLOCKING THE CONTROL OF SQUARES
   
   public static boolean positionalMove () {
      
      boolean win = false;
      boolean moveFound = false;
      int primaryRow = -1;
      
      for (int i = NUMCOL - 2; i >= 1; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            
            if (primaryRow > 0) {
               
            
               grid [primaryRow - 1][i] = 0;
               curPlayer = 0;
               win = checkIfWin ();
               grid [primaryRow - 1][i] = EMPTY;
               
               if (!win) {
                  
                  grid [primaryRow][i] = 1;
                  curPlayer = 1;
                  
                  if (grid [primaryRow][i - 1] == EMPTY) {
                     grid [primaryRow][i - 1] = 1;
                     win = ifHorizontal ();
                     grid [primaryRow][i - 1] = EMPTY;
                  }
                  
                  if (win) {
                     moveFound = true;
                  }
                  
                  if (grid [primaryRow][i + 1] == EMPTY) {
                     grid [primaryRow][i + 1] = 1;
                     win = ifHorizontal ();
                     grid [primaryRow][i + 1] = EMPTY;
                  }
                  
                  
                  if (win) {
                     moveFound = true;
                  }
                  
                  grid [primaryRow][i] = 0;
                  curPlayer = 0;       
                  
                  if (grid [primaryRow][i - 1] == EMPTY) {
                     grid [primaryRow][i - 1] = 0;
                     win = ifHorizontal ();
                     grid [primaryRow][i - 1] = EMPTY;
                  }
                  
                  if (win) {
                     moveFound = true;
                  }
                  
                  if (grid [primaryRow][i + 1] == EMPTY) {
                     grid [primaryRow][i + 1] = 0;
                     win = ifHorizontal ();
                     grid [primaryRow][i + 1] = EMPTY;
                  }
                  
                  
                  if (win) {
                     moveFound = true;
                  }
               
                  grid [primaryRow - 1][i] = 0;
                  win = ifVertical ();
                  grid [primaryRow - 1][i] = EMPTY;
               
                  if (win) {
                     moveFound = true;
                  }
                  
                  if (i >= 1 && i <= NUMROW - 1) {
                     grid [primaryRow - 1][i - 1] = 0;
                     win = upLeft();
                     grid [primaryRow - 1][i - 1] = EMPTY;
               
                     if (win) {
                        moveFound = true;
                     }
                     
                     grid [primaryRow + 1][i - 1] = 0;
                     win = upLeft();
                     grid [primaryRow + 1][i - 1] = EMPTY;
               
                     if (win) {
                        moveFound = true;
                     }
                  }
                  
                  if (i >= 0 && i <= NUMROW - 2) {
                     grid [primaryRow + 1][i + 1] = 0;
                     win = upLeft();
                     grid [primaryRow + 1][i + 1] = EMPTY;
                  
                     if (win) {
                        moveFound = true;
                     }
                     
                     grid [primaryRow - 1][i + 1] = 0;
                     win = upRight();
                     grid [primaryRow - 1][i + 1] = EMPTY;
                  
                     if (win) {
                        moveFound = true;
                     }
                  }
                  
                  
                  
                  if (moveFound) {
                     curPlayer = 1;
                     System.out.println("POSITIONAL MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                     grid[primaryRow][i] = 1;
                     playedCol = i;
                     return true;     
                  }
               } 
               grid [primaryRow][i] = EMPTY;
            }
         
         
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
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            grid [primaryRow][i] = 1;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;         
               secondaryRow = findRow (p);
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  
                  curPlayer = 1;		
                  
                  if (primaryRow > 0) {
                     
                     curPlayer = 0;
                     grid [primaryRow - 1][i] = 0;
                     win = checkIfWin ();
                     grid [primaryRow - 1][i] = EMPTY;
                     
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

   public static void regularMove () {
      boolean win = false;
      boolean loop = true;
      int row = -1;
      int column;
      int numTries = 0;
   
      while (loop) {
         numTries++;
         System.out.println("NUMTRIES: " + numTries);  
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
             
               if (numTries > 100) {
                  curPlayer = 1;
                  grid [row][column] = 1;
                  playedCol = column;
                  System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
                  loop = false;
               }
                 
               if (loop && !win){
                  
                  grid[row - 1][column] = 1;
                  curPlayer = 1;
                  win = checkIfWin();
                  grid[row - 1][column] = EMPTY;
                  
                  if (!win && !defensiveForesight ()) {
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
         System.out.println("COLUMN: " + column);    
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
   
   public static boolean defensiveForesight () {
   
      boolean win = false;
      boolean moveFound = false;
      int numWinMethods = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         primaryRow = findRow(i);
         if (primaryRow != -1 && primaryRow != 0){
            numWinMethods = 0;
            grid [primaryRow][i] = 0;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               win = false;         
               secondaryRow = findRow (p);
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  grid [secondaryRow][p] = 0;
                  curPlayer = 0;		
                  win = checkIfWin();
                  grid [secondaryRow][p] = EMPTY;
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                           
                  if (numWinMethods >= 2) {
                     if (primaryRow > 0){
                        grid[primaryRow - 1][i] = 1;
                        curPlayer = 1;
                        win = checkIfWin();
                        grid[primaryRow - 1][i] = EMPTY;
                                    
                        if (!win){
                           moveFound = true;
                        }
                     } else {
                        moveFound = true;
                     }
                                 
                     if (moveFound) {
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
   
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   public static void ai2 () {
         
      boolean moveFound = false;
   
      System.out.println("\nAI: " + curPlayer);
      if (!winningMove2()) {
         
         if (numMove >= 5){ 
            moveFound = block2 ();
         
         }
      
         if (numMove >=3 && !moveFound) {
            moveFound = advancedBlock2();
         
         } 
         
         if (!moveFound) {
            moveFound = defensiveMove2 ();
         } 
         
         if (!moveFound) {
            moveFound = advancedMove2 ();
         }
         
         if (!moveFound) {
            moveFound = forceMove2 ();
         }
         
         // if (!moveFound) {
      //             moveFound = positionalMove2 ();
      //          } 
         
         if (!moveFound) {
            moveFound = attackingMove2 ();
         }  
      
         if (!moveFound) {
            regularMove2 ();  
         }  
      }   
      numMove++;  
      ifWin();
   
      
   }
   
   
// CHECKS FOR WINNING MOVES TO PLAY
   
   public static boolean winningMove2 () {
   
      boolean win = false;
      int row = -1;
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         row = findRow (i);
         if (row != -1) {
            grid [row][i] = 0;
            curPlayer = 0;							
            win = checkIfWin();
            grid [row][i] = EMPTY;
            if (win){
               System.out.println("WINNING MOVE PLAYS: COLUMN: " + i + " ROW: " + row);
               grid [row][i] = 0;
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
            
         if (row != -1) {
            grid [row][i] = 1;
            curPlayer = 1;							
            win = checkIfWin();
            grid [row][i] = EMPTY;
            curPlayer = 0;	
                  
            if (win){
            
               System.out.println("BLOCK PLAYS: COLUMN: " + i + " ROW: " + row);
               grid [row][i] = 0;
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
            grid [primaryRow][i] = 1;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               win = false;        
               secondaryRow = findRow (p);
               if (secondaryRow != -1 && secondaryRow != 0) {
                  grid [secondaryRow][p] = 1;
                  curPlayer = 1;		
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
                  
                     grid [primaryRow][i] = 0;
                     grid[primaryRow - 1][i] = 1;
                     curPlayer = 1;
                     win = checkIfWin();
                     grid[primaryRow - 1][i] = EMPTY;
                     grid [primaryRow][i] = 1;
                                    
                     if (win){
                        
                        curPlayer = 1;
                        playedCol2 = i;
                        grid[primaryRow][i] = EMPTY;
                           
                        if (recordRow != primaryRow - 1) {
                           primaryRow = recordRow;
                        } else {
                           primaryRow = secondaryRow;
                           recordColumn = p;
                        }
                        grid [primaryRow][recordColumn] = 0;
                           
                        System.out.println("ADVANCED BLOCK FORCES: COLUMN: " + recordColumn + " ROW: " + primaryRow );
                        return true;
                     } else {
                        moveFound = true;
                     }
                  
                                 
                     if (moveFound) {
                        curPlayer = 0;
                        System.out.println("ADVANCED BLOCK PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        grid [primaryRow][i] = 0;
                        playedCol2 = i;
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
   
   public static boolean defensiveMove2 () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
         
            grid [primaryRow][i] = 1;
            
            if (primaryRow > 0) {
               
               curPlayer = 1;	
               grid [primaryRow][i] = 0;
               grid [primaryRow - 1][i] = 1;	
               win = checkIfWin();
               grid [primaryRow - 1][i] = EMPTY;
               
               if (!win) {
               
                  grid [primaryRow][i] = 1;
               
                   
                  for (int p = NUMCOL - 1; p >= 0; p--){
                     
                     win = false;        
                     secondaryRow = findRow (p);
                     
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
                                 curPlayer = 0;
                                 System.out.println("DEFENSIVE MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                                 grid[primaryRow][i] = 0;
                                 playedCol2 = i;
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
            grid [primaryRow][i] = 0;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               win = false;         
               secondaryRow = findRow (p);
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  grid [secondaryRow][p] = 0;
                  curPlayer = 0;		
                  win = checkIfWin();
                  grid [secondaryRow][p] = EMPTY;
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                           
                  if (numWinMethods >= 2) {
                     if (primaryRow > 0){
                        grid[primaryRow - 1][i] = 1;
                        curPlayer = 1;
                        win = checkIfWin();
                        grid[primaryRow - 1][i] = EMPTY;
                                    
                        if (!win){
                           moveFound = true;
                        }
                     } else {
                        moveFound = true;
                     }
                                 
                     if (moveFound) {
                        curPlayer = 0;
                        System.out.println("ADVANCED MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        grid [primaryRow][i] = 0;
                        playedCol2 = i;
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

   public static boolean forceMove2 () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
         
            grid [primaryRow][i] = 0;
            
            if (primaryRow > 0) {
               
               grid [primaryRow - 1][i] = 1;
               curPlayer = 1;		
               win = checkIfWin();
               grid [primaryRow - 1][i] = EMPTY;
               
               if (!win) {
                  grid [primaryRow][i] = 1;
                   
                  for (int p = NUMCOL - 1; p >= 0; p--){
                  
                     win = false;        
                     secondaryRow = findRow (p);
                     
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
                              
                                 curPlayer = 0;
                                 System.out.println("FORCE MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                                 grid[primaryRow][i] = 0;
                                 playedCol2 = i;
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
   
// RETAINS POSITIONAL CONTROL BY TAKING AND BLOCKING THE CONTROL OF SQUARES
   
   public static boolean positionalMove2 () {
      
      boolean win = false;
      boolean moveFound = false;
      int primaryRow = -1;
      
      for (int i = NUMCOL - 2; i >= 1; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            
            if (primaryRow > 0) {
               
            
               grid [primaryRow - 1][i] = 1;
               curPlayer = 1;
               win = checkIfWin ();
               grid [primaryRow - 1][i] = EMPTY;
               
               if (!win) {
                  
                  grid [primaryRow][i] = 0;
                  curPlayer = 0;
                  
                  if (grid [primaryRow][i - 1] == EMPTY) {
                     grid [primaryRow][i - 1] = 0;
                     win = ifHorizontal ();
                     grid [primaryRow][i - 1] = EMPTY;
                  }
                  
                  if (win) {
                     moveFound = true;
                  }
                  
                  if (grid [primaryRow][i + 1] == EMPTY) {
                     grid [primaryRow][i + 1] = 0;
                     win = ifHorizontal ();
                     grid [primaryRow][i + 1] = EMPTY;
                  }
                  
                  
                  if (win) {
                     moveFound = true;
                  }
                  
                  grid [primaryRow][i] = 1;
                  curPlayer = 1;       
                  
                  if (grid [primaryRow][i - 1] == EMPTY) {
                     grid [primaryRow][i - 1] = 1;
                     win = ifHorizontal ();
                     grid [primaryRow][i - 1] = EMPTY;
                  }
                  
                  if (win) {
                     moveFound = true;
                  }
                  
                  if (grid [primaryRow][i + 1] == EMPTY) {
                     grid [primaryRow][i + 1] = 1;
                     win = ifHorizontal ();
                     grid [primaryRow][i + 1] = EMPTY;
                  }
                  
                  
                  if (win) {
                     moveFound = true;
                  }
               
                  grid [primaryRow - 1][i] = 1;
                  win = ifVertical ();
                  grid [primaryRow - 1][i] = EMPTY;
               
                  if (win) {
                     moveFound = true;
                  }
                  
                  if (moveFound) {
                     curPlayer = 0;
                     System.out.println("POSITIONAL MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                     grid[primaryRow][i] = 0;
                     playedCol2 = i;
                     return true;     
                  }
               } 
               grid [primaryRow][i] = EMPTY;
            }
         
         
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
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1 && primaryRow != 0){
            
            grid [primaryRow][i] = 0;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               
               win = false;         
               secondaryRow = findRow (p);
               
               if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  
                  curPlayer = 0;		
                  
                  if (primaryRow > 0) {
                     
                     curPlayer = 1;
                     grid [primaryRow - 1][i] = 1;
                     win = checkIfWin ();
                     grid [primaryRow - 1][i] = EMPTY;
                     
                     if (!win) {
                        
                        win = false;
                        curPlayer = 0;
                        if (primaryRow >= NUMROW - 3 && p != i) {
                           grid [secondaryRow][p] = 0;  
                           horizontalWin = ifHorizontal();
                           win = horizontalWin;
                           grid [secondaryRow][p] = EMPTY;                   
                        } 
                        
                        if (primaryRow >= 1 && primaryRow <= NUMROW - 2 && !horizontalWin) {
                           grid [secondaryRow][p] = 0;
                           win = ifVertical();
                           grid [secondaryRow][p] = EMPTY;  
                        }
                        
                        if (win) {
                        
                           System.out.println("ATTACKING MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                           grid [primaryRow][i] = 0;                           
                           grid [secondaryRow][p] = EMPTY;
                           playedCol2 = i;
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

   public static void regularMove2 () {
      boolean win = false;
      boolean loop = true;
      int row = -1;
      int column;
      int numTries = 0;
   
      while (loop) {
         numTries++;
         System.out.println("NUMTRIES: " + numTries);  
         column = chooseColumn2 (numTries);
         row = findRow (column);
         if (row != -1) {
            
            win = false;
            
            curPlayer = 0;         
            if (row > 0){
               
               grid[row - 1][column] = 1;
               curPlayer = 1;
               win = checkIfWin();
               grid[row - 1][column] = EMPTY;
             
               if (numTries > 100) {
                  curPlayer = 0;
                  grid [row][column] = 0;
                  playedCol2 = column;
                  System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
                  loop = false;
               }
                 
               if (loop && !win){
                  
                  grid[row - 1][column] = 0;
                  curPlayer = 0;
                  win = checkIfWin();
                  grid[row - 1][column] = EMPTY;
                  
                  if (!win) {
                  
                     curPlayer = 0;
                     grid [row][column] = 0;
                     playedCol2 = column;
                     System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
                     loop = false;
                  }
               }  
               
                
               
            } else {
               curPlayer = 0;
               grid [row][column] = 0;
               playedCol2 = column;
               System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
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
         System.out.println("COLUMN: " + column);    
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
   
   
   
     
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public static int randomColumnGenerator (int min, int max) {
      return ThreadLocalRandom.current().nextInt(min, max + 1);
   }




   
  // END OF ENGINE 
   

   public static void ifWin (){
      
      boolean win = checkIfWin();
      if (win) {   
         score [curPlayer]++;
         
         if (score [curPlayer] == 3){
         } 
          
         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid [i][j] = EMPTY;
            }
         }  
         curPlayer = 0;
         numMove = 0;
         playedCol = 3;
         playedCol2 = 3;
      } else if (numMove == 42) {
         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid [i][j] = EMPTY;
            }
         }  
         curPlayer = 0;
         numMove = 0;
         playedCol = 3;
         playedCol2 = 3;
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