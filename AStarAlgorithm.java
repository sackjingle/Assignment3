import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
/**
 * AStar Algorithm
 * Implements a variety of A* searches depending on the need of the agent. It is set out in standard format with a priority queue, visited node set, start node and 
 * path home once goal is found, but has a variety of methods which are slightly altered to suit the job they have to perform.
 *
 *@param queue: priority queue of AStarNodes, ordered according to comparator
 *@param comparator: compares the score of two given AStarNodes
 */
public class AStarAlgorithm{
	private int num_dynamite;
	
	/**
	 * search uses an A Star search algorithm to search across the map to find the gold. It uses a basic A
	 * Star search algorithm to build up an ArrayList of the best nodes to explore, using another A Star
	 * search to look through the map Learner has built up and find the most efficient way to get there.
	 * The basic search has no goal, and so no heuristic making it a Breadth First Search. Depending on 
	 * whether or not the start/end positions are on land or water, will determine how the search manages
	 * to reach its destination.
	 * 
	 * @param learner - the learner that stores all information about the game so far
	 * @param source - the starting position for the search
	 * @param size - the size of the board (total number of possible locations to visit)
	 * @param agent - the state the current agent is in
	 * @return - an ArrayList of Positions for how to get to the destination.
	 */
	public ArrayList<Position> search(Learner learner, Position source, int size, Agent agent) {
	   	////System.out.println("Start Search");
		boolean foundGoal = false;
		Comparator<AStarNode> comparator = new AStarComparator<Position>();
	   	PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>(size, comparator);  
	   	
	   	ArrayList<Position> visited = new ArrayList<Position>();
	   	
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
        	visited.add(parentNode.getNode());
        	Position current = parentNode.getNode();
        	parentNode = queue.poll();
        	//System.out.print("Walk from {"+current.getX()+", "+current.getY()+"} to ");
        	//System.out.println("{"+parentNode.getNode().getX()+", "+parentNode.getNode().getY()+"}");
        	char[][] board = learner.getBoard();

        	AStarAlgorithm subSearch = new AStarAlgorithm();
        	ArrayList<Position> path = new ArrayList<Position>();
        	
        	if ((agent.hasBoat() == false) && (board[parentNode.getNode().getY()][parentNode.getNode().getX()] == ' ')) {
        		//System.out.println("Making move from ' ' t ' ', locations [" + board[current.getY()][current.getX()] + ", " + 
        		//board[parentNode.getNode().getY()][parentNode.getNode().getX()] + "].");
                path = subSearch.searchForPosition(learner, current, parentNode.getNode(), size, agent);
        	} else if ((agent.hasBoat() == true) && (board[parentNode.getNode().getY()][parentNode.getNode().getX()] == '~')){
        		//System.out.println("Making move from '~' t '~', locations [" + board[current.getY()][current.getX()] + ", " + 
                //board[parentNode.getNode().getY()][parentNode.getNode().getX()] + "].");
        		path = subSearch.searchForSeaPosition(learner, current, parentNode.getNode(), size, agent);
        	} else if ((agent.hasBoat() == false) && (board[parentNode.getNode().getY()][parentNode.getNode().getX()] == '~')) {
        		Position boatPosition = agent.getBoatPosition();
        		//System.out.println("Making move from ' ' t '~', locations [" + board[current.getY()][current.getX()] + ", " + 
                //board[parentNode.getNode().getY()][parentNode.getNode().getX()] + "], boat pos is" + boatPosition.getX() + boatPosition.getY());
        		ArrayList<Position> pathToDestination = new ArrayList<Position>();
        		path = subSearch.searchForPosition(learner, current, boatPosition, size, agent);
                pathToDestination = subSearch.searchForSeaPosition(learner, boatPosition, parentNode.getNode(), size, agent);
                path.addAll(pathToDestination);
        	} else if ((agent.hasBoat() == false) && (board[parentNode.getNode().getY()][parentNode.getNode().getX()] == 'B')) { 
        		//System.out.println("Going to the boat");
        		path = subSearch.searchForPosition(learner, current, parentNode.getNode(), size, agent);
        	} else if (board[parentNode.getNode().getY()][parentNode.getNode().getX()] == 'g') {
        		//System.out.println("Hunting for the gold");
        		path = subSearch.searchForGold(learner, source, learner.getGoldLocation(), size, agent);
        	} else {
        		//System.out.println("doing the shitty search");
        		path = subSearch.searchForPosition(learner, current, parentNode.getNode(), size, agent);
        	}
            
            path.add(parentNode.getNode());
            agent.printPositions(path);
            agent.moveAlongPath(path);
		    nodesExpanded++;		 
            
		    //check current.path if it has visited all required nodes
     	    if(foundGoal(learner)){
                //found target nodes
                //System.out.println("	Found target node!");
                foundGoal = true;
                goal = parentNode;
                break;
                
            // else check through all adjacent states and add them to the queue    
            } else {
                for (Position adjacent: getAdjacent(parentNode.getNode(), learner.getBoard(), agent)) {
                    //score given by current cost to state + appropriate heuristic
                    double score = parentNode.getG() + getWeight(parentNode.getNode(), adjacent);                    
                    tempPath.clear();
                    tempPath.addAll(parentNode.getPath());
                    tempPath.add(parentNode.getNode());                    
                    //create new state
                    double h;
                    if (queue.peek() == null){
                    	h = 0;
                    } else {
                    	h = getHeuristic(adjacent, queue.peek().getNode());
                    }
                    AStarNode next = new AStarNode(adjacent, tempPath, score, h);                              
                    ////System.out.println("	" + prNode(next.getNode())+ "["+next.getScore()+"]");                    
                    // add to queue
                    if (!visitedContains(adjacent.getX(),adjacent.getY(), visited)){
                    	//System.out.println("Added ["+adjacent.getX()+", "+adjacent.getY()+"] to queue");
                    	queue.add(next);
                    }
                }
            }
        }        
        //System.out.println(nodesExpanded +" nodes expanded");
        if(foundGoal==false){
        	//System.out.println("ASTAR FAILED");
        }
        if(goal==null){
        	return null;
        }  else {      
        	return goal.getPath(); 
        }    
	}
	

	/**
	 * searchForGold will search for the gold using an A Star search. It uses source as the starting location
	 * and gold as the final position. 
	 * 
	 * @param learner - the learner that has all information stored about the current game so far
	 * @param source - the starting position
	 * @param gold - the final position
	 * @param size - the size of the board
	 * @param agent - the agents current state
	 * @return
	 */
	public ArrayList<Position> searchForGold(Learner learner, Position source, Position gold, int size, Agent agent) {
	   	////System.out.println("Start Search");
		boolean foundGoal = false;
	   	Comparator<AStarNode> comparator = new AStarComparator<Position>();
	   	PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>(size, comparator);  
	   	
	   	ArrayList<Position> visited = new ArrayList<Position>();
	   	
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
        	visited.add(parentNode.getNode());
        	
        	Position current = parentNode.getNode();
        	parentNode = queue.poll();
        	//System.out.print("Walk from {"+current.getX()+", "+current.getY()+"} to ");
        	//System.out.println("{"+parentNode.getNode().getX()+", "+parentNode.getNode().getY()+"}");
        	
        	AStarAlgorithm subSearch = new AStarAlgorithm();
        	ArrayList<Position> path = new ArrayList<Position>();
            path = subSearch.searchForPosition(learner, current, parentNode.getNode(), size, agent);
            path.add(parentNode.getNode());
            agent.printPositions(path);
            agent.moveAlongPath(path);
		    nodesExpanded++;		 
		    nodesExpanded++;		 
            
		    //check current.path if it has visited all required nodes
     	    if((parentNode.getNode().getX() == gold.getX())&&(parentNode.getNode().getY() == gold.getY())){
                //found target nodes
                //System.out.println("	Found the GOLLLLLDD ARRR!");
                foundGoal = true;
                goal = parentNode;
                break;
                
            // else check through all adjacent states and add them to the queue    
            } else {
                for (Position adjacent: getAdjacent(parentNode.getNode(), learner.getBoard(), agent)) {
                    //score given by current cost to state + appropriate heuristic
                    double score = parentNode.getG() + getWeight(parentNode.getNode(), adjacent);                    
                    tempPath.clear();
                    tempPath.addAll(parentNode.getPath());
                    tempPath.add(parentNode.getNode());                    
                    //create new state
                    double h;
                    if (queue.peek() == null){
                    	h = 0;
                    } else {
                    	h = getHeuristic(adjacent, gold);
                    }
                    AStarNode next = new AStarNode(adjacent, tempPath, score, h);                              
                    ////System.out.println("	" + prNode(next.getNode())+ "["+next.getScore()+"]");                    
                    // add to queue
                    if (!visitedContains(adjacent.getX(),adjacent.getY(), visited)){
                    	//System.out.println("Added ["+adjacent.getX()+", "+adjacent.getY()+"] to queue");
                    	queue.add(next);
                    }
                }
            }
        }        
        //System.out.println(nodesExpanded +" nodes expanded");
        if(!foundGoal){
        	//System.out.println("Search4Gold - ASTAR FAILED");
        }
        if(goal==null){
        	return null;
        }  else {      
        	return goal.getPath(); 
        }
    }
	
	//search for known position, does not play moves
	public ArrayList<Position> searchForPosition(Learner learner, Position source, Position gold, int size, Agent agent) {
	   	////System.out.println("Start Search");
		boolean foundGoal = false;
	   	Comparator<AStarNode> comparator = new AStarComparator<Position>();
	   	PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>(size, comparator);  
	   	
	   	ArrayList<Position> visited = new ArrayList<Position>();
	   	
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
        	parentNode = queue.poll();
		    nodesExpanded++;		 
            
		    //check current.path if it has visited all required nodes
     	    if((parentNode.getNode().getX() == gold.getX())&&(parentNode.getNode().getY() == gold.getY())){
                //found target nodes
                //System.out.println("		Sub - Found the position");
                foundGoal = true;
                goal = parentNode;
                break;
                
            // else check through all adjacent states and add them to the queue    
            } else {
                for (Position adjacent: getAdjacent(parentNode.getNode(), learner.getBoard(), agent)) {
                    //score given by current cost to state + appropriate heuristic
                    double score = parentNode.getG() + getWeight(parentNode.getNode(), adjacent);  
                    ////System.out.println(score);
                    tempPath.clear();
                    tempPath.addAll(parentNode.getPath());
                    tempPath.add(parentNode.getNode());                    
                    //create new state
                    double h;
                    if (queue.peek() == null){
                    	h = 0;
                    } else {
                    	h = getHeuristic(adjacent, gold);
                    }
                    ////System.out.println(h);
                    AStarNode next = new AStarNode(adjacent, tempPath, score, h);                              
                    ////System.out.println("	" + prNode(next.getNode())+ "["+next.getScore()+"]");                    
                    // add to queue
                    if (!visitedContains(adjacent.getX(),adjacent.getY(), visited)){
                    	//System.out.println("		Sub - Added ["+adjacent.getX()+", "+adjacent.getY()+"] to queue");
                    	visited.add(adjacent);
                    	queue.add(next);
                    }
                }
            }
        }        
        //System.out.println("		Sub - "+nodesExpanded +" nodes expanded");	      
        if(!foundGoal){
        	//System.out.println("		Sub - ASTAR FAILED");
        }
        if(goal==null){
        	return null;
        }  else {      
        	return goal.getPath(); 
        }
	}
		
	//search for known position, does not play moves
	public ArrayList<Position> searchForPositionWDynamite(Learner learner, Position source, Position gold, int size, Agent agent) {
	   	////System.out.println("Start Search");
		boolean foundGoal = false;
		num_dynamite = agent.getDynamite();
	   	Comparator<AStarNode> comparator = new AStarComparator<Position>();
	   	PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>(size, comparator);  
	   	
	   	ArrayList<Position> visited = new ArrayList<Position>();
	   	
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
        	parentNode = queue.poll();
		    nodesExpanded++;		 
            
		    //check current.path if it has visited all required nodes
     	    if((parentNode.getNode().getX() == gold.getX())&&(parentNode.getNode().getY() == gold.getY())){
                //found target nodes
                //System.out.println("		Sub - Found the position");
                foundGoal = true;
                goal = parentNode;
                break;
                
            // else check through all adjacent states and add them to the queue    
            } else {
                for (Position adjacent: getAdjacentSub(parentNode.getNode(), learner.getBoard(), agent)) {
                    //score given by current cost to state + appropriate heuristic
                    double score = parentNode.getG() + getWeight(parentNode.getNode(), adjacent);  
                    ////System.out.println(score);
                    tempPath.clear();
                    tempPath.addAll(parentNode.getPath());
                    tempPath.add(parentNode.getNode());                    
                    //create new state
                    double h;
                    if (queue.peek() == null){
                    	h = 0;
                    } else {
                    	h = getHeuristic(adjacent, gold);
                    }
                    ////System.out.println(h);
                    AStarNode next = new AStarNode(adjacent, tempPath, score, h);                              
                    ////System.out.println("	" + prNode(next.getNode())+ "["+next.getScore()+"]");                    
                    // add to queue
                    if (!visitedContains(adjacent.getX(),adjacent.getY(), visited)){
                    	//System.out.println("		Sub - Added ["+adjacent.getX()+", "+adjacent.getY()+"] to queue");
                    	visited.add(adjacent);
                    	queue.add(next);
                    }
                }
            }
        }        
        //System.out.println("		Sub - "+nodesExpanded +" nodes expanded");	      
        if(!foundGoal){
        	//System.out.println("		Sub - ASTAR FAILED");
        }
        if(goal==null){
        	return null;
        }  else {      
        	return goal.getPath(); 
        }
	}
				
	


	public ArrayList<Position> searchForSeaPosition(Learner learner, Position source, Position gold, int size, Agent agent) {
	   	////System.out.println("Start Search");
		boolean foundGoal = false;
	   	Comparator<AStarNode> comparator = new AStarComparator<Position>();
	   	PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>(size, comparator);  
	   	
	   	ArrayList<Position> visited = new ArrayList<Position>();
	   	
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
        	parentNode = queue.poll();
		    nodesExpanded++;		 
            
		    //check current.path if it has visited all required nodes
     	    if((parentNode.getNode().getX() == gold.getX())&&(parentNode.getNode().getY() == gold.getY())){
                //found target nodes
                //System.out.println("		Sub - Found the position");
                //System.out.println("Breaking from finding sea");
                foundGoal = true;
                goal = parentNode;
                break;
                
            // else check through all adjacent states and add them to the queue    
            } else {
                for (Position adjacent: getAdjacentWater(parentNode.getNode(), learner.getBoard(), agent)) {
                    //score given by current cost to state + appropriate heuristic
                    double score = parentNode.getG() + getWeight(parentNode.getNode(), adjacent);  
                    ////System.out.println(score);
                    tempPath.clear();
                    tempPath.addAll(parentNode.getPath());
                    tempPath.add(parentNode.getNode());                    
                    //create new state
                    
                    double h;
                    if (queue.peek() == null){
                    	h = 0;
                    } else {
                    	h = getHeuristic(adjacent, gold);
                    }
                    ////System.out.println(h);
                    AStarNode next = new AStarNode(adjacent, tempPath, score, h);                              
                    ////System.out.println("	" + prNode(next.getNode())+ "["+next.getScore()+"]");                    
                    // add to queue
                    if (!visitedContains(adjacent.getX(),adjacent.getY(), visited)){
                    	//System.out.println("		Sub - Added ["+adjacent.getX()+", "+adjacent.getY()+"] to queue");
                    	visited.add(adjacent);
                    	queue.add(next);
                    }
                }
            }
        }       
        
        Position currentPos = new Position(learner.getY(), learner.getX());
        
        //System.out.println("		Sub - "+nodesExpanded +" nodes expanded");	      
        if(!foundGoal){
        	//System.out.println("		Sub - ASTAR FAILED");
        }
        if(goal==null){
        	return null;
        }  else {      
        	return goal.getPath(); 
        }
	}
	
	
		
	//helpers
   private boolean visitedContains(int x, int y, ArrayList<Position>visited) {
	   for (Position p: visited){
		   if ((p.getX()==x)&&(p.getY()==y)){
			   return true;
		   }
	   }
	   return false;
	}

