package yoshikihigo.jmc.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import yoshikihigo.jmc.JMCConfig;

public class FinderDAO {

	static public final FinderDAO SINGLETON = new FinderDAO();

	private PreparedStatement methodSelection;

	private Connection connector;

	private FinderDAO() {
	}

	public void initialize() {

		try {
			Class.forName("org.sqlite.JDBC");
			final String database = JMCConfig.getInstance().getDATABASE();
			this.connector = DriverManager.getConnection("jdbc:sqlite:"
					+ database);

			this.methodSelection = this.connector
					.prepareStatement("select file, fromLine, toLine from methods where hash = ?");

		} catch (final ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public List<Hash> getClonedHashs(final int threshold) {
		final List<Hash> hashs = new ArrayList<>();
		try {
			final PreparedStatement statement = this.connector
					.prepareStatement("select hash from methods where ? <= tokens group by hash having count(hash) >= 2 order by count(hash) desc;");
			statement.setInt(1, threshold);
			final ResultSet results = statement.executeQuery();
			while (results.next()) {
				final Hash hash = new Hash(results.getBytes(1));
				hashs.add(hash);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return hashs;
	}

	public List<JMethod> getMethods(final Hash hash) {
		final List<JMethod> methods = new ArrayList<>();
		try {
			this.methodSelection.setBytes(1, hash.value);
			final ResultSet results = this.methodSelection.executeQuery();
			while (results.next()) {
				final String file = results.getString(1);
				final int fromline = results.getInt(2);
				final int toline = results.getInt(3);
				final JMethod method = new JMethod(file, fromline, toline);
				methods.add(method);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return methods;
	}

	public void close() {
		try {
			this.methodSelection.close();
			this.connector.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}
}
