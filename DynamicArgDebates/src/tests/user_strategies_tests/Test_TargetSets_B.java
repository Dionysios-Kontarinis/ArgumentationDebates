package tests.user_strategies_tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import debateComponents.Configuration;
import debateComponents.Configuration_CLIMA14;
import debateComponents.Gameboard;

/**
 * This class is used in order to test, in many different configurations, the composition of target sets.
 * It also tests the ordering of the target sets (from the smallest to the biggest).
 * @author dennis
 *
 */
public class Test_TargetSets_B {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		// *******************************************************************************
		// ***** INITIALIZE THE FRAMEWORK WHERE A NUMBER OF DEBATES WILL TAKE PLACE. ***** 
		// *******************************************************************************
		
		// We set the value of numOfConfigs, according to the number of experiments we wish to make.
		final int numOfConfigs = 100;
	
		int positiveInitIssues = 0;
		int negativeInitIssues = 0;
		
		int numTSets = 0;
		int sizeTSets = 0;
		
//		PrintWriter writer_configs = new PrintWriter("debate_configs.txt", "UTF-8");
		//PrintWriter writer_results = new PrintWriter("debate_results.txt", "UTF-8");
		PrintWriter writer_tsets = new PrintWriter("targetSets.txt", "UTF-8");
		
		for (int i=0; i<numOfConfigs; i++) {
			/////////////////////////////////////////////////////  Initializing the elements of the debate.
			
			Configuration_CLIMA14 config = new Configuration_CLIMA14(true, false);
			//config.printAllInfo(null);
			//config.printAllInfo(writer_configs);
			System.out.println("===============  Config " + i + "  =================");
			//writer_tsets.println("Config " + i);
			
			Gameboard gb = config.gb;
			gb.printGB(null);
			gb.printTargetSets(null);
			numTSets += gb.targetSets.size();
			for (int j=0; j<gb.targetSets.size(); j++) {
				sizeTSets += gb.targetSets.get(j).size();
			}
			
			//gb.printTargetSets(config, writer_tsets);
			
			if (gb.statusIssue) positiveInitIssues++;
			else negativeInitIssues++;
			
//			System.out.println();
//			System.out.println("ORDERED SETS:");
//			ArrayList<ArrayList<Attack>> orderedTSets = config.agents[0].orderTSetsBySize(gb);
//			for (int j=0; j<orderedTSets.size(); j++) {
//				ArrayList<Attack> tset = orderedTSets.get(j);
//				System.out.println("Target set " + j + " :");
//				for (int k=0; k<tset.size(); k++) {
//					tset.get(k).printAttack(null);
//					System.out.println();
//				}
//			}

		} // end-for (every config)
		
		writer_tsets.close();
		
		System.out.println();
		System.out.println("There were " + positiveInitIssues + " positiveInitIssues.");
		System.out.println("There were " + negativeInitIssues + " negativeInitIssues.");
		System.out.println();
		System.out.println("mean number of tsets per config = " + (double)numTSets/numOfConfigs);
		System.out.println("mean number of tset size = " + (double)sizeTSets/numTSets);
		
	}
}
