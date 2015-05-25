import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Stack;

public class AStarAlgorithm<E>{
	
   public ArrayList<E> search(Graph<E> graph, E source, E target, int size) {
	   System.out.println("Start Search from " + source + " to " + target);
	   Comparator<AStarNode<E>> comparator = new AStarComparator<E>();
	   
	   // The set of nodes already evaluated. 
	   HashMap<E, AStarNode<E>> openSet = new HashMap<E, AStarNode<E>>();
	   // The set of tentative nodes to be evaluated, initially containing the start node 
	   PriorityQueue<AStarNode<E>> queue = new PriorityQueue<AStarNode<E>>(size, comparator);
	   // The map of navigated nodes.
	   HashMap<E, AStarNode<E>> closeSet = new HashMap<E, AStarNode<E>>();
       
	   AStarNode<E> start = new AStarNode<E>(source, null, 0, graph.getHeuristic(source, target));
       openSet.put(source, start);
       queue.add(start);

        AStarNode<E> goal = null;	
        while(!openSet.isEmpty()){
        	//pop off priority queue
            AStarNode<E> parentNode = queue.poll();
            openSet.remove(parentNode.getNode());
            if (parentNode.getCameFrom()==null){
            	System.out.println("Expand " + parentNode.getNode() + " ["+parentNode.getScore()+"]");
            }else{
            	System.out.println("Expand " + parentNode.getNode() + " ["+parentNode.getScore()+"], parent is "+parentNode.getCameFrom().getNode());
            }
     	    if(parentNode.getNode().equals(target)){
                //found target node
                System.out.println("	Found target node " + parentNode.getNode());
                goal = parentNode;
                break;
            } else {
            	//System.out.println("	Search for node:");
                closeSet.put(parentNode.getNode(), parentNode);
                for (ListIterator<E> adjacentList = graph.getAdjacent(parentNode.getNode()); adjacentList.hasNext();) {
                    E adjacentE = adjacentList.next();
                	AStarNode<E> visited = closeSet.get(adjacentE);
                    if (visited == null) {
                        double tentative_g_score = parentNode.getG() + graph.getWeight(parentNode.getNode(), adjacentE);
                        //System.out.println("		tentative score =" + tentative_g_score+" = "+ parentNode.getG()+" + "+graph.getWeight(parentNode.getNode(), adjacentE)+" (parent+weight)");
                        AStarNode<E> next = openSet.get(adjacentE);
                        if (next == null) {
                            //not in the open set
                            next = new AStarNode<E>(adjacentE, parentNode, tentative_g_score, graph.getHeuristic(adjacentE, target));
                            next.setCameFrom(parentNode);
                            System.out.println("	" + next.getNode()+ "["+next.getScore()+"]");
                            openSet.put(adjacentE, next);
                            queue.add(next);
                        } else if (tentative_g_score < next.getG()) {
                            next.setCameFrom(parentNode);
                            next.setG(tentative_g_score);
                            next.setH(graph.getHeuristic(adjacentE, target));
                            System.out.println("	next is now " + next.getNode() + " parent node = " + next.getCameFrom().getNode() + " with g()=" + next.getG() + " with h()=" + next.getH());
                        } else {
                        	System.out.println("	here");
                        }
                    }
                }
            }
        }

     
		
        if(goal != null){
        	double cost = goal.getG();
            ArrayList<E> path = new ArrayList<E>();
            path.add(goal.getNode());
            AStarNode<E> parent = goal.getCameFrom();
            while(parent != null){
            	//cost = cost + parent.getG();
            	path.add(parent.getNode());
            	parent = parent.getCameFrom();
            }
            Collections.reverse(path);
            System.out.println("path: " + path);
            System.out.println("Cost: "+ cost);
            return path;      
        }
        
        return null;  
      
    }

}