package debateComponents;

import java.util.ArrayList;

/**
 * Class used in the lying & hiding work.
 * @author dennis
 *
 */
public class Configuration_EUMAS15 extends Configuration {

	/**
	 * The array containing all the types of the agents in the debate.
	 */
	public ArrayList<AgentType> agentTypes;
	/**
	 * The maximum number of rounds (in each round, each user plays once) that a debate may consist of.
	 */
	public int MAX_NUM_ROUNDS;
	
	/**
	 * Constructor (1/1).
	 * It first generates a "virtual GB", and we can choose:
	 * (1) whether the virtual GB will be a tree or a graph,
	 * (2) whether the virtual GB will be "Dummy" (every pair of arguments gives rise to an attack and to a support), or not.
	 * (3) whether the virtual GB will be necessarily acyclic, or not.
	 *     We note that a "QUAD" argument evaluation of a system is possible iff that system is acyclic.
	 * Then, from the generated virtual GB, a number of Agent Types are generated.
	 * Then, from each Agent Type, a number of Agents are generated. 
	 * TO DO: Implement the acyclicity check (it's not necessary, as long as isGBTree is always set to "true").
	 */
	public Configuration_EUMAS15 (boolean isGBTree, boolean isGBDummy, boolean isGBAcyclic) {
		
		super();
		
		boolean validConfig = false;
		// A configuration is valid iff the virtual GB is acyclic.
		while (!validConfig) {
			// First, we generate a Gameboard. 
			// If the first parameter is set to true, then it's a tree (else it's a graph).
			// If the second parameter is set to true, then all possible attacks and supports are generated (all with weight 0); else only some attacks and supports are generated,
			// and they all have weight 0.
			gb = new Gameboard(isGBTree, isGBDummy, 1); // parameter '1': the lying & hiding work.
			
			// If isGBAcyclic == true, then we must ensure that the GB has no cycles.
//			if (isGBAcyclic) validConfig = ... ; // Correctly implement this check (needed when the GB is a graph).
			validConfig = true; // The easy solution (it's acceptable, as long as we know that this constructor will always be called with isGBTree set to "true").
		}
		
		// Second, we generate all the agent types.
		agentTypes = AgentType.generateAgentTypes(gb);	
		
		// Third, we generate all the agents from the types we've got.
		agents = Agent.generateAgents(agentTypes); 
		// And we compute the values of numPRO, numCON.
		for (int i=0; i<agents.size(); i++) {
			if (agents.get(i).team.equals("PRO")) numPRO++;
			else if (agents.get(i).team.equals("CON")) numCON++;
		}
		
		// Fourth, we set the maximum number of rounds to be equal to the number of relations on the (virtual) GB.
		MAX_NUM_ROUNDS = gb.attacks.size() + gb.supports.size();
	}
	
}
