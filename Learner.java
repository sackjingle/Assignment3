import java.util.ArrayList;

/**
 * The Learner class keeps track of what the agent has already seen in the game, and creates a 80x80 board
 * that can be used to find paths between coordinates, as well as store important facts about the game state,
 * including where the player is currently, where the gold is (once found).
 */
public class Learner {
	final static int EAST   = 0;
	final static int NORTH  = 1;
	final static int WEST   = 2;
	final static int SOUTH  = 3;
	final static char nullpointer = '.';
	final static int BOARD_SIZE = 80;
	
	private char[][] board;
	private Agent agent;
	private int curX;
	private int curY;
	private boolean foundGold;
	public Position goldLocation = new Position (0,0);

	/**
	 * Learner constructor sets up the board as well as placing the agent in the center of the board.
	 * @param input = first view of game state
	 * @param agent = agent playing the game, to communicate facts quickly
	 */
	public Learner(char[][] input, Agent agent){
		board = new char[BOARD_SIZE][BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++){
				board[i][j] = nullpointer;
			}
		}
		this.agent = agent;
		curX = BOARD_SIZE/2;
		curY = BOARD_SIZE/2;
		foundGold = false;
		printBoard();
		setUpMap(input);
		printBoard();
	}
	/**
	 * set up the first state in the board map
	 * @param input
	 */
	private void setUpMap(char[][] input) {
		int x = curX - 2;
		int y = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++){
				board[y+i][x+j] = input[i][j];
			}
		}
		foundGold = false;
		
		//goldLocation.set(1, 1);
	}
	
	/**
	 * update method takes in a new input from the game view, and depending on which direction relative
	 * to the players original direction, updates board appropriately as to keep track of the entire visited board.
	 * @param input = current view of map
	 * @param direction = current direction facing.
	 */
	public void update(char[][] input, int direction){
		if (direction == EAST){
			//System.out.println("EAST");
			updateEast(input);
		} else if (direction == NORTH){
			//System.out.println("NORTH");
			updateNorth(input);
		} else if (direction == WEST){
			//System.out.println("WEST");
			updateWest(input);
		} else if (direction == SOUTH){
			//System.out.println("SOUTH");
			updateSouth(input);
		}	
		foundGold = checkIfGold(input, direction);
		
	}
	
	
	/**
	 * checkIfGold will use the 5x5 player view (called view in agent) and check if
	 * the gold can be seen from where if is. If so, it sets goldLocation using direction
	 * and the coordinates within input and returns true, otherwise it returns false. 
	 * @param input - the 5x5 view the player can see
	 * @param direction - the direction the player is facing
	 * @return
	 */
	private boolean checkIfGold(char[][] input, int direction) {
		for(int i = 0; i<5; i++){
			if(input[0][i]=='g'){
				//System.out.println("Arrrr I see Gold!!!");
				if (direction == EAST) {
					//System.out.println("Setting gold EAST");
					goldLocation.set(curX + 2, curY - 2 + i);
				} else if (direction == NORTH) {
					//System.out.println("Setting gold NORTH, cur [x, y, i] is : [" + curX + ", " + curY + ", " + i + "].");
					goldLocation.set(curX - 2 + i, curY - 2);
				} else if (direction == WEST) {
					//System.out.println("Setting gold WEST");
					goldLocation.set(curX - 2, curY + 2 - i);
				} else {
					//System.out.println("Setting gold SOUTH");
					goldLocation.set(curX + 2 - i, curY + 2);
				}
					//System.out.println("Arrrr I see Gold!!!");
					//System.out.println(" Set the gold location as [X,Y] = [" + goldLocation.getX() + ", " + goldLocation.getY() + "]");
					return true;
			}
		}
		return false;
	}
	
	/**
	 * updateEast will update map within learner called Board, with new information
	 * the player gains as it moves around the map.
	 * 
	 * @param input is the 5x5 view the player can see.
	 */
	private void updateEast(char[][] input){
		
		int x0 = curX - 3;
		int y0 = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			board[y0+i][x0+6] = input[0][i];
		}
		
		if (agent.hasDeparted()==true){
			board[curY][curX] = 'B';
		} else if (agent.hasBoat() == true) {
			board[curY][curX] = '~';
		} else {
			board[curY][curX] = ' ';
		}
		curX++;
		board[curY][curX] = 'P';
		printBoard();
	}
	
	
	/**
	 * updateWest will update map within learner called Board, with new information
	 * the player gains as it moves around the map.
	 * 
	 * @param input is the 5x5 view the player can see.
	 */
	private void updateWest(char[][] input){
		int x0 = curX - 3;
		int y0 = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			board[y0+4-i][x0] = input[0][i];
		}		
		
		if (agent.hasDeparted()==true){
			board[curY][curX] = 'B';
		} else if (agent.hasBoat() == true) {
			board[curY][curX] = '~';
		} else {
			board[curY][curX] = ' ';
		}		curX--;
		board[curY][curX] = 'P';
		
		printBoard();
	}
	
	/**
	 * updateNorth will update map within learner called Board, with new information
	 * the player gains as it moves around the map.
	 * 
	 * @param input is the 5x5 view the player can see.
	 */
	private void updateNorth(char[][] input){
		int x0 = curX - 2;
		int y0 = curY - 3;
		
		for (int i = 0; i < 5; i++) {
			board[y0][x0+i] = input[0][i];
		}

		if (agent.hasDeparted()==true){
			board[curY][curX] = 'B';
		} else if (agent.hasBoat() == true) {
			board[curY][curX] = '~';
		} else {
			board[curY][curX] = ' ';
		}		curY--;
		board[curY][curX] = 'P';
		
		//System.out.println("Just moved one north, printing updated board");
		printBoard();
	}
	
	/**
	 * updateSouth will update map within learner called Board, with new information
	 * the player gains as it moves around the map.
	 * 
	 * @param input is the 5x5 view the player can see.
	 */
	private void updateSouth(char[][] input){
		int x0 = curX - 2;
		int y0 = curY - 3;	

		for (int i = 0; i < 5; i++) {
			board[y0+6][x0+4-i] = input[0][i];
		}

		if (agent.hasDeparted()==true){
			board[curY][curX] = 'B';
		} else if (agent.hasBoat() == true) {
			board[curY][curX] = '~';
		} else {
			board[curY][curX] = ' ';
		}		curY++;
		board[curY][curX] = 'P';
		printBoard();
	}
	
	/**
	 * printBoard prints out the entire board that the learner has built up from what
	 * the player has seen as it moves around.
	 */
	public void printBoard(){
		//System.out.println("\n +--------------------------------------------------------------------------------+");
	      for(int i=0; i < BOARD_SIZE; i++ ) {
	    	  if (i<10){
	    		  //System.out.print(" ");
	    	  }
	         //System.out.print(i+"|");
	         for(int j=0; j < BOARD_SIZE; j++ ) {
	        	 if (board[i][j]== nullpointer){
	        		 //System.out.print(".");
	        	 } else {
	        		 //System.out.print(board[i][j]);
	        	 }
	         }
	         //System.out.println("|");
	      }
	      //System.out.println(" +--------------------------------------------------------------------------------+");
	}
	
	public int getX(){
		return curX;
	}
	public int getY(){
		return curY;
	}
	public boolean getFoundGold() {
		return foundGold;
	}
	public char[][] getBoard() {
		return board;
	}
	
	/**
	 * getWallsClosestToPosition will add the Positions of the walls closest to the goal
	 * in order of how close they are (immediately adjacent walls first, etc.) Returns the
	 * wall locations in order in an ArrayList of Positions.
	 * 
	 * @Position goal - what position you want to examine nearby walls
	 * 
	 */
	public ArrayList<Position> getWallsClosestToPosition(Position goal){
		ArrayList<Position> wallList = new ArrayList<Position>();
		int x = goal.getX();
		int y = goal.getY();
		for(int i = 1; i < 6; i++){
			for(int j = 0; j < i; j++){
				Position p = new Position(x+i-j, y+j);
				if(board[p.getY()][p.getX()]=='*'){
					wallList.add(p);
				}
				
				p = new Position(x-j, y+i-j);
				if(board[p.getY()][p.getX()]=='*'){
					wallList.add(p);
				}
				
				p = new Position(x-i+j, y-j);
				if(board[p.getY()][p.getX()]=='*'){
					wallList.add(p);
				}
				
				p = new Position(x+j, y-i+j);
				if(board[p.getY()][p.getX()]=='*'){
					wallList.add(p);
				}	
			}
		}
		return wallList;
	}
	public Position getGoldLocation() {
		if(foundGold == true){
			return goldLocation;
		} else{
			//System.out.println("gold not found yet");
		}
		return null;
	}
	
}
