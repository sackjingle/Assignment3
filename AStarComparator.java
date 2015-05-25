import java.util.Comparator;


public class AStarComparator<E> implements Comparator<AStarNode<E>>{

	public int compare(AStarNode<E> o1, AStarNode<E> o2) {
		if((int)o1.getNode().toString().charAt(0) < (int)o2.getNode().toString().charAt(0)){
            return -1;
        }else if((int)o1.getNode().toString().charAt(0) > (int)o2.getNode().toString().charAt(0)){
            return 1;
        }else{
            return 0;
        }
		
	}

}
