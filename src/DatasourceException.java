import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Represents an error encountered while retrieving or saving data.
 * 
 * @author gregthegeek
 *
 */
public class DatasourceException extends Exception {
	private static final long serialVersionUID = 4284694529329045821L;

	public DatasourceException(FileNotFoundException e) {
		super(e.getMessage());
	}
	
	public DatasourceException(IOException e) {
		super(e.getMessage());
	}

	public DatasourceException(String string, Object... args) {
		super(String.format(string, args));
	}
}
