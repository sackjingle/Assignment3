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
   
   
   private char[][] view;

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

   public char get_action( char view[][] ) {
	   
	   if (searchMode == FINDGOLD) {
		   if(moves.size()!=0){
	   			if (moves.get(moves.size()-1).getMove() == 'f') {
	   				updateLearner(view, lastDirection);
	   			}
		   }
		   
		  curX = learner.getX();
	   	  curY = learner.getY();
	   	  System.out.println("[X,Y]=["+curX+","+curY+"]");
	   	  System.out.println("In front of player is: " + view[1][2]);
	   	  
	   	  if (view[1][2] == ' ') {
	   		  nextMove = 'f';
	   	   	  System.out.println("Move is: "+nextMove);
	   	  } else if (getCOMove(curX,curY)=='l') {
	   		  nextMove = 'r';
	   	   	  System.out.println("Move is: "+nextMove);
	   		  //updateLearner(view, lastDirection);
	     	  learner.printBoard();
	   		  lastDirection = (lastDirection + 3) % 4;
	   	  } else {
	   	  	  nextMove = 'l';
	   	   	  System.out.println("Move is: "+nextMove);
	   		  //updateLearner(view, lastDirection);
	   	  	  learner.printBoard();
	   	  	  lastDirection = (lastDirection + 1) % 4;
	   	  } 
	   	  
	   	  Move move = new Move(curX, curY, nextMove);
	   	  moves.add(move);
	   	  return nextMove;
	   	  
	   } else {
		   // FIND DA PINGUZ
	   }
	return nullChar;
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
   
   char getCOMove(int x, int y){
	   for(Move m:moves){
		   if ((m.getX()==x)&&(m.getY()==y)){
			   return m.getMove();
		   }	   
	   }
	   return nullChar;
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
      InputStream in  = null;
      OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      char   view[][] = new char[5][5];
      char   action   = 'F';
      int port;
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
}
