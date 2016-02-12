package yoshikihigo.jmc;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import yoshikihigo.jmc.data.FinderDAO;
import yoshikihigo.jmc.data.Hash;
import yoshikihigo.jmc.data.JMethod;

public class JMCFinder {

	public static void main(final String[] args) {

		JMCConfig.initialize(args);
		final int threshold = JMCConfig.getInstance().getTHRESHOLD();

		FinderDAO.SINGLETON.initialize();
		final List<Hash> hashs = FinderDAO.SINGLETON.getClonedHashs(threshold);

		final List<List<JMethod>> clones = hashs.stream()
				.map(hash -> FinderDAO.SINGLETON.getMethods(hash))
				.collect(Collectors.toList());

		try (final PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(JMCConfig
						.getInstance().getCLONES()), "UTF-8")))) {

			int id = 1;
			for (final List<JMethod> methods : clones) {
				for (final JMethod method : methods) {
					writer.print(Integer.toString(id));
					writer.print("\t");
					writer.print(method.file);
					writer.print("\t");
					writer.print(method.fromLine);
					writer.print("\t");
					writer.print(method.toLine);
					writer.println();
				}
				id++;
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
