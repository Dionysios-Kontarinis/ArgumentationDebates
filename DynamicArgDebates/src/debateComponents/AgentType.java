package debateComponents;

import java.util.ArrayList;

/**
 * This class is used in the context of our work analyzing agent behavior (such as lying & hiding) during debates.
 * An object of this class represents a set of agents with similar beliefs.
 * This means that, for any particular agent type, there are two types of elements:
 * A first set of elements (arguments, attacks and supports) which the agents of that type find valid (a priori!),
 * and a second set of elements (arguments, attacks and supports) which the agents of that type find invalid (a priori!).
 * A future extension would be to consider a third set of elements, on which there is no (a priori) agreement among the agents of that type. 
 * Now, specific agents, may deviate from their type's viewpoint in two ways:
 * (1) They may consider / find valid some elements that their type does not.
 * (2) They may find invalid some elements that their type finds valid.
 * Class used in the lying & hiding work.
 * @author dennis
 *
 */
public class AgentType {
	
	///////////////////////
	// Static attributes //
	///////////////////////
	
	// The number of agent types we generate in the simulations depends on these two attributes.
	public static int MIN_NUM_TYPES = 3;
	public static int MAX_NUM_TYPES = 3;
	
	// The number of agents (per type) we generate in the simulations depends on these two attributes.
	public static int MIN_NUM_AGENTS_PER_TYPE = 4;
	public static int MAX_NUM_AGENTS_PER_TYPE = 4;
	
	// The percentage (%) of disagreement between an agent and its type that we generate in the simulations depends on these two attributes.
	// Here, we refer to disagreement on arguments, attacks and supports.
	public static int MIN_DISAGREEMENT_PRCNTG = 10;
	public static int MAX_DISAGREEMENT_PRCNTG = 10;
	
	///////////////////////////
	// Non-static attributes //
	///////////////////////////
	
	/**
	 * Every agent type has an essential element: a Gameboard.
	 * That Gameboard contains all the elements (arguments, attacks and supports) that the agents of this specific type consider (a priori!),
	 * as well as those that they do not consider (a priori!).
	 * The two kinds of elements can be identified by their weights (O, or 1).  
	 */
	public Gameboard typeGB;
	
	
	////////////////////
	// Static methods //
	////////////////////
	
	/**
	 * This method creates a random number of agent types, according to a specific Gameboard "gb".
	 * To do this, it calls the constructor of this class multiple times.
	 * @return
	 */
	public static ArrayList<AgentType> generateAgentTypes(Gameboard gb) {
		ArrayList<AgentType> agentTypes = new ArrayList<AgentType>();
		// Find how many agent types we'll generate.
		int numberOfAgentTypes = MIN_NUM_TYPES + (int) (Math.random() * (MAX_NUM_TYPES - MIN_NUM_TYPES + 1));
		// Generate that number of agent types.
		for (int i=0; i<numberOfAgentTypes; i++) {
			agentTypes.add(new AgentType(gb));
		}
		// Return the generated agent types.
		return agentTypes;
	}
	
	
	////////////////////////
	// Non-static methods //
	////////////////////////
	
	/**
	 * Constructor (1/1).
	 * It creates a single AgentType object which is based on a specific Gameboard ("gb").
	 * It calls a constructor of the "Gameboard" class, which generates a random variation of the "gb".
	 * @param referenceGB
	 */
	public AgentType(Gameboard gb) {
		typeGB = new Gameboard(gb, 1); // The parameter '1' is used here in order to call the correct version of the Gameboard constructor.
	}
	
}
