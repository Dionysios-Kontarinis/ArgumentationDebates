package debateComponents;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * This class contains the definition of the debate's backbone, the Gameboard (GB).
 * @author dennis
 *
 */
public class Gameboard {
	
	///////////////////////
	// Static attributes //
	///////////////////////
	
	// In case we create a GB which is a tree, then the 2 following values are needed.
	// We may consider that with [min=1 and max=3] we get a "long" tree, while with [min=3 and max=5] we get a "bush".
	static int MIN_BRANCHING_FACTOR = 0;
	static int MAX_BRANCHING_FACTOR = 4;
	// The number of addable attacks in the system must be limited (otherwise there's danger of computational explosion when computing target sets).
	static int MIN_ADDABLE_ATTACKS = 3;
	static int MAX_ADDABLE_ATTACKS = 3;
	
	
	///////////////////////////
	// Non-static attributes //
	///////////////////////////
	
	/**
	 * The ArrayList with all the arguments of the Gameboard.
	 */
	public ArrayList<Argument> arguments;
	/**
	 * The ArrayList with all the topics of the Gameboard.
	 */
	public ArrayList<String> topics;
	/**
	 * The ArrayList with all the attacks of the Gameboard.
	 */
	public ArrayList<Attack> attacks;
	/**
	 * The ArrayList with all the supports of the Gameboard.
	 */
	public ArrayList<Support> supports;
	
