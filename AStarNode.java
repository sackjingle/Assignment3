import java.util.ArrayList;
/**
 * AStarNode is a state that represents a path through the graph
 * @author Jordan
 * 
 * @param source: current Position
 * @param path: the path taken to get to the Position
 * @param g: cost score to generate the path so far
 * @param h: extimated cost to get to the goal
 * @param nodesLeft: number of nodes left to find
 */

public class AStarNode{
    private Position node;
    private ArrayList<Position> path;
    private double g;
    private double h;

    public AStarNode(Position source, ArrayList<Position> cameFrom, double g, double h){
    	this.setNode(source);
    	this.path = new ArrayList<Position>();
    	if (cameFrom != null){
        	this.path.addAll(cameFrom);
    	}
    	this.setG(g);
    	this.setH(h);
    }
    //return score(g+h)
	public double getScore() {
		return this.getG() + this.getH();
	}
	//add new Position to path
	public void addToPath(Position parentNode){
		this.path.add(parentNode);
	}
	//getters + setters
	public Position getNode() {
		return node;
	}
	public void setNode(Position source) {
		this.node = source;
	}
	public ArrayList<Position> getPath() {
		return path;
	}
	public void setPath(ArrayList <Position> cameFrom) {
		this.path = cameFrom;
	}
	public double getG() {
		return g;
	}
	public void setG(double g) {
		this.g = g;
	}
	public double getH() {
		return h;
	}
	public void setH(double h) {
		this.h = h;
	}

}
