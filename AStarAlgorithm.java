import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
/**
 * AStar Algorithm
 * Implements and a star search for a path through particular 
 * vertices in a given graph, using a priority queue.
 *	
 * @author Jordan
 *@param queue: priority queue of AStarNodes, ordered according to comparator
 *@param comparator: compares the score of two given AStarNodes
 */
public class AStarAlgorithm{
	private PriorityQueue<AStarNode> queue;
	private Comparator<AStarNode> comparator;
	
	/**
	 * search through states until a state with all required nodes is found,
	 * prioritising nodes with better scores
	 * @param graph: given graph to search on
	 * @param source: starting Position
	 * @param requiredNodes: nodes required to find to break the search
	 * @param size
	 * @return: retruns the shortest path from source through all required nodes
	 */
	public ArrayList<Position> search(Learner learner, Position source, int size) {
	   	//System.out.println("Start Search");
	   	comparator = new AStarComparator<Position>();
	   	queue = new PriorityQueue<AStarNode>(size, comparator);  
	   	
   		ArrayList<Position> tempPath = new ArrayList<Position>();
   		//Start with empty state
   		AStarNode start = new AStarNode(source, tempPath, 0, 0);
   		//add to queue
   		queue.add(start);
        AStarNode goal = null;	
        //no. of nodes expanded so far
        int nodesExpanded = 0;
        
        //if queue is empty and no solution found; failure
        while(!queue.isEmpty()){
        	
        	//pop next best node off priority queue
            AStarNode parentNode = queue.poll();            
		    nodesExpanded++;		 
            
		    //check current.path if it has visited all required nodes
     	    if(foundGoal(learner)){
                //found target nodes
                //System.out.println("	Found target node!");
                goal = parentNode;
                break;
                
            // else check through all adjacent states and add them to the queue    
            } else {
                for (Position adjacent: getAdjacent(parentNode.getNode(), learner.getBoard())) {
                    //Position adjacentCity = adjacentList.next();
                    //score given by current cost to state + appropriate heuristic
                    double score = parentNode.getG() + getWeight(parentNode.getNode(), adjacent);                    
                    tempPath.clear();
                    tempPath.addAll(parentNode.getPath());
                    tempPath.add(parentNode.getNode());                    
                    //create new state
                    AStarNode next = new AStarNode(adjacent, tempPath, score, getHeuristic(adjacent));                              
                    //System.out.println("	" + prNode(next.getNode())+ "["+next.getScore()+"]");                    
                    // add to queue
                    queue.add(next);
                }
            }
        }        
        System.out.println(nodesExpanded +" nodes expanded");
        return goal.getPath();  
    }
   
   private boolean foundGoal(Learner learner) {
	   return learner.getFoundGold();
	}

   private double getHeuristic(Position adjacent) {
		// TODO Auto-generated method stub
		return 0;
	}

	private double getWeight(Position node, Position adjacentCity) {
		return 1;
	}

	private ArrayList<Position> getAdjacent(Position node, char[][] board) {		
		ArrayList<Position> adjacentList = new ArrayList<Position>();
		Position pU = new Position(node.getX()-1, node.getY());
		Position pD = new Position(node.getX()+1, node.getY());
		Position pL = new Position(node.getX(), node.getY()-1);
		Position pR = new Position(node.getX(), node.getY()+1);
		if(board[pU.getY()][pU.getX()]==' '){
			System.out.println("Added pU to A* priority list");
			adjacentList.add(pU);
		}
		if(board[pU.getY()][pD.getX()]==' '){
			System.out.println("Added pD to A* priority list");
			adjacentList.add(pU);
		}
		if(board[pR.getY()][pR.getX()]==' '){
			System.out.println("Added pR to A* priority list");
			adjacentList.add(pR);
		}
		if(board[pL.getY()][pL.getX()]==' '){
			System.out.println("Added pU to A* priority list");
			adjacentList.add(pL);
		}
		return adjacentList;
	}
}
