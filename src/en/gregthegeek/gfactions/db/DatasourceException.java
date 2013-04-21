package en.gregthegeek.gfactions.db;
/**
 * Represents an error encountered while retrieving or saving data.
 * 
 * @author gregthegeek
 *
 */
public class DatasourceException extends Exception {
	private static final long serialVersionUID = 4284694529329045821L;

	public DatasourceException(Exception e) {
		super(e.getMessage());
	}

	public DatasourceException(String string, Object... args) {
		super(String.format(string, args));
	}
}