	/**
	 * The ArrayList containing the subset of attacks which can be added/removed from the Gameboard.
	 * This attribute is redundant (we can always check an attack's weight in order to decide if it's modifiable or not).
	 * Nonetheless, it helps speed-up the computation of target sets. 
	 */
	ArrayList<Attack> attackMod;
	/**
	 * If a binary acceptability semantics is used (eg. the grounded extension), then the value of "statusIssue" is either true (IN), or false (OUT).
	 */
	public boolean statusIssue;
	/**
	 * If a multi-valued acceptability semantics is used (eg. the QUAD evaluation), then the number "evalIssue" indicates how much the issue is accepted.
	 */
	double evalIssue;
	/**
	 * The target sets for changing the status of the issue (from its current value).
	 */
	public ArrayList<ArrayList<Attack>> targetSets;
	
	
	/**
	 * Constructor (1/4).
	 * workType == 0: CLIMA'14 work.
	 * workType == 1: Lying & hiding work.
	 * 
	 */
	public Gameboard(boolean isGBTree, boolean isGBDummy, int workType) {
		// Initialize these variables.
		arguments = new ArrayList<Argument>();
		topics = new ArrayList<String>();
		attacks = new ArrayList<Attack>();
		supports = new ArrayList<Support>();
		attackMod = new ArrayList<Attack>();
		
		// Generate all the arguments (and their topics).
		// Reminder: some of them will be fixed, while others will be addable.
		Argument.generateArgsAndTopics(arguments, topics, workType);
		
		if (workType == 0) {
			/**
			 * CLIMA'14.
			 * No supports are generated!
			 * If (isGBDummy == false), then it creates a Gameboard, where:
			 * Not every argument pair gives rise to an attack.
			 * The attack density is 0.1.
			 * The number of generated addable attacks is random (but it falls into a specific interval).
			 * If (dummy == true), then it creates a Gameboard, where all argument pairs give rise to addable attacks.
			 * This is useful for computing the Merged WAS.
			 * We have the choice of either generating a tree, or a graph.
			 */
			if (isGBDummy) {
				// Create an "empty" Gameboard (every pair of arguments gives rise to a attack of weight 0).
				// The main utility of this choice is for the computation of the Merged WAS (in function Agent.computeMergedWAS()).
				for (int i=0; i<arguments.size(); i++) {
					for (int j=0; j<arguments.size(); j++) {
						// We do not want to create self-attacking arguments.
						if (i != j) {
							// We only create addable attacks, with weight = 0.
							Attack newAtt = new Attack(i,j,0,arguments);
							attacks.add(newAtt);
							attackMod.add(newAtt);
						}
					}
				}
			} else {
				// Generate the attacks appearing on the Gameboard.
				if (!isGBTree) {
					//////////////////////////////
					// HERE WE GENERATE A GRAPH.//
					//////////////////////////////
					double weight;
					double random;
					// Initially generate some fixed attacks (and some non-attacks).
					for (int i=0; i<arguments.size(); i++) {
						for (int j=0; j<arguments.size(); j++) {
							// We do not want to create self-attacking arguments.
							if (i != j) {
								random = Math.random();
								if (random < 0.10) {
									weight = 10000; 	// Create a fixed attack.			
									Attack newAtt = new Attack(i,j,weight,arguments);
									attacks.add(newAtt);
								}
							} 
						}
					}
					// Finally, some of the attacks will be passed from fixed into addable.
					int numAddable = MIN_ADDABLE_ATTACKS + (int) (Math.random() * (MAX_ADDABLE_ATTACKS - MIN_ADDABLE_ATTACKS + 1));
					numAddable = Math.min(numAddable, attacks.size());
					while (numAddable > 0) {
						int attToChange = (int) (Math.random() * attacks.size());
						if (attacks.get(attToChange).weight == 10000) {
							attacks.get(attToChange).weight = 0;
							attackMod.add(attacks.get(attToChange));
							numAddable--;
						}
					}
				} else {
					/////////////////////////////
					// HERE WE GENERATE A TREE.//
					/////////////////////////////
					// Every node which will become a parent is put in the parentNodes ArrayList.
					ArrayList<Integer> parentNodes = new ArrayList<Integer>();
					// The root of the tree is the (only) issue of the debate.
					parentNodes.add(0);
					int childToCreate = 1;
					while (childToCreate < arguments.size()) {
						// Take the next argument for which some children will be generated.
						int currParent = parentNodes.get(0);
						// Find the number of children which will be generated.
						int numChildren = MIN_BRANCHING_FACTOR + (int) (Math.random() * (MAX_BRANCHING_FACTOR - MIN_BRANCHING_FACTOR + 1));
						if (numChildren == 0 && parentNodes.size() == 1) {
							// This continue is needed, in case the currParent node was attributed 0 children, and it is the last node in the parentNodes ArrayList,
							// but, at the same time, the number of nodes of the tree has not been reached yet.
							continue;
						}
						// Create the children.
						for (int i=0; i<numChildren; i++) {
							if (childToCreate < arguments.size()) {
								// Generate the weight's attack.
								int weight = 10000;
								Attack newAtt = new Attack(childToCreate,currParent,weight,arguments);
								attacks.add(newAtt);
								// The generated argument may later be attributed some children, so we add it in the parentNodes ArrayList.
								parentNodes.add(childToCreate);
								// Consider the next argument/child to be created.
								childToCreate++;
							}
						}
						// Remove from the parentNodes ArrayList the argument for which some children have just been generated.
						parentNodes.remove(0);
					}
					// Finally, some of the attacks will be passed from fixed into addable.
					int numAddable = MIN_ADDABLE_ATTACKS + (int) (Math.random() * (MAX_ADDABLE_ATTACKS - MIN_ADDABLE_ATTACKS + 1));
					numAddable = Math.min(numAddable, attacks.size());
					while (numAddable > 0) {
						int attToChange = (int) (Math.random() * attacks.size());
						if (attacks.get(attToChange).weight == 10000) {
							attacks.get(attToChange).weight = 0;
							attackMod.add(attacks.get(attToChange));
							numAddable--;
						}
					}
				}
			}
		} else if (workType == 1) {
			/**
			 * Lying & hiding work.
			 * Both attacks and supports are generated. 
			 * If (isDummy == false), then not every argument pair gives rise to the creation of an attack (or support).
			 * The relation density is 0.1.
			 * All the generated attacks and supports have weight = 0.
			 * If (dummy == true), then it creates a Gameboard, where all argument pairs give rise to attacks and supports.
			 * This is useful for computing the Merged WAS.
			 * We have the choice of either generating a tree, or a graph.
			 */
			// If (isGBDummy == true), then create a GB where every pair of arguments gives rise to an attack (of weight 0), and to a support (of weight 0).
			// We may use this for computing the Merged WAS (in function Agent.computeMergedWAS()).
			if (isGBDummy) {
				for (int i=0; i<arguments.size(); i++) {
					for (int j=0; j<arguments.size(); j++) {
						// We do not want to create self-attacking (or self-supporting) arguments.
						if (i != j) {
							// We only create addable attacks (and supports), with weight == 0.
							Attack newAtt = new Attack(i,j,0,arguments);
							attacks.add(newAtt);
							attackMod.add(newAtt);
							Support newSupp = new Support(i,j,0,arguments);
							supports.add(newSupp);
						}
					}
				}
			} else {
				// Finally, generate the attacks & supports of the Gameboard,
				// according to whether the graph is a tree or not.
				if (!isGBTree) {
					//////////////////////////////
					// HERE WE GENERATE A GRAPH.//
					//////////////////////////////
					double relationDensity = 0.1;
					double random;
					// In the loop that follows, we don't use the attributes MIN_ADDABLE_ATTACKS, and MAX_ADDABLE_ATTACKS, because:
					// (1) We don't want "fixed" attacks on the Gameboard.
					// (2) With "addable" attacks and supports we don't risk a computational explosion, since we'll not need to compute target sets.
					for (int i=0; i<arguments.size(); i++) {
						for (int j=0; j<arguments.size(); j++) {
							// We do not want to create self-attacking (or self-supporting) arguments.
							if (i != j) {
								random = Math.random();
								if (random < relationDensity) {
									if (random < relationDensity/2) {
										// The relation to create is 50% an attack, and 50% a support.
										// Create an addable attack.			
										Attack newAtt = new Attack(i,j,0,arguments);
										attacks.add(newAtt);
										attackMod.add(newAtt);
									} else {
										// Create an addable support.			
										Support newSupp = new Support(i,j,0,arguments);
										supports.add(newSupp);
									}
								}
							} 
						}
					}		
				} else {
					/////////////////////////////
					// HERE WE GENERATE A TREE.//
					/////////////////////////////
					// Every node which is to become a parent is put in the "parentNodes" ArrayList.
					ArrayList<Integer> parentNodes = new ArrayList<Integer>();
					// The root of the tree is the (single) issue of the debate.
					parentNodes.add(0);
					int childToCreate = 1;
					while (childToCreate < arguments.size()) {
						// Take the next argument for which some children will be generated.
						int currParent = parentNodes.get(0);
						// Find the number of children which will be generated.
						int numChildren = MIN_BRANCHING_FACTOR + (int) (Math.random() * (MAX_BRANCHING_FACTOR - MIN_BRANCHING_FACTOR + 1));
						if (numChildren == 0 && parentNodes.size() == 1) {
							// This continue is needed, in case the currParent node was attributed 0 children, and it is the last node in the parentNodes ArrayList,
							// but, at the same time, the number of nodes of the tree has not been reached yet.
							continue;
						}
						// Create the children.
						for (int i=0; i<numChildren; i++) {
							if (childToCreate < arguments.size()) {
								double random = Math.random();
								if (random < 0.5) {
									// Generate an addable attack.
									Attack newAtt = new Attack(childToCreate,currParent,0,arguments);
									attacks.add(newAtt);
									attackMod.add(newAtt);
								} else {
									// Generate an addable support.
									Support newSupp = new Support(childToCreate,currParent,0,arguments);
									supports.add(newSupp);
								}
								// The generated argument may later be attributed some children, so we add it in the parentNodes ArrayList.
								parentNodes.add(childToCreate);
								// Consider the next argument/child to be created.
								childToCreate++;
							}
						}			
						// Remove from the parentNodes ArrayList the argument for which some children have just been generated.
						parentNodes.remove(0);
					}		
				}
			}
		}
		// In every case, compute the status of the issue.
		if (computeGrounded().contains(0)) {
			// We remind that the issue is argument 0.
			statusIssue = true;
		} else {
			statusIssue = false;
		}
		// Also, in every case, compute all the arguments' evaluations (and set "evalIssue").
		computeArgEvaluations();
		evalIssue = arguments.get(0).eval;
	}
	
