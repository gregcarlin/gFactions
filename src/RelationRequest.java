/**
 * Represents the request of one faction to neutral/ally another.
 * 
 * @author gregthegeek
 *
 */
public class RelationRequest {
	private final Faction from;
	private final Faction to;
	private final boolean isNeutral; // false for ally request
	
	public RelationRequest(Faction from, Faction to, boolean isNeutral) {
		this.from = from;
		this.to = to;
		this.isNeutral = isNeutral;
	}
	
	public Faction getFrom() {
		return from;
	}
	
	public Faction getTo() {
		return to;
	}
	
	public boolean isNeutral() {
		return isNeutral;
	}
}
