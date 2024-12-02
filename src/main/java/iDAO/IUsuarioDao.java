package iDAO;

import Models.Usuario;

import java.sql.SQLException;

public interface IUsuarioDao {
    Usuario findByUsername(String input) throws SQLException;
}
