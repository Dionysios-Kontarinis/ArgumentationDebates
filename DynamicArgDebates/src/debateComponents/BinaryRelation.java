package debateComponents;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Both the class Attack and the class Support are children of this class.
 * @author dennis
 *
 */
public class BinaryRelation {

	// Another choice would be, instead of "int", to define these 2 attributes as "Argument" (to do later).
	public int firstArg;
	public int secondArg;
	/**
	 * The weight of the relation.
	 * weight == +10000  : The relation is in the set R / R^- (it is a fixed, non-removable relation).
	 * weight > 0 		 : The relation is in the set R^- (it is a removable relation).
	 * weight == 0 		 : The relation is in the set R^+ (it is an addable relation).
	 * (In settings where fixed, removable and addable relations are considered, such as in the CLIMA'14 work).
	 */
	public double weight;
	public ArrayList<String> topicsOfRel;
	
	
	/**
	 * Constructor (1/1).
	 */
	public BinaryRelation(int first, int second, double w, ArrayList<Argument> args) {
		firstArg = first;
		secondArg = second;
		weight = w;
		topicsOfRel = new ArrayList<String>();
		// Given the two arguments' sets of topics, the topics of the relation is the (multi-set) union of these sets.
		for (int i=0; i<args.get(firstArg).topicsOfArg.size(); i++) {
			topicsOfRel.add(args.get(firstArg).topicsOfArg.get(i));
		}
		for (int i=0; i<args.get(secondArg).topicsOfArg.size(); i++) {
			topicsOfRel.add(args.get(secondArg).topicsOfArg.get(i));
		}
	}
	
	/**
	 * This function is used to "add/remove" relations (mainly attacks) from a system.
	 * It's used to compute target sets.
	 * Method used in the CLIMA'14 work.
	 */
	public void changeWeightSign() {
		if (weight >= 10000) {
			System.err.println("This function must be only called for modifiable relations!");
		} else if (weight != 0 && weight != 0.5) {
			// Simply change the sign of the relation's weight.
			// If the weight was positive (resp. negative), then the relation is "removed" ("inserted").
			weight = -weight;
		} else if (weight == 0) {
			// We must "insert" this relation, but also remember that its weight was 0. 
			// So, we do the following: we set its new weight to +0.5; this way we'll know that it has to be reset to 0.
			weight = (float) 0.5;
		} else {
			// Here, obligatorily the weight is +0.5, so it must be reset to 0.
			weight = 0;
		}
	}
	
	/**
	 * This function prints the two arguments, as well as the relation's weight.
	 */
	public void printRelation(PrintWriter wr) {
		if (wr == null) {
			System.out.print("<(" + firstArg + ", " + secondArg + "), " + weight + ">");
		} else {
			wr.print("<(" + firstArg + ", " + secondArg + "), " + weight + ">");
		}
	}
	
}
