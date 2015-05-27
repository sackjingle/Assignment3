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
	public ArrayList<Position> search(Learner learner, Position source, int size, Agent agent) {
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
        AStarNode parentNode = start;
        //if queue is empty and no solution found; failure
        while(!queue.isEmpty()){
        	
        	//make the sad walk home
        	for(int j = parentNode.getPath().size() - 1; j >= 0; j--){
                Position temp = parentNode.getPath().get(j);
                System.out.println("["+temp.getX()+", "+temp.getY()+"]");
            	agent.makeMove(temp);
            }
        	//pop next best node off priority queue
            parentNode = queue.poll();
            
            // walk through path to popped off position           
            for(int j = 0; j < parentNode.getPath().size(); j++){
                Position temp = parentNode.getPath().get(j);
            	agent.makeMove(temp);
            }
		    nodesExpanded++;		 
            
		    //check current.path if it has visited all required nodes
     	    if(foundGoal(learner)){
                //found target nodes
                System.out.println("	Found target node!");
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
		Position pU = new Position(node.getX(), node.getY()-1);
		Position pD = new Position(node.getX(), node.getY()+1);
		Position pL = new Position(node.getX()-1, node.getY());
		Position pR = new Position(node.getX()+1, node.getY());
		if(board[pU.getY()][pU.getX()]==' '){
			System.out.println("Added pU["+ pU.getX() + ", " +  pU.getY() + "] to A* priority list");
			adjacentList.add(pU);
		}
		if(board[pD.getY()][pD.getX()]==' '){
			System.out.println("Added pD["+ pD.getX() + ", " +  pD.getY() + "] to A* priority list");
			adjacentList.add(pD);
		}
		if(board[pR.getY()][pR.getX()]==' '){
			System.out.println("Added pR["+ pR.getX() + ", " +  pR.getY() + "] to A* priority list");
			adjacentList.add(pR);
		}
		if(board[pL.getY()][pL.getX()]==' '){
			System.out.println("Added pL["+ pL.getX() + ", " +  pL.getY() + "] to A* priority list");
			adjacentList.add(pL);
		}
		return adjacentList;
	}
}