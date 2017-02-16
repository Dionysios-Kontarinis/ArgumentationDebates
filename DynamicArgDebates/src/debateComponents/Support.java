package debateComponents;
import java.util.ArrayList;


/**
 * This class contains the definition of a binary support between arguments.
 * @author dennis
 *
 */
public class Support extends BinaryRelation {
	
	public Support(int first, int second, double w, ArrayList<Argument> args) {
		super(first, second, w, args);
	}
	
}

