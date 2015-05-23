
public class Learner {
	final static int EAST   = 0;
	final static int NORTH  = 1;
	final static int WEST   = 2;
	final static int SOUTH  = 3;
	final static char nullpointer = '.';
	
	private char[][] board;
	private int curX;
	private int curY;
	

	public Learner(char[][] input){
		board = new char[80][80];
		for (int i = 0; i < 80; i++) {
			for (int j = 0; j < 80; j++){
				board[i][j] = nullpointer;
			}
		}
		curX = 40;
		curY = 40;
		setUpMap(input);
		printBoard();
	}
	private void setUpMap(char[][] input) {
		int x = curX - 2;
		int y = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++){
				//System.out.println("added "+ input[i][j]);
				board[x+i][y+j] = input[i][j];
			}
		}
	}
	
	public void update(char[][] input, int direction){
		if (direction == EAST){
			updateEast(input);
		} else if (direction == NORTH){
			updateNorth(input);
		} else if (direction == WEST){
			updateWest(input);
		} else if (direction == SOUTH){
			updateSouth(input);
		}
	}
	
	private void updateEast(char[][] input){
		int x = curX + 3;
		int y = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[x][y + i]);
			board[x][y] = input[x][y + i];
		}
		printBoard();
	}
	
	private void updateWest(char[][] input){
		int x = curX - 3;
		int y = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[x][i]);
			board[x][y+i] = input[x][i];
		}
		printBoard();
	}
	
	private void updateNorth(char[][] input){
		int x0 = curX - 2;
		int y0 = curY - 3;
		
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[i][0]);
			board[x0 + i][y0] = input[i][0];
		}
		board[curX][curY] = ' ';
		curY++;
		board[curX][curY] = 'P';
		printBoard();
	}
	
	private void updateSouth(char[][] input){
		int x = curX - 2;
		int y = curY - 3;
		
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[x + i][y]);
			board[x][y] = input[x + i][y];
		}
		printBoard();
	}
	
	private void printBoard(){
		System.out.println("\n+---------------------------------------------------------------------------------------------------------------------------------------------+");
	      for(int i=0; i < 80; i++ ) {
	         System.out.print("|");
	         for(int j=0; j < 80; j++ ) {
	        	 if (board[i][j]== nullpointer){
	        		 System.out.print(".");
	        	 }
	             System.out.print(board[i][j]);
	         }
	         System.out.println("|");
	      }
	      System.out.println("+---------------------------------------------------------------------------------------------------------------------------------------------+");
	}
	
}
