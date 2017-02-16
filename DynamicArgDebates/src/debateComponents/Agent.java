package debateComponents;

import java.util.ArrayList;


/**
 * This class contains the definition of a debating agent.
 * @author dennis
 *
 */
public class Agent {
	
	
	///////////////////////
	// Static attributes //
	///////////////////////
	
	// Useful attributes when we want to "directly" constrain the number of generated users (CLIMA'14 work),
	// for example when we don't define any agent types (and therefore we don't use their constraints on agent numbers).
	public static final int MIN_NUM_AGENTS = 10;
	public static final int MAX_NUM_AGENTS = 10;
	
	// Regarding user expertise.
	public static final int MIN_EXP_PER_AGENT = 1;
	public static final int MAX_EXP_PER_AGENT = 1;

	// These attributes are used to separate users into "PRO" and "CON" (also, a third group could be defined, containing the "neutral" users),
	// when numerical argument evaluation is used.
	public static final double MIN_THRESHOLD = 0.5;
	public static final double MAX_THRESHOLD = 0.5;

	// This attribute is used to define the behavior of a user towards the issue.
	public static final double PROBABILITY_TRIES_ACC_OR_REJ_ISSUE = 1.0;
	
	///////////////////////////
	// Non-static attributes //
	///////////////////////////
	
	/**
	 * The name of the agent (eg. ag1, ag2, ..., agN).
	 */
	public String agentName;
	/**
	 * The topics of expertise of the agent.
	 */
	public ArrayList<String> agentExpertise;
	/**
	 * The agent's Gameboard represents his viewpoint over the debate.
	 */
	public Gameboard agentGB;
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// If the agents can vote (either positively or negatively) & also lie, then the following three attributes are insufficient to know what the agents have played.
	// (Remember: In the CLIMA work agents cannot lie; and in our work on lying & hiding, agents cannot vote negatively).
	// In the (quite complex) case where agents can do both, we should obligatorily keep track of the polarities of the agents' votes.
	/**
	 * The arguments (belonging to a Gameboard object) on which the agent has already voted during the debate.
	 */
	public ArrayList<Argument> playedArgs;
	/**
	 * The attacks (belonging to a Gameboard object) on which the agent has already voted during the debate.
	 */
	public ArrayList<Attack> playedAtts;
	/**
	 * The supports (belonging to a Gameboard object) on which the agent has already voted during the debate.
	 */
	public ArrayList<Support> playedSupps;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// The following attributes determine the user's goals, and some of the means which he may use in order to achieve these goals.
	
	/**
	 * This determines whether the user has the goal of making the issue accepted / rejected, or not.  
	 */
	public boolean triesAcceptOrRejectIssue;
	/**
	 * The agent's team is set into "PRO" or "CON", upon the agent's creation.
	 */
	public String team;
	
	// Attributes defining the agent's lying behavior.
	public int liesBudget;
	public ArrayList<BinaryRelation> liesMade;
	// Attributes defining the agent's hiding behavior. There are 2 types of hiding:
	// Hiding type A: play pass moves instead of harmful moves;
	// If the attribute "dishonestPassesBudget" is set to be greater or equal to the total number of elements, then the agent can practically hide at will.
	public int dishonestPassesBudget;
	public int numberOfDishonestPassesMade;
//	// Hiding type B: completely hide some moves (not used in our work; it's a possible extension).
//	public int hidingsBudget;
//	public ArrayList<BinaryRelation> hidingsMade;
	// Attributes useful for profiling the agent.
	public int numberOfMovesPlayed;
	public int numberOfHonestPassesMade;
	public ArrayList<Double> effectsOfMovesOnIssue;
	public int numberOfMovesForIssue;
	public int numberOfMovesAgainstIssue;
	
	//////////////////////
	// Static functions //
	//////////////////////
	
	/**
	 * This method generates a random number, and then it calls (that many times) a Constructor of the Agent class.
	 * All the generated agents will be in accordance with the Gameboard which is passed as parameter.
	 * Each of these agents agrees with every fixed attack of the Gameboard, and has a "freedom of choice" as far as the other attacks are concerned.
	 * Method used in the CLIMA'14 work.
	 */
	public static ArrayList<Agent> generateAgents(Gameboard gb) {
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		// Randomly choose how many agents will be generated.
		int numberOfAgents = MIN_NUM_AGENTS + (int) (Math.random() * (MAX_NUM_AGENTS - MIN_NUM_AGENTS + 1));
		
		// Generate the agents (Constructor call).
		for (int i=0; i<numberOfAgents; i++) {
			agents.add(new Agent(i, gb));
		}
		return agents;
	}
	
	/**
	 * This method instantiates a number of agents. 
	 * First, we decide how many agents of each AgentType will be generated (the agent types are passed as a parameter).
	 * Then, we create the same number of agents for every different type.
	 * Method used in the lying & hiding work.
	 */
	public static ArrayList<Agent> generateAgents(ArrayList<AgentType> types) {
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		// Randomly choose how many agents will be generated per type.
		int numberOfAgentsPerType = AgentType.MIN_NUM_AGENTS_PER_TYPE +
				(int) (Math.random() * (AgentType.MAX_NUM_AGENTS_PER_TYPE - AgentType.MIN_NUM_AGENTS_PER_TYPE + 1));
		
		// Generate the agents.
		for (int i=0; i<types.size(); i++) {
			for (int j=0; j<numberOfAgentsPerType; j++) {
//				agents.add(new Agent((i*numberOfAgentsPerType)+j, types.get(i), numericalEvalation,0)); // for testing purposes...
//				agents.add(new Agent((i*numberOfAgentsPerType)+j, types.get(i), numericalEvalation,3)); // for testing purposes...
				agents.add(new Agent((i*numberOfAgentsPerType)+j, types.get(i), j));
			}
		}
		return agents;
	}
	
	/**
	 * This method returns the merged WAS, i.e. the system which is computed if all agents vote on all relations (attacks and supports).
	 * Method used in the CLIMA'14 work.
	 */
	public static Gameboard computeMergedWAS(Configuration config) {
		// Create an empty Gameboard.
		Gameboard merged = new Gameboard(false, true, 0); // isTree: false, isDummy: true
		for (int i=0; i<config.agents.size(); i++) {
			Agent currAg = config.agents.get(i);
			// Every agent votes on every attack.			
			for (int j=0; j<currAg.agentGB.attacks.size(); j++) {
				Attack currAtt = currAg.agentGB.attacks.get(j);
				// The polarity of the vote must be "truthful", therefore:
				boolean isPolarityPositive;
				if (currAtt.weight > 0) isPolarityPositive = true;
				else isPolarityPositive = false;
				merged.playMoveOnGB(currAtt, currAg, isPolarityPositive, true);
			}
			// Every agent votes on every support.			
			for (int j=0; j<currAg.agentGB.supports.size(); j++) {
				Support currSupp = currAg.agentGB.supports.get(j);
				// The polarity of the vote must be "truthful", therefore:
				boolean isPolarityPositive;
				if (currSupp.weight > 0) isPolarityPositive = true;
				else isPolarityPositive = false;
				merged.playMoveOnGB(currSupp, currAg, isPolarityPositive, true);
			}
		}	
		return merged;
	}
	
	
	///////////////////////////
	//  Non-static functions //
	///////////////////////////
		
