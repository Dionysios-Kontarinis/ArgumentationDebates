package debateComponents;

/**
 * An object of this class represents an agent move, which has 2 elements: a binary relation (attack or support) and a polarity.
 * Class used in the lying & hiding work.
 * @author dennis
 *
 */
public class Move {

	public BinaryRelation relationOfMove;
	public boolean polarity;
	
	public Move(BinaryRelation rel, boolean pol) {
		relationOfMove = rel;
		polarity = pol;
	}

}
