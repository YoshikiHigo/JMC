package yoshikihigo.jmc.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import yoshikihigo.jmc.JMCConfig;

public class SearcherDAO {

	static public final SearcherDAO SINGLETON = new SearcherDAO();

	private Connection connector;
	private PreparedStatement statementSelection;
	private PreparedStatement methodSelection;

	private SearcherDAO() {
	}

	public void initialize() {

		try {
			Class.forName("org.sqlite.JDBC");
			final String database = JMCConfig.getInstance().getDATABASE();
			this.connector = DriverManager.getConnection("jdbc:sqlite:"
					+ database);

			this.statementSelection = this.connector
					.prepareStatement("select distinct methodID from statements where hash = ?");
			this.methodSelection = this.connector
					.prepareStatement("select file, fromLine, toLine from methods where id = ?");

		} catch (final ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public List<Integer> getMethodIDs(final byte[] hash) {
		final List<Integer> methodIDs = new ArrayList<>();
		try {
			this.statementSelection.setBytes(1, hash);
			final ResultSet results = this.statementSelection.executeQuery();
			while (results.next()) {
				final int methodID = results.getInt(1);
				methodIDs.add(methodID);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return methodIDs;
	}

	public DBMethod getDBMethod(final int id) {
		DBMethod method = null;
		try {
			this.methodSelection.setInt(1, id);
			final ResultSet results = this.methodSelection.executeQuery();
			if (results.next()) {
				final String file = results.getString(1);
				final int fromLine = results.getInt(2);
				final int toLine = results.getInt(3);
				method = new DBMethod(id, file, fromLine, toLine);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return method;
	}
}
