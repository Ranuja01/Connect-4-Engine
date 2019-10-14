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
public class ConnectFour {
   
   
   final byte EMPTY = 5;
   final int NUMPLAYER;    // number  of players
   final int NUMROW;       // number of rows on the game board
   final int NUMCOL;       // number of columns on the game board
   final int MAXGAME;      // number of games needed to win to win a match

   ConnectFourGUI gui;     // the gui that provides the front end of the game
   byte numMove;            // num of move that has been made in this game
   byte curPlayer;          // the id number of the current player
   int playedCol = 3;
   int numGamesRecorded;
   int positionCount = 0; 
   int numLoops = 0;
   //int numLossesRecorded;
   
   Positions grid = new Positions();   // represents the grid of the game board
   byte score[];            // represents the scores of the players
   
   Games gameData = new Games (42);
   Games lossesDataBase = new Games (100000);
   Games winsDataBase = new Games (100000);
   
   
   
   // byte gameData [][][] = new byte [42][6][7];
//    byte lossesDataBase [][][] = new byte [100000][6][7];
//    byte winsDataBase [][][] = new byte [100000][6][7];


/**
* Constructor:  ConnectFour
*/
   public ConnectFour(ConnectFourGUI gui) throws Exception {
      Scanner sc = new Scanner (System.in);   
      this.gui = gui;
      NUMPLAYER = gui.NUMPLAYER;
      NUMROW = gui.NUMROW;
      NUMCOL = gui.NUMCOL;
      MAXGAME = gui.MAXGAME;
   
      score = new byte [2];
      score [0] = 0;
      score [1] = 0;      
      for (int i = 0; i < NUMROW; i++){
         for (int j = 0; j < NUMCOL; j++){
            grid.setCell (i,j,EMPTY);
         }
      }
      
      loadDatabase ();
        
      curPlayer = 0;
   }
   //5555555 5555515 5551105 5550005 5551010 5550101
   //int grid [][] = { {5,5,5,5,5,5,5 }, {5,5,5,5,5,1,5 }, {5,5,5,1,1,0,5 }, {5,5,5,0,0,0,5 }, {5,5,5,1,0,1,0 }, {5,5,5,0,1,0,1 }};
   //int grid [][] = { {5,5,5,5,5,5,5 }, {5,5,5,5,5,5,5 }, {5,5,5,5,5,5,5 }, {5,5,5,5,5,5,5 }, {5,5,5,5,5,5,5 }, {5,5,5,5,5,5,5 }};
   
/**
* play
* This method will be called when a column is clicked.  Parameter "column" is 
* the number of the column that is clicked by the user
*/
   public void play (int column) throws Exception{
   // TO DO:  implement the logic of the game
      int row = findRow (column);
      if (row != -1){
         placePiece(column,row);
      }
   		
   }  


//    BEGINNING OF ENGINE


   public int findRow (int column){
   
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
   
   
   public void ai () throws Exception {
         
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
            gameData.game [numMove].setCell (i,j,grid.getCell (i,j));
         }
      }
      numMove++;
      
      System.out.println("Number of loops: " + numLoops);
      
      ifWin();
      
