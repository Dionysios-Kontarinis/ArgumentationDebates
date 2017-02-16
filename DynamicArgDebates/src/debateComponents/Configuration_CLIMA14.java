package debateComponents;

/**
 * Class used in the CLIMA'14 work.
 * @author dennis
 *
 */
public class Configuration_CLIMA14 extends Configuration {

	/**
	 * Constructor (1/2).
	 * Here only attacks are considered (no supports).
	 * We first generate a "virtual GB", by choosing:
	 * (1) whether the virtual GB is a tree or a graph,
	 * (2) whether the virtual GB is "Dummy" (every pair of arguments gives rise to an attack), or not.
	 * We don't care whether the virtual GB will contain cycles, or not.
	 * Important: The virtual GB contains some attacks with weight 0 (addable) and some attacks with weight +10000 (fixed).
	 * Then, from the virtual GB, a number of Agents are directly generated (as there are no Agent Types).
	 * Validity check: If the percentage of PRO agents is p, then necessarily 30% < p < 70%.
	 */
	public Configuration_CLIMA14 (boolean isGBTree, boolean isGBDummy) {
		
		super();
		
		boolean validConfig = false;
		// A configuration is valid iff the cardinalities of the PRO and CON groups are similar.
		while (!validConfig) {
			// First, we generate a Gameboard. 
			// If the first parameter is set to true, then it's a tree (else it's a graph).
			// If the second parameter is set to true, then all possible attacks are generated (all with weight 0); else only some attacks are generated,
			// and they all have weight either 0, or +10000.
			gb = new Gameboard(isGBTree, isGBDummy, 0); // parameter '0': the CLIMA'14 work.
			
			// Second, we generate all the agents.
			agents = Agent.generateAgents(gb);
			// And we compute the values of numPRO, numCON.
			numPRO = 0;
			numCON = 0;
			for (int i=0; i<agents.size(); i++) {
				if (agents.get(i).team.equals("PRO")) numPRO++;
				else if (agents.get(i).team.equals("CON")) numCON++;
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////////
			// Filter-out the cases where a vast majority of agents, (almost) all belong into PRO (or CON).//
			/////////////////////////////////////////////////////////////////////////////////////////////////
			
			float percentagePRO = (float)numPRO/agents.size();
			if (percentagePRO > 0.3 && percentagePRO < 0.7) {
				validConfig = true;
				System.out.println("Found a valid config." );
				System.out.println();
			} else {
				System.out.println("There are " + numPRO + " PRO agents. Going for another config." );
				System.out.println();
			}
		}	
	}
	
	/**
	 * Constructor (2/2).
	 * Same as the previous constructor, but with a different validity check.
	 * Validity check: The number of PRO agents must be equal to "numPROwanted".
	 */
	public Configuration_CLIMA14 (boolean isGBTree, boolean isGBDummy, int numPROwanted) {
		
		super();
		
		boolean validConfig = false;
		// A configuration is valid iff the cardinalities of the PRO and CON groups are similar.
		while (!validConfig) {
			// First, we generate a Gameboard. 
			// If the first parameter is set to true, then it is a tree (else it is a graph).
			// If the second parameter is set to false, then some attacks are generated (but not all possible attacks),
			// and they all have weight 0, or +10000.
			gb = new Gameboard(isGBTree, isGBDummy, 0); // parameter '0': the CLIMA'14 work.
			
			// Second, we generate all the agents.
			agents = Agent.generateAgents(gb);
			// And we compute the values of numPRO, numCON.
			numPRO = 0;
			numCON = 0;
			for (int i=0; i<agents.size(); i++) {
				if (agents.get(i).team.equals("PRO")) numPRO++;
				else if (agents.get(i).team.equals("CON")) numCON++;
			}
			
			// Validity check: Did we get the wished number of PRO?
			if (numPROwanted == numPRO) {
				validConfig = true;
				System.out.println("Found a valid config." );
				System.out.println();
			} else {
				System.out.println("There are " + numPRO + " PRO agents. Going for another config." );
				//System.out.println("There are " + numCON + " CON agents. Going for another config." );
				//System.exit(0);
				System.out.println();
			}
		}	
	}
	
}
