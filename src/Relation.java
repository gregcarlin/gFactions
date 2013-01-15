/**
 * Reprsents a relationship between two factions.
 * 
 * @author gregthegeek
 *
 */
public class Relation {
	public enum Type {
		NEUTRAL(Colors.White),
		ALLY(Colors.Purple),
		ENEMY(Colors.Red),
		SAME(Colors.LightGreen);
		
		private final String color;
		
		private Type(String color) {
			this.color = color;
		}
		
		public String getColor() {
			return color;
		}
	}
	
	public Type type;
	private final Faction one;
	private final Faction two;
	
	public Relation(Type type, Faction one, Faction two) {
		this.type = type;
		this.one = one;
		this.two = two;
	}
	
	/**
	 * Gets the first faction described in the relation.
	 * 
	 * @return Faction
	 */
	public Faction getOne() {
		return one;
	}
	
	/**
	 * Gets the second faction described in the relation.
	 * 
	 * @return Faction
	 */
	public Faction getTwo() {
		return two;
	}
	
	/**
	 * Returns whether or not the given faction is either one or two in this relation.
	 * 
	 * @param f The faction to check for.
	 * @return boolean
	 */
	public boolean isInvolved(Faction f) {
		return one.equals(f) || two.equals(f);
	}
	
	/**
	 * Returns the faction in this relation that is not the one given.
	 * Returns null if the given faction is not in this relation.
	 * 
	 * @param f The faction not returned.
	 * @return Faction the other faction.
	 */
	public Faction getOther(Faction f) {
		if(one.equals(f)) {
			return two;
		} else if(two.equals(f)) {
			return one;
		} else {
			return null;
		}
	}
}
