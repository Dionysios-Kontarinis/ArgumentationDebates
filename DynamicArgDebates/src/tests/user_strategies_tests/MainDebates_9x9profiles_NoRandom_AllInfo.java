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
 * Each one of the strategies 1, 2A, 2B, 3A, 3B, 3C, 4A, 4B, 4C is put up against every other one (81 strategy profiles in total).
 * The random strategy (0) is not tested here!
 * Every strategy profile is tested 10 times (for every configuration).
 * Outputs in the "debate_configs.txt", and "debate_results.txt" files.
 * The results are presented compactly with the help of some final recapitulating tables as follows. 
 * (1) For every config, a recapitulating table is created, which contains: 
 *  (i) The percentage of wins by PRO, (ii) the percentage of agreements with the Merged, (iii) the percentage of agreements with the Majority,
 *  (iv) the mean number of rounds, and finally (v) the mean number of no-pass rounds.
 * (2) Three more recapitulating tables contain all the previous info (points (i) to (v)), but this time for all debates:
 *  (a) where issueInMerged==true, (b) where issueInMerged==false, (c) all debates in general.
 * (3) Finally, the percentage of times that some specific pairs of strategy profiles (1vs1 and 2vs2, 1vs1 and 3Cvs3C, 3Cvs3C and 4Cvs4C)
 *  give the same result is presented. 
 * The GB is chosen to be either a graph, or a long tree, or a short tree. 
 * 
 * @author dennis
 *
 */
public class MainDebates_9x9profiles_NoRandom_AllInfo {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		// *******************************************************************************
		// ***** INITIALIZE THE FRAMEWORK WHERE A NUMBER OF DEBATES WILL TAKE PLACE. ***** 
		// *******************************************************************************
		
		// The number of configurations we will test.
		final int numOfConfigs = 90;
		// The number of debates beginning with exactly the same configuration.
		final int numRepetitions = 10;
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final int numOfDebates = 81 * numRepetitions;
//		final int numOfDebates = 2;
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		// The following set of two-dimensional arrays will stock the essential information on the experiments which follow.
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
			
			/////////////////////////////////////////////////////
			int numPROwanted = (i/10) + 1; // Supposing that we consider 90 configs, we want numPROwanted to take all possible values from 1 to 9.
			// Create a tree? Yes! (first parameter == true).
			Configuration_CLIMA14 config = new Configuration_CLIMA14(true, false, numPROwanted);
			/////////////////////////////////////////////////////
			
//			System.out.println("CONFIGURATION " + i);
//			config.printAllInfo(null);
			writer_configs.println("********** CONFIGURATION " + i + " **********");
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
				
				// Find-out which group will play using which strategy.
				stratPROindicator = j / (9 * numRepetitions);
				stratCONindicator = (j % (9 * numRepetitions)) / numRepetitions; 
				
				if (stratPROindicator == 0) strategyPRO = "1";
				else if (stratPROindicator == 1) strategyPRO = "2A";
				else if (stratPROindicator == 2) strategyPRO = "2B";
				else if (stratPROindicator == 3) strategyPRO = "3A";
				else if (stratPROindicator == 4) strategyPRO = "3B";
				else if (stratPROindicator == 5) strategyPRO = "3C";
				else if (stratPROindicator == 6) strategyPRO = "4A";
				else if (stratPROindicator == 7) strategyPRO = "4B";
				else if (stratPROindicator == 8) strategyPRO = "4C";	
				