	/**
	 * Constructor (2/4).
	 * It takes a Gameboard ("refGB") as a parameter, and based on it, it generates another Gameboard.
	 * workType == 0: CLIMA'14 work.
	 * workType == 1: Lying & hiding work.
	 * 
	 */
	public Gameboard(Gameboard refGB, int workType) {
		
		arguments = new ArrayList<Argument>();
		topics = new ArrayList<String>();
		attacks = new ArrayList<Attack>();
		supports = new ArrayList<Support>();
		attackMod = new ArrayList<Attack>();

		// Use a reference to the ArrayList with the topics of refGB (no problem, they won't change anyway).
		topics = refGB.topics;

		if (workType == 0) {
			/**
			 * CLIMA'14.
			 * Here we generate an Agent's Gameboard directly from a Gameboard.
			 * For every argument, attack, and support of "refGB", the constructor replicates it in the new Gameboard, and:
			 * (1) All the fixed elements remain fixed in the new GB.
			 * (2) For every addable attack of the old GB: 50% chance it gets weight 0, and 50% chance it gets weight 1 (removable).
			 */
			// Replicate the arguments.
			for (int i=0; i<refGB.arguments.size(); i++) {
				Argument currArg = refGB.arguments.get(i);
				if (currArg.weight == 10000) {
					arguments.add(new Argument(currArg.argID, 10000, currArg.topicsOfArg));
				} else {
					double random = Math.random();
					int newWeight;
					if (random < 0.5) newWeight = 0;
					else newWeight = 1; // removable
					arguments.add(new Argument(currArg.argID, newWeight, currArg.topicsOfArg));
				}
			}
			// Replicate the attacks.
			for (int i=0; i<refGB.attacks.size(); i++) {
				Attack currAtt = refGB.attacks.get(i);
				if (currAtt.weight == 10000) {
					attacks.add(new Attack(currAtt.firstArg, currAtt.secondArg, 10000, arguments));
				} else {
					double random = Math.random();
					int newWeight;
					if (random < 0.5) newWeight = 0;
					else newWeight = 1; // removable attack
					Attack newAtt = new Attack(currAtt.firstArg, currAtt.secondArg, newWeight, arguments);
					attacks.add(newAtt);
					attackMod.add(newAtt);
				}
			}
			// Replicate the supports.
			for (int i=0; i<refGB.supports.size(); i++) {
				Support currSupp = refGB.supports.get(i);
				if (currSupp.weight == 10000) {
					supports.add(new Support(currSupp.firstArg, currSupp.secondArg, 10000, arguments));
				} else {
					double random = Math.random();
					int newWeight;
					if (random < 0.5) newWeight = 0;
					else newWeight = 1; // removable support
					supports.add(new Support(currSupp.firstArg, currSupp.secondArg, newWeight, arguments));
				}
			}			
		} else if (workType == 1) {
			/**
			 * Lying & hiding work.
			 * Here we generate the Gameboard of an AgentType object, based on the reference GB.
			 * Every element (argument, attack, and support) of "refGB" is replicated in new Gameboard.
			 * Every element of the new Gameboard has a 50% chance to get weight 0, and a 50% chance to get weight 1.
			 */
			// Replicate the arguments (make them either fixed or addable).
			for (int i=0; i<refGB.arguments.size(); i++) {
				double random = Math.random();
				int newWeight;
				if (random < 0.5) newWeight = 0;
				else newWeight = 1;
				Argument currArg = refGB.arguments.get(i);
				arguments.add(new Argument(currArg.argID, newWeight, currArg.topicsOfArg));
			}
			// Replicate the attacks (make them either fixed or addable).
			for (int i=0; i<refGB.attacks.size(); i++) {
				double random = Math.random();
				int newWeight;
				if (random < 0.5) newWeight = 0;
				else newWeight = 1;
				Attack currAtt = refGB.attacks.get(i);
				Attack newAtt = new Attack(currAtt.firstArg, currAtt.secondArg, newWeight, arguments);
				attacks.add(newAtt);
				if (newAtt.weight == 0) attackMod.add(newAtt);
			}
			// Replicate the supports (make them either fixed or addable).
			for (int i=0; i<refGB.supports.size(); i++) {
				double random = Math.random();
				int newWeight;
				if (random < 0.5) newWeight = 0;
				else newWeight = 1;
				Support currSupp = refGB.supports.get(i);
				supports.add(new Support(currSupp.firstArg, currSupp.secondArg, newWeight, arguments));
			}
		}
		// In every case, compute the status of the issue.
		if (computeGrounded().contains(0)) {
			// We remind that the issue is argument 0.
			statusIssue = true;
		} else {
			statusIssue = false;
		}
		// Compute the arguments' evaluations (and set "evalIssue").
		computeArgEvaluations();
		evalIssue = arguments.get(0).eval;
	}
	
