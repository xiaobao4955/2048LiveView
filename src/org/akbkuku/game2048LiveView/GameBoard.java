package org.akbkuku.game2048LiveView;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sonyericsson.extras.liveview.plugins.LiveViewAdapter;
import com.sonyericsson.extras.liveview.plugins.PluginConstants;

public class GameBoard {
	
	// Ints for direction to be used with a switch statement
	public static final int
		UP=0,
		DOWN=1,
		LEFT=2,
		RIGHT=3;
		
	// Pixel location on the display to print the game pieces
	public static int gridPX[][][] = {
		{{5,5},{35,5},{65,5},{95,5}},
		{{5,35},{35,35},{65,35},{95,35}},
		{{5,65},{35,65},{65,65},{95,65}},
		{{5,95},{35,95},{65,95},{95,95}}
		};

	// Array to store values of pieces at locations on the board
	protected int boardValues[][] = {
			{0,0,256,0},
			{2048,0,2,0},
			{0,0,2,4},
			{0,0,32,0}
			};  
	
	// Arraylist to keep a quick array of empty locations on the board to make adding new pieces easier
	ArrayList<int[]> empties = new ArrayList<int[]>();
	
	// Current Running Info
	LiveViewAdapter mLiveViewAdapter;
	int mPluginId;
	Context context;
	
	// Bitmaps
	Bitmap background;