				if (stratCONindicator == 0) strategyCON = "1";
				else if (stratCONindicator == 1) strategyCON = "2A";
				else if (stratCONindicator == 2) strategyCON = "2B";
				else if (stratCONindicator == 3) strategyCON = "3A";
				else if (stratCONindicator == 4) strategyCON = "3B";
				else if (stratCONindicator == 5) strategyCON = "3C";
				else if (stratCONindicator == 6) strategyCON = "4A";
				else if (stratCONindicator == 7) strategyCON = "4B";
				else if (stratCONindicator == 8) strategyCON = "4C";
					
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
						else if (strategyPRO.equals("2A")) move = currAg.strategyCutTSet(gb, 1);
						else if (strategyPRO.equals("2B")) move = currAg.strategyCutTSet(gb, 2);
						else if (strategyPRO.equals("3A")) move = currAg.strategyWeakenTSet(gb, 1);
						else if (strategyPRO.equals("3B")) move = currAg.strategyWeakenTSet(gb, 2);
						else if (strategyPRO.equals("3C")) move = currAg.strategyWeakenTSet(gb, 3);
						else if (strategyPRO.equals("4A")) move = currAg.strategyWeakenReinforceTSet(gb, 1);
						else if (strategyPRO.equals("4B")) move = currAg.strategyWeakenReinforceTSet(gb, 2);
						else if (strategyPRO.equals("4C")) move = currAg.strategyWeakenReinforceTSet(gb, 3);
					} else {
						if (strategyCON.equals("1")) move = currAg.strategyChangeIssue(gb);
						else if (strategyCON.equals("2A")) move = currAg.strategyCutTSet(gb, 1);
						else if (strategyCON.equals("2B")) move = currAg.strategyCutTSet(gb, 2);
						else if (strategyCON.equals("3A")) move = currAg.strategyWeakenTSet(gb, 1);
						else if (strategyCON.equals("3B")) move = currAg.strategyWeakenTSet(gb, 2);
						else if (strategyCON.equals("3C")) move = currAg.strategyWeakenTSet(gb, 3);
						else if (strategyCON.equals("4A")) move = currAg.strategyWeakenReinforceTSet(gb, 1);
						else if (strategyCON.equals("4B")) move = currAg.strategyWeakenReinforceTSet(gb, 2);
						else if (strategyCON.equals("4C")) move = currAg.strategyWeakenReinforceTSet(gb, 3);
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
		
		///////////////////////////
		// Print on a text file: //
		///////////////////////////
		
		//////////////////////////////////////////////////////////////////////
		// 1111111111111111111111111111111111111111111111111111111111111111111
		//////////////////////////////////////////////////////////////////////
		// Print a detailed table with all the information on every debate.
		writer_results.println("#Configuration" + " " + "#Arguments" + " " + "#Attacks" + " " + "#PRO" + " " + "#CON" + " " + "expPRO" + " " + "expCON" + " " + "#InitialTSets" + " " + "issueInMerged" + " " + "isIdeal" +
									" " + " " + "strategyProfile" + " " + "issueInDebate" + " " + "#Rounds" + " " + "#RoundsNoPass");
		for (int i=0; i<numOfConfigs; i++) {
			for (int j=0; j<numOfDebates; j++) {
				writer_results.println((i+1) + " " + numArguments[i][j] + " " + numAttacks[i][j] + " " + numPRO[i][j] + " " + numCON[i][j] +
						" " + expPRO[i][j] + " " + expCON[i][j] + " " + initGBNumTSets[i][j] +
						" " + issueInMerged[i][j] + " " + isIdeal[i][j] + " " + " " +
						strategyProfile[i][j] + " " + issueInDebate[i][j] + " " + numberOfRounds[i][j] + " " + numberOfRoundsNoPass[i][j]);
			}
			writer_results.println();
			writer_results.println();
		}
		//////////////////////////////////////////////////////////////////////
		// 2222222222222222222222222222222222222222222222222222222222222222222
		//////////////////////////////////////////////////////////////////////
		// Print one matrix/configuration (regrouping 10 repetitions). "Rich" table (each cell has 5 numbers).
		for (int i=0; i<numOfConfigs; i++) {
			// The configuration whose info is about to be displayed compactly.
			writer_results.println("#Configuration" + " " + "#Arguments" + " " + "#Attacks" + " " + "#PRO" + " " + "#CON" + " " + "expPRO" + " " + "expCON" + " " + "#InitialTSets" + " " + "issueInMerged" + " " + "isIdeal");
						//+ " " + " " + "strategyProfile (PRO\\CON)" + " " + "issueInDebate" + " " + "#Rounds" + " " + "#RoundsNoPass");
			writer_results.println((i+1) + " " + numArguments[i][0] + " " + numAttacks[i][0] + " " + numPRO[i][0] + " " + numCON[i][0] +
						" " + expPRO[i][0] + " " + expCON[i][0] + " " + initGBNumTSets[i][0] +
						" " + issueInMerged[i][0] + " " + isIdeal[i][0] + " " + " ");
						// + strategyProfile[i][j] + " " + issueInDebate[i][j] + " " + numberOfRounds[i][j] + " " + numberOfRoundsNoPass[i][j]);
			writer_results.println();
			// The compact info of the debates over that configuration.
			writer_results.println("EVERY_CELL_5_NUMBERS: (A)%_PRO_WINS, (B)%_AGREE_MERGED, (C)%_AGREE_MAJORITY, (D)MEAN_NUM_ROUNDS, (E)MEAN_NUM_NO-PASS");
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
				int totalPROwins;
				int totalAgreeMerged;
				int totalAgreeMajority;
				//int totalIsIdeal;
				int totalNumRounds;
				int totalNumRoundsNoPass;
				for (int k=0; k<9; k++) {
					totalPROwins = 0;
					totalAgreeMerged = 0;
					totalAgreeMajority = 0;
					//totalIsIdeal = 0;
					totalNumRounds = 0;
					totalNumRoundsNoPass = 0;
					int start = ((9 * numRepetitions) * j) + (numRepetitions * k);
					int end = start + numRepetitions;
					for (int l=start; l<end; l++) {
						//if (isIdeal[i][l]) totalIsIdeal++;
						if (issueInDebate[i][l]) totalPROwins++;
						if (issueInDebate[i][l] == issueInMerged[i][l]) totalAgreeMerged++;
						if (numPRO[i][l] != numCON[i][l]) {
							if ( (issueInDebate[i][l] && numPRO[i][l]>numCON[i][l]) || (!issueInDebate[i][l] && numCON[i][l]>numPRO[i][l]) ) 
								totalAgreeMajority++;
						} else { // numPRO[i][l] == numCON[i][l]: in this case, by default, PRO is considered the majority.
							if (issueInDebate[i][l]) 
								totalAgreeMajority++;
						}
						totalNumRounds += numberOfRounds[i][l];
						totalNumRoundsNoPass += numberOfRoundsNoPass[i][l];
						
					}
					writer_results.print((float)totalPROwins/numRepetitions + "#" + (float)totalAgreeMerged/numRepetitions + "#" + (float)totalAgreeMajority/numRepetitions + "#" +
								//+ (float)totalIsIdeal/numRepetitions 
								+ (float)totalNumRounds/numRepetitions + "#" + (float)totalNumRoundsNoPass/numRepetitions + " ");
				}
				writer_results.println();
			}
			writer_results.println();
			writer_results.println();
		} // end of recapitulation for every config.
		writer_results.println();
		//////////////////////////////////////////////////////////////////////
		// 3333333333333333333333333333333333333333333333333333333333333333333
		//////////////////////////////////////////////////////////////////////
		// Regroup data OF EACH DELTA(PRO,CON) using 18 "simple" tables (9 tables "agreement debate-Merged", 9 tables "agreement debate-Majority").
