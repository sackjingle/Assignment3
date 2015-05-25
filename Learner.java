
public class Learner {
	final static int EAST   = 0;
	final static int NORTH  = 1;
	final static int WEST   = 2;
	final static int SOUTH  = 3;
	final static char nullpointer = '.';
	
	private char[][] board;
	private int curX;
	private int curY;
	private boolean leftFlag;

	public Learner(char[][] input){
		board = new char[40][40];
		for (int i = 0; i < 40; i++) {
			for (int j = 0; j < 40; j++){
				board[i][j] = nullpointer;
			}
		}
		curX = 20;
		curY = 20;
		printBoard();
		setUpMap(input);
		printBoard();
	}
	private void setUpMap(char[][] input) {
		int x = curX - 2;
		int y = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++){
				System.out.println("added "+ input[i][j]);
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
		
		int x0 = curX - 3;
		int y0 = curY - 2;
		
		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[0][i]);
			board[y0+i][x0+6] = input[0][i];
		}

		board[curY][curX] = ' ';
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
		
		board[curY][curX] = ' ';
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

		board[curY][curX] = ' ';
		curY--;
		board[curY][curX] = 'P';
		
		System.out.println("Just moved one north, printing updated board");
		printBoard();
	}
	
	private void updateSouth(char[][] input){
		int x0 = curX - 2;
		int y0 = curY - 3;	

		for (int i = 0; i < 5; i++) {
			System.out.println("added "+ input[0][i]);
			board[y0+6][x0+4-i] = input[0][i];
		}

		board[curY][curX] = ' ';
		curY++;
		board[curY][curX] = 'P';
		printBoard();
	}
	
	public void printBoard(){
		System.out.println("\n+---------------------------------------------------------------------------------------------------------------------------------------------+");
	      for(int i=0; i < 40; i++ ) {
	         System.out.print("|");
	         for(int j=0; j < 40; j++ ) {
	        	 if (board[i][j]== nullpointer){
	        		 System.out.print(".");
	        	 } else {
	        		 System.out.print(board[i][j]);
	        	 }
	         }
	         System.out.println("|");
	      }
	      System.out.println("+---------------------------------------------------------------------------------------------------------------------------------------------+");
	}
	
	public void turnedLeft() {
		leftFlag = true;
	}
	public int getX(){
		return curX;
	}
	public int getY(){
		return curY;
	}
	
}