      curPlayer = 0;
      gui.setNextPlayer(curPlayer);
      
   }
  
   
   public void loadDatabase () throws Exception {
        
      Games lossGame = null;
      Games winGame = null;
      int count = 0;
      
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
            
           //  for (int j = 0; j < NUMROW; j++) {
         //                      
         //                for (int k = 0; k < NUMCOL; k++) {
         //                   System.out.print (lossGame.game [positionCount].getCell(j,k) + " ");
         //                }
         //                System.out.println ("AAAA");
         //                
         //             }
            
            // for (int i = positionCount; i < positionCount + 4; i++) {                                  
         //                for (int j = 0; j < NUMROW; j++) {
         //                      
         //                   for (int k = 0; k < NUMCOL; k++) {
         //                      System.out.print (lossGame.game [i - positionCount].getCell(j,k));
         //                   }
         //                   System.out.print ("  B ");
         //                }
         //                System.out.println ();
         //             }
         
         
            count = 0;
            while (count < lossGame.game.length) {                   
               
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
      
         //positionCount++;       
         //positionCount *= 4;
         System.out.println (positionCount);
      
      
         stmt.close();
         rs.close();
         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   
   }
   
   
   public void uploadDatabase () throws Exception {
      
      boolean duplicateFound = false;
      int count = 0;
      int index = 0;
            
      String lossPosition = "";
      String winPosition = "";
      
      String [] lossRows;
      String [] winRows;
      
      String lossConcat = "";
      String winConcat = "";
      
      int positionNum = 0;
      
      //byte inverseGameData [][][] = new byte [42][6][7];   
      Games inverseGameData = new Games (4);
      Games requiredGameData = new Games (4);   
    
      String connectionUrl = "jdbc:sqlserver://USER:1433;databaseName=Connect4Memory;user=Ranuja;password=timetwist";
      try {
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
         }
      
         Connection con = DriverManager.getConnection(connectionUrl); 
            
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         
          
      
      ////////////////////////////////////////
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
               oos.writeObject(requiredGameData);
            
               byte[] lossesAsBytes = baos.toByteArray();
               PreparedStatement pstmt = con.prepareStatement("INSERT INTO Losses (positions) VALUES(?)");
            
               ByteArrayInputStream bais = new ByteArrayInputStream(lossesAsBytes);
            
               pstmt.setBinaryStream(1, bais, lossesAsBytes.length);
               pstmt.executeUpdate();
            
            /////
            
               oos.writeObject(inverseGameData);
            
               byte[] winsAsBytes = baos.toByteArray();
               pstmt = con.prepareStatement("INSERT INTO Wins (positions) VALUES(?)");
            
               bais = new ByteArrayInputStream(winsAsBytes);
            
               pstmt.setBinaryStream(1, bais, winsAsBytes.length);
               pstmt.executeUpdate();
               pstmt.close();  
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
            
               oos.writeObject(requiredGameData);
            
               byte[] lossesAsBytes = baos.toByteArray();
               PreparedStatement pstmt = con.prepareStatement("INSERT INTO Losses (positions) VALUES(?)");
            
               ByteArrayInputStream bais = new ByteArrayInputStream(lossesAsBytes);
            
               pstmt.setBinaryStream(1, bais, lossesAsBytes.length);
               pstmt.executeUpdate();
            
            /////
            
               oos.writeObject(inverseGameData);
            
               byte[] winsAsBytes = baos.toByteArray();
               pstmt = con.prepareStatement("INSERT INTO Wins (positions) VALUES(?)");
            
               bais = new ByteArrayInputStream(winsAsBytes);
            
               pstmt.setBinaryStream(1, bais, winsAsBytes.length);
               pstmt.executeUpdate();
               pstmt.close();   
            }
         }
      
      
      } catch (SQLException e) {
         e.printStackTrace();
      }
            
         
   }

   
   public boolean lossesMemoryCheck () {
   
      for (int i = 0; i < positionCount; i++) {
         if (Arrays.deepEquals(lossesDataBase.game [i].position, grid.position)) {
            return true;
         }  
      }   
      return false;
      
   }
   
   public boolean winsMemoryCheck () {
      
   
      for (int i = 0; i < positionCount; i++) {
         if (Arrays.deepEquals(winsDataBase.game [i].position, grid.position)) {                       
                  
            for (int j = 0; j < NUMROW; j++) {
                     
               for (int k = 0; k < NUMCOL; k++) {
                  numLoops++;
                  if (i != positionCount - 2) {
                  
                     if ((winsDataBase.game [i + 1].getCell(j,k) != grid.getCell(j,k)) && (i + 1) % 4 != 0) {
                     
                        System.out.println(i);
                        curPlayer = 1;
                        System.out.println("DATABASE PLAYS: COLUMN: " + j + " ROW: " + k);
                        gui.setPiece(k,j,curPlayer);                         
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
   
   public boolean winningMove () {
   
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
               gui.setPiece(row,i,curPlayer);
               System.out.println("WINNING MOVE PLAYS: COLUMN: " + i + " ROW: " + row);
               grid.setCell(row,i,1);
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
         
            grid.setCell(row,i,0);
            curPlayer = 0;							
            win = checkIfWin();
            grid.setCell(row,i,EMPTY);
            curPlayer = 1;	
                  
            if (win){
            
               gui.setPiece(row,i,curPlayer);
               System.out.println("BLOCK PLAYS: COLUMN: " + i + " ROW: " + row);
               grid.setCell(row,i,1);
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
                        gui.setPiece(primaryRow,recordColumn,curPlayer);
                        grid.setCell(primaryRow,recordColumn,1);                         
                           
                        System.out.println("ADVANCED BLOCK FORCES: COLUMN: " + recordColumn + " ROW: " + primaryRow );
                        return true;
                     } else {
                        moveFound = true;
                     }
                  
                                 
                     if (moveFound) {
                        curPlayer = 1;
                        System.out.println("ADVANCED BLOCK PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        gui.setPiece(primaryRow,i,curPlayer);  
                                               
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
   
   public boolean defensiveMove () {
   
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
                                 System.out.println("DEFENSIVE MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                                 gui.setPiece(primaryRow,i,curPlayer);
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
                              System.out.println("THIRD EYE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                              gui.setPiece(primaryRow,i,curPlayer);                         
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
                        System.out.println("ADVANCED MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                        gui.setPiece(primaryRow,i,curPlayer);
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
                                 System.out.println("FORCE MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                                 gui.setPiece(primaryRow,i,curPlayer);   
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
                     //System.out.println("AAAAAHH: " + i + " ROW: " + primaryRow);
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
                              System.out.println("GODS EYE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                              gui.setPiece(primaryRow,i,curPlayer);                         
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
   
   public boolean positionalMove () {
      
      boolean win = false;
      boolean moveFound = false;
      boolean databaseCheck = false;
      boolean foresight = false;
      int temp = 0;
      
      int primaryRow = -1;
            
      for (int i = NUMCOL - 1; i >= 0; i--){ 
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
                        //System.out.println ("AAAAA");
                        moveFound = true;
                     }
                  }
                        
                        
                  if (primaryRow < NUMROW - 1 && grid.getCell(primaryRow + 1,i - 1) == EMPTY) {
                     
                     grid.setCell(primaryRow + 1,i - 1,0);      
                     win = upRight();
                     grid.setCell(primaryRow + 1,i - 1,EMPTY);
                        
                     if (win) {
                        //System.out.println ("BBB");
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
                        //System.out.println ("CCC");
                        moveFound = true;
                     }
                  }
                        
                  if (primaryRow > 0 && grid.getCell(primaryRow - 1,i + 1) == EMPTY) {
                     
                     grid.setCell(primaryRow - 1,i + 1,0);      
                     win = upRight();
                     grid.setCell(primaryRow - 1,i + 1,EMPTY);
                        
                     if (win) {
                        //System.out.println ("D");
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
                  databaseCheck = lossesMemoryCheck ();
                  grid.setCell(primaryRow,i,EMPTY);
                  
                  if (!databaseCheck) {
                     if (winsMemoryCheck ()){
                        return true;
                     }
                  } else {
                     System.out.println("DATABASE REDIRECTS: COLUMN: " + i + " ROW: " + primaryRow);
                  }
                  
                  grid.setCell(primaryRow,i,1);
                  curPlayer = 0;
                  foresight = defensiveForesight ();                  
                  grid.setCell(primaryRow,i,EMPTY);
                  
                  
                  if (!databaseCheck && !foresight) {
                     curPlayer = 1;
                     System.out.println("POSITIONAL MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                     gui.setPiece(primaryRow,i,1);                         
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
  
   public boolean attackingMove () {
   
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
            
            curPlayer = 1;		
                     
            if (primaryRow != 0) {
               curPlayer = 0;
               grid.setCell(primaryRow - 1,i,0);
               win = checkIfWin ();
               grid.setCell(primaryRow - 1,i,EMPTY);
            }      
                     
            if (!win) {
                   
               for (int p = NUMCOL - 1; p >= 0; p--){
               
                  win = false;         
                  secondaryRow = findRow (p);
                  numLoops++;
               
                  if (secondaryRow != -1 && secondaryRow != 0) {
                  
                  
                  
                        
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
                           System.out.println("DATABASE REDIRECTS: COLUMN: " + i + " ROW: " + primaryRow);
                        }
                        
                        grid.setCell(primaryRow,i,1);
                        curPlayer = 0;
                        foresight = defensiveForesight ();                  
                        grid.setCell(primaryRow,i,EMPTY);
                        
                        if (!databaseCheck && !foresight) {
                        
                           System.out.println("ATTACKING MOVE PLAYS: COLUMN: " + i + " ROW: " + primaryRow);
                           gui.setPiece(primaryRow,i,1);                         
                           grid.setCell(primaryRow,i,1);
                           grid.setCell(secondaryRow,p,EMPTY);                           
                           playedCol = i;
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
         foresight = false;
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
                  gui.setPiece(row,column,curPlayer);
                  grid.setCell(row,column,1);
                  playedCol = column;
                  System.out.println("REGULAR MOVE FORCES: COLUMN: " + column + " ROW: " + row);
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
                           databaseCheck = true;
                        }
                     } else {
                        System.out.println("DATABASE REDIRECTS: COLUMN: " + column + " ROW: " + row);
                     
                     }
                     
                     grid.setCell(row - 1,column,1);
                     curPlayer = 1;
                     win = checkIfWin();
                     grid.setCell(row - 1,column,EMPTY);
                  } 
                  
                  //if (numMove < 20) {
                  grid.setCell(row,column,1);
                  curPlayer = 0;
                  foresight = defensiveForesight ();
                  grid.setCell(row,column,EMPTY);
                 //  } else {
               //                      foresight = false;
               //                   }
                  if (!win  && !foresight &&!databaseCheck) {
                     curPlayer = 1;
                     gui.setPiece(row,column,curPlayer);
                     grid.setCell(row,column,1);
                     playedCol = column;
                     System.out.println("REGULAR MOVE PLAYS: COLUMN: " + column + " ROW: " + row);
                     loop = false;
                  }
                  
                  
               }  
               
                
               
            } else {
               curPlayer = 1;
               gui.setPiece(row,column,curPlayer);
               grid.setCell(row,column,1);
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
                          // System.out.println(win + " AAA");   
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
   

   public void placePiece (int column,int row) throws Exception{
      
      gui.setPiece(row,column,0);
      grid.setCell(row,column,0);
     // gameData [numMove] = grid.clone();
      for (int i = 0; i < NUMROW; i++){
         for (int j = 0; j < NUMCOL; j++){
            gameData.game [numMove].setCell (i,j,grid.getCell(i,j));
         }
      }
      
      numMove++;   
      System.out.println("\nPERSON PLAYS: COLUMN: " + column + " ROW: " + row + " MOVE: " + numMove);
      curPlayer = 0;  
      System.out.println(checkIfWin());
      ifWin();
      
      
      ai ();
      
      if (curPlayer == 0){
         curPlayer = 1;
      } 
      else {
         curPlayer = 0;
      }
   
      
   						    
   }

   public void ifWin () throws Exception{
      
      boolean win = checkIfWin();
      if (win) {   
      
         score [curPlayer]++;
         gui.setPlayerScore(curPlayer, score[curPlayer]);
         
         uploadDatabase ();
         
         
         if (score [curPlayer] == 3){
            gui.showFinalWinnerMessage(curPlayer);
         } 
          
         gui.showWinnerMessage(curPlayer);
        
         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid.setCell(i,j,EMPTY);
            }
         }  
         gui.resetGameBoard();
         curPlayer = 0;
         numMove = 0;
         playedCol = 3;
      } else if (numMove == 42) {
         gui.showTieGameMessage();
         
         for (int i = 0; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               grid.setCell(i,j,EMPTY);
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