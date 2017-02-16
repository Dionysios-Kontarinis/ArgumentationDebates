package debateComponents;

import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * An object of this class represents a single debate configuration, 
 * which is basically a Gameboard (with its arguments, attacks, supports, topics) and a set of debating agents.
 * @author dennis
 * 
 */
public class Configuration {
	
	/**
	 * The common Gameboard of the debate.
	 */
	public Gameboard gb;
	/**
	 * The array containing all the agents in the debate.
	 */
	public ArrayList<Agent> agents;	
	/**
	 * The number of PRO agents in the debate (when: one issue).
	 */
	public int numPRO;
	/**
	 * The number of CON agents in the debate (when: one issue).
	 */
	public int numCON;
	
	
	/**
	 * Constructor (1/1).
	 */
	public Configuration () {
		// default constructor.
	}
	
	
	/**
	 * This method returns true if the majority of agents is PRO (or there is the same number of PRO and CON agents),
	 * and it returns false if the majority of agents is CON.
	 * @return
	 */
	public boolean isMajorityPRO() {
		if (numPRO >= agents.size()/2) return true;
		else return false;
	}
	
	/**
	 * This function prints all the topics in the "topics" array.
	 */
	public void printTopics(PrintWriter wr) {
		if (wr == null) {
			System.out.print("The set of all topics is: { ");
			for (int i=0; i<gb.topics.size(); i++) {
				System.out.print(gb.topics.get(i) + " ");
			}
			System.out.println("}");
			System.out.println();
		} else {
			wr.print("The set of all topics is: { ");
			for (int i=0; i<gb.topics.size(); i++) {
				wr.print(gb.topics.get(i) + " ");
			}
			wr.println("}");
			wr.println();
		}
	}
	
	/**
	 * This function prints all the arguments of the configuration, and their topics.
	 */
	public void printArgs(PrintWriter wr) {
		ArrayList<Argument> argList = gb.arguments;
		if (wr == null) {
			System.out.print("The set of all arguments is: { ");
			for (int i=0; i<argList.size(); i++) {
				System.out.print(argList.get(i).argID + " ");
			}
			System.out.print("}");
			System.out.println(" (and the issue is always argument 0).");
			System.out.println("The arguments' topics are: ");
			for (int i=0; i<argList.size(); i++) {
				System.out.print("top(" + argList.get(i).argID + ") = " + "{ ");
				for (int j=0; j<argList.get(i).topicsOfArg.size(); j++) {
					System.out.print(argList.get(i).topicsOfArg.get(j) + " ");
				}
				System.out.println("}");
			}
			System.out.println();
		} else {
			wr.print("The set of all arguments is: { ");
			for (int i=0; i<gb.arguments.size(); i++) {
				wr.print(i + " ");
			}
			wr.print("}");
			wr.println(" (and the issue is always argument 0).");
			wr.println("The arguments' topics are: ");
			for (int i=0; i<gb.arguments.size(); i++) {
				wr.print("top(" + i + ") = " + "{ ");
				for (int j=0; j<argList.get(i).topicsOfArg.size(); j++) {
					wr.print(argList.get(i).topicsOfArg.get(j) + " ");
				}
				wr.println("}");
			}
			wr.println();
		}
	}
	
	/**
	 * This function prints info about the agents (their expertise and the attacks which they consider valid).
	 */
	public void printAgents(PrintWriter wr) {
		if (wr == null) {
			System.out.println("There are " + agents.size() + " agents in the debate.");
			Agent currAg;
			for (int i=0; i<agents.size(); i++) {
				currAg = agents.get(i);
				// Print the agents' expertise.
				System.out.print("Agent " + currAg.agentName + " is expert in: { ");
				for (int j=0; j<currAg.agentExpertise.size(); j++) {
					System.out.print(currAg.agentExpertise.get(j) + " "); 
				}
				System.out.println("}");
				// Print the agents' attacks.
				System.out.println("Agent " + currAg.agentName + " has the following attacks:");
				for(int j=0; j<currAg.agentGB.attacks.size(); j++) {
					Attack currAtt = currAg.agentGB.attacks.get(j);
					System.out.println("(" + currAtt.firstArg + ", " + currAtt.secondArg + ") is of type " + currAtt.weight);
				}
				// Print the agent's played attacks.
				System.out.println("Agent " + currAg.agentName + " has already played on the following attacks:");
				for(int j=0; j<currAg.playedAtts.size(); j++) {
					Attack currAtt = currAg.playedAtts.get(j);
					System.out.println("(" + currAtt.firstArg + ", " + currAtt.secondArg + ")");
				}
				// Print the agent's team.
				System.out.println("Agent " + currAg.agentName + " belongs to team " + currAg.team + ".");
				System.out.println();
			}
		} else {
			wr.println("There are " + agents.size() + " agents in the debate.");
			Agent currAg;
			for (int i=0; i<agents.size(); i++) {
				currAg = agents.get(i);
				// Print the agents' expertise.
				wr.print("Agent " + currAg.agentName + " is expert in: { ");
				for (int j=0; j<currAg.agentExpertise.size(); j++) {
					wr.print(currAg.agentExpertise.get(j) + " "); 
				}
				wr.println("}");
				// Print the agents' attacks.
				wr.println("Agent " + currAg.agentName + " has the following attacks:");
				for(int j=0; j<currAg.agentGB.attacks.size(); j++) {
					Attack currAtt = currAg.agentGB.attacks.get(j);
					wr.println("(" + currAtt.firstArg + ", " + currAtt.secondArg + ") is of type " + currAtt.weight);
				}
				// Print the agent's played attacks.
				wr.println("Agent " + currAg.agentName + " has already played on the following attacks:");
				for(int j=0; j<currAg.playedAtts.size(); j++) {
					Attack currAtt = currAg.playedAtts.get(j);
					wr.println("(" + currAtt.firstArg + ", " + currAtt.secondArg + ")");
				}
				// Print the agent's team.
				wr.println("Agent " + currAg.agentName + " belongs to team " + currAg.team + ".");
				wr.println();
			}
		}
	}
	
	/**
	 * This function prints all the relevant info of the configuration.
	 * @param wr
	 */
	public void printAllInfo(PrintWriter wr) {
		if (wr == null) {
			printTopics(wr);
			printArgs(wr);
			// Print the info concerning the GB.
			gb.printGB(wr);
			//Print info on the GB's target sets.
			System.out.println("======================================================");
			gb.computeTargetSets();
			gb.printTargetSets(wr);
			System.out.println("======================================================");
			System.out.println();
			printAgents(wr);
			System.out.println();
		} else {
			printTopics(wr);
			printArgs(wr);
			gb.printGB(wr);
			//Print info on the GB's target sets.
			wr.println("======================================================");
			gb.computeTargetSets();
			gb.printTargetSets(wr);
			wr.println("======================================================");
			wr.println();
			printAgents(wr);
			wr.println();
		}
	}
	
}
