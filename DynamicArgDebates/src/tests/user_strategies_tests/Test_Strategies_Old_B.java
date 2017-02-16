package tests.user_strategies_tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import debateComponents.*;

/**
 * This class tests the correct functioning of the target sets and of the possible moves.
 * @author dennis
 *
 */
public class Test_Strategies_Old_B {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		// *******************************************************************************
		// ***** INITIALIZE THE FRAMEWORK WHERE A NUMBER OF DEBATES WILL TAKE PLACE. ***** 
		// *******************************************************************************
		
		// We set the value of numOfConfigs, according to the number of experiments we wish to make.
		final int numOfConfigs = 1;
		// We have defined 10 different strategies for the agents (1 + 1x3 + 1x3 + 1x3). (Here we don't consider the strategy which plays completely random moves).
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// TO DO: We have to amend the strategies which are based on playing on the "optimistically" smallest target sets.
		final int numOfDebates = 1;
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		// The following set of two-dimensional arrays contain the essential information about the experiments.
		int numArguments[][]			= new int[numOfConfigs][numOfDebates];
		int numAttacks[][]				= new int[numOfConfigs][numOfDebates];
		int numPRO[][]					= new int[numOfConfigs][numOfDebates];
		int numCON[][]					= new int[numOfConfigs][numOfDebates];
		// In the expPRO and expCON arrays, a number represents the cardinality of the multiset of expertise of the group. 
		int expPRO[][]					= new int[numOfConfigs][numOfDebates];
		int expCON[][]					= new int[numOfConfigs][numOfDebates];
		// In the initGBNumTSets array, we keep the initial number of target sets of the GB.
		int initGBNumTSets[][]			= new int[numOfConfigs][numOfDebates];
		boolean issueInMerged[][] 		= new boolean[numOfConfigs][numOfDebates];
		// A configuration is "ideal" if the majority of agents agree (on the status' issue) with the Merged WAS.
		boolean isIdeal[][]				= new boolean[numOfConfigs][numOfDebates];
		// The two strategies, used by the two groups (PRO and CON) (eg. "1vs1", "3vs4").
		String strategyProfile[][] 		= new String[numOfConfigs][numOfDebates];
		boolean issueInDebate[][] 		= new boolean[numOfConfigs][numOfDebates];
		int numberOfRounds[][] 			= new int[numOfConfigs][numOfDebates];
		int numberOfRoundsNoPass[][] 	= new int[numOfConfigs][numOfDebates];
		
		PrintWriter writer_configs = new PrintWriter("debate_configs.txt", "UTF-8");
		PrintWriter writer_results = new PrintWriter("debate_results.txt", "UTF-8");
		
