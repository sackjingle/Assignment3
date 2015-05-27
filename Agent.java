/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2015
 *  Jordan East and Jack Single
*/

import java.util.*;
import java.io.*;
import java.net.*;




public class Agent {
	
   final static int EAST   = 0;
   final static int NORTH  = 1;
   final static int WEST   = 2;
   final static int SOUTH  = 3;
   final static char nullChar = '.';

   final static int FINDGOLD = 0;
   final static int RETURNHOME = 1;
   final static int ASTARMODE = 2;
   
   
   private static char[][] view;

   private boolean have_axe  = false;
   private boolean have_key  = false;
   private boolean have_gold = false;
   private boolean in_boat   = false;
   private static int curX;
   private static int curY;
   
   private static int lastDirection;
   private static char nextMove;
   private static Learner learner;
   private static boolean firstTurn = true;
   private static ArrayList<Move> moves;
   private static int searchMode = 0;
   
   //NEEDS TO ACCESS out, in IN PLACES OTHER THAN main()
   private static OutputStream out = null;
   private static InputStream in  = null;
   private static int port;

   public char get_action( char view[][] ) {  	  
	   startUpdate(view);
  	  //getCurrentView();
  	  //Make Move now
	   if (searchMode == FINDGOLD) {
	   	  
	   	  if (view[1][2] == ' ') {
	   		  nextMove = 'f';
	   	   	  System.out.println("Move is: "+nextMove);
	   	  } else if (getCOMove(curX,curY)=='l') {
	   		  nextMove = 'r';
	   	   	  System.out.println("Move is: "+nextMove);
	   		  //updateLearner(view, lastDirection);
	   		  lastDirection = (lastDirection + 3) % 4;
	   	  } else {
	   	  	  nextMove = 'l';
	   	   	  System.out.println("Move is: "+nextMove);
	   		  //updateLearner(view, lastDirection);

	   	  	  //learner.printBoard();
	   	  	  lastDirection = (lastDirection + 1) % 4;
	   	  } 
	   	  
	   	  Move move = new Move(curX, curY, nextMove);
	   	  moves.add(move);
	   	  return nextMove;
	   	  
	   } else if (searchMode == ASTARMODE){
		   Position start = new Position(curX, curY);
		   AStarAlgorithm aStar = new AStarAlgorithm();
		   aStar.search(learner, start, 40*40, this);
		   searchMode = RETURNHOME;
	   } else {
	   
		   // FIND DA PINGUZ 
	   }
	return nullChar;
   }
   
   //need to update stuff before making a move
   static void startUpdate(char[][] view){
	 //update learner if last move was an 'f'
	   if(moves.size()!=0){
  			if (moves.get(moves.size()-1).getMove() == 'f') {
  				updateLearner(view, lastDirection);
  			}
	   }
	  // update curX, Y
	  curX = learner.getX();
  	  curY = learner.getY();
  	  System.out.println("[X,Y]=["+curX+","+curY+"]");
  	  System.out.println("In front of player is: " + view[1][2]);
   }

   static void updateLearner(char view[][], int lastDirection) {
		  if (lastDirection == NORTH) {
     	   	  learner.update(view, NORTH);
     	  } else if (lastDirection == WEST) {
     	   	  learner.update(view, WEST);
     	  } else if (lastDirection == SOUTH) {
     		  learner.update(view, SOUTH);
     	  } else {
     		  learner.update(view, EAST);
     	  }
   }
   
   private char getCOMove(int x, int y){
	   for(Move m:moves){
		   if ((m.getX()==x)&&(m.getY()==y)){
			   return m.getMove();
		   }	   
	   }
	   return nullChar;
   }
   
   private char goToAdjacent(char view[][], Position p) {
	   
	   // Test to see which direction player is headed by comparing current and goal coordinates
	   // Test to see if facing in the direction of goal coordinates, if so
	   // Move forward, otherwise turn left. Keep turning left until facing goal coordinate
	   // If empty space, move forward otherwise return nullChar, meaning theres a wall
	   
	   if (p.getX() - curX == 1) {
		   if (lastDirection == EAST) {
			   if (view[1][2] == ' ') {
				   return 'f';
			   } else {
				   return nullChar;
			   }
		   } else {
				   return 'l';
		   }
	   } else if (p.getX() - curX == -1) {
			   if (lastDirection == WEST) {
			 		if (view[1][2] == ' ') {
			 			return 'f';
			 		} else {
			 			return nullChar;
			 		}
			   } else {
				   return 'l';
			   }
	   } else if (p.getY() - curY == 1) {
		   if (lastDirection == SOUTH) {
			   if (view[1][2] == ' ') {
				  return 'f'; 
			   } else {
				   return nullChar;
			   }
		   } else {
			   return 'l';
		   }
	   } else if (p.getY() - curY == -1) {
		   if (lastDirection == NORTH) {
			   if (view[1][2] == ' ') {
				   return 'f';
			   } else {
				   return nullChar;
			   }
		   } else {
			   return 'l';
		   }
	   } else {
		   System.out.println("Turn Left");
		   return 'l';
	   }
   }
   