	/**
	 * Constructor (1/2).
	 * This Constructor is used when we generate a set of users from the same "mould" (GB).
	 * It calls the Gameboard Constructor which creates a new GB that
	 * "agrees" on all the fixed attacks (and fixed non-attacks, of course) of the parameter GB.
	 * Also, if an attack on the parameter GB has weight 0, then there is a 50% probability it will get 0, and a 50% probability it will get 1.
	 * Constructor used in the CLIMA'14 work.
	 */
	public Agent(int num, Gameboard gb) {
		
		// Generate the agent's name.
		agentName = "ag" + num;
		// Generate the topics of expertise of the agent.
		agentExpertise = Expertise.generateSubsetTopics(MIN_EXP_PER_AGENT, MAX_EXP_PER_AGENT, gb.topics);
		// Generate the agent's GB.
		agentGB = new Gameboard(gb,0);		
		// Initialize the agent's set of played arguments.
		playedArgs = new ArrayList<Argument>();
		// Initialize the agent's set of played attacks.
		playedAtts = new ArrayList<Attack>();
		// Initialize the agent's set of played supports.
		playedSupps = new ArrayList<Support>();
		// Compute the agent's team.
		// Grounded semantics is used.
		if (agentGB.computeGrounded().contains(0)) team = "PRO";
		else team = "CON";
		// The following attributes have not been used in our CLIMA'14 work.
		// Nonetheless, they could be useful in an extension of this work.
		if (Math.random() <= PROBABILITY_TRIES_ACC_OR_REJ_ISSUE) triesAcceptOrRejectIssue = true;
		else triesAcceptOrRejectIssue = false;
		// Lies
		liesBudget = 0;
		liesMade = new ArrayList<BinaryRelation>();	
		// Hidings
		dishonestPassesBudget = 0;
		numberOfDishonestPassesMade = 0;
//		hidingsBudget = 0;
//		hidingsMade = new ArrayList<BinaryRelation>();
		numberOfMovesPlayed = 0;
		effectsOfMovesOnIssue = new ArrayList<Double>();
	}
	
