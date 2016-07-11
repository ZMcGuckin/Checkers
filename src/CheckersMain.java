import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

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
   private boolean rightPlayer;  //checks if the right piece was chosen in relation to the player
   
   private int lastRow;
   private int lastCol;
   private int rSelected;
   private int cSelected;
   
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
            int mouseX = e.getX();
            int mouseY = e.getY();
            // Get the row and column clicked
            rSelected = mouseY / cellSize;
            cSelected = mouseX / cellSize;

            if (currentState == GameState.playing) {
            	if (rSelected >= 0 && rSelected < ROWS && cSelected >= 0 && cSelected < COLS) {
            		if(currentPlayer == Seed.blackPlayer && (board[rSelected][cSelected] == Seed.blackPlayer || board[rSelected][cSelected] == Seed.blackKing)
            				|| currentPlayer == Seed.redPlayer && (board[rSelected][cSelected] == Seed.redPlayer || board[rSelected][cSelected] == Seed.redKing)){
            			rightPlayer = true;
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
	            int mouseX = e.getX();
	            int mouseY = e.getY();
	            // Get the row and column clicked
	            int rSelected2 = mouseY / cellSize;
	            int cSelected2 = mouseX / cellSize;
	            
	            if (currentState == GameState.playing) 
	            {
	            	if (rSelected2 >= 0 && rSelected2 < ROWS && cSelected2 >= 0 && rightPlayer==true
	            			&& cSelected2 < COLS && board[rSelected2][cSelected2] == Seed.empty) 
	                {
	            		if((currentPlayer==Seed.blackPlayer&&board[rSelected][cSelected]==Seed.blackKing)||(currentPlayer==Seed.redPlayer&&board[rSelected][cSelected]==Seed.redKing))
	            		{
	            			if((rSelected2+1==lastRow) && (cSelected2+1==lastCol||cSelected2-1==lastCol) && canJump() == false)
	            			{
	            				board[rSelected2][cSelected2] = (currentPlayer == Seed.blackPlayer) ? Seed.blackKing : Seed.redKing; // Make a move
	            				board[lastRow][lastCol] = Seed.empty;	
	            				moved = true;
	            			}
	            			else if((rSelected2-1==lastRow) && (cSelected2+1==lastCol||cSelected2-1==lastCol) && canJump() == false)
	            			{
	            				board[rSelected2][cSelected2] = (currentPlayer == Seed.blackPlayer) ? Seed.blackKing : Seed.redKing; // Make a move
	            				board[lastRow][lastCol] = Seed.empty;	
	            				moved = true;
	            			}
	            			else if((rSelected2+2==lastRow) && (cSelected2+2==lastCol))
	            			{
	            				if(board[rSelected2+1][cSelected2+1] != currentPlayer && board[rSelected2+1][cSelected2+1] != Seed.empty)
	            				{
	            					board[rSelected2][cSelected2] = (currentPlayer == Seed.blackPlayer) ? Seed.blackKing : Seed.redKing; // Make a move
	            					board[lastRow][lastCol] = Seed.empty;
	            					board[lastRow+1][lastCol+1] = Seed.empty;
	            					jumped = true;
	            				}	
	            			}
	            			else if((rSelected2+2==lastRow) && (cSelected2-2==lastCol))
	            			{
	            				if(board[rSelected2+1][cSelected2-1] != currentPlayer && board[rSelected2+1][cSelected2-1] != Seed.empty)
	            				{
	            					board[rSelected2][cSelected2] = (currentPlayer == Seed.blackPlayer) ? Seed.blackKing : Seed.redKing;; // Make a move
	            					board[lastRow][lastCol] = Seed.empty;
	            					board[rSelected2+1][cSelected2-1] = Seed.empty;
	            					jumped = true;
	            				}
	            			}
	            			else if((rSelected2-2==lastRow) && (cSelected2+2==lastCol))
	            			{
	            				if(board[rSelected2-1][cSelected2+1] != currentPlayer && board[rSelected2-1][cSelected2+1] != Seed.empty)
	            				{
	            					board[rSelected2][cSelected2] = (currentPlayer == Seed.blackPlayer) ? Seed.blackKing : Seed.redKing;; // Make a move
	            					board[lastRow][lastCol] = Seed.empty;
	            					board[rSelected2-1][cSelected2+1] = Seed.empty;
	            					jumped = true;
	            				}	
	            			}
	            			else if((rSelected2-2==lastRow) && (cSelected2-2==lastCol))
	            			{
	            				if(board[rSelected2-1][cSelected2-1] != currentPlayer && board[rSelected2-1][cSelected2-1] != Seed.empty)
	            				{
	            					board[rSelected2][cSelected2] = (currentPlayer == Seed.blackPlayer) ? Seed.blackKing : Seed.redKing;; // Make a move
	            					board[lastRow][lastCol] = Seed.empty;
	            					board[rSelected2-1][cSelected2-1] = Seed.empty;
	            					jumped = true;
	            				}
	            			}
	            		}
	            		else if(currentPlayer == Seed.redPlayer)
	            		{
	            			if((rSelected2-1==lastRow) && (cSelected2+1==lastCol||cSelected2-1==lastCol) && canJump() == false)
	            			{
	            				board[rSelected2][cSelected2] = currentPlayer; // Make a move
	            				board[lastRow][lastCol] = Seed.empty;	
	            				moved = true;
	            			}
	            			else if((rSelected2-2==lastRow) && (cSelected2+2==lastCol))
	            			{
	            				if(board[rSelected2-1][cSelected2+1] == Seed.blackPlayer || board[rSelected2-1][cSelected2+1] == Seed.blackKing)
	            				{
	            					board[rSelected2][cSelected2] = currentPlayer; // Make a move
	            					board[lastRow][lastCol] = Seed.empty;
	            					board[rSelected2-1][cSelected2+1] = Seed.empty;
	            					jumped = true;
	            				}	
	            			}
	            			else if((rSelected2-2==lastRow) && (cSelected2-2==lastCol))
	            			{
	            				if(board[rSelected2-1][cSelected2-1] == Seed.blackPlayer || board[rSelected2-1][cSelected2-1] == Seed.blackKing)
	            				{
	            					board[rSelected2][cSelected2] = currentPlayer; // Make a move
	            					board[lastRow][lastCol] = Seed.empty;
	            					board[rSelected2-1][cSelected2-1] = Seed.empty;
	            					jumped = true;
	            				}
	            			}
	            		}
	            		else if(currentPlayer == Seed.blackPlayer)
	            		{
	            			if((rSelected2+1==lastRow) && (cSelected2+1==lastCol||cSelected2-1==lastCol) && canJump() == false)
	            			{
	            				board[rSelected2][cSelected2] = currentPlayer; // Make a move
	            				board[lastRow][lastCol] = Seed.empty;	
	            				moved = true;
	            			}
	            			else if((rSelected2+2==lastRow) && (cSelected2+2==lastCol))
	            			{
	            				if(board[rSelected2+1][cSelected2+1] == Seed.redPlayer || board[rSelected2+1][cSelected2+1] == Seed.redKing)
	            				{
	            					board[rSelected2][cSelected2] = currentPlayer; // Make a move
	            					board[lastRow][lastCol] = Seed.empty;
	            					board[rSelected2+1][cSelected2+1] = Seed.empty;
	            					jumped = true;
	            				}	
	            			}
	            			else if((rSelected2+2==lastRow) && (cSelected2-2==lastCol))
	            			{
	            				if(board[rSelected2+1][cSelected2-1] == Seed.redPlayer || board[rSelected2+1][cSelected2-1] == Seed.redKing)
	            				{
	            					board[rSelected2][cSelected2] = currentPlayer; // Make a move
	            					board[lastRow][lastCol] = Seed.empty;
	            					board[rSelected2+1][cSelected2-1] = Seed.empty;
	            					jumped = true;
	            				}
	            			}
	            		}
	                }
	               
	            }
	            if(currentPlayer == Seed.redPlayer && rSelected2 == 7 && (moved == true || jumped == true))
	            {
	            	board[rSelected2][cSelected2] = Seed.redKing;
	            	//Switch player
	            	currentPlayer = (currentPlayer == Seed.redPlayer) ? Seed.blackPlayer : Seed.redPlayer;
	            	rightPlayer = false;
	            	jumps = 0;
	            }
	            else if(currentPlayer == Seed.blackPlayer && rSelected2 == 0 && (moved == true || jumped == true))
	            {
	            	board[rSelected2][cSelected2] = Seed.blackKing;
	            	//Switch player
	            	currentPlayer = (currentPlayer == Seed.redPlayer) ? Seed.blackPlayer : Seed.redPlayer;
	            	rightPlayer = false;
	            	jumps = 0;
	            }
	            else if(multiJump(rSelected2, cSelected2) && jumped == true)
	            	jumps += 1;
	            else if(moved == true || jumped == true)
	            {
	            	//Switch player
	            	currentPlayer = (currentPlayer == Seed.redPlayer) ? Seed.blackPlayer : Seed.redPlayer;
	            	rightPlayer = false;
	            	jumps = 0;
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
      for (int row = 0; row < ROWS; ++row) 
      {
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
      currentPlayer = Seed.redPlayer;       // redPlayer plays first
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
	        	
	            if(currentPlayer == Seed.redPlayer && board[row][col] == Seed.redPlayer) 
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
	            }
	            else if(currentPlayer == Seed.redPlayer && board[row][col] == Seed.redKing) 
	            {
	            	if(row+2<ROWS)
	            	{
	            		if(col+2<COLS)
	            		{
	            			if((board[row+1][col+1] == Seed.blackPlayer || board[row+1][col+1] == Seed.blackKing) && board[row+2][col+2] == Seed.empty)
	            				return true;
	            		}
	            		if(col-2>=0)
	            		{
	            			if((board[row+1][col-1] == Seed.blackPlayer || board[row+1][col-1] == Seed.blackKing) && board[row+2][col-2] == Seed.empty)
	            				return true;
	            		}
	            	}
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
	            else if(currentPlayer == Seed.blackPlayer && board[row][col] == Seed.blackPlayer) 
	            {
	            	if(row-2>=0)
	            	{
	            		if(col+2<COLS)
	            		{
	            			if((board[row-1][col+1] == Seed.redPlayer || board[row-1][col+1] == Seed.redKing) && board[row-2][col+2] == Seed.empty)
	            				return true;
	            		}
	            		if(col-2>=0)
	            		{
	            			if((board[row-1][col-1] == Seed.redPlayer || board[row-1][col-1] == Seed.redKing) && board[row-2][col-2] == Seed.empty)
	            				return true;
	            		}
	            	}
	            }
	            else if(currentPlayer == Seed.blackPlayer && board[row][col] == Seed.blackKing) 
	            {
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
	            	if(row-2>=0)
	            	{
	            		if(col+2<COLS)
	            		{
	            			if((board[row-1][col+1] == Seed.redPlayer || board[row-1][col+1] == Seed.redKing) && board[row-2][col+2] == Seed.empty)
	            				return true;
	            		}
	            		if(col-2>=0)
	            		{
	            			if((board[row-1][col-1] == Seed.redPlayer || board[row-1][col-1] == Seed.redKing) && board[row-2][col-2] == Seed.empty)
	            				return true;
	            		}
	            	}
	            }
	         }
	      }
      return false;
   }
   public boolean multiJump(int row, int col) 
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
		}
	   if(currentPlayer == Seed.redPlayer && board[row][col] == Seed.redKing) 
       {
       	if(row+2<ROWS)
       	{
       		if(col+2<COLS)
       		{
       			if((board[row+1][col+1] == Seed.blackPlayer || board[row+1][col+1] == Seed.blackKing) && board[row+2][col+2] == Seed.empty)
       				return true;
       		}
       		if(col-2>=0)
       		{
       			if((board[row+1][col-1] == Seed.blackPlayer || board[row+1][col-1] == Seed.blackKing) && board[row+2][col-2] == Seed.empty)
       				return true;
       		}
       	}
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
	   if(currentPlayer == Seed.blackPlayer)
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
		}
	   if(currentPlayer == Seed.blackPlayer && board[row][col] == Seed.blackKing) 
       {
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
       	if(row-2>=0)
       	{
       		if(col+2<COLS)
       		{
       			if((board[row-1][col+1] == Seed.redPlayer || board[row-1][col+1] == Seed.redKing) && board[row-2][col+2] == Seed.empty)
       				return true;
       		}
       		if(col-2>=0)
       		{
       			if((board[row-1][col-1] == Seed.redPlayer || board[row-1][col-1] == Seed.redKing) && board[row-2][col-2] == Seed.empty)
       				return true;
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
         // Use Graphics2D which allows us to set the pen's stroke
         Graphics2D g2d = (Graphics2D)g;
         g2d.setStroke(new BasicStroke(symbolStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));  // Graphics2D only
         BufferedImage bg = null;
		 try 
		 {
			 bg = ImageIO.read(new File("data/rbg.png"));
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
               /*
               if((row%2 != 0 && col%2 != 0) || (row%2 == 0 && col%2 == 0))
               {
            	   g2d.setColor(Color.LIGHT_GRAY);
                   g2d.fillRect(x1-16, y1-16, symbolSize+32, symbolSize+32);          		
               }
               */
               if (board[row][col] == Seed.redPlayer) 
               {
            	   //g2d.setColor(Color.RED);
                   //g2d.fillOval(x1, y1, symbolSize, symbolSize);
                   BufferedImage img = null;
					try {
						img = ImageIO.read(new File("data/red.png"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.drawImage(img, x1, y1, symbolSize, symbolSize, null);
               }  
               else if (board[row][col] == Seed.blackPlayer) 
               {
            	   //g2d.setColor(Color.BLACK);
                   //g2d.fillOval(x1, y1, symbolSize, symbolSize);
            	   BufferedImage img = null;
					try {
						img = ImageIO.read(new File("data/black.png"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.drawImage(img, x1, y1, symbolSize, symbolSize, null);
               }
               else if (board[row][col] == Seed.redKing) 
               {
            	   /*
            	   g2d.setColor(Color.RED);
                   g2d.fillOval(x1, y1, symbolSize, symbolSize);
                   g2d.setColor(Color.YELLOW);
                   g2d.drawLine(x1+15, y1+35, x1+35, y1+20);
                   g2d.drawLine(x1+35, y1+20, x1+55, y1+35);
                   g2d.drawLine(x1+55, y1+35, x1+15, y1+35);
                   */
            	   BufferedImage img = null;
					try {
						img = ImageIO.read(new File("data/redKing.png"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.drawImage(img, x1, y1, symbolSize, symbolSize, null);
               }
               else if (board[row][col] == Seed.blackKing) 
               {
            	   /*
            	   g2d.setColor(Color.BLACK);
                   g2d.fillOval(x1, y1, symbolSize, symbolSize);
                   g2d.setColor(Color.YELLOW);
                   g2d.drawLine(x1+15, y1+35, x1+35, y1+20);
                   g2d.drawLine(x1+35, y1+20, x1+55, y1+35);
                   g2d.drawLine(x1+55, y1+35, x1+15, y1+35);
                   */
            	   BufferedImage img = null;
					try {
						img = ImageIO.read(new File("data/blackKing.png"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.drawImage(img, x1, y1, symbolSize, symbolSize, null);
               }
            }
         }
         
         // Draw the grid-lines
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