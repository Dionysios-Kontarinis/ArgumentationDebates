package debateComponents;

import java.util.ArrayList;

/**
 * This class contains the basic functionality for generating:
 * (1) the topics of the arguments, and
 * (2) the topics of expertise of the agents.
 * It contains only 2 static methods.
 * @author dennis
 *
 */
public class Expertise {
	
	// With the next two attributes we set the minimum and maximum number of topics in the debate.
	// Attributes used in the lying & hiding work.
	// The CLIMA'14 work "overrides" these attributes, and it sets: #topics = 1/3 * #arguments. 
	public static int MIN_NUM_TOPICS = 1;
	public static int MAX_NUM_TOPICS = 1;	
	
	////////////////////
	// Static methods //
	////////////////////
	
	/**
	 * This function receives an ArrayList<String> named "topics" (2nd parameter), and fills it with topics (Strings) that it generates.
	 * The topics have names: "t1", "t2", "t3", etc.
	 * workType == 0: For the CLIMA'14 work, we create a number of topics which is a fraction (1/3) of the number of arguments.
	 * workType == 1: For the malicious agents work, we create a number of topics which has no relation to the number of arguments.
	 */
	public static void generateTopics(int numberOfArgs, ArrayList<String> topics, int workType) {
		int numTopics = 0;
		if (workType == 0) {
			// For the CLIMA'14 work, we choose the number of topics to be the 1/3 of the number of arguments.
			numTopics = (numberOfArgs / 3);
		} else if (workType == 1) {
			// For the malicious agents work.
			numTopics = MIN_NUM_TOPICS + (int) (Math.random() * (MAX_NUM_TOPICS - MIN_NUM_TOPICS + 1)); 
		}
		// Generate the topics ("t1", "t2", "t3", etc.) and fill the "topics" list.
		for (int i=0; i<numTopics; i++) {
			topics.add("t"+(i+1));
		}
	}
	
	/**
	 * This function returns a subset of the topics appearing in "topics" (2nd parameter).
	 * The cardinality of the generated subset of topics is randomly set between a "min" and a "max" value, which are passed as parameters.
	 * It's useful in order to: generate the topics of specific arguments, and generate the expertise of specific agents.
	 * @return
	 */
	public static ArrayList<String> generateSubsetTopics(int min, int max, ArrayList<String> topics) {
		// The subset of topics will contain "numSubset" topics in total.
		int numSubset = min + (int) (Math.random() * (max - min + 1));
		// Attention: we must take the minimum value between "numSubset" and "topics.size()" (because it can happen that numSubset > topics.size()).
		numSubset = Math.min(numSubset, topics.size());
		ArrayList<String> subsetTopicsList = new ArrayList<String>();
		while (numSubset > 0) {
			int chosenTopic = 1 + (int) (Math.random() * (topics.size() - 1 + 1));
			if (!subsetTopicsList.contains("t" + chosenTopic)) {
				// If this "chosenTopic" hasn't already been chosen, then add it to the list of topics.
				subsetTopicsList.add("t" + chosenTopic);
				numSubset--;
			}
		}
		return subsetTopicsList;
	}
	
//	/**
//	 * This function prints all the topics in the "topics" array.
//	 */
//	public static void printTopics() {
//	}
	
}
