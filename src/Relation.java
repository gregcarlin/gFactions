
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
	
	public Faction getOne() {
		return one;
	}
	
	public Faction getTwo() {
		return two;
	}
}
