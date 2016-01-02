package yoshikihigo.jmc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import yoshikihigo.jmc.data.DBMethod;
import yoshikihigo.jmc.data.JMethod;
import yoshikihigo.jmc.data.JStatement;
import yoshikihigo.jmc.data.SearcherDAO;

public class JMCSearcher {

	final public String path;
	final public int caret;

	public static void main(final String[] args) {

		JMCConfig.initialize(args);
		final String path = JMCConfig.getInstance().getTARGET();
		final int caret = JMCConfig.getInstance().getCARET();
		final long timeToStart = System.nanoTime();
		final JMCSearcher searcher = new JMCSearcher(path, caret);
		final List<DBMethod> methods = searcher.search();
		final long timeToEnd = System.nanoTime();
		System.out.println(TimingUtility.getExecutionTime(timeToStart,
				timeToEnd));
	}

	public JMCSearcher(final String path, final int caret) {
		this.path = path;
		this.caret = caret;
	}

	public List<DBMethod> search() {

		final JMethod targetMethod = this.getTargetMethod();
		final List<JStatement> targetStatements = targetMethod.getStatements();

		if (targetStatements.isEmpty()) {
			return Collections.emptyList();
		}

		final SearcherDAO dao = SearcherDAO.SINGLETON;
		dao.initialize();
		byte[] hash = targetStatements.get(0).getHash();
		final Set<Integer> methodIDs = new HashSet<>(dao.getMethodIDs(hash));
		for (int i = 1; i < targetStatements.size(); i++) {
			hash = targetStatements.get(i).getHash();
			methodIDs.retainAll(dao.getMethodIDs(hash));
		}

		final List<DBMethod> methods = methodIDs.stream()
				.map(methodID -> dao.getDBMethod(methodID))
				.collect(Collectors.toList());
		return methods;
	}

	private JMethod getTargetMethod() {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(this.path),
					StandardCharsets.UTF_8);
		} catch (final Exception e) {
			e.printStackTrace();
			System.err.println(this.path + " is not readable.");
			System.exit(0);
		}

		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(String.join(System.lineSeparator(), lines)
				.toCharArray());

		CompilationUnit unit = null;
		try {
			unit = (CompilationUnit) parser
					.createAST(new NullProgressMonitor());
		} catch (final Exception e) {
			e.printStackTrace();
			System.err.println(this.path + " is not parsable.");
			System.exit(0);
		}

		final JMCVisitor visitor = new JMCVisitor(this.path, unit);
		unit.accept(visitor);
		final List<JMethod> methods = visitor.getMethods();
		JMethod target = null;
		for (final JMethod method : methods) {
			if ((method.fromLine <= this.caret)
					&& (this.caret <= method.toLine)) {
				target = method;
				break;
			}
		}

		if (null == target) {
			System.err.println("specified caret doesn\'t match any methods.");
		}

		return target;
	}
}
