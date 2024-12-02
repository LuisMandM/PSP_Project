package dao;

import Models.Area;
import iDAO.IAreaDao;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.sql.SQLException;

public class AreaDaoImpl extends Dao<Area, String> implements IAreaDao {
    @Override
    public Area find(String id) throws SQLException {
        Manejador.getInstance().getEm().getTransaction().begin();
        Area area = (Area) Manejador.getInstance().getEm().find(Area.class, id);
        Manejador.getInstance().getEm().getTransaction().commit();
        return area;
    }

    @Override
    public Area findByName(String input) throws SQLException {
        Area result = null;
        String HQL_COD = "From Area as area where area.nombre = :nombre ";
        Query query = null;

        try {
            Manejador.getInstance().getEm().getTransaction().begin();
            query = Manejador.getInstance().getEm().createQuery(HQL_COD);
            query.setParameter("nombre", input);
            result = (Area) query.getSingleResult();
            Manejador.getInstance().getEm().getTransaction().commit();

        } catch (SQLException e) {
            System.out.println("Error al obtener el resultado: " + e.getMessage());
        } catch (NoResultException e) {
            System.out.println("Error al encontrar el resultado: " + e.getMessage());
        }
        return result;
    }
}