	/**
	 * Constructor (3/4).
	 * It takes an AgentType as its parameter, and based on it (essentially on its Gameboard), it generates a new Gameboard.
	 * We use this Constructor to generate the Gameboard of an Agent object, who belongs in some AgentType type.
	 * The generated GB must agree in p% of all the elements in AgentType's GB. 
	 * Constructor used in lying and hiding work.
	 */
	public Gameboard(AgentType agType) {
		// We will be based on the Gameboard of the type (parameter).
		Gameboard refGB = agType.typeGB;
		
		// Instantiate the attributes.
		arguments = new ArrayList<Argument>();
		topics = new ArrayList<String>();
		attacks = new ArrayList<Attack>();
		supports = new ArrayList<Support>();
		attackMod = new ArrayList<Attack>();
		
		// Use a reference to the ArrayList with the topics of refGB (no problem, they won't change anyway).
		topics = refGB.topics;
		
		// Randomly choose the disagreement percentage (useful for arguments, attacks, supports).
		int disagrPrcntg = AgentType.MIN_DISAGREEMENT_PRCNTG +
				(int) (Math.random() * (AgentType.MAX_DISAGREEMENT_PRCNTG - AgentType.MIN_DISAGREEMENT_PRCNTG + 1));
		
		double newWeight = 0;
		// Replicate the arguments (weight 0 or 1).
		for (int i=0; i<refGB.arguments.size(); i++) {
			Argument currArg = refGB.arguments.get(i);
			newWeight = currArg.weight;
			// Will we change this argument's weight (from 0 to 1, or from 1 to 0)?
			if (Math.random() * 100 < disagrPrcntg) {
				// We'll change the weight.
				if (currArg.weight == 0) newWeight = 1; 
				else newWeight = 0;
			}
			arguments.add(new Argument(currArg.argID, newWeight, currArg.topicsOfArg));
		}
		// Replicate the attacks (weight 0 or 1).
		for (int i=0; i<refGB.attacks.size(); i++) {
			Attack currAtt = refGB.attacks.get(i);
			newWeight = currAtt.weight;
			// Will we change this attack's weight (from 0 to 1, or from 1 to 0)?
			if (Math.random() * 100 < disagrPrcntg) {
				// We'll change this attack's weight.
				if (currAtt.weight == 0) newWeight = 1; 
				else newWeight = 0;
			}
			Attack newAtt = new Attack(currAtt.firstArg, currAtt.secondArg, newWeight, arguments);
			attacks.add(newAtt);
			attackMod.add(newAtt);
		}
		// Replicate the supports (weight 0 or 1).
		for (int i=0; i<refGB.supports.size(); i++) {
			Support currSupp = refGB.supports.get(i);
			newWeight = currSupp.weight;
			// Will we change this support's weight (from 0 to 1, or from 1 to 0)?
			if (Math.random() * 100 < disagrPrcntg) {
				// We'll change this support's weight.
				if (currSupp.weight == 0) newWeight = 1; 
				else newWeight = 0;
			}
			supports.add(new Support(currSupp.firstArg, currSupp.secondArg, newWeight, arguments));
		}
		// In every case, compute the status of the issue.
		if (computeGrounded().contains(0)) {
			// We remind that the issue is argument 0.
			statusIssue = true;
		} else {
			statusIssue = false;
		}
		// Compute the arguments' evaluations (and set "evalIssue").
		computeArgEvaluations();
		evalIssue = arguments.get(0).eval;
	}
	
