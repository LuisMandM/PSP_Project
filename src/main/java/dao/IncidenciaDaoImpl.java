package dao;

import Models.Estado;
import Models.Incidencia;
import iDAO.IIncidenciaDao;
import jakarta.persistence.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDaoImpl extends Dao<Incidencia, String> implements IIncidenciaDao {
    @Override
    public Incidencia find(String id) throws SQLException {
        Manejador.getInstance().getEm().getTransaction().begin();
        Incidencia incidencia = (Incidencia) Manejador.getInstance().getEm().find(Incidencia.class, id);
        Manejador.getInstance().getEm().getTransaction().commit();
        return incidencia;
    }

    @Override
    public List<Incidencia> getByUser(Long user) {
        ArrayList<Incidencia> incidencias = new ArrayList<Incidencia>();
        String HQL_COD = "From Incidencia as Inc where Inc.usuario.id = :user";
        Query query = null;

        try {
            Manejador.getInstance().getEm().getTransaction().begin();
            query = Manejador.getInstance().getEm().createQuery(HQL_COD);
            query.setParameter("user", user);
            List resultado = query.getResultList();
            if (resultado != null) {
                incidencias = (ArrayList<Incidencia>) resultado;
            }
            Manejador.getInstance().getEm().getTransaction().commit();

        } catch (SQLException e) {
            System.out.println("Error al obtener el resultado: " + e.getMessage());
        }

        return incidencias;
    }

    @Override
    public List<Incidencia> getByArea(Long user) {
        ArrayList<Incidencia> incidencias = new ArrayList<Incidencia>();
        String HQL_COD = "From Incidencia as Inc where Inc.area.id = :user and Inc.estado = :estado";
        Query query = null;

        try {
            Manejador.getInstance().getEm().getTransaction().begin();
            query = Manejador.getInstance().getEm().createQuery(HQL_COD);
            query.setParameter("user", user);
            query.setParameter("estado", Estado.AREA);
            List resultado = query.getResultList();
            if (resultado != null) {
                incidencias = (ArrayList<Incidencia>) resultado;
            }
            Manejador.getInstance().getEm().getTransaction().commit();

        } catch (SQLException e) {
            System.out.println("Error al obtener el resultado: " + e.getMessage());
        }

        return incidencias;
    }
}
