package tests.user_behavior_tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import debateComponents.Agent;
import debateComponents.Configuration_EUMAS15;
import debateComponents.Gameboard;
import debateComponents.Move;

public class Tests_EUMAS15 {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		/**
		 * The number of debates we'll perform.
		 * For the EUMAS'15 work we had performed 100,000 debates.
		 */
		final int numberOfDebates = 100000;
		
		// The following numbers are useful statistics, and we compute them for a number of debates.
		int liarNumberOfTruePositives = 0;
		int liarNumberOfFalsePositives = 0;
		int liarNumberOfTrueNegatives = 0;
		int liarNumberOfFalseNegatives = 0;
		
		int hiderNumberOfTruePositives = 0;
		int hiderNumberOfFalsePositives = 0;
		int hiderNumberOfTrueNegatives = 0;
		int hiderNumberOfFalseNegatives = 0;
		
		int maliciousNumberOfTruePositives = 0;
		int maliciousNumberOfFalsePositives = 0;
		int maliciousNumberOfTrueNegatives = 0;
		int maliciousNumberOfFalseNegatives = 0;
		
		/**
		 * All the debate's results will be summarized in this text file.
		 */
		PrintWriter results_file = new PrintWriter("results_file.txt", "UTF-8");
		
		
		// Launch a number of debates, the one after the other.
		for (int h=0; h<numberOfDebates; h++) {
		
			// Create a Configuration_EUMAS15 object. 
			Configuration_EUMAS15 config = new Configuration_EUMAS15(true, false, true);
			
//			System.out.println("**********************************************************************************");
//			// Print all agent types.
//			for (int i=0; i<config.agentTypes.size(); i++) {
//				System.out.println("AGENT TYPE-" + i + " GAMEBOARD");
//				config.agentTypes.get(i).typeGB.printGB(null);
//			}
//			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//			// Print all agents.
//			for (int i=0; i<config.agents.size(); i++) {
//				//System.out.println("Agent Honesty Type : " + config.agents.get(i).);
//				System.out.println("AGENT " + i + " GAMEBOARD");
//				config.agents.get(i).agentGB.printGB(null);
//			}
//			System.out.println("**********************************************************************************");
	
			Gameboard gb = config.gb;
			Move mv;
			Agent agToPlay = null;
			int numConseqPasses = 0;
			
			/////////////////////////////
			// SINGLE DEBATE BEGINNING //
			/////////////////////////////
			
			for (int i=1; i<=config.MAX_NUM_ROUNDS && numConseqPasses < config.agents.size() ; i++) {
//				System.out.println("ROUND " + i + ": ");
//				System.out.println("==========");
				for (int j=0; j<config.agents.size(); j++) {
//					System.out.println("AGENT " + j + " PLAYS:");
					agToPlay = config.agents.get(j);
					mv = agToPlay.strategyNumericalEval(gb);
					if (mv != null) {
						gb.playMoveOnGB(mv.relationOfMove, agToPlay, mv.polarity, true);
						numConseqPasses = 0;
					} else {
						numConseqPasses++;
					}
				}
			}
			
			///////////////////////
			// SINGLE DEBATE END //
			///////////////////////
			
			
	//		// PRINT INFO ON THE AGENTS.
	//		System.out.println();
	//		for (int i=0; i<config.agents.size(); i++) {
	//			System.out.println("Info for agent " + i + ":");
	//			System.out.println("AGENT BEHAVIOUR (based on the agent's beliefs):");
	//			System.out.println("The agent has played in total " + config.agents.get(i).numberOfMovesPlayed + " moves.");
	//			System.out.println("The agent has played " + config.agents.get(i).numberOfHonestPassesMade + " honest passes.");
	//			System.out.println("The agent has played " + config.agents.get(i).numberOfDishonestPassesMade + " dishonest passes.");
	//			System.out.println("The agent has played " + config.agents.get(i).liesMade.size() + " lies.");
	//			System.out.println("AGENT PROFILE (based on the agent's locutions & agent types):");
	//			System.out.println("Opinionatedness of agent on issue = " + config.agents.get(i).computeOpinionatednessIssue());
	//			System.out.println("Classifiability of ag" + i + " = " + config.agents.get(i).computeAgentClassifiability(gb, config.agentTypes));
	//			System.out.println();
	//			//System.out.println(config.agents.get(i).computeOpinionatednessIssue());
	//		}
	//		// DEBUGGING OF THE TOPICS.
	//		System.out.print("Topics of argument 0: ");
	//		for (int i=0; i<config.gb.arguments.get(0).topicsOfArg.size(); i++) {
	//			System.out.println(config.gb.arguments.get(0).topicsOfArg.get(i));
	//		}
	//		System.out.println("Topics of the first attack: ");
	//		for (int i=0; i<config.gb.attacks.get(0).topicsOfRel.size(); i++) {
	//			System.out.println(config.gb.attacks.get(0).topicsOfRel.get(i));
	//		}
	//		System.out.print("Expertise of agent 0: ");
	//		for (int i=0; i<config.agents.get(0).agentExpertise.size(); i++) {
	//			System.out.println(config.agents.get(0).agentExpertise.get(i));
	//		}
	//		System.out.println();
			
						
			// The average values of activity, opinionatedness, and classifiability of the users (computed next), in a single debate.
			// Since these attributes have values in [0,1], the averages also have values in [0,1]. 
			double avgActivity = 0;
			double avgOpinionatednessIssue = 0;
			double avgClassifiability = 0;
			// The average percentages of lies and hides of the users (computed next).
			double avgPrcLies = 0;
			double avgPrcHides = 0;
			
			Agent currAg;
			for (int i=0; i<config.agents.size(); i++) {
				currAg = config.agents.get(i);
				avgActivity += currAg.computeActivity();
				avgOpinionatednessIssue += currAg.computeOpinionatednessIssue();	
				avgClassifiability += currAg.computeClassifiability(config.gb, config.agentTypes);
				avgPrcLies += (double)(currAg.liesMade.size()) / currAg.numberOfMovesPlayed;
				avgPrcHides += (double)(currAg.numberOfDishonestPassesMade) / currAg.numberOfMovesPlayed;	
			}
			avgActivity = avgActivity / config.agents.size();
			avgOpinionatednessIssue = avgOpinionatednessIssue / config.agents.size();
			avgClassifiability = avgClassifiability / config.agents.size();
			avgPrcLies = avgPrcLies / config.agents.size();
			avgPrcHides = avgPrcHides / config.agents.size();
			
			
//			System.out.println();
//			System.out.println("==========================================================================================");
//			System.out.println("Agent # : Acti | Dacti || Opin | Dopin || Clas | Dclas  ***  Lie% | DLie% || Hid% | DHid%"); // Mal%" );
//			System.out.println("==========================================================================================");
//			for (int i=0; i<config.agents.size(); i++) {
//				currAg = config.agents.get(i);
//				
//				System.out.println("Agent " + i + " : " +
//										String.format("%.2f", currAg.computeActivity()) + " | " +
//										String.format("%+.2f", currAg.computeActivity() - avgActivity) + " || " +
//	//									String.format("%.2f", currAg.computeImpactIssue()) + " | " +
//										String.format("%.2f", currAg.computeOpinionatednessIssue()) + " | " +
//										String.format("%+.2f", currAg.computeOpinionatednessIssue() - avgOpinionatednessIssue) + " || " +
//										String.format("%.2f", currAg.computeClassifiability(config.gb, config.agentTypes)) + " | " +
//										String.format("%+.2f", currAg.computeClassifiability(config.gb, config.agentTypes) - avgClassifiability) + "  ***  " +
//										String.format("%.2f", (double)(currAg.liesMade.size()) / currAg.numberOfMovesPlayed) + " | " +
//										String.format("%+.2f", ((double)(currAg.liesMade.size()) / currAg.numberOfMovesPlayed) - avgPrcLies) + " || " +
//										String.format("%.2f", (double)(currAg.numberOfDishonestPassesMade) / currAg.numberOfMovesPlayed) + " | " +
//										String.format("%+.2f", ((double)(currAg.numberOfDishonestPassesMade) / currAg.numberOfMovesPlayed) - avgPrcHides)
//	//									String.format("%.2f", (double)((currAg.liesMade.size() + currAg.numberOfDishonestPassesMade)) / currAg.numberOfMovesPlayed)									
//				);
//			}	
			
			///////////////////////////////////////////
			// SUMMARIZE THE RESULTS IN A TEXT FILE. //
			///////////////////////////////////////////
			
			for (int i=0; i<config.agents.size(); i++) {
				currAg = config.agents.get(i);
				double currAgActivity = currAg.computeActivity();
				double currAgOpinionatednessIssue = currAg.computeOpinionatednessIssue();
				double currAgClassifiability = currAg.computeClassifiability(config.gb, config.agentTypes);
				
				////////////////////////////////////////
				// "Observable" features of a debate. //
				////////////////////////////////////////
				
				// Columns 1-2: Agent Activity | (Agent Activity - Avg Agent Activity)
				// Columns 3-4: Agent Opinionat. | (Agent Opinionat. - Avg Agent Opinionat.)
				// Columns 5-6: Agent Classif. | (Agent Classif. - Avg Agent Classif.)				
				results_file.printf("%.6f", currAgActivity); // value in [0,1]
				results_file.print(" ");
				results_file.printf("%.6f", (currAgActivity - avgActivity)); // value in [-1,1]
				results_file.print(" ");
				results_file.printf("%.6f", currAgOpinionatednessIssue); // value in [0,1]
				results_file.print(" ");
				results_file.printf("%.6f", (currAgOpinionatednessIssue - avgOpinionatednessIssue)); // value in [-1,1]
				results_file.print(" ");
				results_file.printf("%.6f", currAgClassifiability); // value in [0,1]
				results_file.print(" ");
				results_file.printf("%.6f", (currAgClassifiability - avgClassifiability)); // value in [-1,1]
				results_file.print(" ");
				
				////////////////////////////////////////////
				// "Non-observable" features of a debate. //
				////////////////////////////////////////////
				
				// Columns 7-10: isLiar {0,1} | hasLied {0,1} | # of Agent Lies {0,1,2,...} | hasBeenAboveAvgLiar {0,1} (% of Agent Lies >= Avg % of Agents Lies)
				if (currAg.liesBudget == 0) results_file.print(0);
				else results_file.print(1);
				results_file.print(" ");
				if (currAg.liesMade.size() == 0) results_file.print(0);
				else results_file.print(1);
				results_file.print(" ");
				results_file.print(currAg.liesMade.size());
				results_file.print(" ");
				if (((double)(currAg.liesMade.size()) / currAg.numberOfMovesPlayed) - avgPrcLies >= 0) {
					results_file.print(1);
				} else results_file.print(0);
				results_file.print(" ");
				
				// Columns 11-14: isHider {0,1} | hasHidden {0,1} | # of Agent Hides {0,1,2,...} | hasBeenAboveAvgHider {0,1} (% of Agent Hides >= Avg % of Agents Hides)
				if (currAg.dishonestPassesBudget == 0) results_file.print(0);
				else results_file.print(1);
				results_file.print(" ");				
				if (currAg.numberOfDishonestPassesMade == 0) results_file.print(0);
				else results_file.print(1);
				results_file.print(" ");
				results_file.print(currAg.numberOfDishonestPassesMade);
				results_file.print(" ");
				if (((double)(currAg.numberOfDishonestPassesMade) / currAg.numberOfMovesPlayed) - avgPrcHides >= 0) {
					results_file.print(1);
				} else results_file.print(0);
				// End of current row.
				results_file.println();
			}
			
			////////////////////////////////
			// HYPOTHESES TEST (EUMAS'15) //
			////////////////////////////////
						
			for (int i=0; i<config.agents.size(); i++) {
				currAg = config.agents.get(i);
			
				// Hypothesis on Liars: "High Activity + High Opinionatedness + Low Classifiability ==> High Liar" 
				
				if ((currAg.computeActivity() - avgActivity >= 0) &&
					(currAg.computeOpinionatednessIssue() - avgOpinionatednessIssue >= 0) &&
					(currAg.computeClassifiability(config.gb, config.agentTypes) - avgClassifiability) <= 0) {
					// The hypothesis on liars says that currAg is a liar!
					if ( ((double)(currAg.liesMade.size()) / currAg.numberOfMovesPlayed) - avgPrcLies >= 0 )
						liarNumberOfTruePositives++;
					else
						liarNumberOfFalsePositives++;
				} else {
					// The (inverse of the) hypothesis on liars says that currAg is not a liar.
					if ( ((double)(currAg.liesMade.size()) / currAg.numberOfMovesPlayed) - avgPrcLies >= 0 )
						liarNumberOfFalseNegatives++;
					else
						liarNumberOfTrueNegatives++;
				}	
			
				// Hypothesis on Hiders: "Low Activity + High Opininatedness ==> High Hider"
			
				if ((currAg.computeActivity() - avgActivity <= 0) &&
					(currAg.computeOpinionatednessIssue() - avgOpinionatednessIssue >= 0)) {
					// The hypothesis on hiders says that currAg is a hider!
					if ( ((double)(currAg.numberOfDishonestPassesMade) / currAg.numberOfMovesPlayed) - avgPrcHides >= 0)
						hiderNumberOfTruePositives++;
					else
						hiderNumberOfFalsePositives++;
				} else {
					// The (inverse of the) hypothesis on hiders says that currAg is not a hider.
					if ( ((double)(currAg.numberOfDishonestPassesMade) / currAg.numberOfMovesPlayed) - avgPrcHides >= 0)
						hiderNumberOfFalseNegatives++;
					else
						hiderNumberOfTrueNegatives++;
				}	
			
//				// Hypothesis on Malicious: not stated in the EUMAS'15 paper, it is implicit, but quite straightforward.
//				
//				if ( (  (currAg.computeActivity() - avgActivity >= 0) &&
//						(currAg.computeOpinionatednessIssue() - avgOpinionatednessIssue >= 0) &&
//						(currAg.computeClassifiability(config.gb, config.agentTypes) - avgClassifiability <= 0)
//					 ) ||
//					 (  (currAg.computeActivity() - avgActivity <= 0) &&
//						(currAg.computeOpinionatednessIssue() - avgOpinionatednessIssue >= 0)
//				     )     ) {
//					// We estimate him as either liar, or hider.
//					if (   (((double)(currAg.liesMade.size()) / currAg.numberOfMovesPlayed) - avgPrcLies >= 0)
//							   ||
//						   (((double)(currAg.numberOfDishonestPassesMade) / currAg.numberOfMovesPlayed) - avgPrcHides >= 0)  ) {
//						// He is indeed liar or hider.
//						maliciousNumberOfTruePositives++;
//					} else {
//						maliciousNumberOfFalsePositives++;
//					}						
//				} else {
//					// We check him as honest.
//					if (   (((double)(currAg.liesMade.size()) / currAg.numberOfMovesPlayed) - avgPrcLies >= 0)
//							   ||
//						   (((double)(currAg.numberOfDishonestPassesMade) / currAg.numberOfMovesPlayed) - avgPrcHides >= 0)  ) {
//						maliciousNumberOfFalseNegatives++;
//					} else {
//						maliciousNumberOfTrueNegatives++;
//					}	
//				}
				
			} // for-all-agents in a debate.
		}	// for-all-debates.
		
		
		// Close the text file where we have summarized the debates' results.
		results_file.close();
		
		
		///////////////////////////////////////////////////////
		// EVALUATION OF HYPOTHESES (after multiple debates) //
		///////////////////////////////////////////////////////
		