private boolean foundGoal(Learner learner) {
	   return learner.getFoundGold();
	}

   private double getHeuristic(Position current, Position goal ) {
		int xDist = Math.abs(current.getX() - goal.getX());
		int yDist = Math.abs(current.getY() - goal.getY());
		return (xDist + yDist);	
	}

	private double getWeight(Position node, Position adjacentCity) {
		return 1;
	}
	
	
	private ArrayList<Position> getAdjacent(Position node, char[][] board, Agent agent) {		
		ArrayList<Position> adjacentList = new ArrayList<Position>();
		Position pU = new Position(node.getX(), node.getY()-1);
		Position pD = new Position(node.getX(), node.getY()+1);
		Position pL = new Position(node.getX()-1, node.getY());
		Position pR = new Position(node.getX()+1, node.getY());
		if (canWalkOver(pU, board, agent) == true) {
			////System.out.println("Added pU["+ pU.getX() + ", " +  pU.getY() + "] to A* priority list");
			adjacentList.add(pU);
		}
		if (canWalkOver(pD, board, agent) == true) {
			////System.out.println("Added pD["+ pD.getX() + ", " +  pD.getY() + "] to A* priority list");
			adjacentList.add(pD);
		}
		if (canWalkOver(pL, board, agent) == true) {
			////System.out.println("Added pR["+ pR.getX() + ", " +  pR.getY() + "] to A* priority list");
			adjacentList.add(pL);
		}
		if (canWalkOver(pR, board, agent) == true) {
			////System.out.println("Added pL["+ pL.getX() + ", " +  pL.getY() + "] to A* priority list");
			adjacentList.add(pR);
		}
		return adjacentList;
	}
	
	
	
	/**
	 * canWalkOver determines whether or not the player can walk over the adjacent position p,
	 * given its current state in agent.
	 * 
	 * @param p - destination position
	 * @param board - the entire board in learner
	 * @param agent - the agents state
	 * @return - whether or not the player can walk on position
	 */
	private boolean canWalkOver (Position p, char[][] board, Agent agent) {		
		if(board[p.getY()][p.getX()]==' '){
			return true;
		} else if (board[p.getY()][p.getX()]=='g'){
			return true;
		} else if (board[p.getY()][p.getX()]=='a') {
			return true;
		} else if (board[p.getY()][p.getX()]=='d') {
			return true;
		} else if (board[p.getY()][p.getX()]=='B') {
			return true;
		} else if ((board[p.getY()][p.getX()]=='T')&&(agent.hasAxe()==true)) {
			return true;
		} else if ((board[p.getY()][p.getX()]=='~')&&(agent.hasBoat()==true)) {
			return true;	
		} else {
			return false;
		}
	}
	
		
	/**
	 * getAdjacentSub returns an ArrayList of positions around the player,
	 * seeing if the player can get walk there on, calling the
	 * canWalkOverSub function.
	 * 
	 * @param node - the current position of the player
	 * @param board - the entire board taken from learner
	 * @param agent - the current state of the agent
	 * @return - an ArrayList of Positions that are accessible.
	 */
	private ArrayList<Position> getAdjacentSub(Position node, char[][] board, Agent agent) {		
		ArrayList<Position> adjacentList = new ArrayList<Position>();
		Position pU = new Position(node.getX(), node.getY()-1);
		Position pD = new Position(node.getX(), node.getY()+1);
		Position pL = new Position(node.getX()-1, node.getY());
		Position pR = new Position(node.getX()+1, node.getY());
		if (canWalkOverSub(pU, board, agent) == true) {
			////System.out.println("Added pU["+ pU.getX() + ", " +  pU.getY() + "] to A* priority list");
			adjacentList.add(pU);
		}
		if (canWalkOverSub(pD, board, agent) == true) {
			////System.out.println("Added pD["+ pD.getX() + ", " +  pD.getY() + "] to A* priority list");
			adjacentList.add(pD);
		}
		if (canWalkOverSub(pL, board, agent) == true) {
			////System.out.println("Added pR["+ pR.getX() + ", " +  pR.getY() + "] to A* priority list");
			adjacentList.add(pL);
		}
		if (canWalkOverSub(pR, board, agent) == true) {
			////System.out.println("Added pL["+ pL.getX() + ", " +  pL.getY() + "] to A* priority list");
			adjacentList.add(pR);
		}
		return adjacentList;
	}
	
	/**
	 * Used by the A Star subroutines, that determines if the player can walk over the adjacent Position p
	 * 
	 * @param p - goal position
	 * @param board - the whole board built up in learner
	 * @param agent - the current state of agent
	 * @return - returns whether or not can walk over
	 */
	private boolean canWalkOverSub(Position p, char[][] board, Agent agent) {
		if (num_dynamite >= 1){	
			//System.out.print(num_dynamite);
			Position wall = agent.getClosestWall();
				if ((board[p.getY()][p.getX()]=='*')&&(wall.getX()==p.getX())&&(wall.getY()==p.getY())) {
					//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> destroy da wall at ["+wall.getX()+", "+wall.getY()+"]");
					num_dynamite--;
					//agent.removeClosestWall();
					return true;
				}
		}
		if(board[p.getY()][p.getX()]==' '){
			return true;
		} else if (board[p.getY()][p.getX()]=='g'){
			return true;
		} else if (board[p.getY()][p.getX()]=='a') {
			return true;
		} else if (board[p.getY()][p.getX()]=='d') {
			return true;
		} else if (board[p.getY()][p.getX()]=='B') {
			return true;
		} else if ((board[p.getY()][p.getX()]=='T')&&(agent.hasAxe()==true)) {
			return true;
		} else if ((board[p.getY()][p.getX()]=='~')&&(agent.hasBoat()==true)) {
			return true; 
		} else {
			return false;
		}
	}
	
	
	/**
	 * getAdjacentWater returns an ArrayList of positions around the player within the water,
	 * seeing if the player can get there via the boat that it is currently on, calling the
	 * canWalkOverWater function.
	 * 
	 * @param node - the current position of the player
	 * @param board - the entire board taken from learner
	 * @param agent - the current state of the agent
	 * @return - an ArrayList of Positions that are accessible by water.
	 */
	private ArrayList<Position> getAdjacentWater(Position node, char[][] board, Agent agent) {		
		ArrayList<Position> adjacentList = new ArrayList<Position>();
		Position pU = new Position(node.getX(), node.getY()-1);
		Position pD = new Position(node.getX(), node.getY()+1);
		Position pL = new Position(node.getX()-1, node.getY());
		Position pR = new Position(node.getX()+1, node.getY());
		if (canWalkOverWater(pU, board, agent) == true) {
			////System.out.println("Added pU["+ pU.getX() + ", " +  pU.getY() + "] to A* priority list");
			adjacentList.add(pU);
		}
		if (canWalkOverWater(pD, board, agent) == true) {
			////System.out.println("Added pD["+ pD.getX() + ", " +  pD.getY() + "] to A* priority list");
			adjacentList.add(pD);
		}
		if (canWalkOverWater(pL, board, agent) == true) {
			////System.out.println("Added pR["+ pR.getX() + ", " +  pR.getY() + "] to A* priority list");
			adjacentList.add(pL);
		}
		if (canWalkOverWater(pR, board, agent) == true) {
			////System.out.println("Added pL["+ pL.getX() + ", " +  pL.getY() + "] to A* priority list");
			adjacentList.add(pR);
		}
		return adjacentList;
	}
	
	private boolean canWalkOverWater(Position p, char[][] board, Agent agent) {
		if((board[p.getY()][p.getX()]=='~')){
			return true;
		}
		return false;
	}
	
	
}