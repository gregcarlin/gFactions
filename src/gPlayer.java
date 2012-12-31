
public class gPlayer {
	private final String name;
	private int power;
	public String title;
	
	public gPlayer(String name, int power) {
		this.name = name;
		this.power = power;
		this.title = "";
	}
	
	public String getName() {
		return name;
	}
	
	public int getPower() {
		return power;
	}
}
