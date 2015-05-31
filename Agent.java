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
		   System.out.println("Find Dyno");
		   if(num_dynamite==0){
			   System.out.println("no dynamite :(");
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
					   System.out.println("found suitable path!!!");
					   path.add(gold);
					   printPositions(path);
					   moveAlongPath(path);	 
					   searchMode = RETURNHOME;
					   break;
				   } else {
//					   Position r = removeClosestWall();
//					   walls.add(r);
//					   System.out.println("removed wall["+r.getX()+", "+r.getY()+"]");
				   }
			   }
			   if (searchMode!=RETURNHOME){
				   System.out.println("Failed to find path with Dynamite");
				   searchMode = GETGOLD;
			   }
		   }
	   }
	   if (searchMode == GETGOLD) { 
		   // GETGOLD uses an a star search to travel from current position to golds position
		   // Then set searchMode to RETURNHOME
		   System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		   Position start = new Position(curX, curY);
		   Position gold = learner.getGoldLocation();
		   AStarAlgorithm aStarFindGold = new AStarAlgorithm();
		   pathHome1 = aStarFindGold.searchForGold(learner, start, gold, BOARD_SIZE*BOARD_SIZE, this);
		   if (pathHome1 == null){
			   searchMode = DYNO;			 
		   } else {
			   searchMode = RETURNHOME;
		   }
	   }

	   // RETURNHOME uses an a star search to get from current location (gold location)
	   // to starting position [20,20]
	   // **** SHOULD USE A STAR ON BUILT UP MAP IN LEARNER< THAT WAY DOESNT HAVE TO WASTE MOVES UNTIL KNOWS SHORTEST PATH TO HOM
	   // Goal position for a star here is just [BOARD_SIZE,BOARD_SIZE]
	   // Start is [curX, curY]
	   if (searchMode == RETURNHOME){  		  
		   Position start = new Position(curX, curY);
		   Position home = new Position(BOARD_SIZE/2, BOARD_SIZE/2);
		   ArrayList<Position> path = new ArrayList<Position>();
		   AStarAlgorithm aStarFindGold = new AStarAlgorithm();
		   path = aStarFindGold.searchForPosition(learner, start, home, BOARD_SIZE*BOARD_SIZE, this);
		   if (path == null){
			   searchMode = STUCK;			 
		   } else {
			   searchMode = RETURNHOME;
		   }
		   path.add(home);
		   printPositions(path);
		   moveAlongPath(path);
		   //pathHome1.remove(pathHome1.size()); 
		   searchMode = DONE;		   
	   } 
	   if (searchMode == STUCK){
		   System.out.println("Help Im Stuck");
		   lastDirection = (lastDirection + 1) % 4;
		   return 'l';
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
   
   private char checkIfCanMoveFoward(char[][] view) {
	   if ((view[1][2] == ' ')||(view[1][2] == 'g')) {
		   if(in_boat==true){
			   in_boat = false;
			   just_departed_vessel=true;
		   } else {
			   just_departed_vessel=false;
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
   		   System.out.println("in boat!");
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
	  pathHome1 = new ArrayList<Position>();

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
            	learner = new Learner(view, agent);
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
   
   public void moveAlongPath(ArrayList<Position> sequenceOfMoves) {
	   for (Position p: sequenceOfMoves) {	
		   while ((curX != p.getX())||(curY != p.getY())){
			    System.out.println("\n"+"////////////////////////////////////////////////////////////////////////");
			    System.out.println(">>>MAKE MOVE towards [X,Y] = ["+ p.getX() + ", " +  p.getY() + "]<<<");
			    System.out.println(">>>Starting at [X,Y] = ["+ curX + ", " +  curY + "]<<<");
				char action = this.goToAdjacent(view, p);
				if (action == '.'){
					System.out.println("    hit object");
				}
				Move move = new Move(curX, curY, action);
				moves.add(move);
				System.out.println("AStar Action is:"+action);				
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
   

	public boolean makeMove(Position p) {	
		System.out.println("Make Move towards [X,Y] = ["+ p.getX() + ", " +  p.getY() + "]");	
		System.out.println("Starting at [X,Y] = ["+ curX + ", " +  curY + "]");
		while ((curX != p.getX())||(curY != p.getY())){
			    System.out.println("\n"+"////////////////////////////////////////////////////////////////////////");
			    System.out.println(">>>MAKE MOVE towards [X,Y] = ["+ p.getX() + ", " +  p.getY() + "]<<<");
			    System.out.println(">>>Starting at [X,Y] = ["+ curX + ", " +  curY + "]<<<");
				char action = this.goToAdjacent(view, p);
				if (action == '.'){
					System.out.println("    hit object");
					return false;
				}
				Move move = new Move(curX, curY, action);
				moves.add(move);
				System.out.println("AStar Action is:"+action);				
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
	
	
	//helpers	
	public void printPositions(ArrayList<Position> list){
		System.out.print("Path is:");
		for (Position p: list){
			System.out.print("["+p.getX()+", "+p.getY()+"]->");
		}
		System.out.println();
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

}