	/**
	 * GameBoard
	 * 
	 * An implementation of the game 2048 for the Sony LiveView
	 * 
	 * @param mLiveViewAdapter
	 * @param mPluginId
	 * @param context
	 */
	public GameBoard(LiveViewAdapter mLiveViewAdapter, int mPluginId, Context context)
	{
		this.mLiveViewAdapter = mLiveViewAdapter;
		this.mPluginId = mPluginId;
		this.context = context;
		
		newGame();

	    // Get bitmaps
		background = BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.background));
	}
	
	/**
	 * drawBoard
	 * 
	 * Draws current pieces to the LiveView's screen
	 */
	public void drawBoard()
	{
		// Print Background
        mLiveViewAdapter.sendImageAsBitmap(mPluginId, 0, 0, background);
		
        // Go through array and print a pieces with a non-zero value
        for (int x=0;x < 4;x++)
		{
			for (int y=0;y < 4;y++)
			{
				if (boardValues[y][x] != 0)
				{
					// Gets value and prints the image for it at the location stored in the pixel location array
                    mLiveViewAdapter.sendImageAsBitmap(mPluginId, GameBoard.gridPX[y][x][0],GameBoard.gridPX[y][x][1], getBitmap(boardValues[y][x]));
					
				}
			}
		}
	}
	
	/**
	 * newGame
	 * 
	 * Callsto reset the board and add two random pieces
	 */
	public void newGame()
	{
	    Log.d(PluginConstants.LOG_TAG_GAME, "--New Game--");
		reset();
		addPiece();
		addPiece();
	}
	
	/**
	 * reset
	 * 
	 * Clears board
	 */
	public void reset()
	{
	    Log.d(PluginConstants.LOG_TAG_GAME, "Clearing Board");		
		// Set all spaces to empty
		for (int x=0;x < 4;x++)
		{
			for (int y=0;y < 4;y++)
			{
				boardValues[y][x] = 0;
			}
			
		}
	    getEmpties();
		
	}
	
	/**
	 * getEmpties
	 * 
	 * Runs through values array and finds all empty spaces
	 */
	private void getEmpties()
	{

	    Log.d(PluginConstants.LOG_TAG_GAME, "Getting Empty Spaces");		
		empties.clear();
		for (int x=0;x < 4;x++)
		{
			for (int y=0;y < 4;y++)
			{
				if (boardValues[y][x] == 0)
				{
					// Add empty location coordinates
					int[] loc = {y,x};
					empties.add(loc);
				}
			}
			
		}
	}
	
	/**
	 * addPiece
	 * 
	 * Adds a random piece, either 2 or 4, to the board in a random location
	 * 
	 * @return boolean of whether or not a piece was placed.
	 */
	private boolean addPiece()
	{
	    Log.d(PluginConstants.LOG_TAG_GAME, "Adding new piece");

		// Check for empty spaces to 
	    getEmpties();
		if ( empties.size() == 0 )
		{
		    Log.d(PluginConstants.LOG_TAG_GAME, "Out of empty Spaces");
			return false;
		}
		
		
	    Random rand = new Random();
	    int emptyToFill,
	    	chance,
	    	value;
	    
	    // Get an empty location
		emptyToFill = rand.nextInt(empties.size());
		
		// Determine whether it will be a 2 or 4 with a 10% chance of being a 4
		chance = rand.nextInt(11);
		if (chance > 8)
		{
			value = 4;
		}
		else
		{
			value = 2;
		}
		
		// Set the new value to the location
		boardValues[empties.get(emptyToFill)[0]][empties.get(emptyToFill)[1]] = value;
		empties.remove(emptyToFill);
	    Log.d(PluginConstants.LOG_TAG_GAME, "New piece added");
		
		return true;
	}
	
	/**
	 * slide
	 * 
	 * Slides all tiles in a given direction and adds a new piece
	 * 
	 * @param direction use GameBoard.UP,GameBoard.DOWN,GameBoard.LEFT,GameBoard.RIGHT
	 */
	public void slide(int direction)
	{

	    int tempBoard[][] = (int[][])boardValues.clone();
		switch (direction)
		{
			case GameBoard.UP:
			    Log.d(PluginConstants.LOG_TAG_GAME, "Slide Up");		
				moveAll(0,-1);
				break;
			case GameBoard.DOWN:
			    Log.d(PluginConstants.LOG_TAG_GAME, "Slide Down");	
				moveAll(0,1);
				break;
			case GameBoard.LEFT:
			    Log.d(PluginConstants.LOG_TAG_GAME, "Slide Left");	
				moveAll(-1,0);
				break;
			case GameBoard.RIGHT:
			    Log.d(PluginConstants.LOG_TAG_GAME, "Slide Right");	
				moveAll(1,0);
				break;
		}
		//TODO - Need to test that a move was made before adding
		boolean moveMade = true;

		for (int x=0;x < 4;x++)
		{
			for (int y=0;y < 4;y++)
			{
				if (boardValues[y][x] == 0)
				{
					if (tempBoard[y][x] != boardValues[y][x])
					{
						moveMade = false;
					}
				}
			}
		}
		if (moveMade)
		{
		    Log.d(PluginConstants.LOG_TAG_GAME, "A Move was made");	
			addPiece();
		}
	}
	
	/**
	 * moveAll
	 * 
	 * Applies the direction indicated by the X and Y change supplied
	 * 
	 * @param xM Amount to shift horizontally
	 * @param yM Amount to shift vertically
	 */
	private void moveAll(int xM, int yM)
	{
		for (int x=0;x < 4;x++)
		{
			for (int y=0;y < 4;y++)
			{
				// Check if empty
				if(boardValues[y][x] != 0)
				{
					// Save pieces current location, is modified to move more than one space
					int tx = x,
						ty=y;
					
					// Loop until the pieces path is blocked
					boolean stillMoving = true;
					while(stillMoving)
					{
						// Test if the piece is at the edge of the board
						if ((tx+xM>-1 && tx+xM<4) && (ty+yM>-1 && ty+yM<4))
						{
							// Test if the next location is empty
							if( boardValues[ty+yM][tx+xM] == 0  )
							{
								// Move value to next location
								boardValues[ty+yM][tx+xM] = boardValues[ty][tx];
								boardValues[ty][tx] = 0;
								tx=tx+xM;
								ty=ty+yM;
							}
							
							// If the next location it not empty, test if it's the same value
							else if( boardValues[ty+yM][tx+xM] == boardValues[ty][tx]  )
							{
								// Merge value with next location
							    Log.d(PluginConstants.LOG_TAG_GAME, "Merging ("+(ty)+","+(tx)+") with ("+(ty+yM)+","+(tx+xM)+") to create a: "+boardValues[ty][tx]*2);	
								boardValues[ty+yM][tx+xM] = boardValues[ty][tx]*2;
								boardValues[ty][tx] = 0;
								tx=tx+xM;
								ty=ty+yM;
								
							}
							
							// Stop Moving
							else
							{
								stillMoving = false;
							}
						}
						// Never Move
						else
						{
							stillMoving = false;
						}
					}
				}
			}
			
		}
		
	}
	
	/**
	 * getBitmap
	 * 
	 * Returns the piece bitmap associated with a stored value
	 * 
	 * @param value Value of piece to display
	 * @return Bitmap of piece for given value
	 */
	private Bitmap getBitmap(int value)
	{
		switch(value)
		{
			case 2:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t2));
		
			case 4:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t4));
		
			case 8:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t8));
		
			case 16:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t16));
		
			case 32:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t32));
		
			case 64:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t64));
		
			case 128:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t128));
		
			case 256:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t256));
		
			case 512:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t512));
		
			case 1024:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t1024));
		
			case 2048:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t2048));
		
			case 4096:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t4096));
		
			case 8192:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t8192));
		
			case 16384:
				return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.t16384));
		}
		
		// Return background if used wrong
		return background;
	}
}
