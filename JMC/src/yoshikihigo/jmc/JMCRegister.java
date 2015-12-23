package yoshikihigo.jmc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import yoshikihigo.jmc.data.DAO;
import yoshikihigo.jmc.data.JMethod;
import yoshikihigo.jmc.data.JStatement;

public class JMCRegister {

	public static void main(final String[] args) {

		JMCConfig.initialize(args);

		final long timeToStart = System.nanoTime();
		final String directory = JMCConfig.getInstance().getSOURCE();
		final List<File> files = getJavaFiles(new File(directory));
		parseJavaFiles(files);
		final long timeToEnd = System.nanoTime();

		System.out.println("read failed files:");
		MethodParseThread.READ_FAILED_FILES.stream().forEach(
				file -> System.out.println(file));

		System.out.println("parse failed files:");
		MethodParseThread.PARSE_FAILED_FILES.stream().forEach(
				file -> System.out.println(file));

		System.out.print("execution time: ");
		System.out.println(TimingUtility.getExecutionTime(timeToStart,
				timeToEnd));
	}

	private static List<File> getJavaFiles(final File file) {
		final List<File> files = new ArrayList<>();
		if (file.isFile()) {
			if (file.getName().endsWith(".java")) {
				files.add(file);
			}
		} else if (file.isDirectory()) {
			for (final File child : file.listFiles()) {
				files.addAll(getJavaFiles(child));
			}
		} else {
			System.out.println("\"" + file.getPath() + "\" is invalid.");
			System.exit(0);
		}
		return files;
	}

	private static void parseJavaFiles(final List<File> files) {

		final BlockingQueue<JMethod> mQueue = new ArrayBlockingQueue<>(1000000,
				true);
		final BlockingQueue<JStatement> sQueue = new ArrayBlockingQueue<>(
				1000000, true);

		// final ExecutorService registerThreadPool = Executors
		// .newSingleThreadExecutor();
		// final MethodRegisterThread rThread = new MethodRegisterThread(mQueue,
		// sQueue);
		// final Future<?> rFuture = registerThreadPool.submit(rThread);

		DAO.SINGLETON.initialize();
		final ExecutorService parserThreadPool = Executors
				.newFixedThreadPool(JMCConfig.getInstance().getTHREAD());
		final List<Future<?>> pFutures = new ArrayList<>();

		files.stream().forEach(
				file -> {
					final Future<?> future = parserThreadPool
							.submit(new MethodParseThread(file
									.getAbsolutePath(), mQueue, sQueue));
					pFutures.add(future);
				});

		try {
			for (final Future<?> future : pFutures) {
				future.get();
			}
			// rThread.finish();
			// rFuture.get();
			DAO.SINGLETON.flush();
			DAO.SINGLETON.addIndices();
			DAO.SINGLETON.close();
		} catch (final ExecutionException | InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			// registerThreadPool.shutdown();
			parserThreadPool.shutdown();
		}
	}
}
