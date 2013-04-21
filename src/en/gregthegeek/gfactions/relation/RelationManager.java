package en.gregthegeek.gfactions.relation;

import java.util.ArrayList;

import en.gregthegeek.gfactions.db.Datasource;
import en.gregthegeek.gfactions.faction.Faction;
import en.gregthegeek.gfactions.faction.FactionManager;
import en.gregthegeek.util.Utils;

/**
 * Manages the relationships between factions.
 * 
 * @author gregthegeek
 *
 */
public class RelationManager {
	private final ArrayList<Relation> relations = new ArrayList<Relation>();
	private final ArrayList<RelationRequest> requests = new ArrayList<RelationRequest>();
	
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
			if(r.isInvolved(one) && r.isInvolved(two)) {
				return r.type;
			}
		}
		
		Relation r = Utils.plugin.getDataSource().getRelation(one, two);
		if(r != null) {
			relations.add(r);
			return r.type;
		} else {
			r = Utils.plugin.getDataSource().getRelation(two, one);
			if(r != null) {
				relations.add(r);
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
	
	public void setRelation(Faction one, Faction two, Relation.Type type) {
		int index = locateRelation(one, two);
		if(index < 0) {
			Relation r = new Relation(type, one, two);
			relations.add(r);
			if(Utils.plugin.getConfig().getSaveInterval() < 0) {
				Utils.warning("saving relation %s", r);
				Utils.plugin.getDataSource().save(new Relation[] {r});
			}
		} else {
			relations.get(index).type = type; 
		}
	}
	
	private int locateRelation(Faction one, Faction two) {
		int size = relations.size();
		for(int i=0; i<size; i++) {
			Relation r = relations.get(i);
			if(r.isInvolved(one) && r.isInvolved(two)) {
				return i;
			}
		}
		return -1;
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
	
	/**
	 * Manages a relation request between two factions.
	 * 
	 * @param from The faction sending the request.
	 * @param to The faction receiving the request.
	 * @param isNeutral True if neutral request, false if ally
	 * @return boolean Whether or not the two factions are now neutral/allied.
	 */
	public boolean request(Faction from, Faction to, boolean isNeutral) {
		int size = requests.size();
		for(int i=0; i<size; i++) {
			RelationRequest ar = requests.get(i);
			if(ar.isNeutral() == isNeutral && ar.getFrom().equals(to)) {
				setRelation(from, to, isNeutral ? Relation.Type.NEUTRAL : Relation.Type.ALLY);
				requests.remove(i);
				return true;
			}
		}
		requests.add(new RelationRequest(from, to, isNeutral));
		return false;
	}
	
	/**
	 * Removes all relations and requests related to the given faction.
	 * 
	 * @param f The faction to remove stuff of.
	 */
	public void removeAll(Faction f) {
		Datasource ds = Utils.plugin.getDataSource();
		for(int i=relations.size()-1; i>=0; i--) {
			Relation r = relations.get(i);
			if(r.isInvolved(f)) {
				relations.remove(r);
				ds.delete(r);
			}
		}
		
		for(int i=requests.size()-1; i>=0; i--) {
			RelationRequest r = requests.get(i);
			if(r.getFrom().equals(f) || r.getTo().equals(f)) {
				requests.remove(r);
			}
		}
	}
}
