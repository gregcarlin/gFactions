import java.util.ArrayList;

/**
 * Manages the relationships between factions.
 * 
 * @author gregthegeek
 *
 */
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
	
	/**
	 * Returns the relation between two players.
	 * 
	 * @param one The first player.
	 * @param two The second player.
	 * @return Relation.Type
	 */
	public Relation.Type getRelation(String one, String two) {
		FactionManager fm = Utils.plugin.getFactionManager();
		return getRelation(fm.getFaction(one), fm.getFaction(two));
	}
	
	/**
	 * Saves all relations to storage.
	 */
	public void save() {
		Object[] arr = relations.toArray();
		Relation[] rt = new Relation[arr.length];
		for(int i=0; i<rt.length; i++) {
			rt[i] = (Relation) arr[i];
		}
		Utils.plugin.getDataSource().save(rt);
	}
	
	/**
	 * Returns an array of the factions that have a given relation with the given faction.
	 * 
	 * @param original The faction to check relations with.
	 * @param type The type of relation to search for. Should be either ALLY or ENEMY.
	 * @return Faction[]
	 */
	public Faction[] getRelations(Faction original, Relation.Type type) {
		ArrayList<Faction> rt = new ArrayList<Faction>();
		for(Relation r : relations) {
			if(r.isInvolved(original) && r.type == type) {
				rt.add(r.getOther(original));
			}
		}
		return rt.toArray(new Faction[0]);
	}
}
