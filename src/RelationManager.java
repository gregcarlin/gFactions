import java.util.ArrayList;


public class RelationManager {
	private final ArrayList<Relation> relations = new ArrayList<Relation>();
	
	/**
	 * Returns the relation between two factions.
	 * 
	 * @param one The first faction.
	 * @param two The second faction.
	 * @return Relation.Type
	 */
	public Relation.Type getRelation(Faction one, Faction two) {
		if(one == null || two == null) {
			return Relation.Type.NEUTRAL;
		} else if(one.equals(two)) {
			return Relation.Type.SAME;
		}
		for(Relation r : relations) {
			if(r.getOne().equals(one) && r.getTwo().equals(two)) {
				return r.type;
			}
		}
		return Relation.Type.NEUTRAL;
	}
}
