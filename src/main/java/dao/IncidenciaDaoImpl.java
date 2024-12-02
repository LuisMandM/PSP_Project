package dao;

import Models.Incidencia;
import iDAO.IIncidenciaDao;

import java.sql.SQLException;

public class IncidenciaDaoImpl extends Dao<Incidencia,String> implements IIncidenciaDao {
    @Override
    public Incidencia find(String id) throws SQLException {
        Manejador.getInstance().getEm().getTransaction().begin();
        Incidencia incidencia = (Incidencia) Manejador.getInstance().getEm().find(Incidencia.class, id);
        Manejador.getInstance().getEm().getTransaction().commit();
        return incidencia;
    }
}
