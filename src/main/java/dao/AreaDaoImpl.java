package dao;

import Models.Area;
import Models.Incidencia;
import iDAO.IAreaDao;

import java.sql.SQLException;

public class AreaDaoImpl extends Dao<Area,String> implements IAreaDao {
    @Override
    public Area find(String id) throws SQLException {
        Manejador.getInstance().getEm().getTransaction().begin();
        Area area = (Area) Manejador.getInstance().getEm().find(Area.class, id);
        Manejador.getInstance().getEm().getTransaction().commit();
        return area;
    }
}
