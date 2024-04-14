package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//Vamos implementar metodos estaticos auxiliares para se obter e fechar uma conexao do banco de dados
public class DB {

	// Vai ser o objeto de conexao com o banco de dados do JDBC
	private static Connection conn = null;

	// Agora vamos fazer o metodo para conectar ao banco de dados
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				conn = DriverManager.getConnection(url, props); // DriverManager eh uma classe do JDBC
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}

	// Metodo para fechar conexao
	public static void closeConection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	// Private pois so vai ser usado aqui
	private static Properties loadProperties() {
		try (FileInputStream fs = new FileInputStream("db.properties")) { // O try vai tentar abrir o arquivo (precisa
																			// do try para compilar)
			Properties props = new Properties();
			props.load(fs); // O objeto props do tipo Properties chama o seu metodo load para carregar o
							// arquivo apontado pelo objeto fs
			return props;
		} catch (IOException e) {
			throw new DbException(e.getMessage()); // Lanca excecao personalizada caso de IOException no try
		}
	}

	// Estamos fazendo esse meto aqui para ja tratar a excecao aqui em vez de ter
	// que colocar try catch no "Program"
	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
}