	/**
	 * Constructor (4/4).
	 * We "manually" generate a Gameboard, with a specific structure.
	 * A priori this constructor is not used, but it could be useful in specific cases (e.g. for debugging).
	 */
	public Gameboard(Configuration config) {
		attacks = new ArrayList<Attack>();
		attackMod = new ArrayList<Attack>();
		// Generate the attacks appearing on the Gameboard.
		Attack att;
		att = new Attack(2,0,0,arguments);
		attacks.add(att);
		attackMod.add(att);
	//	att = new Attack(3,0,0,config);
	//	attacks.add(att);
	//	attackMod.add(att);
		att = new Attack(3,2,10000,arguments);
		attacks.add(att);
		att = new Attack(4,2,10000,arguments);
		attacks.add(att);
		att = new Attack(5,2,10000,arguments);
		attacks.add(att);
		att = new Attack(6,3,0,arguments);
		attacks.add(att);
		attackMod.add(att);
		att = new Attack(7,4,0,arguments);
		attacks.add(att);
		attackMod.add(att);
		att = new Attack(8,5,0,arguments);
		attacks.add(att);
		attackMod.add(att);
		att = new Attack(1,0,0,arguments);
		attacks.add(att);
		attackMod.add(att);
		att = new Attack(10,0,0,arguments);
		attacks.add(att);
		attackMod.add(att);
		System.out.println("CONSTRUCTING THE ATTACK: ");
		att.printRelation(null);
		// Compute the status' issue.
		if (computeGrounded().contains(0)) {
			statusIssue = true;
		} else {
			statusIssue = false;
		}
		// Compute the arguments' evaluations (and set "evalIssue").
		computeArgEvaluations();
		evalIssue = arguments.get(0).eval;
	}
	
	
	/**
	 * This function can be called after a debate has ended, in order to reset the Gameboard, as it was initially.
	 * Useful (potentially) in our CLIMA'14 work in order to help us launch multiple debates (all starting with the same configuration). 
	 */
	public void resetGB() {
		// Properly reset the values of every argument, attack, and support.
		// Fixed elements are reset to weight=10000, while addable elements to weight=0.
		for (int i=0; i<arguments.size(); i++) {
			Argument currArg = arguments.get(i);
			if (currArg.weight >= 10000) {
				currArg.weight = 10000;
			} else {
				currArg.weight = 0;
			}
		}
		for (int i=0; i<attacks.size(); i++) {
			Attack currAtt = attacks.get(i);
			if (currAtt.weight >= 10000) {
				currAtt.weight = 10000;
			} else {
				currAtt.weight = 0;
			}
		}
		for (int i=0; i<supports.size(); i++) {
			Support currSupp = supports.get(i);
			if (currSupp.weight >= 10000) {
				currSupp.weight = 10000;
			} else {
				currSupp.weight = 0;
			}
		}
		// Compute the status' issue.
		if (computeGrounded().contains(0)) {
			// We remind that the issue is argument 0.
			statusIssue = true;
		} else {
			statusIssue = false;
		}
		// Compute the arguments' evaluations (and set "evalIssue").
		computeArgEvaluations();
		evalIssue = arguments.get(0).eval;
	}
	
	/**
	 * This function executes a move (which is a pair "relation - polarity") on the Gameboard.
	 * A move is a positive or a negative "vote" on an Attack or on a Support.
	 * For now, we implement this, as if all the arguments were already appearing on the GB, so we don't touch these Argument objects.
	 * Possible improvement: In order to avoid bugs related to forgetting the update of the "playedAtts" and "playedSupps" attributes,
	 * we could update these attributes here (when a move is actually played), instead of updating them in the agents' strategy functions (when a move is chosen)
	 * as it is currently done. 
	 */
	public void playMoveOnGB(BinaryRelation relation, Agent ag, boolean isPolarityPositive, boolean verbose) {
		if (relation == null) {
			// If the proposed move is a pass (null), then we do nothing.
			return;
		}
		// Calculate the move's impact, based on the voter's expertise on the relevant topics.
		double moveImpact = 0;
		for (int j=0; j<ag.agentExpertise.size(); j++) {
			if (relation.topicsOfRel.contains(ag.agentExpertise.get(j))) {
				moveImpact++;
			}
		}
		// The relation is either an Attack or a Support.
		if (relation instanceof Attack) {
			Attack currAtt;
			for (int i=0; i<attacks.size(); i++) {
				currAtt = attacks.get(i);
				if (currAtt.firstArg == relation.firstArg && currAtt.secondArg == relation.secondArg) {
//					// Previous approach (had to be CHANGED, because now AGENTS MAY LIE!!!
//					// The polarity of the vote is either + or -, according to whether the agent has that attack or not in his system.
//					if (move.weight>0) {
//						currAt.weight += moveImpact;
//						if (verbose) System.out.println("Agent " + ag.agentName + " played <(" + move.firstArg + "," + move.secondArg + "), " + moveImpact + ">");
//					}
//					else {
//						currAt.weight -= moveImpact;
//						if (verbose) System.out.println("Agent " + ag.agentName + " played <(" + move.firstArg + "," + move.secondArg +  "), " + -moveImpact + ">");
//					}
					if (isPolarityPositive) {
						currAtt.weight += moveImpact;
						if (verbose) System.out.println("Agent " + ag.agentName + " played the attack <(" + relation.firstArg + "," + relation.secondArg + "), " + moveImpact + ">");
					} else {
						// The move's polarity is negative.
						currAtt.weight -= moveImpact;
						if (verbose) System.out.println("Agent " + ag.agentName + " played the attack <(" + relation.firstArg + "," + relation.secondArg +  "), " + -moveImpact + ">");
					}
				}
			}
		} else {
			// The relation is a support.
			Support currSupp;
			for (int i=0; i<supports.size(); i++) {
				currSupp = supports.get(i);
				if (currSupp.firstArg == relation.firstArg && currSupp.secondArg == relation.secondArg) {
					if (isPolarityPositive) {
						currSupp.weight += moveImpact;
						if (verbose) System.out.println("Agent " + ag.agentName + " played the support <(" + relation.firstArg + "," + relation.secondArg + "), " + moveImpact + ">");
					} else {
						// The move's polarity is negative.
						currSupp.weight -= moveImpact;
						if (verbose) System.out.println("Agent " + ag.agentName + " played the support <(" + relation.firstArg + "," + relation.secondArg +  "), " + -moveImpact + ">");
					}
				}
			}
		}
		// After every (non-pass) move on the GB, we recalculate the status of the issue.
		// We remind that the issue is argument 0.
		statusIssue = (computeGrounded().contains(0));
//		if (verbose) System.out.println("The issue's status is now " + statusIssue);
		// Compute the arguments' evaluations (and set "evalIssue").
		computeArgEvaluations();
		evalIssue = arguments.get(0).eval;
		if (verbose) System.out.println("The issue's evaluation is now " + evalIssue);
		return;
	}
	
