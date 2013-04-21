package en.gregthegeek.util;
/**
 * Represents a compass direction.
 * 
 * @author gregthegeek
 *
 */
public enum Direction {
	EAST,
	SOUTH_EAST,
	SOUTH,
	SOUTH_WEST,
	WEST,
	NORTH_WEST,
	NORTH,
	NORTH_EAST,
	ERROR;
	
	public static Direction fromRot(double degrees) {
		degrees += 360; // will fix negatives >= -360
		degrees %= 360; // will fix numbers > 360 and previous line
		if (0 <= degrees && degrees < 22.5) {
            return EAST;
        } else if (22.5 <= degrees && degrees < 67.5) {
            return SOUTH_EAST;
        } else if (67.5 <= degrees && degrees < 112.5) {
            return SOUTH;
        } else if (112.5 <= degrees && degrees < 157.5) {
            return SOUTH_WEST;
        } else if (157.5 <= degrees && degrees < 202.5) {
            return WEST;
        } else if (202.5 <= degrees && degrees < 247.5) {
            return NORTH_WEST;
        } else if (247.5 <= degrees && degrees < 292.5) {
            return NORTH;
        } else if (292.5 <= degrees && degrees < 337.5) {
            return NORTH_EAST;
        } else if (337.5 <= degrees && degrees < 360.0) {
            return EAST;
        } else {
            return ERROR;
        }
	}
}
