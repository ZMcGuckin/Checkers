import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Simple Checkers game created using Java Swing Graphics.
 *
 * @author Zach McGuckin
 *
 */
@SuppressWarnings("serial")
public class CheckersMain extends JFrame 
{
   // Named-constants for the game board
   public static final int ROWS = 8;  // ROWS by COLS cells
   public static final int COLS = 8;
   // Named-constants of the various dimensions used for graphics drawing
   public static final int cellSize = 100; // cell width and height (square)
   public static final int canvasWidth = cellSize * COLS;  // the drawing canvas
   public static final int canvasHeight = cellSize * ROWS;
   public static final int gridWidth = 6;                   // Grid-line's width
   public static final int gridWidthHalf = gridWidth / 2; // Grid-line's half-width
   // Symbols (redPlayer/blackPlayer) are displayed inside a cell, with padding from border
   public static final int cellPadding = cellSize / 6;
   public static final int symbolSize = cellSize - cellPadding * 2; // width/height
   public static final int symbolStrokeWidth = 8; // pen's stroke width

   // Use an enumeration (inner class) to represent the various states of the game
   public enum GameState 
   {
      playing, redWon, blackWon
   }
   private GameState currentState;  // the current game state

   // Use an enumeration (inner class) to represent the seeds and cell contents
   public enum Seed 
   {
      empty, redPlayer, blackPlayer, redKing, blackKing
   }
   private Seed currentPlayer;  // the current player
   private Seed picked; //piece selected by mouse click
   private boolean rightPlayer;  //checks if the right piece was chosen in relation to the player
   
   private int lastRow;
   private int lastCol;  
   
   private boolean moved;
   private boolean jumped;
   private int jumps;

   private Seed[][] board; // Game board of ROWS-by-COLS cells
   private DrawCanvas canvas; // Drawing canvas (JPanel) for the game board
   private JLabel statusBar;  // Status Bar