	/**
	 * This method plays a move, sent by an agent, on the Gameboard (but its weight is the opposite of what it should normally be).
	 * Possible improvement: In order to prevent confusion and bugs, first amend the method "strategyChangeIssue(gb)" (which is the only method calling this one)
	 * and then delete this method.
	 */
	public void takebackMoveOnGB(Attack move, Agent ag) {
		for (int i=0; i<attacks.size(); i++) {
			Attack currAt = attacks.get(i);
			if (currAt.firstArg == move.firstArg && currAt.secondArg == move.secondArg) {
				// Calculate the move's impact, based on the voter's expertise on the relevant topics.
				double moveImpact = 0;
				for (int j=0; j<ag.agentExpertise.size(); j++) {
					if (move.topicsOfRel.contains(ag.agentExpertise.get(j))) {
						moveImpact++;
					}
				}
				// The polarity of the vote is either + or -, according to whether the agent has that attack or not in his system.
				if (move.weight>0) {
					currAt.weight -= moveImpact;
//					System.out.println("The played move was <(" + move.firstArg + "," + move.secondArg + "), " + moveImpact + ">");
				}
				else {
					currAt.weight += moveImpact;
//					System.out.println("The played move was <(" + move.firstArg + "," + move.secondArg +  "), " + -moveImpact + ">");
				}
				// After every move on the GB, we recalculate the status of the issue.
				statusIssue = (ag.agentGB.computeGrounded().contains(0));
				// Compute the arguments' evaluations (and set "evalIssue").
				computeArgEvaluations();
				evalIssue = arguments.get(0).eval;
				return;
			}
		}
	}	
	
	/**
	 * This method computes the grounded extension of a Gameboard.
	 */
	public ArrayList<Integer> computeGrounded() {
		// The vector grounded will contain the arguments of the grounded extension.
		ArrayList<Integer> grounded = new ArrayList<Integer>();
		// The possiblyGr list contains the arguments which are (i) already in the grounded extension and (ii) 
		// possibly added in the grounded extension later, during the computation.
		ArrayList <Integer> possiblyGr = new ArrayList <Integer>();
		for (int i=0; i<arguments.size(); i++) {
			possiblyGr.add(i);
		}
		
		boolean isGroundedFound = false;
		ArrayList<Integer> newlyAddedGr = new ArrayList<Integer>();
		
		while (!isGroundedFound) {
			newlyAddedGr.clear();
			// During this loop, if at least one argument is put in grounded, then the computation hasn't finished yet.
			isGroundedFound = true;
			// For all the possiblyGr arguments, find those which must be put in grounded.
			for (int i=0; i<possiblyGr.size(); i++) {
				int currArg = possiblyGr.get(i);
				// Check if the currArg is already in the grounded extension.
				if (!grounded.contains(currArg)) {
					boolean addToGrounded = true;
					// If currArg receives an attack from an argument in possiblyGr, it won't be put in the extension now.
					for (int j=0; j<attacks.size(); j++) {
						Attack currAtt = attacks.get(j);
						if (currAtt.secondArg == currArg && possiblyGr.contains(currAtt.firstArg) && currAtt.weight>0) {
							addToGrounded = false;
						}
					}
					if (addToGrounded) {
						// Put currArg in the grounded extension.
						grounded.add(currArg);
						//System.out.println("ADDITION " + currArg);
						newlyAddedGr.add(currArg);
						isGroundedFound = false;
					}	
				}
			}
			// Then, find all the arguments which must be removed from the possiblyGr list. 
			for (int i=0; i<newlyAddedGr.size(); i++) {
				int currArg = newlyAddedGr.get(i);
				for (int j=0; j<attacks.size(); j++) {
					if (attacks.get(j).firstArg==currArg && attacks.get(j).weight>0) {
						// This argument is attacked by a member of the grounded extension, so it's "OUT".
						possiblyGr.remove((Integer)attacks.get(j).secondArg);
					}
				}
			}
		}
		return grounded;
	}
	
