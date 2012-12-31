/**
 * Represents a chunk of land and stores ownership data for it.
 * 
 * @author gregthegeek
 *
 */
public class Land {
	private final int x;
	private final int z;
	public String owner = null;
	
	public Land(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
}
