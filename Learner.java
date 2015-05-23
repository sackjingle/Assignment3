
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
				board[y+i][x+j] = input[i][j];
			}
		}
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
	}
//	private void updateBoard(char[][] input){
//
//		System.out.println("player is : '"+board[curY][curX]+"'");
//		System.out.println("behind player is : '"+board[curY+1][curX]+"'");
//		int x0 = curX - 2;
//		int y0 = curY - 2;
//		
//		for (int i = 0; i < 5; i++) {
//			for(int j = 0; j< 5; j++){
//				System.out.println("added "+ input[0][i]);
//				board[y0+i][x0+j] = input[i][j];
//			}
//		}
//		board[curY][curX] = 'P';
//		printBoard();
//	}
	
	private void updateEast(char[][] input){
		int x0 = curX - 1;
		int y0 = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[0][i]);
			board[y0+i][x0+4] = input[0][i];
		}
		board[curY][curX] = 'p';
		curX++;
		board[curY][curX] = 'P';
		printBoard();
	}
	
	private void updateWest(char[][] input){
		int x0 = curX - 3;
		int y0 = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[0][i]);
			board[y0+4-i][x0] = input[0][i];

		}
		board[curY][curX] = 'p';
		curX--;
		board[curY][curX] = 'P';
		printBoard();
	}
	
	private void updateNorth(char[][] input){

		System.out.println("player is : '"+board[curY][curX]+"'");
		System.out.println("behind player is : '"+board[curY+1][curX]+"'");
		int x0 = curX - 2;
		int y0 = curY - 3;
		
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[0][i]);
			board[y0][x0+i] = input[0][i];
		}

		board[curY][curX] = 'p';
		curY--;
		board[curY][curX] = 'P';

		printBoard();
	}
	
	private void updateSouth(char[][] input){
		int x0 = curX - 2;
		int y0 = curY - 1;	
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[0][i]);
			board[y0+4][x0+4-i] = input[0][i];
		}
		board[curY][curX] = 'p';
		curY++;
		board[curY][curX] = 'P';
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
