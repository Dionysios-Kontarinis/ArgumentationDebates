package debateComponents;
import java.util.ArrayList;


/**
 * This class contains the definition of a binary attack between arguments.
 * @author dennis
 *
 */
public class Attack extends BinaryRelation {

	public Attack(int first, int second, double w, ArrayList<Argument> args) {
		super(first, second, w, args);
	}
	
}
