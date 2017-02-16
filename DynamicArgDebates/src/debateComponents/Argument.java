package debateComponents;

import java.util.ArrayList;

/**
 * This class contains the definition of an (abstract) argument.
 * It also contains the functionality (static attributes and method) for generating all the arguments, and the topics they refer to.
 * @author dennis
 *
 */
public class Argument {
	
	///////////////////////
	// Static attributes //
	///////////////////////
	
	// The number of arguments will be set according to these values.
	public static final int MIN_NUM_ARGS = 20;
	public static final int MAX_NUM_ARGS = 20;
	// An argument has a number of topics it refers to.
	public static final int MIN_TOPICS_PER_ARG = 1;
	public static final int MAX_TOPICS_PER_ARG = 1;
		
	
	///////////////////////////
	// Non-static attributes //
	///////////////////////////
	
	/**
	 * Each argument has a different argID.
	 */
	public int argID;
	/**
	 * The weight of the argument.
	 * weight == +10000  : The argument is fixed, non-removable.
	 * weight > 0 		 : The argument is removable.
	 * weight == 0 		 : The argument is addable.
	 * (In settings where fixed, removable and addable arguments are considered, such as in the CLIMA'14 work).
	 */
	public double weight;
	/**
	 * The topics that the argument refers to.
	 */
	public ArrayList<String> topicsOfArg;
	/**
	 * The numerical evaluation of an argument. We'll assume that a non-evaluated argument has an evaluation of -10, by default.
	 */
	public double eval;
//	// The following two attributes indicate how many moves have been played (in a debate, by an agent) for/against this argument.  
//	public int numberOfMovesFor;
//	public int numberOfMovesAgainst;

	
	////////////////////
	// Static methods //
	////////////////////
	
	/**
	 * This static method generates a (random) number of arguments and puts them into the "args" ArrayList (first parameter).
	 * It makes some arguments "fixed", and others "addable".
	 * Also, it generates a (random) number of topics, and puts them into the "topics" array (second parameter).
	 * workType == 0: CLIMA'14 work.
	 * workType == 1: Lying & hiding work.
	 */
	public static void generateArgsAndTopics(ArrayList<Argument> args, ArrayList<String> topics, int workType) {
		
		// First, randomly choose how many arguments will be generated.
		int numberOfArgs = MIN_NUM_ARGS + (int) (Math.random() * (MAX_NUM_ARGS - MIN_NUM_ARGS + 1));
		
		// Second, generate all the topics of expertise (their number depends on the number of arguments).
		Expertise.generateTopics(numberOfArgs, topics, workType);
		
		// Third, generate all the arguments (with their argIDs, weights, topics etc.)
		// Their argIDs are: 0, 1, 2, etc.
		for (int i=0; i<numberOfArgs; i++) {
			// Fill the "args" ArrayList with newly created arguments.
			double random = Math.random();
			if (random < 0.0) {
				// There's a 0% probability that the new argument is "fixed".
				args.add(new Argument(i, 10000, topics));
			} else {
				// There's a 100% probability that the new argument is "addable".
				args.add(new Argument(i, 0, topics));
			}
		}
	}
	
	
	////////////////////////
	// Non-static methods //
	////////////////////////
	
	/**
	 * Constructor (1/1).
	 * Creates an argument. By default its weight is 0 (it's an addable argument).
	 */
	public Argument(int id, double w, ArrayList<String> topics) {
		argID = id;
		weight = w;
		eval = -10;
		topicsOfArg = Expertise.generateSubsetTopics(MIN_TOPICS_PER_ARG, MAX_TOPICS_PER_ARG, topics);
	}
	
	/**
	 * Returns the argument in the Arraylist "arglist" which has "argID".
	 * Otherwise, it returns null. 
	 */
	public static Argument getArg(int argID, ArrayList<Argument> arglist) {
		for (int i=0; i<arglist.size(); i++) {
			Argument currArg = arglist.get(i);
			if (currArg.argID == argID) {
				return currArg;
			}
		}
		return null;
	}
	
//	/**
//	 * This function prints info on all the generated arguments, and their topics.
//	 */
//	public static void printArgs() {
//	}
	
}
