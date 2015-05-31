/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2015
 *  Jordan East and Jack Single
 *  z3462985 & z3462998
*/

/**
 * Briefly describe how your program works, including any algorithms and data structures employed, and explain any design decisions you made along the way.
 * 
 * Our Agent Makes use of multiple search modes that implement an A* Search algorithm slightly differently to find the best set of moves between two states,
 * such as finding the gold from home position, collecting the gold once found, returning to home once the gold is collected, destroying the appropriate walls with
 * dynamite or sailing the boat to find the gold on another island.
 * The A* search is set out in standard format with a priority queue, visited node set, start node and path home once goal is found, but has a variety of 
 * methods which are slightly altered to suit the job they have to perform.
 * For example, at first you do not know where the gold is, so the A* roughly performs Dijkstra's algorithm , until the gold is found, and can be recorded in the learner.
 * Once we know where the gold is we can perform a normal A* search with a goal, using the Manhattan distance algorithm to guide us toward the goal quickly.
 * We also used A* to discover if a path exists between two positions. This is useful when trying to figure out how to best use the dynamite you have picked up, as the
 * algorithm can check if it has reached its goal before it performs any in-game moves.
 * 
 * As for data structures, we had to keep track of what the agent has learned somehow, and so we though it would be best if we had a north-orientated board that could
 * remember what was in each position in the map. We used a 2D array of chars, much like the view[][] input, so we could quickly and efficiently look up any positions data.
 * The challenge was ensuring that the orientation of learner's board was consistent with the input map. This board allowed us to run A* searches with the knowledge of
 * what we had already seen, letting us easily create paths between two positions.
 * 
 * We approached this project with baby-steps; firstly getting the agent to slowly make its way around the map until it found the gold, then returning home.
 * Gradually we added more search modes and search algorithms, increasing the agents ability to navigate more difficult maps. 
 * By using different search modes we could ensure that the agent could still complete basic maps even if it struggles with the extremely complex maps.
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
   final static int BOARD_SIZE = 80;

   final static int FINDGOLD = 0;
   final static int RETURNHOME = 1;
   final static int ASTARMODE = 2;
   final static int GETGOLD = 3;
   final static int DONE = 4;
   final static int STUCK = 5;
   final static int DYNO = 6;
   
   
   private static char[][] view;

   private boolean have_axe  = false;
   private boolean have_key  = false;
   private boolean have_gold = false;
   private boolean in_boat   = false;
   private boolean just_departed_vessel = false;
   private static int num_dynamite = 0;
   private static int curX;
   private static int curY;
   
   private static int lastDirection;
   private static char nextMove;
   private static Learner learner;
   private static boolean firstTurn = true;
   private static ArrayList<Move> moves;
   private static int searchMode = 2;
   private static ArrayList<Position> pathHome1;
   private static ArrayList<Position> walls;
   private static Position boatPosition = new Position(-1,-1);
   
   //NEEDS TO ACCESS out, in IN PLACES OTHER THAN main()
   private static OutputStream out = null;
   private static InputStream in  = null;
   private static int port;

   /** Depending on which search mode agent is currently in, will use different ASTARMODE searches
    *  to determine what moves to make, using one A Star search with no goal/heuristic (becomes BFS),
    *  and another A Star search to find the best route to get there. ASTAR uses a general A star 
    *  search to explore the map.DYNO searchMode checks if it can blow up enough walls with available
    *  dynamite near gold to get to it. GETGOLD will begin another A star search using the current position
    *  as the starting location and the gold as the finishing location.Once it has found the gold, RETURNHOME
    *  mode will create a path from current position to home, using the map build up in learner to determine
    *  the best route.
    * @view is the 5x5 window that the get_action function is given to determine its next move
    * @get_action returns its move as a char L, R, F, C, B.
    */
   
   public char get_action( char view[][] ) {  	  
	   startUpdate(view);
  	  //getCurrentView();
	  
  	  //Make Move now
	   if (searchMode == ASTARMODE){
		   // Use a star search until player can see gold from starting position blindly through map
		   // then set searchMode to GETGOLD
		   Position start = new Position(curX, curY);
		   AStarAlgorithm aStar = new AStarAlgorithm();
		   pathHome1 = aStar.search(learner, start, BOARD_SIZE*BOARD_SIZE, this);
		   if (pathHome1 == null){
			   searchMode = STUCK;			 
		   } else {
			   searchMode = DYNO;
		   }
	   }
	   if (searchMode == DYNO){
		   ////System.out.println("Find Dyno");
		   if(num_dynamite==0){
			   ////System.out.println("no dynamite :(");
			   searchMode = GETGOLD;			   
		   } else {
			   Position gold = learner.getGoldLocation();
			   walls = learner.getWallsClosestToPosition(gold);
			   printPositions(walls);		   		   
			   Position start = new Position(curX, curY);
			   ArrayList<Position> path = new ArrayList<Position>();
			   AStarAlgorithm aStarFindGoldWDyno = new AStarAlgorithm();
			   while((curX != gold.getX())||(curY != gold.getY())){
				   path = aStarFindGoldWDyno.searchForPositionWDynamite(learner, start, gold, BOARD_SIZE*BOARD_SIZE, this);
				   if (path != null){
					   //System.out.println("found suitable path!!!");
					   path.add(gold);
					   printPositions(path);
					   moveAlongPath(path);	 
					   searchMode = RETURNHOME;
					   break;
				   } else {
//					   Position r = removeClosestWall();
//					   walls.add(r);
//					   //System.out.println("removed wall["+r.getX()+", "+r.getY()+"]");
				   }
			   }
			   if (searchMode!=RETURNHOME){
				   //System.out.println("Failed to find path with Dynamite");
				   searchMode = GETGOLD;
			   }
		   }
	   }
	   if (searchMode == GETGOLD) { 
		   // GETGOLD uses an a star search to travel from current position to golds position
		   // Then set searchMode to RETURNHOME
		   //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		   Position start = new Position(curX, curY);
		   Position gold = learner.getGoldLocation();
		   //System.out.println(gold.getX()+", "+gold.getY());
		   AStarAlgorithm aStarFindGold = new AStarAlgorithm();
		   ArrayList<Position> path = new ArrayList<Position>();
		   if(hasBoat()){
			   path = aStarFindGold.searchForPosition(learner, start, gold, BOARD_SIZE*BOARD_SIZE, this);
			   path.add(gold);
			   printPositions(path);
			   moveAlongPath(path);
		   }else{
			   path = aStarFindGold.searchForGold(learner, start, gold, BOARD_SIZE*BOARD_SIZE, this);
		   }
		   if (path == null){
			   searchMode = DYNO;			 
		   } else {
			   searchMode = RETURNHOME;
		   }
	   }
	   if (searchMode == RETURNHOME){  	
		   //System.out.println("return home");
		   Position start = new Position(curX, curY);
		   Position home = new Position(BOARD_SIZE/2, BOARD_SIZE/2);
		   ArrayList<Position> path = new ArrayList<Position>();
		   AStarAlgorithm aStarFindGold = new AStarAlgorithm();
		   
		   if ((getBoatPosition().getX() != -1) && (getBoatPosition().getY() != -1)) {
			   if (hasBoat() == false) {
				   //System.out.println("Finding path to the boat");
				   //System.out.println("Boat is at " + getBoatPosition().getY() + getBoatPosition().getX());
				   path = aStarFindGold.searchForPosition(learner, start, getBoatPosition(), BOARD_SIZE*BOARD_SIZE, this);
				   //ArrayList<Position> pathTwo = new ArrayList<Position>();
				   path.add(getBoatPosition());
				   //System.out.println("just made path to boat");
				   moveAlongPath(path);

				   //System.out.println("Finding path from boat to home");
				   path = aStarFindGold.searchForPosition(learner, getBoatPosition(), home, BOARD_SIZE*BOARD_SIZE, this);
				   path.add(home);
			   }
			   //System.out.println("Heading out of condition amoved boat");
		   } else {
			   //System.out.println("In the else here");
			   path = aStarFindGold.searchForPosition(learner, start, home, BOARD_SIZE*BOARD_SIZE, this);
			   path.add(home);

		   }

		   printPositions(path);
		   moveAlongPath(path);
		   //pathHome1.remove(pathHome1.size()); 
	   } 
	   if (searchMode == STUCK){
		   //System.out.println("Help Im Stuck");
		   lastDirection = (lastDirection + 1) % 4;
		   return 'l';
	   } else {		   		   
		   // FIND DA PINGUZ 
	   }
	return nullChar;
   }

   /** The startUpdate function updates the map in learner, adding new information about the map
    * to learner, and updates the current position (curX, curY) of the player in Agent.
    * 
    * @view is a 5x5 grid as a two dimensional array of what the player can currently see.
    **/
   
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
  	  //System.out.println("[X,Y]=["+curX+","+curY+"]");
  	  //System.out.println("In front of player is: " + view[1][2]);
   }

   /** updateLearner updates the learner, giving it the current view and direction so that
    * learner can determine where abouts new map information needs to be added.
    * 
    * @view is the 5x5 view of what the player can see
    * @lastDirection is the last direction that the player was facing
    **/
   
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
		  
