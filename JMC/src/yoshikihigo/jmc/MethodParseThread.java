package yoshikihigo.jmc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import yoshikihigo.jmc.data.JMethod;
import yoshikihigo.jmc.data.JStatement;

public class MethodParseThread extends Thread {

	final static public List<String> READ_FAILED_FILES = Collections
			.synchronizedList(new ArrayList<>());
	final static public List<String> PARSE_FAILED_FILES = Collections
			.synchronizedList(new ArrayList<>());

	final public String file;
	final BlockingQueue<JMethod> mQueue;
	final BlockingQueue<JStatement> sQueue;

	MethodParseThread(final String file, final BlockingQueue<JMethod> mQueue,
			final BlockingQueue<JStatement> sQueue) {
		this.file = file;
		this.mQueue = mQueue;
		this.sQueue = sQueue;
	}

	@Override
	public void run() {

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(this.file),
					StandardCharsets.UTF_8);
		} catch (final Exception e) {
			READ_FAILED_FILES.add(this.file);
			return;
		}

		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(String.join(System.lineSeparator(), lines)
				.toCharArray());

		CompilationUnit unit = null;
		try {
			unit = (CompilationUnit) parser
					.createAST(new NullProgressMonitor());
		} catch (final Exception e) {
			PARSE_FAILED_FILES.add(this.file);
			return;
		}

		final JMCVisitor visitor = new JMCVisitor(file, unit);
		unit.accept(visitor);
		final List<JMethod> methods = visitor.getMethods();
//		this.mQueue.addAll(methods);
//		methods.stream().forEach(
//				method -> this.sQueue.addAll(method.getStatements()));
	}
}
