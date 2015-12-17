package yoshikihigo.jmc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import yoshikihigo.jmc.data.DAO;
import yoshikihigo.jmc.data.JMethod;
import yoshikihigo.jmc.data.JStatement;

public class MethodRegisterThread extends Thread {

	final private BlockingQueue<JMethod> mQueue;
	final private BlockingQueue<JStatement> sQueue;
	private boolean finished;

	MethodRegisterThread(final BlockingQueue<JMethod> mQueue,
			final BlockingQueue<JStatement> sQueue) {
		this.mQueue = mQueue;
		this.sQueue = sQueue;
		this.finished = false;
	}

	@Override
	public void run() {

		try {
			final DAO dao = new DAO();

			final List<JMethod> methods = new ArrayList<>();
			final List<JStatement> statements = new ArrayList<>();
			while (!this.finished) {
				if (0 < this.mQueue.size()) {
					this.mQueue.drainTo(methods);
					dao.registerMethods(methods);
					methods.clear();
				}
				if (0 < this.sQueue.size()) {
					this.sQueue.drainTo(statements);
					dao.registerStatements(statements);
					statements.clear();
				}
			}

			this.mQueue.drainTo(methods);
			dao.registerMethods(methods);
			this.sQueue.drainTo(statements);
			dao.registerStatements(statements);
			dao.flush();
			dao.close();
		}

		catch (final Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void finish() {
		this.finished = true;
	}
}
