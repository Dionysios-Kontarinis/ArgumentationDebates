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
 * This class tests the effect of the 3 heuristics on the length of the debate:
 * It tests the strategy profiles 3Avs3A, 3Bvs3B and 3Cvs3C.
 * The GB is chosen to be either a graph, or a long tree, or a short tree. 
 * Outputs in the "debate_configs.txt", and "debate_results.txt" files.
 * @author dennis
 *
 */
public class MainDebates_Heuristics {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		// *******************************************************************************
		// ***** INITIALIZE THE FRAMEWORK WHERE A NUMBER OF DEBATES WILL TAKE PLACE. ***** 
		// *******************************************************************************
		
		// We set the value of numOfConfigs, according to the number of experiments we wish to make.
		final int numOfConfigs = 200;
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final int numOfDebates = 3;
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
			
			String strategyPRO = "";
			int stratPROindicator = 0;
			String strategyCON = "";
			int stratCONindicator = 0;
			
			for (int j=0; j<numOfDebates; j++) {
				
				// **********************************************
				// ***** A SINGLE DEBATE PROCEDURE (BEGIN). ***** 
				// **********************************************		
				
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
					if (currAg.team.equals("PRO")) {
						if (j==0) move = currAg.strategyWeakenTSet(gb, 1);
						else if (j==1) move = currAg.strategyWeakenTSet(gb, 2);
						else if (j==2) move = currAg.strategyWeakenTSet(gb, 3);
					} else {
						if (j==0) move = currAg.strategyWeakenTSet(gb, 1);
						else if (j==1) move = currAg.strategyWeakenTSet(gb, 2);
						else if (j==2) move = currAg.strategyWeakenTSet(gb, 3);
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
		
//		// Print on the console:
//		for (int i=0; i<numOfConfigs; i++) {
//			for (int j=0; j<numOfDebates; j++) {
//				System.out.println("numConfig = " + (i+1) + ", numArgs = " + numArguments[i][j] + ", numAttacks = " + numAttacks[i][j] + ", numPRO = " + numPRO[i][j] + ", numCON = " + numCON[i][j] +
//						", expPRO = " + expPRO[i][j] + ", expCON = " + expCON[i][j] + ", initNumTSets = " + initGBNumTSets[i][j] +
//						", issueInMerged = " + issueInMerged[i][j] + ", isIdeal = " + isIdeal[i][j] + "   |   " +
//						" strategyProfile " + strategyProfile[i][j] + ", issueInDebate = " + issueInDebate[i][j] + ", numberOfRounds = " + numberOfRounds[i][j] + " numberOfRoundsNoPass = " + numberOfRoundsNoPass[i][j]);
//			}
//			System.out.println();
//			System.out.println();
//		}
		
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
		
		
		
		for (int i=0; i<numOfConfigs; i++) {
			writer_results.println("#Configuration" + " " + "#Arguments" + " " + "#Attacks" + " " + "#PRO" + " " + "#CON" + " " + "expPRO" + " " + "expCON" + " " + "#InitialTSets" + " " + "issueInMerged" + " " + "isIdeal");
						//+ " " + " " + "strategyProfile (PRO\\CON)" + " " + "issueInDebate" + " " + "#Rounds" + " " + "#RoundsNoPass");
			writer_results.println((i+1) + " " + numArguments[i][0] + " " + numAttacks[i][0] + " " + numPRO[i][0] + " " + numCON[i][0] +
						" " + expPRO[i][0] + " " + expCON[i][0] + " " + initGBNumTSets[i][0] +
						" " + issueInMerged[i][0] + " " + isIdeal[i][0] + " " + " ");
						// + strategyProfile[i][j] + " " + issueInDebate[i][j] + " " + numberOfRounds[i][j] + " " + numberOfRoundsNoPass[i][j]);
			writer_results.println();
			
			writer_results.println("IN_EVERY_CELL_4_NUMBERS: (A)%_PRO_WINS, (B)%_AGREE_WITH_MERGED, (C)MEAN_NUM_ROUNDS, (D)MEAN_NUM_NO-PASS");
			writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
			writer_results.println();
			
			for (int j=0; j<9; j++) {
				if (j==0) writer_results.print("1" + " ");
				else if (j == 1) writer_results.print("2A" + " ");
				else if (j == 2) writer_results.print("2B" + " ");
				else if (j == 3) writer_results.print("3A" + " ");
				else if (j == 4) writer_results.print("3B" + " ");
				else if (j == 5) writer_results.print("3C" + " ");
				else if (j == 6) writer_results.print("4A" + " ");
				else if (j == 7) writer_results.print("4B" + " ");
				else if (j == 8) writer_results.print("4C" + " ");
				
				
				// Calculate the percentage of PRO wins...
//				int PROprc;
//				int agreeMergedPrc;
//				int meanNumRounds;
//				int meanNumRoundsNoPass;
//				for (int k=0; k<9; k++) {
//					PROprc = 0;
//					agreeMergedPrc = 0;
//					meanNumRounds = 0;
//					meanNumRoundsNoPass = 0;
//					int start = (90 * j) + (10 * k);
//					int end = start + 10;
//					for (int l=start; l<end; l++) {
//						if (issueInDebate[i][l]) PROprc++;
//						if (issueInDebate[i][l] == issueInMerged[i][l]) agreeMergedPrc++;
//						meanNumRounds += numberOfRounds[i][l];
//						meanNumRoundsNoPass += numberOfRoundsNoPass[i][l];
//					}
//					writer_results.print((float)PROprc/10 + "#" + (float)agreeMergedPrc/10 + "#" + (float)meanNumRounds/10 + "#" + (float)meanNumRoundsNoPass/10 + " ");
//				}
//				writer_results.println();
//			
			}
			
			writer_results.println();
			writer_results.println();
			

		}
		
		writer_configs.close();
		writer_results.close();
	}
	
}
