package yoshikihigo.jmc;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JMCRegister {

	public static void main(final String[] args) {

		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource("aiueo".toCharArray());
		final CompilationUnit unit = (CompilationUnit)parser
				.createAST(new NullProgressMonitor());
	}
}
