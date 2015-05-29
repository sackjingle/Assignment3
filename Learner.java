import java.util.ArrayList;


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
	
	public void update(char[][] input, int direction){
		if (direction == EAST){
			System.out.println("EAST");
			updateEast(input);
		} else if (direction == NORTH){
			System.out.println("NORTH");
			updateNorth(input);
		} else if (direction == WEST){
			System.out.println("WEST");
			updateWest(input);
		} else if (direction == SOUTH){
			System.out.println("SOUTH");
			updateSouth(input);
		}	
		foundGold = checkIfGold(input, direction);
		
	}
	
	private boolean checkIfGold(char[][] input, int direction) {
		for(int i = 0; i<5; i++){
			if(input[0][i]=='g'){
				System.out.println("Arrrr I see Gold!!!");
				if (direction == EAST) {
					System.out.println("Setting gold EAST");
					goldLocation.set(curX + 2, curY - 2 + i);
				} else if (direction == NORTH) {
					System.out.println("Setting gold NORTH, cur [x, y, i] is : [" + curX + ", " + curY + ", " + i + "].");
					goldLocation.set(curX - 2 + i, curY - 2);
				} else if (direction == WEST) {
					System.out.println("Setting gold WEST");
					goldLocation.set(curX - 2, curY + 2 - i);
				} else {
					System.out.println("Setting gold SOUTH");
					goldLocation.set(curX + 2 - i, curY + 2);
				}
					System.out.println("Arrrr I see Gold!!!");
					System.out.println(" Set the gold location as [X,Y] = [" + goldLocation.getX() + ", " + goldLocation.getY() + "]");
					return true;
			}
		}
		return false;
	}
	
	private void updateEast(char[][] input){
		
		int x0 = curX - 3;
		int y0 = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			board[y0+i][x0+6] = input[0][i];
		}
		
		if (agent.hasDeparted()==true){
			board[curY][curX] = 'B';
		} else {
			board[curY][curX] = ' ';
		}
		curX++;
		board[curY][curX] = 'P';
		printBoard();
	}
	
	private void updateWest(char[][] input){
		int x0 = curX - 3;
		int y0 = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			board[y0+4-i][x0] = input[0][i];
		}		
		
		if (agent.hasDeparted()==true){
			board[curY][curX] = 'B';
		} else {
			board[curY][curX] = ' ';
		}		curX--;
		board[curY][curX] = 'P';
		
		printBoard();
	}
	
	private void updateNorth(char[][] input){
		int x0 = curX - 2;
		int y0 = curY - 3;
		
		for (int i = 0; i < 5; i++) {
			board[y0][x0+i] = input[0][i];
		}

		if (agent.hasDeparted()==true){
			board[curY][curX] = 'B';
		} else {
			board[curY][curX] = ' ';
		}		curY--;
		board[curY][curX] = 'P';
		
		System.out.println("Just moved one north, printing updated board");
		printBoard();
	}
	
	private void updateSouth(char[][] input){
		int x0 = curX - 2;
		int y0 = curY - 3;	

		for (int i = 0; i < 5; i++) {
			board[y0+6][x0+4-i] = input[0][i];
		}

		if (agent.hasDeparted()==true){
			board[curY][curX] = 'B';
		} else {
			board[curY][curX] = ' ';
		}		curY++;
		board[curY][curX] = 'P';
		printBoard();
	}
	
	public void printBoard(){
		System.out.println("\n+--------------------------------------------------------------------------------+");
	      for(int i=0; i < BOARD_SIZE; i++ ) {
	         System.out.print("|");
	         for(int j=0; j < BOARD_SIZE; j++ ) {
	        	 if (board[i][j]== nullpointer){
	        		 System.out.print(".");
	        	 } else {
	        		 System.out.print(board[i][j]);
	        	 }
	         }
	         System.out.println("|");
	      }
	      System.out.println("+--------------------------------------------------------------------------------+");
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
	public ArrayList<Position> getWallsClosestToGold(){
		// fucked if this will work
		ArrayList<Position> wallList = new ArrayList<Position>();
		int x = goldLocation.getX();
		int y = goldLocation.getY();
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				x = x+i;
				y = y+j;
				Position p = new Position(x, y);
				wallList.add(p);
				
			}
		}
		return wallList;
	}
	
}