// 	   	  if (learner.getFoundGold() == true) {
// 	   		  searchMode = GETGOLD;
// 	   	  }
   }
   
   private char getCOMove(int x, int y){
	   for(Move m:moves){
		   if ((m.getX()==x)&&(m.getY()==y)){
			   return m.getMove();
		   }	   
	   }
	   return nullChar;
   }
   

   /**
    * goToAdjacent will first determine which diretion the player is facing, by comparing it's
    * current direction to its intended direction. Then, depending if its facing in the right direction,
    * it will check if it can go forward using checkIfCanMoveFoward, or turn so that it faces the 
    * right direction.
    * 
    * @view is the 5x5 view that the player can see
    * @p is the Position of the space the player wishes to travel to
    * @goToAdjacent returns a char L, R or F 
    */

   private char goToAdjacent(char view[][], Position p) {
	   
	   char move = 'l';
	   // Test to see which direction player is headed by comparing current and goal coordinates
	   // Test to see if facing in the direction of goal coordinates, if so
	   // Move forward, otherwise turn left. Keep turning left until facing goal coordinate
	   // If empty space, move forward otherwise return nullChar, meaning theres a wall
	   
	   if (p.getX() - curX == -1) {
		   if (lastDirection == WEST) {
			  move = checkIfCanMoveFoward(view);
		   } else if (lastDirection == SOUTH) {		   	   
			   lastDirection = (lastDirection + 3) % 4;
			   move = 'r';
	   	   } else {
	   		   // Turns left by default, need to update lastDirection
		   	   lastDirection = (lastDirection + 1) % 4;
		   }
	   } else if (p.getX() - curX == 1) {
		   if (lastDirection == EAST) {
			   move = checkIfCanMoveFoward(view);
		   } else if (lastDirection == NORTH){
		   	   lastDirection = (lastDirection + 3) % 4;
			   move = 'r';
		   } else {
	   		   // Turns left by default, need to update lastDirection
		   	   lastDirection = (lastDirection + 1) % 4;
		   }
	   } else if (p.getY() - curY == -1) {
		   if (lastDirection == NORTH) {
			   move = checkIfCanMoveFoward(view);
		   } else if (lastDirection == WEST) {
		   	   lastDirection = (lastDirection + 3) % 4;
			   move = 'r';
		   } else {
	   		   // Turns left by default, need to update lastDirection
		   	   lastDirection = (lastDirection + 1) % 4;
		   }
	   } else if (p.getY() - curY == 1) {
		   if (lastDirection == SOUTH) {
			   move = checkIfCanMoveFoward(view);
		   } else if (lastDirection == EAST) {
		   	   lastDirection = (lastDirection + 3) % 4;
			   move = 'r';
	   	   } else {
	   		   // Turns left by default, need to update lastDirection
		   	   lastDirection = (lastDirection + 1) % 4;
		   }
	   } else {
	   	   lastDirection = (lastDirection + 1) % 4;
	   }
	   return move;
   }
   
   
   /**
    * checkIfCanMoveForward determines if a player can move forward. If there is a space or gold in front,
    * it will return trye, and ensure that it is no longer in a boat, and if it was turn on the just_departed_vessel
    * flag so that a boat can be dropped in the learner's map. If there is an axe, it will set the have_axe flag, 
    * as with dynamite. The Boat will turn on the in boat flag. depending on whether the player is in a boat, has an axe,
    * or dynamite, the player may sail, chop or blast respectively. Else, return nullChar (.), signifying move cant
    * be made.
    * 
    * @view is the 5x5 view the player can see.
    * @checkIfCanMoveForward returns a char F, C, B if it can move, or . if it cant.
    **/
   
   private char checkIfCanMoveFoward(char[][] view) {
	   if ((view[1][2] == ' ')||(view[1][2] == 'g')) {
		   if(in_boat==true){
			   boatPosition.set(curX, curY);
			   //System.out.println("just dropped boat at: " + curX + curY);
			   in_boat = false;
			   just_departed_vessel = true;
		   } else if (just_departed_vessel == true) {
			   just_departed_vessel=false;
			   //System.out.println(just_departed_vessel);
		   }
		   return 'f';
	   } else if (view[1][2] == 'a') {
		   in_boat = false;
		   have_axe = true;
		   return 'f';  
   	   } else if (view[1][2] == 'd') {
   		in_boat = false;
   		   num_dynamite++;
   		   return 'f';
   	   } else if (view[1][2] == 'B') {
   		   in_boat = true;
   		   //System.out.println("in boat!");
   		   return 'f';
   	   } else if ((view[1][2] == '~')&&(in_boat==true)) {		   
		   return 'f';
   	   } else if ((view[1][2] == 'T') && (have_axe == true)) { 
   		   return 'c';
   	   } else if ((view[1][2] == '*') && (num_dynamite >= 1)) { 
   		   num_dynamite--;
		   return 'b';
       } else {
		   return nullChar;
	   }
   }
   
   /**
    * print_View iterates through the current view view[][], and prints the contents of each space.
    * 
    * @view is the 5x5 view the player can see.
    */
   void print_view( char view[][] )
   {
      int i,j;

      //System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         //System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               //System.out.print('^');
            }
            else {
               //System.out.print( view[i][j] );
            }
         }
         //System.out.println("|");
      }
      //System.out.println("+-----+");
   }


   /**
    * main is the main function, which communicates with the Bounty via a socket, sending and receiving data
    * about the game being played, and calls the agent function to determine the next move.
    */
   
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
	  pathHome1 = new ArrayList<Position>();

      if( args.length < 2 ) {
         //System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         //System.out.println("herehIn" + in);
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         //System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }

      try { // scan 5-by-5 window around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     ////System.out.println("ch = "+ch);
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
            	learner = new Learner(view, agent);
            	lastDirection = NORTH;              	
            }
            
            //System.out.println("\n"+"////////////////////////////////////////////////////////////////////////");
            //System.out.println(">>>NEW MOVE<<<");
            agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
            action = agent.get_action( view );
            out.write( action );
            
         }
      }
      catch( IOException e ) {
         //System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
   
   /**
    * moveAlongPath takes in an ArrayList of Positions and moves the player along this path
    * until it reaches the destination, calling go to adjacent to move it.
    * 
    * @sequenceOfMoves is an ArrayList of consecutive Positions the player will move along.
    */
   
   public void moveAlongPath(ArrayList<Position> sequenceOfMoves) {
	   for (Position p: sequenceOfMoves) {	
		   while ((curX != p.getX())||(curY != p.getY())){
			    //System.out.println("\n"+"////////////////////////////////////////////////////////////////////////");
			    //System.out.println(">>>MAKE MOVE towards [X,Y] = ["+ p.getX() + ", " +  p.getY() + "]<<<");
			    //System.out.println(">>>Starting at [X,Y] = ["+ curX + ", " +  curY + "]<<<");
				char action = this.goToAdjacent(view, p);
				if (action == '.'){
					//System.out.println("    hit object");
				}
				Move move = new Move(curX, curY, action);
				moves.add(move);
				//System.out.println("AStar Action is:"+action);				
			    try {
					out.write( action );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    view = getCurrentView();
	            this.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION	
				startUpdate(view);
			    
			 }
		}
	   
   }
   
   /**
    * makeMove takes in an adjacent Position p, and moves the player to this position, if it can.
    * If it receives a nullChar from action, it will return false meaning it cant move to this position,
    * otherwise it will return true.
    * 
    * @p is the position the player wants to move to.
    */
	public boolean makeMove(Position p) {	
		//System.out.println("Make Move towards [X,Y] = ["+ p.getX() + ", " +  p.getY() + "]");	
		//System.out.println("Starting at [X,Y] = ["+ curX + ", " +  curY + "]");
		while ((curX != p.getX())||(curY != p.getY())){
			    //System.out.println("\n"+"////////////////////////////////////////////////////////////////////////");
			    //System.out.println(">>>MAKE MOVE towards [X,Y] = ["+ p.getX() + ", " +  p.getY() + "]<<<");
			    //System.out.println(">>>Starting at [X,Y] = ["+ curX + ", " +  curY + "]<<<");
				char action = this.goToAdjacent(view, p);
				if (action == '.'){
					//System.out.println("    hit object");
					return false;
				}
				Move move = new Move(curX, curY, action);
				moves.add(move);
				//System.out.println("AStar Action is:"+action);				
			    try {
					out.write( action );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    view = getCurrentView();
	            this.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION	
				startUpdate(view);
			    
		//	 }
		}
		return true;
	}
	
	/**
	 * getCurrentView gets the current view around the player.
	 */
	
	public char[][] getCurrentView(){
		//System.out.println("get current view");
		boolean gotView = true;
		int i,j, ch = 0;
	      char   v[][] = new char[5][5];
	      try { // scan 5-by-5 window around current location
	          while( gotView ) {
	             for( i=0; i < 5; i++ ) {
	                for( j=0; j < 5; j++ ) {
	                   if( !(( i == 2 )&&( j == 2 ))) {
	                      ////System.out.println("Reading in:");
	                	  ch = in.read();
	                      ////System.out.print("ch = "+ch+", ");
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
	             //System.out.println("got view");             
	          }
	       }
	       catch( IOException e ) {
	          //System.out.println("Lost connection to port: "+ port );
	          System.exit(-1);
	       }
	      return v;
	}
	

	/**
	 * printPositions prints all the positions in an ArrayList of Positions, so the debugger
	 * can see which path the player is trying to take.
	 * 
	 * @list is an ArrayList of Positions
	 */
	public void printPositions(ArrayList<Position> list){
		//System.out.print("Path is:");
		for (Position p: list){
			//System.out.print("["+p.getX()+", "+p.getY()+"]->");
		}
		//System.out.println();
	}
	
	//getters
	public boolean hasAxe() {
		return have_axe;		
	}

	public boolean hasBoat() {
		return in_boat;
	}
	public boolean hasDeparted() {
		return just_departed_vessel;
	}
	public int getDynamite() {
		return num_dynamite;
	}

	public Position getClosestWall() {
		//printPositions(walls);
		Random random = new Random();
		int randomNum =  random.nextInt(walls.size() - 0) + 0;
		Position w = walls.get(randomNum);
		return w;
	}
	public Position removeClosestWall() {
		return walls.remove(0);
	}
	public ArrayList<Position> getWalls(){
		return walls;
	}

	public Position getBoatPosition() {
		return boatPosition;
	}
	
}


