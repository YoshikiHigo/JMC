package yoshikihigo.jmc.data;

import java.util.concurrent.atomic.AtomicInteger;

public class JMethod {

	final private static AtomicInteger ID_GENERATOR = new AtomicInteger(0);
	final public int id;
	final public String file;
	final public int fromLine;
	final public int toLine;
	
	public JMethod(final String file, final int fromLine, final int toLine){
		this.id = ID_GENERATOR.getAndIncrement();
		this.file = file;
		this.fromLine = fromLine;
		this.toLine = toLine;
	}
}