	/**
	 * Constructor (2/2).
	 * It calls the Gameboard Constructor which creates a new GB such that:
	 * for every element of the parameter GB, that new GB "disagrees" with it with a slight probability.
	 * This Constructor is used when we create agents belonging to specific agent classes.
	 *  honestyType == 0 : the agent is totally honest 		(  0% liar,   0% hider).
	 *  honestyType == 1 : the agent is a liar 				(100% liar,   0% hider).
	 *  honestyType == 2 : the agent is a hider 			(  0% liar, 100% hider).
	 *  honestyType == 3 : the agent is totally dishonest 	(100% liar, 100% hider). 
	 *  Constructor used in the lying & hiding work.
	 */
	public Agent(int num, AgentType type, int honestyType) {
		
		// Generate the agent's name.
		agentName = "ag" + num;
		// Generate the topics of expertise of the agent.
		agentExpertise = Expertise.generateSubsetTopics(MIN_EXP_PER_AGENT, MAX_EXP_PER_AGENT, type.typeGB.topics);
		// Generate the agent's GB (according to its type).
		agentGB = new Gameboard(type);	
		// Initialize the agent's set of played arguments.
		playedArgs = new ArrayList<Argument>();
		// Initialize the agent's set of played attacks.
		playedAtts = new ArrayList<Attack>();
		// Initialize the agent's set of played supports.
		playedSupps = new ArrayList<Support>();
		// Compute the agent's team.
		if (agentGB.evalIssue < MIN_THRESHOLD) team = "CON";
		else if (agentGB.evalIssue >= MAX_THRESHOLD) team = "PRO";
		
		if (Math.random() <= PROBABILITY_TRIES_ACC_OR_REJ_ISSUE) triesAcceptOrRejectIssue = true;
		else triesAcceptOrRejectIssue = false;
		
		// According to its "honestyType", the lying and hiding budgets of the agent are set.
		int totalNumberRelations = agentGB.attacks.size() + agentGB.supports.size();
		if (honestyType == 0) {
			System.out.println("Create a honest agent");
			liesBudget = 0;
			dishonestPassesBudget = 0;
		} else if (honestyType == 1) {
			System.out.println("Created a liar agent");
			liesBudget = totalNumberRelations;
			dishonestPassesBudget = 0;
		} else if (honestyType == 2) {
			System.out.println("Created a hiding agent");
			liesBudget = 0;
			dishonestPassesBudget = totalNumberRelations;
		} else { // honestyType == 3, 4, ...
			System.out.println("Created a dishonest agent");
			liesBudget = totalNumberRelations;
			dishonestPassesBudget = totalNumberRelations;
		}
		// Lies
		liesMade = new ArrayList<BinaryRelation>();	
		// Hidings
		numberOfDishonestPassesMade = 0;
//		hidingsBudget = 0;
//		hidingsMade = new ArrayList<BinaryRelation>();
		numberOfMovesPlayed = 0;
		effectsOfMovesOnIssue = new ArrayList<Double>();
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////// AGENT STRATEGIES (BEGIN) ////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Strategy 0a (strategyRandomDummy):
	 * The agent chooses randomly among the attacks which he hasn't already played (and if he has already played on all attacks, he "passes").
	 * Note that if all agents use this strategy, then the result of the debate will be the same as the Merged WAS.
	 * Strategy used in the CLIMA'14 work.
	 * @return
	 */
	public Attack strategyRandomDummy() {
		Attack chosenMove = null;
		ArrayList<Attack> possibleMoves = new ArrayList<Attack>();
		Attack currAtt;
		for (int i=0; i<agentGB.attacks.size(); i++) {
			currAtt = agentGB.attacks.get(i);
			// If the currAtt has not already been played, it is a possible move.
			if (!playedAtts.contains(currAtt)) {
				possibleMoves.add(currAtt);
			}
		}
		if (!possibleMoves.isEmpty()) {
			// Randomly choose one move among those in the possibleMoves ArrayList.
			int moveNum = (int) (Math.random() * possibleMoves.size());
			chosenMove = possibleMoves.get(moveNum);
			playedAtts.add(chosenMove);
		}
		// If the agent finds no (new) attack to play on, then chosenMove is null.
		return chosenMove;
	}
	
	/**
	 * Strategy 0b (strategyRandom):
	 * The agent searches for a non-pass move iff he's currently losing (this is the difference compared to the previous strategy).
	 * He chooses randomly among the attacks which he hasn't already played (and if he has already played on all attacks, he "passes").
	 * Strategy used in the CLIMA'14 work.
	 * @return
	 */
	public Attack strategyRandom(Gameboard gb) {
		Attack chosenMove = null;
		ArrayList<Attack> possibleMoves = new ArrayList<Attack>();
		Attack currAtt;
		if (!isWinning(gb)) {
			for (int i=0; i<agentGB.attacks.size(); i++) {
				currAtt = agentGB.attacks.get(i);
				// If the currAtt has not already been played, it is a possible move.
				if (!playedAtts.contains(currAtt)) {
					possibleMoves.add(currAtt);
				}
			}
			if (!possibleMoves.isEmpty()) {
				// Randomly choose one move among those in the possibleMoves ArrayList.
				int moveNum = (int) (Math.random() * possibleMoves.size());
				chosenMove = possibleMoves.get(moveNum);
				playedAtts.add(chosenMove);
			}
			// If the agent finds no (new) attack to play on, then chosenMove remains null.
		} 
		return chosenMove;
	}
	
	/**
	 * Strategy 1 (strategyChangeIssue):
	 * If the agent is winning, then he will play "pass".
	 * If the agent is losing, then he will play a move, randomly chosen among all the moves able to change the issue's status.
	 * TO DO: Check this. Also, rewrite it in a way that we don't call "playMoveOnGB" and "takebackMoveOnGB".
	 * Strategy used in the CLIMA'14 work.
	 * @return
	 */
	public Attack strategyChangeIssue(Gameboard gb) {
		Attack chosenMove = null;
		ArrayList<Attack> possibleMoves = new ArrayList<Attack>();
		Attack currAtt;
		if (!isWinning(gb)) {
			// If the agent is not winning he searches for all his possible moves.
			System.out.println("Agent " + this.agentName + " is not winning, so...");
			for (int i=0; i<agentGB.attacks.size(); i++) {
				currAtt = agentGB.attacks.get(i);
				if (!playedAtts.contains(currAtt)) {
					// Check if this is a possible move.
					// First, find its polarity (users are truthful).
					boolean polarity;
					if (currAtt.weight > 0) polarity = true;
					else polarity = false;
					// Then, play the move on the GB.
					gb.playMoveOnGB(currAtt,this,polarity,false);
					if (isWinning(gb)) {
						// If, by playing this move, the status of the issue changes, then this is a possible move.
						possibleMoves.add(currAtt);
					}
					gb.takebackMoveOnGB(currAtt,this);
				}
			}
		}
		if (!possibleMoves.isEmpty()) {
			// Randomly choose one move among those in the possibleMoves ArrayList.
			int moveNum = (int) (Math.random() * (possibleMoves.size()));
			chosenMove = possibleMoves.get(moveNum);
			playedAtts.add(chosenMove);
		}
		// If the agent is winning the debate, or he finds no attack to vote on, then chosenMove is null.
		return chosenMove;
	}
	
	/**
	 * Strategy 2 (strategyCutTSet):
	 * If the agent is winning, then he will play "pass".
	 * If the agent is losing, then he will play a move, randomly chosen among all moves able to change the sign of the weight of an attack appearing in a target set.
	 * In this strategy, target sets are left unordered (orderType==1), or they are ordered from the smallest to the biggest (orderType==2), or finally
	 * they are ordered from the (optimistically) smallest to the (optimistically) biggest (orderType==3).
	 * Strategy used in the CLIMA'14 work.
	 * @return
	 */
	public Attack strategyCutTSet(Gameboard gb, int heuristic) {
		ArrayList<ArrayList<Attack>> orderedTargetSets = new ArrayList<ArrayList<Attack>>();
		if (heuristic == 1) orderedTargetSets = gb.targetSets;
		else if (heuristic == 2) orderedTargetSets = orderTSetsBySize(gb);
		Attack chosenMove = null;
		ArrayList<Attack> possibleMoves = new ArrayList<Attack>();
		Attack currTSAtt;
		Attack currAtt = null;
		if (!isWinning(gb)) {
			System.out.println("Agent " + this.agentName + " is not winning, so he tries to cut an attack from a target set...");
			// The agent is currently losing the debate, so he searches for a move which cuts an attack from a target set.
			int stoppage = 5000;
			boolean foundPossibleMove = false;
			for (int i=0; i<orderedTargetSets.size(); i++) {
				ArrayList<Attack> currTSet = orderedTargetSets.get(i);
				// We will check for possible moves in the currTSet iff one of the 3 following conditions holds:
				// (1) orderType==1:   In this case, we just want to play in some target set (no matter its size).
				// (2) currTSet.size()<=stoppage:   In this case, we are in a target set having the same size as the previously checked target set (so we must check it).
				// (3) currTSet.size()>stoppage && !foundPossibleMove:   In this case, we pass in a bigger target set, but provided we haven't found any possible moves, we must check it.
				if (heuristic==1 || currTSet.size()<=stoppage || (currTSet.size()>stoppage && !foundPossibleMove)) {
					stoppage = currTSet.size();
					for (int j=0; j<currTSet.size(); j++) {
						currTSAtt = currTSet.get(j);
						for (int k=0; k<agentGB.attacks.size(); k++) {
							if ( (agentGB.attacks.get(k).firstArg == currTSAtt.firstArg) && (agentGB.attacks.get(k).secondArg == currTSAtt.secondArg) ) {
								currAtt = agentGB.attacks.get(k);
								break;
							}
						}
						// If the agent has not already played this attack, it may be a possible move.
						if (!playedAtts.contains(currAtt)) {
							// It is a possible move iff it can change the sign of the attack's weight.
							if (provokedChangeByVoting(currTSAtt, gb) == -1) {
								possibleMoves.add(currAtt);
								foundPossibleMove = true;
							}
						}
					}
				} else {
					// In this case, there is no need to check for possible moves in the remaining target sets.
					break;
				}
			}	
		}
		if (!possibleMoves.isEmpty()) {
			// Randomly choose one move among those in the possibleMoves ArrayList.
			int moveNum = (int) (Math.random() * (possibleMoves.size()));
			chosenMove = possibleMoves.get(moveNum);
			playedAtts.add(chosenMove);
		}
		// If the agent is winning the debate, or he finds no attack to vote on, then chosenMove is null.
		return chosenMove;
	}
	
	/**
	 * Strategy 3 (strategyWeakenTSet):
	 * If the agent is winning, then he will play "pass".
	 * If the agent is losing, then he will play a move, randomly chosen among all moves able to cut an attack of a target set, or (at least) weaken it.
	 * In this strategy, target sets are left unordered (orderType==1), or they are ordered from the smallest to the biggest (orderType==2), or finally
	 * they are ordered from the (optimistically) smallest to the (optimistically) biggest (orderType==3).
	 * Strategy used in the CLIMA'14 work.
	 * @return
	 */
	public Attack strategyWeakenTSet(Gameboard gb, int heuristic) {
		ArrayList<ArrayList<Attack>> orderedTargetSets = new ArrayList<ArrayList<Attack>>();
		if (heuristic == 1) orderedTargetSets = gb.targetSets;
		else if (heuristic == 2 || heuristic == 3) orderedTargetSets = orderTSetsBySize(gb);
		Attack chosenMove = null;
		ArrayList<Attack> possibleMoves = new ArrayList<Attack>();
		Attack currTSAtt;
		Attack currAtt = null;
		if (!isWinning(gb)) {
			System.out.println("Agent " + this.agentName + " is not winning, so...");
			// The agent is currently losing the debate, so he searches for a move (other than pass).
			int stoppage = 5000;
			boolean foundPossibleMove = false;
			for (int i=0; i<orderedTargetSets.size(); i++) {
				ArrayList<Attack> currTSet = orderedTargetSets.get(i);
				// We will check for possible moves in the currTSet iff one of the 3 following conditions holds:
				// (1) orderType==1:   In this case, we just want to play in some target set (no matter its size).
				// (2) currTSet.size()<=stoppage:   In this case, we are in a target set having the same size as the previously checked target set (so we must check it).
				// (3) currTSet.size()>stoppage && !foundPossibleMove:   In this case, we pass in a bigger target set, but provided we haven't found any possible moves, we must check it.
				if (heuristic==1 || currTSet.size()<=stoppage || (currTSet.size()>stoppage && !foundPossibleMove)) {
					stoppage = currTSet.size();
					for (int j=0; j<currTSet.size(); j++) {
						currTSAtt = currTSet.get(j);
						for (int k=0; k<agentGB.attacks.size(); k++) {
							if ( (agentGB.attacks.get(k).firstArg == currTSAtt.firstArg) && (agentGB.attacks.get(k).secondArg == currTSAtt.secondArg) ) {
								currAtt = agentGB.attacks.get(k);
								break;
							}
						}
						// If the agent has not already played this attack, it may be a possible move.
						if (!playedAtts.contains(currAtt)) {
							int typeOfChange = provokedChangeByVoting(currTSAtt, gb);
							// It is a possible move iff it can change the sign of the attack's weight, or decrease its absolute value.
							if (typeOfChange == -1 || typeOfChange == 0) {
								possibleMoves.add(currAtt);
								foundPossibleMove = true;
							}
						}		
					}
				} else {
					// In this case, there is no need to check for possible moves in the remaining target sets.
					break;
				}
			}	
		}
		if (heuristic == 3) {
			// With the third heuristic, the agent filters the possibleMoves, keeping only those attacks which he can change by voting. 
			boolean oneAttCanChange = false;
			// The agent will filter-out the attacks he cannot change iff there exists at least one attack which he can change.
			for (int i=0; i<possibleMoves.size(); i++) {
				currAtt = possibleMoves.get(i);
				if (provokedChangeByVoting(currAtt, gb) == -1) {
					oneAttCanChange = true;
					break;
				}
			}
			if (oneAttCanChange) {
				// The agent filters-out all the attacks which he cannot change.
				for (int i=0; i<possibleMoves.size(); i++) {
					currAtt = possibleMoves.get(i);
					if (provokedChangeByVoting(currAtt, gb) != -1) {
//						System.out.println("REMOVE a possible attack.");
						possibleMoves.remove(i);
						// Attention, the ArrayList got smaller!
						i--;
					}
				}
			}
		}
		if (!possibleMoves.isEmpty()) {
			// Randomly choose one move among those in the possibleMoves ArrayList.
			int moveNum = (int) (Math.random() * (possibleMoves.size()));
			chosenMove = possibleMoves.get(moveNum);
			playedAtts.add(chosenMove);
		}
		// If the agent is winning the debate, or he finds no attack to vote on, then chosenMove is null.
		return chosenMove;
	}
	
	/**
	 * Strategy 4 (strategyWeakenReinforceTSet):
	 * If he's winning, then he will play a move, randomly chosen among all moves able to reinforce the weight of an attack of a target set.
	 * If he's losing, then (as in strategyWeakenTSet) he will play a move, randomly chosen among all moves able to cut an attack of a target set, or (at least) weaken it.
	 * In this strategy, target sets are left unordered (orderType==1), or they are ordered from the smallest to the biggest (orderType==2), or finally
	 * they are ordered from the (optimistically) smallest to the (optimistically) biggest (orderType==3).
	 * Strategy used in the CLIMA'14 work.
	 * @return
	 */
	public Attack strategyWeakenReinforceTSet(Gameboard gb, int heuristic) {
		ArrayList<ArrayList<Attack>> orderedTargetSets = new ArrayList<ArrayList<Attack>>();
		if (heuristic == 1) orderedTargetSets = gb.targetSets;
		else if (heuristic == 2 || heuristic == 3) orderedTargetSets = orderTSetsBySize(gb);
		Attack chosenMove = null;
		ArrayList<Attack> possibleMoves = new ArrayList<Attack>();
		Attack currTSAtt;
		Attack currAtt = null;
		int stoppage = 5000;
		boolean foundPossibleMove = false;
		for (int i=0; i<orderedTargetSets.size(); i++) {
			ArrayList<Attack> currTSet = orderedTargetSets.get(i);
			// We will check for possible moves in the currTSet iff one of the 3 following conditions holds:
			// (1) orderType==1:   In this case, we just want to play in some target set (no matter its size).
			// (2) currTSet.size()<=stoppage:   In this case, we are in a target set having the same size as the previously checked target set (so we must check it).
			// (3) currTSet.size()>stoppage && !foundPossibleMove:   In this case, we pass in a bigger target set, but provided we haven't found any possible moves, we must check it.
			if (heuristic==1 || currTSet.size()<=stoppage || (currTSet.size()>stoppage && !foundPossibleMove)) {
				stoppage = currTSet.size();
				for (int j=0; j<currTSet.size(); j++) {
					currTSAtt = currTSet.get(j);
					for (int k=0; k<agentGB.attacks.size(); k++) {
						if ( (agentGB.attacks.get(k).firstArg == currTSAtt.firstArg) && (agentGB.attacks.get(k).secondArg == currTSAtt.secondArg) ) {
							currAtt = agentGB.attacks.get(k);
							break;
						}
					}
					// If the agent has not already played this attack, it may be a possible move.
					if (!playedAtts.contains(currAtt)) {
						// The agent can play his move on this attack iff either:
						// (1) He is currently losing, and he can change the sign of the attack's weight, or decrease its absolute value.
						// (2) He is currently winning, and he can reinforce the attack's weight.
						int typeOfChange = provokedChangeByVoting(currTSAtt, gb);
						if ( (!isWinning(gb) && (typeOfChange == -1 || typeOfChange == 0))
								|| (isWinning(gb) && typeOfChange == 1) ) {
							possibleMoves.add(currAtt);
							foundPossibleMove = true;
						}
					}
				}
			} else {
				// In this case, there is no need to check for possible moves in the remaining target sets.
				break;
			}
		}	
		if (heuristic == 3 && !isWinning(gb)) {
			// In strategyWeakenReinforceTSet, the third heuristic will filter the possibleMoves iff the agent is currently losing the debate. 
			// With the third heuristic, the agent filters the possibleMoves, keeping only those attacks which he can change by voting. 
			boolean oneAttCanChange = false;
			// The agent will filter-out the attacks he cannot change iff there exists at least one attack which he can change.
			for (int i=0; i<possibleMoves.size(); i++) {
				currAtt = possibleMoves.get(i);
				if (provokedChangeByVoting(currAtt, gb) == -1) {
					oneAttCanChange = true;
					break;
				}
			}
			if (oneAttCanChange) {
				// The agent filters-out all the attacks which he cannot change.
				for (int i=0; i<possibleMoves.size(); i++) {
					currAtt = possibleMoves.get(i);
					if (provokedChangeByVoting(currAtt, gb) != -1) {
//						System.out.println("REMOVE a possible attack.");
						possibleMoves.remove(i);
						// Attention, the ArrayList got smaller!
						i--;
					}
				}
			}
		}
		if (!possibleMoves.isEmpty()) {
			// Randomly choose one move among those in the possibleMoves ArrayList.
			int moveNum = (int) (Math.random() * (possibleMoves.size()));
			chosenMove = possibleMoves.get(moveNum);
			playedAtts.add(chosenMove);
		}
		// If the agent finds no attack to vote on, then chosenMove is null.
		return chosenMove;
	}
	
	/**
	 * This is a possible strategy for a user who considers numerical argument evaluation.
	 * (A) If the user wants to make the issue accepted / rejected (which is the case we care about), then he:
	 * - Considers all his possible moves (if he can lie, then among his possible moves there may be some lies).
	 * - Checks their effects on the issue, and prefers the move with the most "positive" effect (for him).
	 * - "Neutral" moves (w.r.t. their effect on the issue) are not taken into account.
	 * - If he ends up with only "negative" possible moves, then:
	 *   (i)  if he can still "hide", then he plays a "malicious pass";
	 *   (ii) otherwise, he plays the first NON-LIE move (we don't want the agent to lie & shoot himself in the foot).
	 *        (alternative, non-used, choice: he plays the best non-lie move).
	 * (B) If the user doesn't care about affecting the issue's acceptability, then there are many possible choices (eg. honest contributor, troll).
	 * Strategy used in the lying & hiding work.
	 * @param gb
	 * @return
	 */
	public Move strategyNumericalEval(Gameboard gb) {
		// This is the move we're searching for.
		Move moveToPlay;
		// The following ArrayLists help us consider all possible moves.
		ArrayList<BinaryRelation> possibleRelations = new ArrayList<BinaryRelation>();
		ArrayList<Boolean> possiblePolarities = new ArrayList<Boolean>();
		ArrayList<Double> possibleNewEvals = new ArrayList<Double>();
		
		///////////////////////////////////////////////////
		// 1. FIND ALL POSSIBLE MOVES AND EVALUATE THEM. //
		///////////////////////////////////////////////////
		
//		System.out.println("Find possible moves.");
		
		BinaryRelation currRel = null;
		boolean believesRelation = true;
		// First focus on attacks.
		for (int i=0; i<agentGB.attacks.size(); i++) {
			currRel = agentGB.attacks.get(i);
			
			// Is this attack already put on the GB by some user? If yes, then the user cannot play on it.
			boolean alreadyOnGB = false;
			for (int j=0; j<gb.attacks.size(); j++) {
				Attack currAtt = gb.attacks.get(j);
				if (currAtt.firstArg == currRel.firstArg && currAtt.secondArg == currRel.secondArg) {
					if (currAtt.weight > 0) {
						alreadyOnGB = true;
//						System.out.println("Attack already put (TRUE).");
					} else {
//						System.out.println("Attack already put (FALSE).");
					}
				}
			}
			if (currRel.weight > 0) believesRelation = true;
			else believesRelation = false; // This attack addition would be a lie.
			if ( !alreadyOnGB && (believesRelation || (!believesRelation && canStillLie())) ) {
//				System.out.println("Agent can play attack.");
				// If this attack hasn't been played by any user AND
				// EITHER the user has it in his system OR he doesn't have it, but he can still lie.
				// Then, we have 1 possible move here (attack addition).
				
				// We must check if this move is "neutral" (not affecting the issue's valuation).
				double initialIssueEval = gb.evalIssue;
//				System.out.println(" ** initialIssueEval = " + initialIssueEval);
				// Play the move, in order to compute the issue's new evaluation.
				gb.playMoveOnGB(currRel, this, true, false);
				if (initialIssueEval != gb.evalIssue) {
//					System.out.println("Non-neutral move, as it can make " + gb.evalIssue);
					// It's a non-neutral move (relevant to the issue), so we must consider it.
					possibleRelations.add(currRel);
					possiblePolarities.add(true);
					possibleNewEvals.add(gb.evalIssue);
//					System.out.print("If I play a move on ");
//					currRel.printRelation(null);
//					System.out.println(" then issue eval = " + gb.evalIssue);
				}
				// Now, play the move with inversed polarity (take it back).
				gb.playMoveOnGB(currRel, this, false, false);
//				System.out.print("If I take back my move on ");
//				currRel.printRelation(null);
//				System.out.println(" then issue eval = " + gb.evalIssue);
			}
		}
		// Then, focus on supports (the reasoning process is the same, so we could try to fusion these 2 for-loops).
		for (int i=0; i<agentGB.supports.size(); i++) {
			currRel = agentGB.supports.get(i);
			// Is this support already put on the GB by some user? If it is, then the user cannot play on it.
			boolean alreadyOnGB = false;
			for (int j=0; j<gb.supports.size(); j++) {
				Support currSupp = gb.supports.get(j);
				if (currSupp.firstArg == currRel.firstArg && currSupp.secondArg == currRel.secondArg) {
					if (currSupp.weight > 0) alreadyOnGB = true;
				}
			}
			if (currRel.weight > 0) believesRelation = true;
			else believesRelation = false; // The support addition would be a lie.
			if ( !alreadyOnGB && (believesRelation || (!believesRelation && canStillLie())) ) {
				// If this support hasn't been played by any user AND
				// EITHER the user has it in his system OR he doesn't have it, but he can still lie.
				// Then, we have 1 possible move here (support addition).
				
				// We must check if this move is "neutral" (not affecting the issue's valuation).
				double initialIssueEval = gb.evalIssue;
				// Play the move, in order to compute the issue's new evaluation.
				gb.playMoveOnGB(currRel, this, true, false);
				if (initialIssueEval != gb.evalIssue) {
					// It's a non-neutral move (relevant to the issue), so we must consider it.
					possibleRelations.add(currRel);
					possiblePolarities.add(true);
					possibleNewEvals.add(gb.evalIssue);
//					System.out.print("If I play a move on ");
//					currRel.printRelation(null);
//					System.out.println(" then issue eval = " + gb.evalIssue);
				} 		
				// Play the move with inversed polarity (take it back).
				gb.playMoveOnGB(currRel, this, false, false);
//				System.out.print("If I take back my move on ");
//				currRel.printRelation(null);
//				System.out.println(" then issue eval = " + gb.evalIssue);
			}
		}
		
		// If there are 0 possible moves, then obligatorily pass. It's a honest, non-strategic pass: the pass budget is not decreased.
		if (possibleRelations.isEmpty()) {
			numberOfMovesPlayed++;
			numberOfHonestPassesMade++;
			effectsOfMovesOnIssue.add(0.0);
			System.out.println("PASS (honest)");
			return null;
		}
//		System.out.println("Number of possible moves = " + possibleRelations.size());
		
		//////////////////////////////////////////////////
		// 2. CHOOSE ONE MOVE AMONG THE POSSIBLE MOVES. //
		//////////////////////////////////////////////////
		
		if (triesAcceptOrRejectIssue) {
			// Since the user is focused on the issue, he tries to turn its evaluation to 1 or to 0 (the two extreme values for the QUAD evaluation).
			double wishedValue;
			if (agentGB.evalIssue >= 0.5) wishedValue = 1.0;
			else wishedValue = 0.0;
			// currentBest is a "pointer" used to remember the move which had the best effect (for this user).
			int currentBestPtr = 0;
			// The lowest this value is for a move, the better the move is for the user (0 is the best a user can get).
			double currSmallestDistanceFromWishValue = Math.abs(wishedValue - possibleNewEvals.get(0));
			for (int i=0; i<possibleNewEvals.size(); i++) {
				if (Math.abs(wishedValue - possibleNewEvals.get(i)) <= currSmallestDistanceFromWishValue) {
					// We have a new "best" move (which can get us closer to the wish value than all other moves).
					currentBestPtr = i;
					currSmallestDistanceFromWishValue = Math.abs(wishedValue - possibleNewEvals.get(i));
				}	
			}
			if (currSmallestDistanceFromWishValue < Math.abs(wishedValue - gb.evalIssue)) {
				// The best move was strictly "positive" for the user (so we'll return this move).
				moveToPlay = new Move(possibleRelations.get(currentBestPtr), possiblePolarities.get(currentBestPtr));
				// Update the "played" lists.
				if (moveToPlay.relationOfMove instanceof Attack) playedAtts.add((Attack)moveToPlay.relationOfMove);
				else playedSupps.add((Support)moveToPlay.relationOfMove);
				// Update the lying budget (if needed).
				if (possibleRelations.get(currentBestPtr).weight <= 0) {
					System.out.println("This as a lie!");
					liesMade.add(possibleRelations.get(currentBestPtr));
				}
				// Update the number of moves pro/con the issue (here the user's move was towards its goal).
				if (wishedValue == 1.0) numberOfMovesForIssue++;
				else numberOfMovesAgainstIssue++;
				numberOfMovesPlayed++;
				effectsOfMovesOnIssue.add(possibleNewEvals.get(currentBestPtr) - gb.evalIssue); // to do: test
				System.out.println("Played a good move.");
				return moveToPlay;
			} else {
				// The best move was strictly "negative" for the user (therefore all possible moves are bad).
				// We must check if there's, at least, one truthful move. 
				// If there's a truthful move, then the agent will check his hidingBudget, and will either play pass (hiding), or it will play that move.
				// If all moves are lies, then the agent does a (honest) pass.
				for (int i=0; i<possibleRelations.size(); i++) {
					if (possibleRelations.get(i).weight > 0) {
						// There's a truthful move.
						if (numberOfDishonestPassesMade < dishonestPassesBudget) {
							// If the user is able to hide, then he passes.
							numberOfDishonestPassesMade++;
							numberOfMovesPlayed++;
							effectsOfMovesOnIssue.add(0.0);
							System.out.println("PASS (hiding).");
							return null;
						} else {
							moveToPlay = new Move(possibleRelations.get(i), possiblePolarities.get(i));
							// Update the "played" lists.
							if (moveToPlay.relationOfMove instanceof Attack) playedAtts.add((Attack)moveToPlay.relationOfMove);
							else playedSupps.add((Support)moveToPlay.relationOfMove);
							// Update the number of moves pro/con the issue (here the user's move was away from its goal).
							if (wishedValue == 1.0) numberOfMovesAgainstIssue++;
							else numberOfMovesForIssue++;
							numberOfMovesPlayed++;
							effectsOfMovesOnIssue.add(possibleNewEvals.get(currentBestPtr) - gb.evalIssue);
							System.out.println("Played a bad move.");
							return moveToPlay;
						}
					}
				}
				// All available (bad) moves are lies, so the agent does a pass (honest).
				numberOfMovesPlayed++;
				numberOfHonestPassesMade++;
				effectsOfMovesOnIssue.add(0.0);
				System.out.println("PASS (honest).");
				return null;		
			}
		} 
//		else { 
//			// The user doesn't have the goal of making the issue accepted or rejected.
//			// He may, for example, just want to contribute to the debate, or be a troll, or have some other goal.
//			// For the moment we don't consider this possibility.
//			int toReturn = (int) (Math.random() * possibleRelations.size());
//			if (possibleRelations.get(toReturn) instanceof Attack) playedAtts.add((Attack)possibleRelations.get(toReturn));
//			else playedSupps.add((Support)possibleRelations.get(toReturn));
//			moveToPlay = new Move(possibleRelations.get(toReturn), possiblePolarities.get(toReturn));
//			// ... POSITIVES AND NEGATIVES ...
//			numberOfMovesPlayed++;
//			return moveToPlay;
//		}
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	/////////////////////////// AGENT STRATEGIES (END) ////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	
	
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////////// AGENT PROFILING (BEGIN) //////////////////////////
	//////////////////////////////////////////////////////////////////////////////
		
	/**
	 * This method computes how active the agent has been during the debate.
	 * It returns a double value in [0,1].
	 * That value is the percentage of non-pass moves that the agent has played during the debate.
	 * A move is the insertion of an argument - relation pair. No votes are allowed.
	 * Method used in the lying & hiding work.
	 * @return
	 */
	public double computeActivity() {
		double activity;
		int numNonPassMovesPlayed = numberOfMovesPlayed - numberOfDishonestPassesMade - numberOfHonestPassesMade;
		// Version (A):
		if (numberOfMovesPlayed == 0) activity = 0.0;
		else activity = (double)numNonPassMovesPlayed / numberOfMovesPlayed;
		
//		// Version (B): instead of dividing by the numberOfMovesPlayed (by the agent), we divide by the sum of non-pass moves of all the agents. 
//		// In this case, the ArrayList<Agent> agents must be passed as a parameter.
//		double numOfNonPassMovesAllAgentsPlayed = 0;
//		for (int i=0; i<agents.size(); i++) {
//			numOfNonPassMovesAllAgentsPlayed += agents.get(i).numberOfMovesPlayed;
//			numOfNonPassMovesAllAgentsPlayed -= agents.get(i).numberOfDishonestPassesMade;
//			numOfNonPassMovesAllAgentsPlayed -= agents.get(i).numberOfHonestPassesMade;
//		}
//		activity = (double)numNonPassMoves / numOfNonPassMovesAllAgentsPlayed;
		
		return activity;
	}
	
	/**
	 * This method computes how innovative the agent has been during the debate.
	 * It returns a double value in [0,1].
	 * That value is the percentage of non-pass, innovative moves (argument - relation insertions) the agent has played during the debate.
	 * Currently agents cannot cast votes, so the method's code is the same as the code of the "computeAgentActivity()" method. 
	 * @param agents
	 * @return
	 */
	public double computeInnovation(ArrayList<Agent> agents) {
		double innovation;
		int numNonPassMovesPlayed = numberOfMovesPlayed - numberOfDishonestPassesMade - numberOfHonestPassesMade;
		// Version (A):
		if (numberOfMovesPlayed == 0) innovation = 0.0;
		else innovation = (double)numNonPassMovesPlayed / numberOfMovesPlayed;
		
//		// Version (B): instead of dividing by the numberOfMovesPlayed (by the agent), we divide by the sum of non-pass moves of all the agents. 
//		// In this case, the ArrayList<Agent> agents must be passed as a parameter.
//		double numOfNonPassMovesAllAgentsPlayed = 0;
//		for (int i=0; i<agents.size(); i++) {
//			numOfNonPassMovesAllAgentsPlayed += agents.get(i).numberOfMovesPlayed;
//			numOfNonPassMovesAllAgentsPlayed -= agents.get(i).numberOfDishonestPassesMade;
//			numOfNonPassMovesAllAgentsPlayed -= agents.get(i).numberOfHonestPassesMade;
//		}
//		activity = (double)numNonPassMoves / numOfNonPassMovesAllAgentsPlayed;
		
		return innovation;
	}
	
	/**
	 * This method computes how confident the agent has been during the debate.
	 * It returns a double value in [0,1] (unless the agent has played only pass moves, and in that case the returned "default" value is 9.99).
	 * TO DO: Finish this.
	 * @return
	 */
	public double computeConfidence() {
		double confidence = 9.99;
		
		return confidence;
	}
	
	/**
	 * This method computes how much the agent has impacted the issue of the debate.
	 * It returns a double value in [0,1].
	 * TO DO: Check this.
	 */
	public double computeImpactIssue() {
		double impactIssue;
		if (numberOfMovesForIssue + numberOfMovesAgainstIssue == 0) impactIssue = 0.0;
		else {
			double impact = 0.0;
			for (int i=0; i<this.effectsOfMovesOnIssue.size(); i++) {
				impact += Math.abs(effectsOfMovesOnIssue.get(i));
			}
			// In our debate setting, in each turn of the debate, each user gets to play once.
			// Therefore, the number of rounds of the debate is equal to "numberOfMovesPlayed".
			// By dividing "impact" with "numberOfMovesPlayed", we are certain to obtain a result in [0,1].
			// For example, an agent has a maximum impact on the issue (=1) iff with every move that he played, he made the issue's evaluation swing from 0 to 1, or vice-versa.
			impactIssue = impact / numberOfMovesPlayed;
		}
		return impactIssue; 
	}

	/**
	 * This method returns an evaluation of how much the user has been focusing, during the debate, on the issue.
	 * It returns a value in [0,1]. 
	 * TO DO: Check this.
	 */
	public double computeFocusIssue() {
		double focus;
//		// Version (A) Based on the changes that the agent's moves had on the arguments' (not only on the issue's) evaluations.
//		Compute the fraction: changes made on issue's eval / changes made on all arguments' evals.
//		// Check the latex file and finish this.
		// Version (B): SImpler, based on the percentage of moves which affected the issue's evaluation.
		if (numberOfMovesPlayed == 0) focus = 0;
		else focus = (double)(numberOfMovesForIssue + numberOfMovesAgainstIssue) / numberOfMovesPlayed;

		return focus;
	}
	
	/**
	 * This method computes how opinionated the user has been, during the debate, with respect to the issue.
	 * It returns a value in [0,1].
	 * Method used in the lying & hiding work.
	 */
	public double computeOpinionatednessIssue() {
		double opinionatednessIssue;
		// Version (A): Based on the changes that the moves had on the issue's evaluation.
		double opinion = 0.0;
		double impact = 0.0;
		for (int i=0; i<this.effectsOfMovesOnIssue.size(); i++) {
			opinion += effectsOfMovesOnIssue.get(i);
			impact += Math.abs(effectsOfMovesOnIssue.get(i));
		}
		if (impact == 0.0) opinionatednessIssue = 0.0;
		else opinionatednessIssue = Math.abs(opinion) / impact;
		
//		// Version (B): Based on the percentages of moves which had a positive (and a negative) effect on the issue's evaluation.
//		if (numberOfMovesForIssue + numberOfMovesAgainstIssue != 0) {
//			opinionatedness = (double)Math.abs(numberOfMovesForIssue - numberOfMovesAgainstIssue) / numberOfMovesPlayed; 
//			// other possible denominator: (numberOfMovesForIssue + numberOfMovesAgainstIssue);
//		}
		return opinionatednessIssue;
	}
	
	/**
	 * This method computes how classifiable an agent is, based on two things:
	 * (1) the set of all agent types,
	 * (2) everything the agent has said during the debate,
	 * (3) the Gameboard of the debate (because we also want to know what the others have said).
	 * It returns a value in [0,1].
	 * We may compute a user's classifiability whenever we want during a debate, 
	 * though it's more meaningful to compute it at the end of a debate.
	 * If we add votes, then this method must be rewritten (almost all of its points should be amended).
	 * Method used in the lying & hiding work. 
	 */
	public double computeClassifiability(Gameboard gb, ArrayList<AgentType> types) {
		// We'll measure the distance of this user from all user types.
		ArrayList<Double> distancesFromTypes = new ArrayList<Double>();	
		AgentType currType;
		Attack currAtt;
		Support currSupp;
		int disagreements;
		int agreements;
		boolean relationIsPosOnTypeGB;
		
//		System.out.println("Played attacks were:");
//		for (int i=0; i<playedAtts.size(); i++) {
//			playedAtts.get(i).printRelation(null);
//			System.out.print(" ");
//		}
//		System.out.println();
//		System.out.println("Played supports were:");
//		for (int i=0; i<playedSupps.size(); i++) {
//			playedSupps.get(i).printRelation(null);
//			System.out.print(" ");
//		}
//		System.out.println();
		
		for (int i=0; i<types.size(); i++) {
			// For every agent type, measure the distance. 
			currType = types.get(i);
			disagreements = 0;
			agreements = 0;
			// First, regarding the attacks:
			for (int j=0; j<playedAtts.size(); j++) {
				// Compare the user's played attacks, with the GB of the type. 
				// # Attacks the user has played, but this type does not agree with (--> lies?)
				currAtt = playedAtts.get(j);
				relationIsPosOnTypeGB = false;
				for (int k=0; k<currType.typeGB.attacks.size(); k++) {
					if (currAtt.firstArg == currType.typeGB.attacks.get(k).firstArg &&
							currAtt.secondArg == currType.typeGB.attacks.get(k).secondArg &&
							currType.typeGB.attacks.get(k).weight > 0)
						relationIsPosOnTypeGB = true;
				}
				if (!relationIsPosOnTypeGB) {
//					System.out.print("Agent has probably lied on ");
//					System.out.print("Disagreements++ on: ");
//					currAtt.printRelation(null);
//					System.out.println();
					disagreements++;
				} else {
					agreements++;
				}
			}
			for (int j=0; j<currType.typeGB.attacks.size(); j++) {
				currAtt = currType.typeGB.attacks.get(j);
				// Compare the GB of the type, with the GB of the debate.
				// Focus on attacks which this type has (weight>0), and this agent has not played (the played ones were previously checked).
				if (currAtt.weight > 0 && !existsAttackInList(currAtt, this.playedAtts)) {
					// Check if no other agent has played the currAtt (weight<=0 on the gb). (--> hide?)
					if (!existsAttackSameWeightInList(currAtt, gb.attacks)) {
//						System.out.print("Agent has probably hidden ");
//						System.out.print("Disagreements++ on: ");
//						currAtt.printRelation(null);
//						System.out.println();
						disagreements++;
					} else {
						agreements++;
					}
				}
			}
			// Compare the user's played supports, with the GB of the type. 
			// Second, regarding the supports:
			for (int j=0; j<playedSupps.size(); j++) {
				// # Supports the user has played, but this type does not agree with (--> lies?)
				currSupp = playedSupps.get(j);
				relationIsPosOnTypeGB = false;
				for (int k=0; k<currType.typeGB.supports.size(); k++) {
					if (currSupp.firstArg == currType.typeGB.supports.get(k).firstArg && currSupp.secondArg == currType.typeGB.supports.get(k).secondArg &&
							currType.typeGB.supports.get(k).weight > 0)
						relationIsPosOnTypeGB = true;
				}
				if (!relationIsPosOnTypeGB) {
//					System.out.print("Agent has probably lied on ");
//					System.out.print("Disagreements++ on: ");
//					currSupp.printRelation(null);
//					System.out.println();
					disagreements++;
				} else {
					agreements++;
				}
			}
			for (int j=0; j<currType.typeGB.supports.size(); j++) {
				currSupp = currType.typeGB.supports.get(j);
				// Compare the GB of the type, with the GB of the debate.
				// Focus on supports which this type has (weight>0), and this agent has not played (the played ones were previously checked).
				if (currSupp.weight > 0 && !existsSupportInList(currSupp, this.playedSupps)) {
					// Check if no other agent has played the currSupp (weight<=0 on the gb). (--> hide?)
					if (!existsSupportSameWeightInList(currSupp, gb.supports)) {
//						System.out.print("Agent has probably hidden ");
//						System.out.print("Disagreements++ on: ");
//						currSupp.printRelation(null);
//						System.out.println();
						disagreements++;
					} else {
						agreements++;
					}
				}
			}
			// The sum of agreements and disagreements cannot be zero, as (for example) every agent type system has at least one relation.
			distancesFromTypes.add((double)(disagreements) / (agreements + disagreements));
		}
		// Find the smallest distance.
		double minDistance = distancesFromTypes.get(0);
		for (int i=0; i<distancesFromTypes.size()-1; i++) {
			minDistance = Math.min(minDistance, distancesFromTypes.get(i+1));
		}
		// Compute the agent's classifiability.
		return 1 - minDistance;
//		return 1 - ((double)(minDistance) / (agentGB.attacks.size() + agentGB.supports.size())); // old (code used for submission to EUMAS'15).
	}

	
	
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////////// AGENT PROFILING (END) ////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * This method returns an ArrayList with all the target sets of the Gameboard, ordered from the smallest to the biggest.
	 * Method used in the CLIMA'14 work.
	 * @param gb
	 * @return
	 */
	public ArrayList<ArrayList<Attack>> orderTSetsBySize(Gameboard gb) {
		ArrayList<ArrayList<Attack>> orderedTSets = new ArrayList<ArrayList<Attack>>();
		if (!gb.targetSets.isEmpty()) {
			int smallest = gb.targetSets.get(0).size();
			int biggest = gb.targetSets.get(0).size();
			ArrayList<Attack> currTSet;
			for (int i=0; i<gb.targetSets.size(); i++) {
				currTSet = gb.targetSets.get(i);
				if (currTSet.size() < smallest) smallest = currTSet.size();	
				if (currTSet.size() > biggest) biggest = currTSet.size();
			}
			for (int currSize=smallest; currSize<=biggest; currSize++) {
				for (int i=0; i<gb.targetSets.size(); i++) {
					currTSet = gb.targetSets.get(i);
					if (currTSet.size() == currSize) orderedTSets.add(currTSet);
				}
			}
		}
		return orderedTSets;
	}
	
//	/**
//	 * TO DO: DEBUG THIS!
//	 * This function returns an ArrayList with all the target sets of the Gameboard, ordered from the optimistically smallest to the optimistically biggest.
//	 * eg. suppose that there are two target sets: t1 = {att1, att2, att3} and t2 = {att4, att5}.
//	 * If the agent can play on attacks att1, att2, att3, att4 (decreasing the absolute values of their weights), then
//	 * the optimistic size of t1 is 0, and the optimistic size of t2 is 1. Therefore, the agent considers that t1 is "smaller" (easier to change) than t2.
//	 * @param gb
//	 * @return
//	 */
//	public ArrayList<ArrayList<Attack>> orderTSetsByOptimisticSize(Gameboard gb) {
//		ArrayList<ArrayList<Attack>> orderedTSets = new ArrayList<ArrayList<Attack>>();
//		// The optimisticSizesTSets ArrayList will be filled with the optimistic sizes of all target sets (the order is the same as in the gb.targetSets ArrayList).
//		ArrayList<Integer> optimisticSizesTSets = new ArrayList<Integer>();
//		if (!gb.targetSets.isEmpty()) {
//			int smallest = 10000;
//			int biggest = 0;
//			ArrayList<Attack> currTSet;
//			int currOptSize;
//			for (int i=0; i<gb.targetSets.size(); i++) {
//				currTSet = gb.targetSets.get(i);
//				// Initially set the optimistic size to be the same as the actual size.
//				currOptSize = currTSet.size();
//				for (int j=0; j<currTSet.size(); j++) {
//					Attack currAtt = currTSet.get(j);
//					if (provokedChangeByVoting(currAtt) == 0 || provokedChangeByVoting(currAtt) == -1) {
//						// Decrease the value of the optimistic size.
//						currOptSize--;
//					}
//				}
//				optimisticSizesTSets.add(currOptSize);
//				if (currOptSize < smallest) smallest = currOptSize;	
//				if (currOptSize > biggest) biggest = currOptSize;
//			}
//			for (int currSize=smallest; currSize<=biggest; currSize++) {
//				for (int i=0; i<optimisticSizesTSets.size(); i++) {
//					currTSet = gb.targetSets.get(i);
//					if (optimisticSizesTSets.get(i) == currSize) orderedTSets.add(currTSet);
//				}
//			}
//		}
//		return orderedTSets;
//	}
	
	/**
	 * This function returns true iff the status of the issue coincides in: (a) the agent's system, and (b) the Gameboard.
	 * @param gb
	 * @return
	 */
	public boolean isWinning(Gameboard gb) {
		return ( (gb.statusIssue && team.equals("PRO")) || (!gb.statusIssue && team.equals("CON")) );
	}
	
	/**
	 * This function returns either +1, or 0, or -1, according to the following:
	 * Returns +1: If the agent, by voting on the attack, is able to increase the absolute value of its weight (reinforce it), or leave it unchanged.
	 * Returns  0: If the agent, by voting on the attack, is able to decrease the absolute value of its weight (weaken it), but not change its sign.
	 * Returns -1: If the agent, by voting on the attack, is able to change the attack's weight from non-positive to positive (or vice-versa).
	 * Method used in the CLIMA'14 work.
	 * @param att, gb
	 * @return
	 */
	public int provokedChangeByVoting(Attack att, Gameboard gb) {
		Attack agentAtt = null;
		for (int i=0; i<agentGB.attacks.size(); i++) {
			if ( (agentGB.attacks.get(i).firstArg == att.firstArg) && (agentGB.attacks.get(i).secondArg == att.secondArg) ) {
				agentAtt = agentGB.attacks.get(i);
				break;
			}
		}
		Attack gbAtt = null;
		for (int i=0; i<gb.attacks.size(); i++) {
			if ( (gb.attacks.get(i).firstArg == att.firstArg) && (gb.attacks.get(i).secondArg == att.secondArg) ) {
				gbAtt = gb.attacks.get(i);
				break;
			}
		}
		if ( (gbAtt.weight > 0 && agentAtt.weight > 0) || (gbAtt.weight <= 0 && agentAtt.weight <= 0) ) {
			// The agent's vote would reinforce the attack's weight (or, at least, it would provoke no change).
			return 1;
		} else {
			// gbAtt.weight and agentAtt.weight have opposite signs, therefore the agent's vote would
			// either weaken, or change the sign of the attack's weight (or, at least, it would provoke no change).
			int effect = 0;
			for (int i=0; i<agentExpertise.size(); i++) {
				if (gbAtt.topicsOfRel.contains(agentExpertise.get(i))) {
					effect++;
				}
			}
			if ( (gbAtt.weight > 0 && effect >= gbAtt.weight) || (gbAtt.weight <= 0 && effect > Math.abs(gbAtt.weight)) ) {
				// The agent's vote would "change the sign" of the attack's weight.
				return -1;
			} else {
				// The agent's vote would weaken the attack's weight (or, at least, it would provoke no change),
				// but without "changing its sign".
				return 0;
			}
		}
	}
	
	/**
	 * Returns true (resp. false) if the agent can still (resp. cannot anymore) lie.
	 * Method used in the lying & hiding work.
	 * @return
	 */
	public boolean canStillLie() {
		return (liesMade.size() < liesBudget);
	}
	
	/**
	 *
	 */
	public boolean existsAttackInList(Attack att, ArrayList<Attack> list) {
		boolean exists = false;
		Attack currAtt;
		for (int i=0; i<list.size(); i++) {
			currAtt = list.get(i);
			if (att.firstArg == currAtt.firstArg && att.secondArg == currAtt.secondArg) {
				exists = true;
			}
		}
		return exists;
	}
	
	/**
	 *
	 */
	public boolean existsSupportInList(Support supp, ArrayList<Support> list) {
		boolean exists = false;
		Support currSupp;
		for (int i=0; i<list.size(); i++) {
			currSupp = list.get(i);
			if (supp.firstArg == currSupp.firstArg && supp.secondArg == currSupp.secondArg) {
				exists = true;
			}
		}
		return exists;
	}
	
	/**
	 * Returns true (resp. false) if the Attack "att" is (resp. isn't) found in the ArrayList "list",
	 * and its weight has the same sign.
	 * @return
	 */
	public boolean existsAttackSameWeightInList(Attack att, ArrayList<Attack> list) {
		boolean exists = false;
		Attack currAtt;
		for (int i=0; i<list.size(); i++) {
			currAtt = list.get(i);
			if (att.firstArg == currAtt.firstArg && att.secondArg == currAtt.secondArg) {
				// Do the attacks have the same sign of weight?
				if ((currAtt.weight > 0 && att.weight > 0) || (currAtt.weight <= 0 && att.weight <= 0))
					exists = true;
			}
		}
		return exists;
	}
	
	/**
	 * Returns true (resp. false) if the Support "supp" is (resp. isn't) found in the ArrayList "list",
	 * and its weight has the same sign.
	 * @return
	 */
	public boolean existsSupportSameWeightInList(Support supp, ArrayList<Support> list) {
		boolean exists = false;
		Support currSupp;
		for (int i=0; i<list.size(); i++) {
			currSupp = list.get(i);
			if (supp.firstArg == currSupp.firstArg && supp.secondArg == currSupp.secondArg) {
				// Do the supports have the same sign of weight?
				if ((currSupp.weight > 0 && supp.weight > 0) || (currSupp.weight <= 0 && supp.weight <= 0))
					exists = true;			}
		}
		return exists;
	}
	
	/**
	 * This function prints info about the agents.
	 */
	public void printAgent() {
		// TO DO: Finish this
		System.out.println("Agent " + this.agentName + " belongs in team " + this.team);
	}
	
}
