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
public class ConnectFour {
   
   
   final int EMPTY = 5;
   final int NUMPLAYER;    // number  of players
   final int NUMROW;       // number of rows on the game board
   final int NUMCOL;       // number of columns on the game board
   final int MAXGAME;      // number of games needed to win to win a match

   ConnectFourGUI gui;     // the gui that provides the front end of the game
   int numMove;            // num of move that has been made in this game
   int curPlayer;          // the id number of the current player
   int grid[][];           // represents the grid of the game board
   int score[];            // represents the scores of the players
   boolean ai;
   int playedCol = 3;
/**
* Constructor:  ConnectFour
*/
   public ConnectFour(ConnectFourGUI gui) {
      Scanner sc = new Scanner (System.in);   
      this.gui = gui;
      NUMPLAYER = gui.NUMPLAYER;
      NUMROW = gui.NUMROW;
      NUMCOL = gui.NUMCOL;
      MAXGAME = gui.MAXGAME;
   	
   //    System.out.println("Enter false for two player and true for single player");
     // ai = sc.nextBoolean();   	
      ai = true;
   // TO DO:  creation of arrays, and initialization of variables should be added here
      grid = new int [NUMROW][NUMCOL];
      score = new int [2];
      score [0] = 0;
      score [1] = 0;      
      for (int i = 0; i < NUMROW; i++){
         for (int j = 0; j < NUMCOL; j++){
            grid [i][j] = EMPTY;
         }
      }  
      curPlayer = 0;
   }

/**
* play
* This method will be called when a column is clicked.  Parameter "column" is 
* the number of the column that is clicked by the user
*/
   public void play (int column) {
   // TO DO:  implement the logic of the game
      int row = findRow (column);
      if (row != -1){
         placePiece(column,row);
      }
   		
   }  


//    BEGINNING OF ENGINE


   public int findRow (int column){
   
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
   
   
   
   public void ai () {
         
      boolean moveFound = false;
      curPlayer = 1;
      gui.setNextPlayer(curPlayer);
   
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
            moveFound = advancedMove ();
         
         }
         
         if (!moveFound) {
            moveFound = forceMove ();
         } 
         
         if (!moveFound) {
            regularMove ();  
         }  
      }     
      ifWin();
      