	/**
	 * This method computes all the target sets (for grounded acceptability) on the Gameboard.
	 * If currently the issue is "In"/"Out", then the goal is to make it "Out"/"In".
	 */
	public void computeTargetSets() {
		targetSets = new ArrayList<ArrayList<Attack>>();
		ArrayList<ArrayList<Integer>> targetSetsPointer = new ArrayList<ArrayList<Integer>>();
		// Initialize the modAttPointer list which helps us compute the possible combinations of attack changes.
		ArrayList<Integer> modAttPointer = new ArrayList<Integer>();
		for (int i=0; i<attackMod.size(); i++) {
			modAttPointer.add(0);
		}
		boolean existMore = true;
		while (existMore) {
//			System.out.println("exist more...");
			// Set up the next composition of the modAttPointer list. The 0 elements will not be changed, whereas the 1 elements will.
			for (int i=0; i<modAttPointer.size(); i++) {
				if (modAttPointer.get(i)==0) {
					modAttPointer.set(i, 1);
					break;
				} else {
					modAttPointer.set(i, 0);
				}
			}
			// Check whether this composition is a superset of a composition which is a target set (then there is no need to examine it).
			boolean isSupersetTSet = false;
			for (int i=0; i<targetSetsPointer.size() && !isSupersetTSet; i++) {
				isSupersetTSet = true;
				for (int j=0; j<targetSetsPointer.get(i).size(); j++) {
					if (targetSetsPointer.get(i).get(j) == 1 && modAttPointer.get(j) == 0) {
						isSupersetTSet = false;	
					}
				}
			}
			if (isSupersetTSet) {
//				for (int i=0; i<modAttPointer.size(); i++) {
//					System.out.print(modAttPointer.get(i) + " ");
//				}
//				System.out.println("   SUPERSET");
			}
			if (!isSupersetTSet) {
//				System.out.println("I have " + targetSetsPointer.size() + " target sets: ");
//				for (int i=0; i<targetSetsPointer.size(); i++) {
//					for (int j=0; j<targetSetsPointer.get(i).size(); j++) {
//						System.out.print(targetSetsPointer.get(i).get(j) + " ");
//					}
//				}
//				
//				System.out.println("Also I am currently analyzing the tset: ");
//				for (int i=0; i<modAttPointer.size(); i++) {
//					System.out.print(modAttPointer.get(i) + " ");
//				}
//				System.out.println("   NO-SUPERSET");
				// Using modAttPointer, change the weights of the "chosen" attacks.
				for (int i=0; i<modAttPointer.size(); i++) {
					if (modAttPointer.get(i) == 1) {
						attackMod.get(i).changeWeightSign();
					}
				}
				// Check if the status has changed, and in that case add the newly found target set into the targetSets list.
				// We remind that the issue is argument 0.
				// TO DO: Make this better (Argument class).
				if (statusIssue != computeGrounded().contains(0)) {
					ArrayList<Attack> tset = new ArrayList<Attack>();
					ArrayList<Integer> tsetPtr = new ArrayList<Integer>();
					for (int i=0; i<modAttPointer.size(); i++) {
						if (modAttPointer.get(i) == 1) {
							tset.add(attackMod.get(i));
							tsetPtr.add(1);
						} else {
							tsetPtr.add(0);
						}
					}
//					System.out.println("-----ADD A NEW TSET!------");
					targetSets.add(tset);
					targetSetsPointer.add(tsetPtr);
				}
				// Using modAttPointer, restore the old weight values of the attacks.
				for (int i=0; i<modAttPointer.size(); i++) {
					if (modAttPointer.get(i) == 1) {
						attackMod.get(i).changeWeightSign();
					}
				}
			}
			// Check if there are more possible combinations.
			existMore = false;
			for (int i=0; i<modAttPointer.size(); i++) {
				if (modAttPointer.get(i)==0) {
					existMore=true;
					break;
				}
			}
		}	
	}
	
	/**
	 * This method computes the evaluations of the GB's arguments. It's a generic method which starts from unattacked arguments, and 
	 * computes their evaluations on the basis of their direct attackers and defenders.
	 * Attention: It should not be used if the Gameboard contains cycles!
	 */
	public void computeArgEvaluations() {
		// We have to evaluate all the arguments in "gb".
		// First, we delete the arguments' previous evaluations.
		// If (eval == -10), then this argument is not currently evaluated.  
		for (int i=0; i<arguments.size(); i++) {
			arguments.get(i).eval = (double) -10.0;
		}
		int numEvaluated = 0;
		boolean fixPointReached = false;
		while (numEvaluated < arguments.size() && !fixPointReached) {
			// There are still arguments to be evaluated.
			fixPointReached = true; // This will remain like that, unless an argument is evaluated during this loop.
			for (int i=0; i<arguments.size(); i++) {
				Argument currArg = arguments.get(i);
				boolean needsEval = true;
				// We must not evaluate this argument if:
				// (1) It already has an evaluation
				if (currArg.eval != -10) needsEval = false;
				// (2) It has one attacker/supporter which is non-evaluated.
				ArrayList<Argument> attackers = new ArrayList<Argument>();
				ArrayList<Argument> supporters= new ArrayList<Argument>();
				Attack currAtt;
				// Find all its attackers and supporters.
				for (int j=0; j<attacks.size(); j++) {
					currAtt = attacks.get(j);
					if (currAtt.secondArg == currArg.argID && currAtt.weight > 0)  { // Reminder: the attack's weight must be positive.
						attackers.add(Argument.getArg(attacks.get(j).firstArg, arguments));
					}
				}
				Support currSupp;
				for (int j=0; j<supports.size(); j++) {
					currSupp = supports.get(j);
					if (currSupp.secondArg == currArg.argID && currSupp.weight > 0) { // Reminder: the support's weight must be positive.
						supporters.add(Argument.getArg(supports.get(j).firstArg, arguments));
					}
				}
				// Is there any attacker/supporter non-evaluated?
				for (int j=0; j<attackers.size(); j++) {
					if (attackers.get(j).eval == -10) needsEval = false; 
				}
				for (int j=0; j<supporters.size(); j++) {
					if (supporters.get(j).eval == -10) needsEval = false; 
				}
				if (needsEval) {
					// Evaluate this argument.
					currArg.eval = evaluateArgQUAD(currArg, attackers, supporters);
					numEvaluated++;
					fixPointReached = false;
				}
			}
		}
		return;
	}
	
