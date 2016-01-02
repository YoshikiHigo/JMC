package yoshikihigo.jmc.data;

public class DBMethod {

	final public int id;
	final public String file;
	final public int fromLine;
	final public int toLine;

	public DBMethod(final int id, final String file, final int fromLine,
			final int toLine) {
		this.id = id;
		this.file = file;
		this.fromLine = fromLine;
		this.toLine = toLine;
	}
}