//		writer_results.print("For_case_5/5" + " " + "%_PRO_wins");
		writer_results.print("For_each_DELTA" + " " + "agreement debate-Merged" + " " + "and agreement debate-Majority");

//		// Percentage PRO wins (1 table).
//		for (int v=4; v<5; v++) {
//			// v indicates the current type of Delta(PRO,CON) - we just want the 5/5.
//			writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
//			writer_results.println();
//			for (int w=0; w<9; w++) {
//				// For every line of the final table.
//				if (w==0) writer_results.print("1" + " ");
//				else if (w == 1) writer_results.print("2A" + " ");
//				else if (w == 2) writer_results.print("2B" + " ");
//				else if (w == 3) writer_results.print("3A" + " ");
//				else if (w == 4) writer_results.print("3B" + " ");
//				else if (w == 5) writer_results.print("3C" + " ");
//				else if (w == 6) writer_results.print("4A" + " ");
//				else if (w == 7) writer_results.print("4B" + " ");
//				else if (w == 8) writer_results.print("4C" + " ");
//				int totalPROwins = 0;
//				for (int x=0; x<9; x++) {
//					// For every column of the final table.
//					totalPROwins = 0;
//					for (int y=v*10; y<v*10+10; y++) {
//						// For every configuration OF THE CURRENT DELTA(PRO,CON).
//						for (int z=0; z<numRepetitions; z++) {
//							// For all set of debates.
//							// Consider debates of that couple (config and strategy profile).
//							int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
//							if (issueInDebate[y][target]) totalPROwins++;
//						}
//					}
//					writer_results.print((float)totalPROwins/(10*numRepetitions) + " "); // 10*numRepetitions, because there are 10 configs in every Delta(PRO,CON).
//				}
//				writer_results.println();
//			}
//			writer_results.println();
//		}

		// Agreement debate-Merged (9 tables).
		for (int v=0; v<9; v++) {
			// v indicates the current type of Delta(PRO,CON).
				writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
				writer_results.println();
				for (int w=0; w<9; w++) {
					// For every line of the final table.
					if (w==0) writer_results.print("1" + " ");
					else if (w == 1) writer_results.print("2A" + " ");
					else if (w == 2) writer_results.print("2B" + " ");
					else if (w == 3) writer_results.print("3A" + " ");
					else if (w == 4) writer_results.print("3B" + " ");
					else if (w == 5) writer_results.print("3C" + " ");
					else if (w == 6) writer_results.print("4A" + " ");
					else if (w == 7) writer_results.print("4B" + " ");
					else if (w == 8) writer_results.print("4C" + " ");
					int totalAgreeMerged = 0;
					for (int x=0; x<9; x++) {
						// For every column of the final table.
						totalAgreeMerged = 0;
						for (int y=v*10; y<v*10+10; y++) {
							// For every configuration OF THE CURRENT DELTA(PRO,CON).
							for (int z=0; z<numRepetitions; z++) {
								// For all set of debates.
								// Consider debates of that couple (config and strategy profile).
								int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
								if (issueInDebate[y][target] == issueInMerged[y][target]) totalAgreeMerged++;
							}
						}
						writer_results.print((float)totalAgreeMerged/(10*numRepetitions) + " "); // 10*numRepetitions, because there are 10 configs in every Delta(PRO,CON).
					}
					writer_results.println();
				}
				writer_results.println();
		}
		// Agreement debate-Majority (9 tables).
		for (int v=0; v<9; v++) {
			// v indicates the current type of Delta(PRO,CON).
			writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
			writer_results.println();
			for (int w=0; w<9; w++) {
				// For every line of the final table.
				if (w==0) writer_results.print("1" + " ");
				else if (w == 1) writer_results.print("2A" + " ");
				else if (w == 2) writer_results.print("2B" + " ");
				else if (w == 3) writer_results.print("3A" + " ");
				else if (w == 4) writer_results.print("3B" + " ");
				else if (w == 5) writer_results.print("3C" + " ");
				else if (w == 6) writer_results.print("4A" + " ");
				else if (w == 7) writer_results.print("4B" + " ");
				else if (w == 8) writer_results.print("4C" + " ");
				int totalAgreeMajority = 0;
				for (int x=0; x<9; x++) {
					// For every column of the final table.
					totalAgreeMajority = 0;
					for (int y=v*10; y<v*10+10; y++) {
						// For every configuration OF THE CURRENT DELTA(PRO,CON).
						for (int z=0; z<numRepetitions; z++) {
							// For all set of debates.
							// Consider debates of that couple (config and strategy profile).
							int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
							if (numPRO[y][target] != numCON[y][target]) {
								if ( (issueInDebate[y][target] && numPRO[y][target]>numCON[y][target]) || (!issueInDebate[y][target] && numCON[y][target]>numPRO[y][target]) ) 
									totalAgreeMajority++;
							} else { // numPRO[i][l] == numCON[i][l]: in this case, by default, PRO is considered the majority.
								if (issueInDebate[y][target]) 
									totalAgreeMajority++;
							}
						}
					}
					writer_results.print((float)totalAgreeMajority/(10*numRepetitions) + " "); // 10*numRepetitions, because there are 10 configs in every Delta(PRO,CON).
				}
				writer_results.println();
			}
			writer_results.println();
		}
		//////////////////////////////////////////////////////////////////////
		// 4444444444444444444444444444444444444444444444444444444444444444444
		//////////////////////////////////////////////////////////////////////		
		// Print a "rich" matrix with info on all the debates (and all the configurations).
		// Then print 5 "simple" matrixes (each for one important characteristic).
		writer_results.println("***INFO_ALL_DEBATES***");
		writer_results.println();
		writer_results.println("EVERY_CELL_5_NUMBERS: (A)%_PRO_WINS, (B)%_AGREE_MERGED, (C)%_AGREE_MAJORITY, (D)MEAN_NUM_ROUNDS, (E)MEAN_NUM_NO-PASS");
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalPROwins = 0;
			int totalAgreeMerged = 0;
			int totalAgreeMajority = 0;
			//int totalIsIdeal;
			int totalNumRounds = 0;
			int totalNumRoundsNoPass = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalPROwins = 0;
				totalAgreeMerged = 0;
				totalAgreeMajority = 0;
				// totalIsIdeal = 0;
				totalNumRounds = 0;
				totalNumRoundsNoPass = 0;
				for (int y=0; y<numOfConfigs; y++) {
					// For every configuration.
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (issueInDebate[y][target]) totalPROwins++;
						if (issueInDebate[y][target] == issueInMerged[y][target]) totalAgreeMerged++;
						if (numPRO[y][target] != numCON[y][target]) {
							if ( (issueInDebate[y][target] && numPRO[y][target]>numCON[y][target]) || (!issueInDebate[y][target] && numCON[y][target]>numPRO[y][target]) ) 
								totalAgreeMajority++;
						} else { // numPRO[i][l] == numCON[i][l]: in this case, by default, PRO is considered the majority.
							if (issueInDebate[y][target]) 
								totalAgreeMajority++;
						}
						totalNumRounds += numberOfRounds[y][target];
						totalNumRoundsNoPass += numberOfRoundsNoPass[y][target];
					}
				}
				writer_results.print((float)totalPROwins/(numOfConfigs*numRepetitions) + "#" + (float)totalAgreeMerged/(numOfConfigs*numRepetitions) + "#" +
						(float)totalAgreeMajority/(numOfConfigs*numRepetitions) + "#" + //+ (float)totalIsIdeal/(numOfConfigs*numRepetitions) 
						(float)totalNumRounds/(numOfConfigs*numRepetitions) + "#" + (float)totalNumRoundsNoPass/(numOfConfigs*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();
		
		// Print a "simple" matrix with info on the percentage of PRO wins (all debates, all configurations).
		writer_results.println("***INFO_ALL_DEBATES" + " " + "PER_ELEMENT***");
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalPROwins = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalPROwins = 0;
				for (int y=0; y<numOfConfigs; y++) {
					// For every configuration.
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (issueInDebate[y][target]) totalPROwins++;
					}
				}
				writer_results.print((float)totalPROwins/(numOfConfigs*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();
		
		// Print a "simple" matrix with info on percentage of agreement between debate and Merged (all debates, all configurations).
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalAgreeMerged = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalAgreeMerged = 0;
				for (int y=0; y<numOfConfigs; y++) {
					// For every configuration.
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (issueInDebate[y][target] == issueInMerged[y][target]) totalAgreeMerged++;
					}
				}
				writer_results.print((float)totalAgreeMerged/(numOfConfigs*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();
		
		// Print a "simple" matrix with info on the percentage of agreement between debate and Majority (all debates, all configurations).
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalAgreeMajority = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalAgreeMajority = 0;
				for (int y=0; y<numOfConfigs; y++) {
					// For every configuration.
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (numPRO[y][target] != numCON[y][target]) {
							if ( (issueInDebate[y][target] && numPRO[y][target]>numCON[y][target]) || (!issueInDebate[y][target] && numCON[y][target]>numPRO[y][target]) ) 
								totalAgreeMajority++;
						} else { // numPRO[i][l] == numCON[i][l]: in this case, by default, PRO is considered the majority.
							if (issueInDebate[y][target]) 
								totalAgreeMajority++;
						}
					}
				}
				writer_results.print((float)totalAgreeMajority/(numOfConfigs*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();
		
		// Print a "simple" matrix with info on the average number of rounds (all debates, all configurations).
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalNumRounds = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalNumRounds = 0;
				for (int y=0; y<numOfConfigs; y++) {
					// For every configuration.
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						totalNumRounds += numberOfRounds[y][target];
					}
				}
				writer_results.print((float)totalNumRounds/(numOfConfigs*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();
		
		// Print a "simple" matrix with info on the average number of no-pass rounds (all debates, all configurations).
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalNumRoundsNoPass = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalNumRoundsNoPass = 0;
				for (int y=0; y<numOfConfigs; y++) {
					// For every configuration.
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						totalNumRoundsNoPass += numberOfRoundsNoPass[y][target];
					}
				}
				writer_results.print((float)totalNumRoundsNoPass/(numOfConfigs*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();
		//////////////////////////////////////////////////////////////////////
		// 5555555555555555555555555555555555555555555555555555555555555555555
		//////////////////////////////////////////////////////////////////////	
		// Print a "rich" matrix with info on all the debates (and all the configurations) BUT ONLY ON THE FILTERED DELTA(PRO,CON) (7/3, 6/4, 5/5, 4/6, 3/7).
		// Then print 5 "simple" tables (each for one important characteristic).
		writer_results.println("***INFO_FILTERED_DEBATES***");
		writer_results.println();
		writer_results.println("EVERY_CELL_5_NUMBERS: (A)%_PRO_WINS, (B)%_AGREE_MERGED, (C)%_AGREE_MAJORITY, (D)MEAN_NUM_ROUNDS, (E)MEAN_NUM_NO-PASS");
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalPROwins = 0;
			int totalAgreeMerged = 0;
			int totalAgreeMajority = 0;
			//int totalIsIdeal;
			int totalNumRounds = 0;
			int totalNumRoundsNoPass = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalPROwins = 0;
				totalAgreeMerged = 0;
				totalAgreeMajority = 0;
				// totalIsIdeal = 0;
				totalNumRounds = 0;
				totalNumRoundsNoPass = 0;
				for (int y=20; y<70; y++) {
					// For every configuration IN FILTERED DELTA(PRO,CON).
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (issueInDebate[y][target]) totalPROwins++;
						if (issueInDebate[y][target] == issueInMerged[y][target]) totalAgreeMerged++;
						if (numPRO[y][target] != numCON[y][target]) {
							if ( (issueInDebate[y][target] && numPRO[y][target]>numCON[y][target]) || (!issueInDebate[y][target] && numCON[y][target]>numPRO[y][target]) ) 
								totalAgreeMajority++;
						} else { // numPRO[i][l] == numCON[i][l]: in this case, by default, PRO is considered the majority.
							if (issueInDebate[y][target]) 
								totalAgreeMajority++;
						}
						totalNumRounds += numberOfRounds[y][target];
						totalNumRoundsNoPass += numberOfRoundsNoPass[y][target];
					}
				}
				writer_results.print((float)totalPROwins/(50*numRepetitions) + "#" + (float)totalAgreeMerged/(50*numRepetitions) + "#" +
						(float)totalAgreeMajority/(50*numRepetitions) + "#" + //+ (float)totalIsIdeal/(50*numRepetitions) 
						(float)totalNumRounds/(50*numRepetitions) + "#" + (float)totalNumRoundsNoPass/(50*numRepetitions) + " "); 
				// 50*numRepetitions, because there are 50 configs in the filtered Delta(PRO,CON).
			}
			writer_results.println();
		}
		writer_results.println();
		
		// Print a "simple" matrix with info on the percentage of PRO wins (all FILTERED configurations).
		writer_results.println("***INFO_ALL_FILTERED_DEBATES" + " " + "PER_ELEMENT***");
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalPROwins = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalPROwins = 0;
				for (int y=20; y<70; y++) {
					// For every configuration IN FILTERED DELTA(PRO,CON).
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (issueInDebate[y][target]) totalPROwins++;
					}
				}
				writer_results.print((float)totalPROwins/(50*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();

		// Print a "simple" matrix with info on percentage of agreement between debate and Merged (all FILTERED configurations).
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalAgreeMerged = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalAgreeMerged = 0;
				for (int y=20; y<70; y++) {
					// For every configuration IN FILTERED DELTA(PRO,CON).
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (issueInDebate[y][target] == issueInMerged[y][target]) totalAgreeMerged++;
					}
				}
				writer_results.print((float)totalAgreeMerged/(50*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();

		// Print a "simple" matrix with info on the percentage of agreement between debate and Majority (all FILTERED configurations).
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalAgreeMajority = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalAgreeMajority = 0;
				for (int y=20; y<70; y++) {
					// For every configuration IN FILTERED DELTA(PRO,CON).
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (numPRO[y][target] != numCON[y][target]) {
							if ( (issueInDebate[y][target] && numPRO[y][target]>numCON[y][target]) || (!issueInDebate[y][target] && numCON[y][target]>numPRO[y][target]) ) 
								totalAgreeMajority++;
						} else { // numPRO[i][l] == numCON[i][l]: in this case, by default, PRO is considered the majority.
							if (issueInDebate[y][target]) 
								totalAgreeMajority++;
						}
					}
				}
				writer_results.print((float)totalAgreeMajority/(50*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();

		// Print a "simple" matrix with info on the average number of rounds (all FILTERED configurations).
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalNumRounds = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalNumRounds = 0;
				for (int y=20; y<70; y++) {
					// For every configuration IN FILTERED DELTA(PRO,CON).
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						totalNumRounds += numberOfRounds[y][target];
					}
				}
				writer_results.print((float)totalNumRounds/(50*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();

		// Print a "simple" matrix with info on the average number of no-pass rounds (all FILTERED configurations).
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int totalNumRoundsNoPass = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				totalNumRoundsNoPass = 0;
				for (int y=20; y<70; y++) {
					// For every configuration IN FILTERED DELTA(PRO,CON).
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						totalNumRoundsNoPass += numberOfRoundsNoPass[y][target];
					}
				}
				writer_results.print((float)totalNumRoundsNoPass/(50*numRepetitions) + " ");
			}
			writer_results.println();
		}
		writer_results.println();
		//////////////////////////////////////////////////////////////////////
		// 6666666666666666666666666666666666666666666666666666666666666666666
		//////////////////////////////////////////////////////////////////////
		// Print a "rich" matrix with info on all the debates (and all the configurations), filtering only debates where Merged==IN.
		writer_results.println("***INFO_DEBATES_(MERGED==IN)***");
		writer_results.println();
		writer_results.println("EVERY_CELL_5_NUMBERS: (A)%_PRO_WINS, (B)%_AGREE_MERGED, (C)%_AGREE_MAJORITY, (D)MEAN_NUM_ROUNDS, (E)MEAN_NUM_NO-PASS");
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int numMergedIn = 0;
			int totalPROwins = 0;
			int totalAgreeMerged = 0;
			int totalAgreeMajority = 0;
			//int totalIsIdeal;
			int totalNumRounds = 0;
			int totalNumRoundsNoPass = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				numMergedIn = 0;
				totalPROwins = 0;
				totalAgreeMerged = 0;
				totalAgreeMajority = 0;
				// totalIsIdeal = 0;
				totalNumRounds = 0;
				totalNumRoundsNoPass = 0;
				for (int y=0; y<numOfConfigs; y++) {
					// For every configuration.
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (issueInMerged[y][target]) {
							// We filter only the debates where issueInMerged == true.
							numMergedIn++;
							if (issueInDebate[y][target]) totalPROwins++;
							if (issueInDebate[y][target] == issueInMerged[y][target]) totalAgreeMerged++;
							if (numPRO[y][target] != numCON[y][target]) {
								if ( (issueInDebate[y][target] && numPRO[y][target]>numCON[y][target]) || (!issueInDebate[y][target] && numCON[y][target]>numPRO[y][target]) ) 
									totalAgreeMajority++;
							} else { // numPRO[i][l] == numCON[i][l]: in this case, by default, PRO is considered the majority.
								if (issueInDebate[y][target]) 
									totalAgreeMajority++;
							}
							totalNumRounds += numberOfRounds[y][target];
							totalNumRoundsNoPass += numberOfRoundsNoPass[y][target];
						}
					}
				}
				writer_results.print((float)totalPROwins/numMergedIn + "#" + (float)totalAgreeMerged/numMergedIn + "#" +
						(float)totalAgreeMajority/numMergedIn + "#" + //+ (float)totalIsIdeal/numMergedIn 
						(float)totalNumRounds/numMergedIn + "#" + (float)totalNumRoundsNoPass/numMergedIn + " ");
			}
			writer_results.println();
		}
		
		// Print a "rich" matrix with info on all the debates (and all the configurations), filtering only debates where Merged==Out.
		writer_results.println("***INFO_DEBATES_(MERGED==OUT)***");
		writer_results.println();
		writer_results.println("EVERY_CELL_5_NUMBERS: (A)%_PRO_WINS, (B)%_AGREE_MERGED, (C)%_AGREE_MAJORITY, (D)MEAN_NUM_ROUNDS, (E)MEAN_NUM_NO-PASS");
		writer_results.print("StrategyPRO\\StrategyCON" + " " + "1" + " " + "2A" + " " + "2B" + " " + "3A" + " " + "3B" + " " + "3C" + " " + "4A" + " " + "4B" + " " + "4C");
		writer_results.println();
		for (int w=0; w<9; w++) {
			// For every line of the final table.
			if (w==0) writer_results.print("1" + " ");
			else if (w == 1) writer_results.print("2A" + " ");
			else if (w == 2) writer_results.print("2B" + " ");
			else if (w == 3) writer_results.print("3A" + " ");
			else if (w == 4) writer_results.print("3B" + " ");
			else if (w == 5) writer_results.print("3C" + " ");
			else if (w == 6) writer_results.print("4A" + " ");
			else if (w == 7) writer_results.print("4B" + " ");
			else if (w == 8) writer_results.print("4C" + " ");
			int numMergedOut = 0;
			int totalPROwins = 0;
			int totalAgreeMerged = 0;
			int totalAgreeMajority = 0;
			//int totalIsIdeal;
			int totalNumRounds = 0;
			int totalNumRoundsNoPass = 0;
			for (int x=0; x<9; x++) {
				// For every column of the final table.
				numMergedOut = 0;
				totalPROwins = 0;
				totalAgreeMerged = 0;
				totalAgreeMajority = 0;
				// totalIsIdeal = 0;
				totalNumRounds = 0;
				totalNumRoundsNoPass = 0;
				for (int y=0; y<numOfConfigs; y++) {
					// For every configuration.
					for (int z=0; z<numRepetitions; z++) {
						// For all set of debates.
						// Consider debates of that couple (config and strategy profile).
						int target = ((9 * numRepetitions) * w) + (numRepetitions * x) + z;
						if (!issueInMerged[y][target]) {
							// We filter only the debates where issueInMerged == true.
							numMergedOut++;
							if (issueInDebate[y][target]) totalPROwins++;
							if (issueInDebate[y][target] == issueInMerged[y][target]) totalAgreeMerged++;
							if (numPRO[y][target] != numCON[y][target]) {
								if ( (issueInDebate[y][target] && numPRO[y][target]>numCON[y][target]) || (!issueInDebate[y][target] && numCON[y][target]>numPRO[y][target]) ) 
									totalAgreeMajority++;
							} else { // numPRO[i][l] == numCON[i][l]: in this case, by default, PRO is considered the majority.
								if (issueInDebate[y][target]) 
									totalAgreeMajority++;
							}
							totalNumRounds += numberOfRounds[y][target];
							totalNumRoundsNoPass += numberOfRoundsNoPass[y][target];
						}
					}
				}
				writer_results.print((float)totalPROwins/numMergedOut + "#" + (float)totalAgreeMerged/numMergedOut + "#" +
						(float)totalAgreeMajority/numMergedOut + "#" + //+ (float)totalIsIdeal/numMergedOut 
						(float)totalNumRounds/numMergedOut + "#" + (float)totalNumRoundsNoPass/numMergedOut + " ");
			}
			writer_results.println();
		}
		writer_results.println();
		//////////////////////////////////////////////////////////////////////
		// 7777777777777777777777777777777777777777777777777777777777777777777
		//////////////////////////////////////////////////////////////////////
		// Calculate the percentage of agreement between some pairs of strategy profiles.
		int line1vs1 = 0, column1vs1 = 0;
		int line2vs2 = 1, column2vs2 = 1;
		int line3Cvs3C = 5, column3Cvs3C = 5;
		int line4Cvs4C = 8, column4Cvs4C = 8;
		int agree_1vs1_2vs2 = 0;
		int agree_1vs1_3Cvs3C = 0;
		int agree_3Cvs3C_4Cvs4C = 0;
		for (int i=0; i<numOfConfigs; i++) {
			for (int j=0; j<numRepetitions; j++) {
				if (issueInDebate[i][90*line1vs1 + 10*column1vs1 + j] == issueInDebate[i][90*line2vs2 + 10*column2vs2 + j]) {
					agree_1vs1_2vs2++;
				}
				if (issueInDebate[i][90*line1vs1 + 10*column1vs1 + j] == issueInDebate[i][90*line3Cvs3C + 10*column3Cvs3C + j]) {
					agree_1vs1_3Cvs3C++;
				}
				if (issueInDebate[i][90*line3Cvs3C + 10*column3Cvs3C + j] == issueInDebate[i][90*line4Cvs4C + 10*column4Cvs4C + j]) {
					agree_3Cvs3C_4Cvs4C++;
				}
			}
		}
		writer_results.println("AGREEMENT_1vs1_WITH_2vs2 =" + (float)agree_1vs1_2vs2/(numOfConfigs*numRepetitions));
		writer_results.println("AGREEMENT_1vs1_WITH_3Cvs3C =" + (float)agree_1vs1_3Cvs3C/(numOfConfigs*numRepetitions));
		writer_results.println("AGREEMENT_3Cvs3C_WITH_4Cvs4C =" + (float)agree_3Cvs3C_4Cvs4C/(numOfConfigs*numRepetitions));

		// Close the files.
		writer_configs.close();
		writer_results.close();
	}
	
}