		for (int i=0; i<numOfConfigs; i++) {
			/////////////////////////////////////////////////////  Initializing the elements of the debate.
			
			Configuration_CLIMA14 config = new Configuration_CLIMA14(true, false);
			config.printAllInfo(null);
			config.printAllInfo(writer_configs);
			
			Gameboard gb = config.gb;
			
			//  Computing & printing the Merged WAS.
			System.out.println("Computing the Merged WAS...");
			Gameboard merged = Agent.computeMergedWAS(config);
//			merged.printGB(null);
			
			double strategyPRO = 0;
			double strategyCON = 0;
			
			for (int j=0; j<numOfDebates; j++) {
				
				// **********************************************
				// ***** A SINGLE DEBATE PROCEDURE (BEGIN). ***** 
				// **********************************************
				
				gb = config.gb;
				
//				System.out.println("Resetting the Gameboard...");
//				gb.reset(config);
//				gb.printGB(null);
				
				// The agents' played moves are erased.
				for (int k=0; k<config.agents.size(); k++) {
					config.agents.get(k).playedAtts.clear();
				}
				
				System.out.println("********** Starting a new debate procedure... **********");
				System.out.println();
				
				int numRounds = 0;
				int numRoundsNoPass = 0;
				int numPassMoves = 0;
				int agentNum = 0;
				Attack move = null;
				Agent currAg;
				////////////////////////
				gb.computeTargetSets();
//				gb.printTargetSets();
				////////////////////////
				
				while (numPassMoves < config.agents.size()) {
					numRounds++;
					currAg = config.agents.get(agentNum);
					// Apply the proper strategy (all the agents will use it during this debate).
					if (j==0) { move = currAg.strategyWeakenTSet(gb, 1); strategyPRO = strategyCON = 3.1;}
					else if (j==33) { move = currAg.strategyWeakenTSet(gb, 2); strategyPRO = strategyCON = 3.2;}
					
					if (move == null) {
						System.out.println("It was a pass move.");
						numPassMoves++;
					} else {
						// The agent plays the move he has chosen on the Gameboard (the fourth parameter is "verbose").
						gb.playMoveOnGB(move, currAg, true, false);
						numRoundsNoPass++;
						gb.computeTargetSets();
						numPassMoves = 0;
					}
					// Prepare for the next loop.		
					if (agentNum == config.agents.size()-1) {
						agentNum = 0;
					} else {
						agentNum++;
					}
				}
				System.out.println();
				System.out.println("Finishing the debate procedure...");
				System.out.println();

				// ********************************************
				// ***** A SINGLE DEBATE PROCEDURE (END). ***** 
				// ********************************************

				
				// *****************************************************
				// ***** THE RESULTS COLLECTION PROCEDURE (BEGIN). *****
				// *****************************************************
				
//				System.out.println("Analyzing the results...");
//				System.out.println("The GB ends up as follows:");
//				gb.printGB();
				numArguments[i][j] = config.gb.arguments.size();
				numAttacks[i][j] = gb.attacks.size();
				numPRO[i][j] = config.numPRO;
				numCON[i][j] = config.numCON;
				expPRO[i][j] = 0;
				expCON[i][j] = 0;
				for (int k=0; k<config.agents.size(); k++) {
					currAg = config.agents.get(k);
					if (currAg.team.equals("PRO")) expPRO[i][j] += currAg.agentExpertise.size();
					else expCON[i][j] += currAg.agentExpertise.size();
				}
				if (merged.statusIssue) {
//					System.out.println("In the merged system, the winner is PRO.");
					issueInMerged[i][j] = true;
				} else {
//					System.out.println("In the merged system, the winner is CON.");
					issueInMerged[i][j] = false;
				}
				if ( (config.numPRO >= config.numCON) && merged.statusIssue
						|| (config.numCON >= config.numPRO) && !merged.statusIssue) {
					System.out.println("The debate is ideal.");
					isIdeal[i][j] = true;
				} else {
					System.out.println("The debate is not ideal.");
					isIdeal[i][j] = false;
				}
				strategyProfile[i][j] = strategyPRO + "vs" + strategyCON;
				if (gb.statusIssue) {
//					System.out.println("In the debate, the winner is PRO.");
					issueInDebate[i][j] = true;
				} else {
//					System.out.println("In the debate, the winner is CON.");
					issueInDebate[i][j] = false;
				}
//				System.out.println("The debate consisted in " + numRounds + " rounds.");
				numberOfRounds[i][j] = numRounds;
//				System.out.println("Among these rounds, there were " + numRoundsNoPass + " with no-pass moves.");
				numberOfRoundsNoPass[i][j] = numRoundsNoPass;
				
				gb.resetGB();
				gb.computeTargetSets();
				initGBNumTSets[i][j] = gb.targetSets.size();
				
				// ***************************************************
				// ***** THE RESULTS COLLECTION PROCEDURE (END). *****
				// ***************************************************
			} // end-for (every strategy)
		} // end-for (every config)
		
		
		
		// ******************************************
		// ***** PRINT INFO ON ALL THE DEBATES. *****
		// ******************************************
		
		// Print on the console:
		for (int i=0; i<numOfConfigs; i++) {
			for (int j=0; j<numOfDebates; j++) {
				System.out.println("numConfig = " + (i+1) + ", numArgs = " + numArguments[i][j] + ", numAttacks = " + numAttacks[i][j] + ", numPRO = " + numPRO[i][j] + ", numCON = " + numCON[i][j] +
						", expPRO = " + expPRO[i][j] + ", expCON = " + expCON[i][j] + ", initNumTSets = " + initGBNumTSets[i][j] +
						", issueInMerged = " + issueInMerged[i][j] + ", isIdeal = " + isIdeal[i][j] + "   |   " +
						" strategyProfile " + strategyProfile[i][j] + ", issueInDebate = " + issueInDebate[i][j] + ", numberOfRounds = " + numberOfRounds[i][j] + " numberOfRoundsNoPass = " + numberOfRoundsNoPass[i][j]);
			}
			System.out.println();
			System.out.println();
		}
		
		// Print on a text file:
		writer_results.println("#Configuration" + " " + "#Arguments" + " " + "#Attacks" + " " + "#PRO" + " " + "#CON" + " " + "expPRO" + " " + "expCON" + " " + "#InitialTSets" + " " + "issueInMerged" + " " + "isIdeal" +
							" " + " " + "strategyProfile" + " " + "issueInDebate" + " " + "#Rounds" + " " + "#RoundsNoPass");
		for (int i=0; i<numOfConfigs; i++) {
			for (int j=0; j<numOfDebates; j++) {
				writer_results.println((i+1) + " " + numArguments[i][j] + " " + numAttacks[i][j] + " " + numPRO[i][j] + " " + numCON[i][j] +
						" " + expPRO[i][j] + " " + expCON[i][j] + " " + initGBNumTSets[i][j] +
						" " + issueInMerged[i][j] + " " + isIdeal[i][j] + " " + " " +
						strategyProfile[i][j] + " " + issueInDebate[i][j] + " " + numberOfRounds[i][j] + " " + numberOfRoundsNoPass[i][j]);
				// if (j % 10 == 9) writer.println();
			}
			writer_results.println();
			writer_results.println();
		}
		
		writer_configs.close();
		writer_results.close();
	}
	
}
