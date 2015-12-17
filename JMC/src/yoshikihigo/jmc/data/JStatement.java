package yoshikihigo.jmc.data;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class JStatement {

	final private static AtomicInteger ID_GENERATOR = new AtomicInteger(0);
	final public int id;
	final public int methodID;
	final public byte[] hash;
	final public int line;

	public JStatement(final int methodID, final byte[] hash, final int line) {
		this.id = ID_GENERATOR.getAndIncrement();
		this.methodID = methodID;
		this.hash = Arrays.copyOf(hash, hash.length);
		this.line = line;
	}
}
