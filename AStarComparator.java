import java.util.Comparator;

/**
 * Compares the scores of two states or AStarNodes.
 * @author Jordan
 *
 * @param <E> = city
 */
public class AStarComparator<E> implements Comparator<AStarNode>{

	public int compare(AStarNode o1, AStarNode o2) {
		if(o1.getScore() < o2.getScore()){
            return -1;
        }else if(o1.getScore() > o2.getScore()){
            return 1;
        }else{
            return 0;
        }
	}

}
