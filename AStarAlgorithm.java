import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
	public ArrayList<Position> search(char [][] graph, Position source, int size) {
	   	//System.out.println("Start Search");
		size = size*size;
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
            		
            		/*/print stuff
		            if (parentNode.getPath().isEmpty()){
		            	System.out.println("Expand " + prNode(parentNode.getNode()) + " ["+parentNode.getScore()+"]");
		            }else{
		            	System.out.print("Expand " + prNode(parentNode.getNode()) + " ["+parentNode.getScore()+"], parent is ");
		            	printCityPath(parentNode.getPath());
		            	System.out.println();
		            }
		            /*/        
		    nodesExpanded++;
		    //check current.path if it has visited all required nodes
            ArrayList<Position> checkPath = parentNode.getPath();
     	    if(foundGoal(checkPath, requiredNodes)){
                //found target nodes
                //System.out.println("	Found target node!");
                goal = parentNode;
                break;
                
            // else check through all adjacent states and add them to the queue    
            } else {
                for (Position adjacentCity: getAdjacent(parentNode.getNode())) {
                    //Position adjacentCity = adjacentList.next();
                    //score given by current cost to state + appropriate heuristic
                    double score = parentNode.getG() + getWeight(parentNode.getNode(), adjacentCity) + parentNode.getNode().getLayoverTime();                    
                    tempPath.clear();
                    tempPath.addAll(parentNode.getPath());
                    tempPath.add(parentNode.getNode());                    
                    //create new state
                    AStarNode next = new AStarNode(adjacentCity, tempPath, score, getHeuristic(adjacentCity,reqMap(tempPath,requiredNodes)));                              
                    //System.out.println("	" + prNode(next.getNode())+ "["+next.getScore()+"]");                    
                    // add to queue
                    queue.add(next);
                }
            }
        }        
        System.out.println(nodesExpanded +" nodes expanded");
        return goal.getPath();  
    }
   
   private double getHeuristic(Position adjacentCity,
			HashMap<Position, ArrayList<Position>> reqMap) {
		// TODO Auto-generated method stub
		return 0;
	}

private double getWeight(Position node, Position adjacentCity) {
		// TODO Auto-generated method stub
		return 0;
	}

private ArrayList<Position> getAdjacent(Position node) {
		// TODO Auto-generated method stub
		return null;
	}

/**
    * checks if path has required flight nodes
    * @param checkPath: current path qe are checking
    * @param requiredNodes: hashmap of nodes we are looking for
    * @return true or false
    */
   private boolean hasRequiredNodes(ArrayList<Position> checkPath, HashMap<Position,ArrayList<Position>> requiredNodes) {
	   //System.out.print("	checking for goal on [");
	   //printCityPath(checkPath);
	   //System.out.println("]");
	   if (checkPath.isEmpty()){
		   return false;
	   } else {
		   for(Position e: requiredNodes.keySet()){
	   			for(Position c: requiredNodes.get(e)){
				   //System.out.println("		"+prNode(e));
				   for(int i=0;i<checkPath.size();i++){
					   //System.out.println("		"+i);
					   if (i==checkPath.size()-1){
						   //none found
						   return false;
					   }
					   if (((checkPath.get(i).equals(e))&&(checkPath.get(i+1).equals(c)))){
						   //flight found
						   break;
					   }
		
				   	}
	   			}
		   }
		   return true;
	   }
   }
   
   /**
    * gives a set of nodes that the path missed
    * @param checkPath: current path qe are checking
    * @param requiredNodes: hashmap of nodes we are looking for
    * @return hashmap of missed nodes
    */
   private HashMap<Position,ArrayList<Position>> reqMap(ArrayList<Position> checkPath, HashMap<Position, ArrayList<Position>> requiredNodes) {
	   //System.out.println("check path is "+checkPath);
	   HashMap<Position,ArrayList<Position>> map = new HashMap<Position,ArrayList<Position>>();
	   //map.putAll(requiredNodes);
	   //System.out.println("add to map");
	   for(Position e1: requiredNodes.keySet()){
		   //System.out.print("          "+e1.getName()+" to:");
		   ArrayList<Position> inner = new ArrayList<Position>();
			for(Position c1: requiredNodes.get(e1)){
				//System.out.print(c1.getName()+" ");
				inner.add(c1);
			}
			//System.out.println();
			map.put(e1, inner);
		}
	   //System.out.println("map key set is " + map.keySet());
	   //System.out.print("	checking for goal on [");
	   //printCityPath(checkPath);
	   //System.out.println("]");
	   if (checkPath.isEmpty()){
		   return map;
	   } else {
		   //System.out.println("search through map");
		   for(Position e: map.keySet()){
			   //System.out.print("          "+e.getName()+" too:");
			   for(int j = 0; j<map.get(e).size();j++){
				   Position c = map.get(e).get(j);
				   //System.out.print(c.getName()+". ");
				   for(int i=0;i<checkPath.size();i++){
					   //System.out.println("		"+i);
					   if (i==checkPath.size()-1){
						   //none found
						   break;
					   }
					   if (((checkPath.get(i).equals(e))&&(checkPath.get(i+1).equals(c)))){
						   //flight found
						   //System.out.println("Found from");
						   //printMapPath(map);
						   //System.out.println();
						   //System.out.print("FLIGHT "+e.getName()+" to "+c.getName()+" Removed ");					
						   ArrayList<Position> remover = new ArrayList<Position>();
						   remover = map.get(e);
						   remover.remove(c);
						   //System.out.println(); 
						   //printMapPath(map);
						   //System.out.println();
						   break;
					   }
				   }					 
			   }
			   //System.out.println();
		   }
		   return map;
	   }
   }


 /*functions for veiwing output
private void printCityPath(ArrayList<Position> checkPath) {
	for (Position c: checkPath){
		System.out.print(prNode(c)+" ");
	}
}
private void printMapPath(HashMap<Position,ArrayList<Position>> map) {
	for(Position e: map.keySet()){
		System.out.print("          "+e.getName()+" to:");
		for(Position c: map.get(e)){
			System.out.print(c.getName()+" ");
		}
		System.out.println();
	}
}

   private String prNode(Position start){
		return start.getName();
   }*/
}