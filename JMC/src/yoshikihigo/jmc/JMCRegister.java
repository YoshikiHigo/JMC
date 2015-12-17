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

import yoshikihigo.jmc.data.JMethod;
import yoshikihigo.jmc.data.JStatement;

public class JMCRegister {

	public static void main(final String[] args) {

		JMCConfig.initialize(args);

		final String directory = JMCConfig.getInstance().getSOURCE();
		final List<File> files = getJavaFiles(new File(directory));
		parseJavaFiles(files);
	}

	private static List<File> getJavaFiles(final File file) {
		final List<File> files = new ArrayList<>();
		if (file.isFile() && file.getName().endsWith(".java")) {
			files.add(file);
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

		final BlockingQueue<JMethod> mQueue = new ArrayBlockingQueue<>(100000,
				true);
		final BlockingQueue<JStatement> sQueue = new ArrayBlockingQueue<>(
				100000, true);

		final MethodRegisterThread registerThread = new MethodRegisterThread(
				mQueue, sQueue);

		final ExecutorService executorService = Executors
				.newFixedThreadPool(JMCConfig.getInstance().getTHREAD());
		final List<Future<?>> futures = new ArrayList<>();

		files.stream().forEach(
				file -> {
					final Future<?> future = executorService
							.submit(new MethodParseThread(file
									.getAbsolutePath(), mQueue, sQueue));
					futures.add(future);
				});

		try {
			for (final Future<?> future : futures) {
				future.get();
			}
			registerThread.finish();
			registerThread.join();
		} catch (final ExecutionException | InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			executorService.shutdown();
		}

	}
}
