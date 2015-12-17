package yoshikihigo.jmc.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import yoshikihigo.jmc.JMCConfig;

public class DAO {

	static public final String METHODS_SCHEMA = "id integer primary key, file string, fromline integer, toline integer";
	static public final String STATEMENTS_SCHEMA = "id integer primary key, methodID integer, hash blob, line integer";

	private Connection connector;
	private PreparedStatement methodsPS;
	private PreparedStatement statementsPS;
	private int numberOfMethods;
	private int numberOfStatements;

	public DAO() {

		try {
			Class.forName("org.sqlite.JDBC");
			final String database = JMCConfig.getInstance().getDATABASE();
			this.connector = DriverManager.getConnection("jdbc:sqlite:"
					+ database);

			final Statement statement = this.connector.createStatement();
			statement.executeUpdate("create table if not exists methods ("
					+ METHODS_SCHEMA + ")");
			statement.executeUpdate("create table if not exists statements ("
					+ STATEMENTS_SCHEMA + ")");
			statement.close();

			this.methodsPS = this.connector
					.prepareStatement("insert into methods values (?, ?, ?, ?)");
			this.statementsPS = this.connector
					.prepareStatement("insert into changes values(?, ?, ?)");

			this.numberOfMethods = 0;
			this.numberOfStatements = 0;

		} catch (final ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void registerMethod(final JMethod method) {

		try {
			this.methodsPS.setString(1, method.file);
			this.methodsPS.setInt(2, method.fromLine);
			this.methodsPS.setInt(3, method.toLine);
			this.methodsPS.addBatch();
			this.numberOfMethods++;

			if (10000 < this.numberOfMethods) {
				if (JMCConfig.getInstance().isVERBOSE()) {
					System.out.println("writing \'methods\' table ...");
				}
				this.methodsPS.executeBatch();
				this.numberOfMethods = 0;
			}
		}

		catch (final SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void registerMethods(final Collection<JMethod> methods) {
		methods.stream().forEach(method -> this.registerMethod(method));
	}

	public void registerStatement(final JStatement statement) {

		try {
			this.statementsPS.setInt(1, statement.methodID);
			this.statementsPS.setBytes(2, statement.hash);
			this.statementsPS.setInt(3, statement.line);
			this.statementsPS.addBatch();
			this.numberOfStatements++;

			if (10000 < this.numberOfStatements) {
				if (JMCConfig.getInstance().isVERBOSE()) {
					System.out.println("writing \'statements\' table ...");
				}
				this.statementsPS.executeBatch();
				this.numberOfStatements = 0;
			}
		}

		catch (final SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void registerStatements(final Collection<JStatement> statements) {
		statements.stream().forEach(
				statement -> this.registerStatement(statement));
	}

	public void flush() {
		try {
			if (0 < this.numberOfMethods) {
				if (JMCConfig.getInstance().isVERBOSE()) {
					System.out.println("writing \'methods\' table ...");
				}
				this.methodsPS.executeBatch();
				this.numberOfMethods = 0;
			}
			if (0 < this.numberOfStatements) {
				if (JMCConfig.getInstance().isVERBOSE()) {
					System.out.println("writing \'statements\' table ...");
				}
				this.statementsPS.executeBatch();
				this.numberOfStatements = 0;
			}

		} catch (final SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void close() {
		try {
			this.methodsPS.close();
			this.statementsPS.close();
			this.connector.close();
		} catch (final SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