		System.out.println();
		System.out.println("Number of true positive liar predictions = " 		+ liarNumberOfTruePositives);
		System.out.println("Number of false positive liar predictions = " 		+ liarNumberOfFalsePositives);
		System.out.println("Number of true negative liar predictions = " 		+ liarNumberOfTrueNegatives);
		System.out.println("Number of false negative liar predictions = " 		+ liarNumberOfFalseNegatives);
		System.out.println();
		System.out.println("Number of true positive hider predictions = " 		+ hiderNumberOfTruePositives);
		System.out.println("Number of false positive hider predictions = " 		+ hiderNumberOfFalsePositives);
		System.out.println("Number of true negative hider predictions = " 		+ hiderNumberOfTrueNegatives);
		System.out.println("Number of false negative hider predictions = " 		+ hiderNumberOfFalseNegatives);
		System.out.println();
//		System.out.println("Number of true positive malicious predictions = " 		+ maliciousNumberOfTruePositives);
//		System.out.println("Number of false positive malicious predictions = " 		+ maliciousNumberOfFalsePositives);
//		System.out.println("Number of true negative malicious predictions = " 		+ maliciousNumberOfTrueNegatives);
//		System.out.println("Number of false negative malicious predictions = " 		+ maliciousNumberOfFalseNegatives);
		
	}
}