   void print_view( char view[][] )
   {
      int i,j;

      System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               System.out.print('^');
            }
            else {
               System.out.print( view[i][j] );
            }
         }
         System.out.println("|");
      }
      System.out.println("+-----+");
   }
   
   public ArrayList<Position> breadthFirstSearch(Position start, Position goal){
		System.out.println("bfs from "+start+" to "+ goal);
		HashMap<Position,Position> connectedTo = new HashMap<Position,Position>();
		ArrayList<Position> visited = new ArrayList<Position>();
		Queue<Position> queue = new ArrayDeque<Position>();
		queue.add(start);
		visited.add(start);
		while (!queue.isEmpty()){
			Position parentVertex = queue.remove();
			System.out.println(parentVertex);
			for (ListIterator<Position> adjacent = getAdjacent(parentVertex); adjacent.hasNext();){
				Position child = adjacent.next();
				System.out.print("	"+child);
				if(!visited.contains(child))
				{
					System.out.print(" - added");
                   connectedTo.put(child, parentVertex);
                   visited.add(child); 	
					queue.add(child);
				}
				System.out.println();
			}
		}
		ArrayList<Position> path = new ArrayList<Position>();
		path.add(goal);
		while (connectedTo.get(goal) != start){
			goal = connectedTo.get(goal);
			path.add(goal);
		}
		path.add(start);
		Collections.reverse(path);

		return path;
	}
   
   
   private ListIterator<Position> getAdjacent(Position parentVertex) {
	// TODO Auto-generated method stub
	return null;
}

//Main
   public static void main( String[] args )
   {
      //InputStream in  = null;
      //OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      view = new char[5][5];
      char   action   = 'F';
      int ch;
      int i,j;
      moves = new ArrayList<Move>();

      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         System.out.println("herehIn" + in);
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }

      try { // scan 5-by-5 window around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     //System.out.println("ch = "+ch);
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     view[i][j] = (char) ch;
                  } else if (( i == 2 )&&( j == 2 )){
                      view[i][j] = 'P';
                  }
               }
            }
            if (firstTurn == true){
            	firstTurn = false;
            	learner = new Learner(view);
            	lastDirection = NORTH;              	
            }
            
            System.out.println("\n"+"////////////////////////////////////////////////////////////////////////");
            System.out.println(">>>NEW MOVE<<<");
            agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
            action = agent.get_action( view );
            out.write( action );
            
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }

	public void makeMove(Position p) {	
		System.out.println("Make Move towards [X,Y] = ["+ p.getX() + ", " +  p.getY() + "]");	
		System.out.println("Starting at [X,Y] = ["+ curX + ", " +  curY + "]");
		while ((curX != p.getX())||(curY != p.getY())){
			    System.out.println("\n"+"////////////////////////////////////////////////////////////////////////");
			    System.out.println(">>>MAKE MOVE towards [X,Y] = ["+ p.getX() + ", " +  p.getY() + "]<<<");
			    System.out.println(">>>Starting at [X,Y] = ["+ curX + ", " +  curY + "]<<<");
				char action = this.goToAdjacent(view, p);
				Move move = new Move(curX, curY, action);
				moves.add(move);
				startUpdate(view);
				System.out.println("AStar Action is:"+action);
			    try {
					out.write( action );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    view = getCurrentView();
	            this.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION	
		//	 }
		}
	}
	public char[][] getCurrentView(){
		System.out.println("get current view");
		boolean gotView = true;
		int i,j, ch = 0;
	      char   v[][] = new char[5][5];
	      try { // scan 5-by-5 window around current location
	          while( gotView ) {
	             for( i=0; i < 5; i++ ) {
	                for( j=0; j < 5; j++ ) {
	                   if( !(( i == 2 )&&( j == 2 ))) {
	                      //System.out.println("Reading in:");
	                	  ch = in.read();
	                      //System.out.print("ch = "+ch+", ");
	                      if( ch == -1 ) {
	                         System.exit(-1);
	                      }
	                      v[i][j] = (char) ch;
	                   } else if (( i == 2 )&&( j == 2 )){
	                       v[i][j] = 'P';
	                   }
	                }
	             }	           
	             gotView = false;
	             System.out.println("got view");             
	          }
	       }
	       catch( IOException e ) {
	          System.out.println("Lost connection to port: "+ port );
	          System.exit(-1);
	       }
	      return v;
	}
}


