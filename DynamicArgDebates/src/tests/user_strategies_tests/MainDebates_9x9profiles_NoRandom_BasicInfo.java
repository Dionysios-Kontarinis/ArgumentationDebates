package tests.user_strategies_tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import debateComponents.Agent;
import debateComponents.Attack;
import debateComponents.Configuration;
import debateComponents.Configuration_CLIMA14;
import debateComponents.Gameboard;

/**
 * This class tests the following:
 * Each of the strategies 1, 2a, 2b, 3a, 3b, 3c, 4a, 4b, 4c is put up against every other one.
 * The random strategy (0) is not tested here!
 * Every such strategy profile is tested 10 times (for every configuration we consider). 
 * The GB is chosen to be either a graph, or a long tree, or a short tree. 
 * Outputs in the "debate_configs.txt", and "debate_results.txt" files.
 * @author dennis
 *
 */
public class MainDebates_9x9profiles_NoRandom_BasicInfo {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		// *******************************************************************************
		// ***** INITIALIZE THE FRAMEWORK WHERE A NUMBER OF DEBATES WILL TAKE PLACE. ***** 
		// *******************************************************************************
		
		// We set the value of numOfConfigs, according to the number of experiments we wish to make.
		final int numOfConfigs = 10;
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final int numOfDebates = 810;
//		final int numOfDebates = 2;
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
			merged.printGB(null);
			
			String strategyPRO = "";
			int stratPROindicator = 0;
			String strategyCON = "";
			int stratCONindicator = 0;
			
			for (int j=0; j<numOfDebates; j++) {
				
				// **********************************************
				// ***** A SINGLE DEBATE PROCEDURE (BEGIN). ***** 
				// **********************************************
				
				// Find-out which group will play using which strategy.
				stratPROindicator = j / 90;
				stratCONindicator = (j % 90) / 10; 
				
				if (stratPROindicator == 0) strategyPRO = "1";
				else if (stratPROindicator == 1) strategyPRO = "2a";
				else if (stratPROindicator == 2) strategyPRO = "2b";
				else if (stratPROindicator == 3) strategyPRO = "3a";
				else if (stratPROindicator == 4) strategyPRO = "3b";
				else if (stratPROindicator == 5) strategyPRO = "3c";
				else if (stratPROindicator == 6) strategyPRO = "4a";
				else if (stratPROindicator == 7) strategyPRO = "4b";
				else if (stratPROindicator == 8) strategyPRO = "4c";	
				
				if (stratCONindicator == 0) strategyCON = "1";
				else if (stratCONindicator == 1) strategyCON = "2a";
				else if (stratCONindicator == 2) strategyCON = "2b";
				else if (stratCONindicator == 3) strategyCON = "3a";
				else if (stratCONindicator == 4) strategyCON = "3b";
				else if (stratCONindicator == 5) strategyCON = "3c";
				else if (stratCONindicator == 6) strategyCON = "4a";
				else if (stratCONindicator == 7) strategyCON = "4b";
				else if (stratCONindicator == 8) strategyCON = "4c";
				
				gb = config.gb;
				
				System.out.println("Resetting the Gameboard...");
				gb.resetGB();
				gb.printGB(null);
				
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
//					if (j == 0) move = currAg.strategyChangeIssue(gb);
					if (currAg.team.equals("PRO")) {
						if (strategyPRO.equals("1")) move = currAg.strategyChangeIssue(gb);
						else if (strategyPRO.equals("2a")) move = currAg.strategyCutTSet(gb, 1);
						else if (strategyPRO.equals("2b")) move = currAg.strategyCutTSet(gb, 2);
						else if (strategyPRO.equals("3a")) move = currAg.strategyWeakenTSet(gb, 1);
						else if (strategyPRO.equals("3b")) move = currAg.strategyWeakenTSet(gb, 2);
						else if (strategyPRO.equals("3c")) move = currAg.strategyWeakenTSet(gb, 3);
						else if (strategyPRO.equals("4a")) move = currAg.strategyWeakenReinforceTSet(gb, 1);
						else if (strategyPRO.equals("4b")) move = currAg.strategyWeakenReinforceTSet(gb, 2);
						else if (strategyPRO.equals("4c")) move = currAg.strategyWeakenReinforceTSet(gb, 3);
					} else {
						if (strategyCON.equals("1")) move = currAg.strategyChangeIssue(gb);
						else if (strategyCON.equals("2a")) move = currAg.strategyCutTSet(gb, 1);
						else if (strategyCON.equals("2b")) move = currAg.strategyCutTSet(gb, 2);
						else if (strategyCON.equals("3a")) move = currAg.strategyWeakenTSet(gb, 1);
						else if (strategyCON.equals("3b")) move = currAg.strategyWeakenTSet(gb, 2);
						else if (strategyCON.equals("3c")) move = currAg.strategyWeakenTSet(gb, 3);
						else if (strategyCON.equals("4a")) move = currAg.strategyWeakenReinforceTSet(gb, 1);
						else if (strategyCON.equals("4b")) move = currAg.strategyWeakenReinforceTSet(gb, 2);
						else if (strategyCON.equals("4c")) move = currAg.strategyWeakenReinforceTSet(gb, 3);
					}
					
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
				strategyProfile[i][j] = strategyPRO + "VS" + strategyCON;
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
