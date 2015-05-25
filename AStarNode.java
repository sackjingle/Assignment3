
public class AStarNode<E> {
    private E node;

    //used to construct the path after the search is done
    private AStarNode<E> cameFrom;

    // Distance from source along optimal path
    private double g;

    // Heuristic estimate of distance from the current node to the target node
    private double h;

    public AStarNode(E source, AStarNode<E> cameFrom, double g, double h){
    	this.setNode(source);
    	this.setCameFrom(cameFrom);
    	this.setG(g);
    	this.setH(h);
    }
	public double getScore() {
		return this.getG() + this.getH();
	}
	
	//getters + setters
	public E getNode() {
		return node;
	}
	public void setNode(E source) {
		this.node = (E) source;
	}
	public AStarNode<E> getCameFrom() {
		return cameFrom;
	}
	public void setCameFrom(AStarNode<E> cameFrom) {
		this.cameFrom = cameFrom;
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
