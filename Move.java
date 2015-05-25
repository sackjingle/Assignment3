
public class Move {
	private int x;
	private int y;
	private char move;
	
	public Move(int x,int y,char move){
		this.x = x;
		this.y = y;
		this.move = move;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public char getMove(){
		return move;
	}
}