   /** Constructor to setup the game and the GUI components */
   public CheckersMain() 
   {
      canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
      canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));

      // The canvas (JPanel) fires a MouseEvent upon mouse-click
      canvas.addMouseListener(new MouseAdapter() 
      {
         //@Override
         public void mousePressed(MouseEvent e) // mouse-clicked handler
         {
        	//if the game is going
            if (currentState == GameState.playing) {
            	int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int rSelected = mouseY / cellSize;
                int cSelected = mouseX / cellSize;
            	if (rSelected >= 0 && rSelected < ROWS && cSelected >= 0 && cSelected < COLS) {
            		if(currentPlayer == Seed.blackPlayer && (board[rSelected][cSelected] == Seed.blackPlayer || board[rSelected][cSelected] == Seed.blackKing)
            				|| currentPlayer == Seed.redPlayer && (board[rSelected][cSelected]== Seed.redPlayer || board[rSelected][cSelected] == Seed.redKing)){
            			//The player picked their own piece
            			rightPlayer = true;
            			//Keep that information of where the piece was
            			picked = board[rSelected][cSelected];
            			lastRow = rSelected;
            			lastCol = cSelected;
            		}
                } 
            } 
            else        // game over
               initGame(); // restart the game
         }
         public void mouseReleased(MouseEvent e) 
		{  // mouse-clicked handler
        	 
	            if (currentState == GameState.playing) 
	            {
	            	int mouseX = e.getX();
		            int mouseY = e.getY();
		            // Get the row and column clicked
		            int rReleased = mouseY / cellSize;
		            int cReleased = mouseX / cellSize;
	            	if (rReleased >= 0 && rReleased < ROWS && cReleased >= 0 && cReleased < COLS && rightPlayer == true && board[rReleased][cReleased] == Seed.empty) 
	                {
	            		if(!canJump()){
	           				//Moving Up
	           				if((rReleased+1==lastRow) && (cReleased+1==lastCol||cReleased-1==lastCol) && picked != Seed.redPlayer)
	           				{
	           					board[rReleased][cReleased] = picked; // Make a move
	           					board[lastRow][lastCol] = Seed.empty;	
	           					moved = true;
	           				}
	           				//Moving Down
	           				else if((rReleased-1==lastRow) && (cReleased+1==lastCol||cReleased-1==lastCol) && picked != Seed.blackPlayer)
	           				{
	            				board[rReleased][cReleased] = picked; // Make a move
	            				board[lastRow][lastCol] = Seed.empty;	
	            				moved = true;
	            			}
	            		}
	            		//Otherwise a piece the player has, has to jump
	            		else{
	            			Seed kingType = (currentPlayer == Seed.blackPlayer) ? Seed.blackKing : Seed.redKing;
	            			//Jumping Up and Left
            				if((rReleased+2==lastRow) && (cReleased+2==lastCol) && picked != Seed.redPlayer)
            				{
            					if(board[rReleased+1][cReleased+1] != currentPlayer && board[rReleased+1][cReleased+1] != kingType && board[rReleased+1][cReleased+1] != Seed.empty)
            					{
            						board[rReleased][cReleased] = picked; // Make a move
            						board[lastRow][lastCol] = Seed.empty;
            						board[rReleased+1][cReleased+1] = Seed.empty;
            						jumped = true;
            					}	
            				}
            				//Jumping Up and Right
            				else if((rReleased+2==lastRow) && (cReleased-2==lastCol) && picked != Seed.redPlayer)
            				{
            					if(board[rReleased+1][cReleased-1] != currentPlayer && board[rReleased+1][cReleased-1] != kingType && board[rReleased+1][cReleased-1] != Seed.empty)
            					{
            						board[rReleased][cReleased] = picked; // Make a move
            						board[lastRow][lastCol] = Seed.empty;
            						board[rReleased+1][cReleased-1] = Seed.empty;
            						jumped = true;
            					}
            				}
            				//Jumping Down and Left
            				else if((rReleased-2==lastRow) && (cReleased+2==lastCol) && picked != Seed.blackPlayer)
            				{
            					if(board[rReleased-1][cReleased+1] != currentPlayer && board[rReleased-1][cReleased+1] != kingType && board[rReleased-1][cReleased+1] != Seed.empty)
            					{
            						board[rReleased][cReleased] = picked; // Make a move
            						board[lastRow][lastCol] = Seed.empty;
            						board[rReleased-1][cReleased+1] = Seed.empty;
            						jumped = true;
            					}	
            				}
            				//Jumping Down and Right
            				else if((rReleased-2==lastRow) && (cReleased-2==lastCol) && picked != Seed.blackPlayer)
            				{
            					if(board[rReleased-1][cReleased-1] != currentPlayer && board[rReleased-1][cReleased-1] != kingType && board[rReleased-1][cReleased-1] != Seed.empty)
            					{
            						board[rReleased][cReleased] = picked; // Make a move
            						board[lastRow][lastCol] = Seed.empty;
            						board[rReleased-1][cReleased-1] = Seed.empty;
            						jumped = true;
            					}
            				}
	            		}
	            	}
	            	//King the Player
		            if((rReleased == 7 || rReleased == 0) && (moved == true || jumped == true))
			        {
		            	board[rReleased][cReleased] = (currentPlayer == Seed.blackPlayer) ? Seed.blackKing : Seed.redKing;; // Make them a King
			            //Switch player
			            currentPlayer = (currentPlayer == Seed.redPlayer) ? Seed.blackPlayer : Seed.redPlayer;
			            rightPlayer = false;
			            jumps = 0;
			        }
		            //If the player jumped and didn't become a king, check to see if they have more jumps
			        else if(jumped == true && canJumpAgain(rReleased, cReleased))
			           	jumps += 1;
			        else if(moved == true || jumped == true)
			        {
			           	//Switch player
			           	currentPlayer = (currentPlayer == Seed.redPlayer) ? Seed.blackPlayer : Seed.redPlayer;
			           	rightPlayer = false;
			           	jumps = 0;
			        }
	            }
	            
	            repaint();  // Call-back paintComponent().
	            updateGame(currentPlayer);
	            
	            jumped = false;
            	moved = false;
			}
      });

      // Setup the status bar (JLabel) to display status message
      statusBar = new JLabel("  ");
      statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
      statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      cp.add(canvas, BorderLayout.CENTER);
      cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      pack();  // pack all the components in this JFrame
      setTitle("Checkers");
      setVisible(true);  // show this JFrame

      board = new Seed[ROWS][COLS]; // allocate array
      initGame(); // initialize the game board contents and game variables
   }

   /** Initialize the game-board contents and the status */
   public void initGame() 
   {
      for (int row = 0; row < ROWS; ++row){
         for (int col = 0; col < COLS; ++col) 
         {
            if(row == 0 || row == 2)
            {
            	if(col%2==0)
            		board[row][col] = Seed.redPlayer;
            }
            else if(row == 1)
            {
            	if(col%2!=0)
            		board[row][col] = Seed.redPlayer;
            }            
            else if(row == 5 || row == 7)
            {
            	if(col%2!=0)
            		board[row][col] = Seed.blackPlayer;
            }
            else if(row == 6)
            {
            	if(col%2==0)
            		board[row][col] = Seed.blackPlayer;
            }            
            else
            	board[row][col] = Seed.empty;
         }
      }
      
      currentState = GameState.playing; // ready to play
      currentPlayer = Seed.redPlayer;  // redPlayer plays first
   }

   /* Update the currentState after the player with "theSeed" has placed on
       (rSelected, cSelected). */
   public void updateGame(Seed theSeed) 
   {
      if (hasWon(theSeed)) 
         currentState = (theSeed == Seed.redPlayer) ? GameState.blackWon : GameState.redWon;
      // Otherwise, no change to current state (still GameState.playing).
   }

   /** Return true if the player with "theSeed" has won after placing at
       (rSelected, cSelected) */
   public boolean hasWon(Seed theSeed) 
   {
	   for (int row = 0; row < ROWS; ++row) 
	      {
	         for (int col = 0; col < COLS; ++col) 
	         {
	        	 //Check to see if the player has any pieces left on the board, if not they lose
	        	 if(currentPlayer == Seed.redPlayer)
	        	 {
	        		 if (board[row][col] == currentPlayer || board[row][col] == Seed.redKing)
	        			 return false;
	        	 }
	        	 else if(currentPlayer == Seed.blackPlayer)
	        	 {
	        		 if (board[row][col] == currentPlayer || board[row][col] == Seed.blackKing)
	        			 return false;
	        	 }
	         }
	      }
      return true;
   }
   
   public boolean canJump() 
   {
	   for (int row = 0; row < ROWS; ++row) 
	      {
	         for (int col = 0; col < COLS; ++col) 
	         {
	        	
	            if(currentPlayer == Seed.redPlayer && (board[row][col] == Seed.redPlayer || board[row][col] == Seed.redKing)) 
	            {
	            	if(row+2<ROWS)
	            	{
	            		if(col+2<COLS)
	            		{
	            			if((board[row+1][col+1] == Seed.blackPlayer || board[row+1][col+1] == Seed.blackKing)&& board[row+2][col+2] == Seed.empty)
	            				return true;
	            		}
	            		if(col-2>=0)
	            		{
	            			if((board[row+1][col-1] == Seed.blackPlayer || board[row+1][col-1] == Seed.blackKing) && board[row+2][col-2] == Seed.empty)
	            				return true;
	            		}
	            	}
	            	//Handle Backwards Jumping for kings
            		if(board[row][col] == Seed.redKing){
            			if(row-2>=0)
    	            	{
    	            		if(col+2<COLS)
    	            		{
    	            			if((board[row-1][col+1] == Seed.blackPlayer || board[row-1][col+1] == Seed.blackKing) && board[row-2][col+2] == Seed.empty)
    	            				return true;
    	            		}
    	            		if(col-2>=0)
    	            		{
    	            			if((board[row-1][col-1] == Seed.blackPlayer || board[row-1][col-1] == Seed.blackKing) && board[row-2][col-2] == Seed.empty)
    	            				return true;
    	            		}
    	            	}
            		}
	            }
	            else if(currentPlayer == Seed.blackPlayer && (board[row][col] == Seed.blackPlayer || board[row][col] == Seed.blackKing)) 
	            {
	            	if(row-2>=0)
	            	{
	            		if(col+2<COLS)
	            		{
	            			if((board[row-1][col+1] == Seed.redPlayer || board[row-1][col+1] == Seed.redKing) && board[row-2][col+2] == Seed.empty){
	            				return true;
	            			}
	            		}
	            		if(col-2>=0)
	            		{
	            			if((board[row-1][col-1] == Seed.redPlayer || board[row-1][col-1] == Seed.redKing) && board[row-2][col-2] == Seed.empty){
	            				return true;
	            			}
	            		}
	            	}
	            	//Handle backwards jumping for king
	            	if(board[row][col] == Seed.blackKing){
	            		if(row+2<ROWS)
		            	{
		            		if(col+2<COLS)
		            		{
		            			if((board[row+1][col+1] == Seed.redPlayer || board[row+1][col+1] == Seed.redKing) && board[row+2][col+2] == Seed.empty)
		            				return true;
		            		}
		            		if(col-2>=0)
		            		{
		            			if((board[row+1][col-1] == Seed.redPlayer || board[row+1][col-1] == Seed.redKing)&& board[row+2][col-2] == Seed.empty)
		            				return true;
		            		}
		            	}
	            	}
	            }
	         }
	      }
	   //return false if true wasn't already returned
      return false;
   }
   public boolean canJumpAgain(int row, int col) 
   {
	   if(currentPlayer == Seed.redPlayer)
		{
		   if(row+2<ROWS)
		   {
			   if(col+2<COLS)
			   {
				   if(board[row+1][col+1]==Seed.blackPlayer && board[row+2][col+2]==Seed.empty)
				   {
						return true;
				   }
			   }
			   if(col-2>=0)
			   {
				   if(board[row+1][col-1]==Seed.blackPlayer && board[row+2][col-2]==Seed.empty)
				   {
					   return true;
				   }
			   }
		   }
		   //Handle backwards jumps for kings
		   if(board[row][col] == Seed.redKing){
			   if(row-2>=0)
		       	{
		       		if(col+2<COLS)
		       		{
		       			if((board[row-1][col+1] == Seed.blackPlayer || board[row-1][col+1] == Seed.blackKing) && board[row-2][col+2] == Seed.empty)
		       				return true;
		       		}
		       		if(col-2>=0)
		       		{
		       			if((board[row-1][col-1] == Seed.blackPlayer || board[row-1][col-1] == Seed.blackKing) && board[row-2][col-2] == Seed.empty)
		       				return true;
		       		}
		       	}
		   }
		}
	   //The current player is black
	   else
	   {
		   if(row-2>=0)
		   {
			   if(col+2<COLS)
			   {
				   if(board[row-1][col+1]==Seed.redPlayer && board[row-2][col+2]==Seed.empty)
				   {
						return true;
				   }
			   }
			   if(col-2>=0)
			   {
				   if(board[row-1][col-1]==Seed.redPlayer && board[row-2][col-2]==Seed.empty)
				   {
					   return true;
				   }
			   }
		   }
		   if(board[row][col] == Seed.blackKing){
			   if(row+2<ROWS)
		       	{
		       		if(col+2<COLS)
		       		{
		       			if((board[row+1][col+1] == Seed.redPlayer || board[row+1][col+1] == Seed.redKing) && board[row+2][col+2] == Seed.empty)
		       				return true;
		       		}
		       		if(col-2>=0)
		       		{
		       			if((board[row+1][col-1] == Seed.redPlayer || board[row+1][col-1] == Seed.redKing)&& board[row+2][col-2] == Seed.empty)
		       				return true;
		       		}
		       	}
		   }
		}
      return false;
   }

   /**
    *  Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
    */
   class DrawCanvas extends JPanel 
   {
      //@Override
      public void paintComponent(Graphics g) // invoke via repaint()
      {  
         super.paintComponent(g);    // fill background
         setBackground(Color.WHITE); // set its background color

         // Draw the Seeds of all the cells if they are not empty
         // Use Graphics2D
         Graphics2D g2d = (Graphics2D)g;
         g2d.setStroke(new BasicStroke(symbolStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));  // Graphics2D only
         BufferedImage bg = null;
		 try 
		 {
			 URL url = CheckersMain.class.getResource("/resources/rbg.png");
			 bg = ImageIO.read(url);
		 } 
		 catch (Exception e) 
		 {
			 e.printStackTrace();
		 }
		 g.drawImage(bg, 0, 0, canvasWidth, canvasHeight, null);
         for (int row = 0; row < ROWS; ++row) 
         {
            for (int col = 0; col < COLS; ++col) 
            {
               int x1 = col * cellSize + cellPadding;
               int y1 = row * cellSize + cellPadding;
               /*Grid Lines
               if((row%2 != 0 && col%2 != 0) || (row%2 == 0 && col%2 == 0))
               {
            	   g2d.setColor(Color.LIGHT_GRAY);
                   g2d.fillRect(x1-16, y1-16, symbolSize+32, symbolSize+32);          		
               }
               */
               if (board[row][col] == Seed.redPlayer) 
               {
            	   //g2d.setColor(Color.RED); Red painted Symbol
                   //g2d.fillOval(x1, y1, symbolSize, symbolSize);
                   BufferedImage img = null;
					try {
						URL url = CheckersMain.class.getResource("/resources/red.png");
						img = ImageIO.read(url);
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.drawImage(img, x1, y1, symbolSize, symbolSize, null);
               }  
               else if (board[row][col] == Seed.blackPlayer) 
               {
            	   //g2d.setColor(Color.BLACK); Black painted Symbol
                   //g2d.fillOval(x1, y1, symbolSize, symbolSize);
            	   BufferedImage img = null;
					try {
						URL url = CheckersMain.class.getResource("/resources/black.png");
						img = ImageIO.read(url);
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.drawImage(img, x1, y1, symbolSize, symbolSize, null);
               }
               else if (board[row][col] == Seed.redKing) 
               {
            	   /*
            	   g2d.setColor(Color.RED); Red painted King
                   g2d.fillOval(x1, y1, symbolSize, symbolSize);
                   g2d.setColor(Color.YELLOW);
                   g2d.drawLine(x1+15, y1+35, x1+35, y1+20);
                   g2d.drawLine(x1+35, y1+20, x1+55, y1+35);
                   g2d.drawLine(x1+55, y1+35, x1+15, y1+35);
                   */
            	   BufferedImage img = null;
					try {
						URL url = CheckersMain.class.getResource("/resources/redKing.png");
						img = ImageIO.read(url);
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.drawImage(img, x1, y1, symbolSize, symbolSize, null);
               }
               else if (board[row][col] == Seed.blackKing) 
               {
            	   /*
            	   g2d.setColor(Color.BLACK); Black painted King
                   g2d.fillOval(x1, y1, symbolSize, symbolSize);
                   g2d.setColor(Color.YELLOW);
                   g2d.drawLine(x1+15, y1+35, x1+35, y1+20);
                   g2d.drawLine(x1+35, y1+20, x1+55, y1+35);
                   g2d.drawLine(x1+55, y1+35, x1+15, y1+35);
                   */
            	   BufferedImage img = null;
					try {
						URL url = CheckersMain.class.getResource("/resources/blackKing.png");
						img = ImageIO.read(url);
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.drawImage(img, x1, y1, symbolSize, symbolSize, null);
               }
            }
         }
         
         // Draw the grid-lines for when not using pictures
         /*
         g.setColor(Color.GRAY);
         for (int row = 1; row < ROWS; ++row) 
         {
            g.fillRoundRect(0, cellSize * row - gridWidthHalf,
                  canvasWidth, gridWidth, gridWidth, gridWidth);
         }
         for (int col = 1; col < COLS; ++col) 
         {
            g.fillRoundRect(cellSize * col - gridWidthHalf, 0,
                  gridWidth, canvasHeight, gridWidth, gridWidth);
         }
         */

         // Print status-bar message
         if (currentState == GameState.playing) 
         {            
            if (currentPlayer == Seed.redPlayer)
            {
               statusBar.setForeground(Color.RED);
               if(jumps != 0)
            	   statusBar.setText("Red Has To Jump Again");
               else if(canJump())
            	   statusBar.setText("Red Has To Jump");
               else
            	   statusBar.setText("Red's Turn");
            }
            else 
            {
               statusBar.setForeground(Color.BLACK);
               if(jumps != 0)
            	   statusBar.setText("Black Has To Jump Again");
               else if(canJump())
            	   statusBar.setText("Black Has To Jump");
               else
            	   statusBar.setText("Black's Turn");
            }
         }
         else if (currentState == GameState.redWon) 
         {
            statusBar.setForeground(Color.GREEN);
            statusBar.setText("Red Won! Click to play again. This is a Zach McGuckin production.");
         } 
         else if (currentState == GameState.blackWon) 
         {
            statusBar.setForeground(Color.GREEN);
            statusBar.setText("Black Won! Click to play again. This is a Zach McGuckin production.");
         }
      }
   }

   /** The entry main() method */
   public static void main(String[] args) 
   {
      // Run GUI codes in the Event-Dispatching thread for thread safety
      SwingUtilities.invokeLater(new Runnable() 
      {
         @Override
         public void run() 
         {
            new CheckersMain(); // Let the constructor do the job
         }
      });
   }
}