package yoshikihigo.jmc.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DBClone {

	static final private AtomicInteger ID_GENERATOR = new AtomicInteger(1);
	public final int id;
	private List<DBMethod> methods;

	public DBClone() {
		this.id = ID_GENERATOR.getAndIncrement();
		this.methods = new ArrayList<DBMethod>();
	}

	public void addMethod(final DBMethod method) {
		this.methods.add(method);
	}

	public List<DBMethod> getMethods() {
		final List<DBMethod> methods = new ArrayList<>(this.methods);
		methods.sort((m1, m2) -> Integer.compare(m1.id, m2.id));
		return methods;
	}
}
