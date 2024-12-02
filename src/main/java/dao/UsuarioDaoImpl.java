package dao;

import Models.Incidencia;
import Models.Usuario;
import iDAO.IUsuarioDao;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl extends Dao<Usuario, String> implements IUsuarioDao {
    @Override
    public Usuario find(String id) throws SQLException {
        Manejador.getInstance().getEm().getTransaction().begin();
        Usuario usuario = (Usuario) Manejador.getInstance().getEm().find(Usuario.class, id);
        Manejador.getInstance().getEm().getTransaction().commit();
        return usuario;
    }

    public Usuario findByUsername(String input) throws SQLException {

        Usuario result = null;
        String HQL_COD = "From Usuario as user where user.username = :nombre ";
        Query query = null;

        try {
            Manejador.getInstance().getEm().getTransaction().begin();
            query = Manejador.getInstance().getEm().createQuery(HQL_COD);
            query.setParameter("nombre", input);
            result = (Usuario) query.getSingleResult();
            Manejador.getInstance().getEm().getTransaction().commit();

        } catch (SQLException e) {
            System.out.println("Error al obtener el resultado: " + e.getMessage());
        } catch (NoResultException e) {
            System.out.println("Error al encontrar el resultado: " + e.getMessage());
        }
        return result;
    }
}
