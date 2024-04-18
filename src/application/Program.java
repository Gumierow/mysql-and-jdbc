package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import db.DB;
import db.DbException;
import db.DbIntegrityException;

public class Program {

	public static void main(String[] args) {

// ------------ AULA 270 ------------		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			conn = DB.getConnection();
			st = conn.createStatement(); // Instanciamos um objeto da classe Statement

			rs = st.executeQuery("SELECT * FROM department"); // Esse metodo recebe uma String, que vai ser ser o nosso
																// comando SQL

			while (rs.next()) {
				System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

// ------------ AULA 271 ------------

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("INSERT INTO seller " + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES " + "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, "Carl Purple");
			pst.setString(2, "carl@gmail.com");
			pst.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));
			pst.setDouble(4, 3000.0);
			pst.setInt(5, 4);

			int rowsAffected = pst.executeUpdate();

			if (rowsAffected > 0) {
				rs = pst.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1); // O 1 eh a posicao, estamos fazendo uma tabela auxiliar so com os Id's, e no
											// caso so vai ter so 1 linha, pois so inserimos uma chave. Como o Id
											// incrementa sozinho a cada insercao no BD, o Id que o rs.getInt(1) vai
											// pegar eh o Id que acabamos de inserir.
					System.out.println("Done! Id = " + id);
				}
			} else {
				System.out.println("No rows affected!");
			}

		} catch (SQLException e) {

		} catch (ParseException e) {
			e.printStackTrace();
		}

// ------------ AULA 272 ------------

		try {
			pst = conn.prepareStatement(
					"UPDATE seller " + "SET BaseSalary = BaseSalary + ? " + "WHERE " + "(DepartmentId = ?)");

			pst.setDouble(1, 200.0);
			pst.setInt(2, 2);

			int rowsAffected = pst.executeUpdate();

			System.out.println("Done! Rows affected = " + rowsAffected);
		} catch (SQLException e) {
			e.printStackTrace();
		}
// ------------ AULA 273 ------------

		/*
		 * 
		 * try { pst = conn.prepareStatement("DELETE FROM department " + "WHERE " +
		 * "Id = ?");
		 * 
		 * pst.setInt(1, 2); // O interrogacao numero 1 vai ser preenchido com 5, e o Id
		 * 5 vai ser deletado
		 * 
		 * int rowsAffected = pst.executeUpdate();
		 * 
		 * System.out.println("Done! Affected rows = " + rowsAffected); } catch
		 * (SQLException e) { throw new DbIntegrityException(e.getMessage()); }
		 * 
		 */

// ------------ AULA 273 ------------

		try {
			conn.setAutoCommit(false); // #2 - Como deu o erro falso e so commitou a alteracao pro rows1 estamos
										// colocando esse metodo aqui

			int rows1 = st.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 1");

			int x = 1;
			if (x < 2) {
				throw new SQLException("Fake error!"); // #1 - Aqui criamos um "erro" no meio da transacao para testar
			}

			int rows2 = st.executeUpdate("UPDATE seller SET BaseSalary = 3090 WHERE DepartmentId = 2");

			conn.commit(); // #3 - Agora colocamos isso para permitir o commit

			System.out.println("rows1 = " + rows1 + ", rows2 =" + rows2);

		} catch (SQLException e) {
			try {
				conn.rollback(); // #4 - Caso tenha o erro vamos desfazer o que ja tinha ocorrido na transacao
									// com o rollback
				throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
			} catch (SQLException e1) {
				throw new DbException("Error trying to rollback! Caused by: " + e1.getMessage()); // #5 - Aqui eh pra
																									// quando der erro
																									// no rollback
			}
		}
	}
}