	/**
	 * This method uses the QUAD argument evaluation algorithm, in order to evaluate an argument.
	 * The evaluation is based on the argument's base score, on its direct attackers, and on its direct supporters.
	 * @param arg
	 * @param attackers
	 * @param supporters
	 */
	public double evaluateArgQUAD(Argument arg, ArrayList<Argument> attackers, ArrayList<Argument> supporters) {
		// The value we must compute.
		double vFinal = 0.0;
		
		// We assume that every argument has a base score of 0.5.
		double vBase = (double) 0.5;
		
		double vAtt;
		// vAtt gets a "nil" value (here: -10) if either (1) the argument has no attackers, or (2) all its attackers have eval 0.
		boolean vAttIsNil = false;
		if (attackers.size()==0) {
			vAttIsNil = true;
		} else {
			vAttIsNil = true;
			for (int i=0; i<attackers.size(); i++) {
				if (attackers.get(i).eval != 0.0) vAttIsNil = false;
			}
		}
		if (vAttIsNil) {
			// vAtt has a "nil" value.
			vAtt = -10;
		} else {
			// Start from arg.eval, and repeatedly compute the "effect" of every attack.
			vAtt = vBase;
			for (int i=0; i<attackers.size(); i++) {
				vAtt = vAtt - (vAtt * attackers.get(i).eval);
			}
		}
		
		double vSupp;
		// vSupp gets a "nil" value (here: -10) if either (1) the argument has no supporters, or (2) all its supporters have eval 0.
		boolean vSuppIsNil = false;
		if (supporters.size()==0) {
			vSuppIsNil = true;
		} else {
			vSuppIsNil = true;
			for (int i=0; i<supporters.size(); i++) {
				if (supporters.get(i).eval != 0.0) vSuppIsNil= false;
			}
		}
		if (vSuppIsNil) {
			// vSupp has a "nil" value.
			vSupp = -10;
		} else {
			// Start from arg.eval, and repeatedly compute the "effect" of every support.
			vSupp = vBase;
			for (int i=0; i<supporters.size(); i++) {
				vSupp = vSupp + ((1 - vSupp) * supporters.get(i).eval);
			}
		}
		
		// Compute vFinal (based on vBase, vAtt, vSupp).
		if ((vAtt == -10) && (vSupp != -10)) {
			vFinal = vSupp;
		} else if ((vAtt != -10) && (vSupp == -10)) {
			vFinal = vAtt;
		} else if ((vAtt == -10) && (vSupp == -10)) {
			vFinal = vBase;
		} else {
			// Balance attack and support.
//			System.out.println("Balance attack & support.");
//			System.out.println("vAtt = " + vAtt);
//			System.out.println("vSupp = " + vSupp);
			vFinal = (vAtt + vSupp) / 2;
		}
		
		return vFinal;
	}
	
	
	////////////////
	// PRINT METHODS //
	///////////////////

	/**
	 * This function prints all the information of the GB (the weights of its attacks).
	 */
	public void printGB(PrintWriter wr) {
		System.out.println("====== Printing info on the GB: ======");
		if (wr == null) {
			System.out.println("The GB has the following attacks:");
			for(int i=0; i<attacks.size(); i++) {
				attacks.get(i).printRelation(wr);
				System.out.print(" ");
			}
			System.out.println();
			System.out.println("The GB has the following supports:");
			for(int i=0; i<supports.size(); i++) {
				supports.get(i).printRelation(wr);
				System.out.print(" ");

			}
			System.out.println();
			System.out.println("Under grounded semantics, status of issue = " + statusIssue);
			System.out.println("Under QUAD, evaluation of issue = " + evalIssue);
		} else {
			wr.println("The GB has the following attacks:");
			for(int i=0; i<attacks.size(); i++) {
				attacks.get(i).printRelation(wr);
			}
			wr.println("The GB has the following supports:");
			for(int i=0; i<supports.size(); i++) {
				supports.get(i).printRelation(wr);
			}
			wr.println("The status of the issue is: " + statusIssue);
			wr.println("The evaluation of the issue is: " + evalIssue);
			wr.println();
		}
		System.out.println("======================================");
	}

	/**
	 * This function prints all the target sets of the Gameboard.
	 */
	public void printTargetSets(PrintWriter wr) {
		if (wr == null) {
			System.out.println("The target sets of the Gameboard are: ");
			computeTargetSets();
			for (int i=0; i<targetSets.size(); i++) {
				ArrayList<Attack> tset = targetSets.get(i);
				System.out.println("Target set " + i + " :");
				for (int j=0; j<tset.size(); j++) {
					tset.get(j).printRelation(wr);
					System.out.println();
				}
			}
		} else {
			wr.println("The target sets of the Gameboard are: ");
			computeTargetSets();
			for (int i=0; i<targetSets.size(); i++) {
				ArrayList<Attack> tset = targetSets.get(i);
				wr.println("Target set " + i + " :");
				for (int j=0; j<tset.size(); j++) {
					tset.get(j).printRelation(wr);
					wr.println();
				}
			}
		}
	}
	
}