      curPlayer = 0;
      gui.setNextPlayer(curPlayer);
      numMove++;
   }
   
   

   
   public boolean winningMove () {
   
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
               gui.setPiece(row,i,curPlayer);
               System.out.println("WINNING MOVE PLAYS: COLUMN: " + i + " ROW: " + row);
               grid [row][i] = 1;
               return true;
            }         
         }
      }
            
      return false;   
   }


   public boolean block () {
   
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
               gui.setPiece(row,i,curPlayer);
               System.out.println("BLOCK PLAYS: COLUMN: " + i + " ROW: " + row);
               grid [row][i] = 1;
               playedCol = i;
               return true;
            }
         }
        
      }     
      return false;
   }




   public boolean advancedBlock () {
   
      boolean win = false;
      boolean moveFound = false;
      int numWinMethods = 0;
      int recordColumn = 0;
      int recordRow = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         primaryRow = findRow(i);
         if (primaryRow != -1){
            numWinMethods = 0;
            grid [primaryRow][i] = 0;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               win = false;        
               secondaryRow = findRow (p);
               if (secondaryRow != -1) {
                  grid [secondaryRow][p] = 0;
                  curPlayer = 0;		
                  win = checkIfWin();
                  grid [secondaryRow][p] = EMPTY;
                              
                  if (win) {                              
                     numWinMethods ++;
                  }
                  
                  if (numWinMethods == 1) {
                     recordColumn = p;
                     recordRow = secondaryRow;
                  }
                           
                  if (numWinMethods >= 2) {
                     if (i > 0){
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
                              primaryRow = recordRow;
                           } else {
                              primaryRow = secondaryRow;
                              recordColumn = p;
                           }
                           gui.setPiece(primaryRow,recordColumn,curPlayer);                         
                           grid [primaryRow][recordColumn] = 1;
                           
                           System.out.println("ADVANCED BLOCK FORCES: COLUMN: " + recordColumn + " ROW: " + primaryRow );
                           return true;
                        } else {
                           moveFound = true;
                        }
                     } else {
                        moveFound = true;
                     }
                                 
                     if (moveFound) {
                        curPlayer = 1;
                        System.out.println("ADVANCED BLOCK PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        gui.setPiece(primaryRow,i,curPlayer);                         
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
   
   
   public boolean defensiveMove () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1){
         
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
                     
                     if (secondaryRow != -1) {
                        
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
                                 gui.setPiece(primaryRow,i,curPlayer);                         
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
               } else {
                  curPlayer = 1;
                  System.out.println("DEFENSIVE MOVE PASSES");
                  playedCol = i;
                  grid [primaryRow][i] = EMPTY;
                  return false;
               }
               
            }
         
            grid[primaryRow][i] = EMPTY;      
         }             
      
      }
      
      return false;
   }


   public boolean advancedMove () {
   
      boolean win = false;
      boolean moveFound = false;
      int numWinMethods = 0;
   
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         primaryRow = findRow(i);
         if (primaryRow != -1){
            numWinMethods = 0;
            grid [primaryRow][i] = 1;
                   
            for (int p = NUMCOL - 1; p >= 0; p--){
               win = false;         
               secondaryRow = findRow (p);
               
               if (secondaryRow != -1) {
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
                                    
                        if (win){
                           playedCol = i;
                           grid[primaryRow][i] = EMPTY;
                           System.out.println("ADVANCED MOVE PASSES");
                           return false;
                        } else {
                           moveFound = true;
                        }
                     } else {
                        moveFound = true;
                     }
                                 
                     if (moveFound) {
                        curPlayer = 1;
                        System.out.println("ADVANCED MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        gui.setPiece(primaryRow,i,curPlayer);                         
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
   

   public boolean forceMove () {
   
      boolean win = false;
      int primaryRow = -1;
      int secondaryRow = -1;
   
   
      for (int i = NUMCOL - 1; i >= 0; i--){ 
         
         primaryRow = findRow(i);
         
         if (primaryRow != -1){
         
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
                     
                     if (secondaryRow != -1) {
                        
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
                                 gui.setPiece(primaryRow,i,curPlayer);                         
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
               } else {
                  curPlayer = 1;
                  System.out.println("FORCE MOVE PASSES");
                  playedCol = i;
                  grid [primaryRow][i] = EMPTY;
                  return false;
               }
            }    
         
            grid [primaryRow][i] = EMPTY;      
         }             
      
      }
      
      return false;
   }




   public void regularMove () {
      boolean win = false;
      boolean loop = true;
      int row = -1;
      int column;
      int numTries = 0;

      while (loop) {

         column = chooseColumn (numTries);
         row = findRow (column);
         if (row != -1) {
            numTries++;
            win = false;
            
            curPlayer = 1;         
            if (row > 0){
               
               grid[row - 1][column] = 0;
               curPlayer = 0;
               win = checkIfWin();
               grid[row - 1][column] = EMPTY;
             
               if (numTries > 100) {
                  curPlayer = 1;
                  gui.setPiece(row,column,curPlayer);
                  grid [row][column] = 1;
                  playedCol = column;
                  System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
                  loop = false;
               }
                 
               if (loop && !win){
                  
                  curPlayer = 1;
                  gui.setPiece(row,column,curPlayer);
                  grid [row][column] = 1;
                  playedCol = column;
                  System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
                  loop = false;
                  
               }  
               
                
               
            } else {
               curPlayer = 1;
               gui.setPiece(row,column,curPlayer);
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
         System.out.println("NUMTRIES: " + numTries);    
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
         //System.out.println("NUMTRIES: " + numTries);    
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
   
   public int randomColumnGenerator (int min, int max) {
      return ThreadLocalRandom.current().nextInt(min, max + 1);
   }




   
  // END OF ENGINE 
   

   public void placePiece (int column,int row){
      
      gui.setPiece(row,column,curPlayer);
      grid [row][column] = curPlayer;	
      numMove++;   
      System.out.println("\nPERSON PLAYS: COLUMN: " + column + " ROW: " + row + " MOVE: " + numMove);
           	
      ifWin();
      
      
      if (ai == true){        
         ai ();	
      } else  {
         if (curPlayer == 0){
            curPlayer = 1;
         } 
         else {
            curPlayer = 0;
         }
      }  
      
   						    
   }

   public void ifWin (){
      
      boolean win = checkIfWin();
      if (win) {   
         score [curPlayer]++;
         gui.setPlayerScore(curPlayer, score[curPlayer]);
         
         if (score [curPlayer] == 3){
            gui.showFinalWinnerMessage(curPlayer);
         } 
          
         gui.showWinnerMessage(curPlayer);
         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid [i][j] = EMPTY;
            }
         }  
         gui.resetGameBoard();
         curPlayer = 0;
         numMove = 0;
         playedCol = 3;
      } else if (numMove == 43) {
         gui.showTieGameMessage();
         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid [i][j] = EMPTY;
            }
         }  
         gui.resetGameBoard();
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